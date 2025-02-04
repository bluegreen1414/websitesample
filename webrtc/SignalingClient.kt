/*
 * Copyright 2023 Stream.IO, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kapirti.social_chat_food_video.webrtc

import com.kapirti.social_chat_food_video.BuildConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class SignalingClient(
    val uId: String,
    val roomId: String
) {
    private val signalingScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val client = OkHttpClient()
    private val request = Request
        .Builder()
        .url(BuildConfig.SIGNALING_SERVER_IP_ADDRESS)
        .addHeader("uid", uId)
        .addHeader("roomId", roomId)
        .build()

    // opening web socket with signaling server
    private val ws = client.newWebSocket(request, SignalingWebSocketListener())

    // session flow to send information about the session state to the subscribers
    private val _sessionStateFlow = MutableStateFlow(WebRTCSessionState.Offline)
    val sessionStateFlow: StateFlow<WebRTCSessionState> = _sessionStateFlow

    // signaling commands to send commands to value pairs to the subscribers
    private val _signalingCommandFlow = MutableSharedFlow<Pair<SignalingCommand, String>>()
    val signalingCommandFlow: SharedFlow<Pair<SignalingCommand, String>> = _signalingCommandFlow

    private var myId: String? = null
    private var otherUserId: String? = null

    fun setPeers(myOwnId: String, otherId: String) {
        myId = myOwnId
        otherUserId = otherId
    }

    fun sendCommand(signalingCommand: SignalingCommand, message: String) {
        if (myId.isNullOrEmpty().not() && otherUserId.isNullOrEmpty().not()) {
            val model = WebRtcMessageModel(
                myId = myId.orEmpty(),
                otherUserId = otherUserId.orEmpty(),
                command = signalingCommand.name,
                message = message
            )
            //ws.send("$signalingCommand $message")
            ws.send(Json.encodeToString(model))
        }
    }

    private inner class SignalingWebSocketListener : WebSocketListener() {
        override fun onMessage(webSocket: WebSocket, text: String) {
            when {
                text.startsWith(SignalingCommand.STATE.toString(), true) ->
                    handleStateMessage(text)

                text.startsWith(SignalingCommand.OFFER.toString(), true) ->
                    handleSignalingCommand(SignalingCommand.OFFER, text)

                text.startsWith(SignalingCommand.ANSWER.toString(), true) ->
                    handleSignalingCommand(SignalingCommand.ANSWER, text)

                text.startsWith(SignalingCommand.ICE.toString(), true) ->
                    handleSignalingCommand(SignalingCommand.ICE, text)
            }
        }
    }

    private fun handleStateMessage(message: String) {
        android.util.Log.d("myTag","the handled message is : $message")
        val state = getSeparatedMessage(message)
        _sessionStateFlow.value = WebRTCSessionState.valueOf(state)
    }

    private fun handleSignalingCommand(command: SignalingCommand, text: String) {
        val value = getSeparatedMessage(text)
        signalingScope.launch {
            _signalingCommandFlow.emit(command to value)
        }
    }

    private fun getSeparatedMessage(text: String) = text.substringAfter(' ')

    fun dispose() {
        _sessionStateFlow.value = WebRTCSessionState.Offline
        signalingScope.cancel()
        ws.cancel()
    }
}

enum class WebRTCSessionState {
    Active, // Offer and Answer messages has been sent
    Creating, // Creating session, offer has been sent
    Ready, // Both clients available and ready to initiate session
    Impossible, // We have less than two clients connected to the server
    Offline // unable to connect signaling server
}

enum class SignalingCommand {
    STATE, // Command for WebRTCSessionState
    OFFER, // to send or receive offer
    ANSWER, // to send or receive answer
    ICE // to send and receive ice candidates
}

@Serializable
data class WebRtcMessageModel(
    val myId: String,
    val otherUserId: String,
    val command: String,
    val message: String
)
