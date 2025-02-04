/*
 * Copyright (C) 2025 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kapirti.social_chat_food_video.webrtc

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.kapirti.social_chat_food_video.model.CallRecord
import com.kapirti.social_chat_food_video.model.service.FirestoreService
import com.kapirti.social_chat_food_video.ui.presentation.calls.CallType
import com.kapirti.social_chat_food_video.webrtc.peer.StreamPeerConnectionFactory
import com.kapirti.social_chat_food_video.webrtc.sessions.WebRtcSessionManager
import com.kapirti.social_chat_food_video.webrtc.sessions.WebRtcSessionManagerImpl
import com.kapirti.social_chat_food_video.webrtc.ui.WebRtcUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@HiltViewModel
class WebRtcViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    private val savedStateHandle: SavedStateHandle,
    private val firestoreService: FirestoreService,
    private val firebaseAuth: FirebaseAuth,
) : ViewModel() {

    private val savedStateRoomId: String? = savedStateHandle.get<String>("roomId")
    private val isReceiver: Boolean? = savedStateHandle.get<Boolean>("receiver")


    var uiState = mutableStateOf(WebRtcUiState())
        private set

    var sessionManager: WebRtcSessionManager = WebRtcSessionManagerImpl(
        context = context,
        signalingClient = SignalingClient(
            uId = firebaseAuth.currentUser?.uid.orEmpty(),
            roomId = savedStateRoomId.orEmpty()
        ),
        peerConnectionFactory = StreamPeerConnectionFactory(context)
    )

    val sessionStateFlow = sessionManager.signalingClient.sessionStateFlow

    init {
        savedStateRoomId?.let { setRoomId(roomId = it) }
        observerState()
    }

    private fun setRoomId(roomId: String) = viewModelScope.launch {
        android.util.Log.d("myTag","saved state room id : $savedStateRoomId")
        android.util.Log.d("myTag","is receiver : $isReceiver")
        val chatRoom = firestoreService.getChatRoomFromChatRoom(roomId)
        val myId = firebaseAuth.currentUser!!.uid
        val otherUserId = chatRoom!!.userIds.filterNot { it == myId }.first()
        android.util.Log.d("myTag", "other user id in webRtcSession : $otherUserId")
        val otherUser = firestoreService.getUser(otherUserId)



//        sessionManager = WebRtcSessionManagerImpl(
//            context = context,
//            signalingClient = SignalingClient(),
//            peerConnectionFactory = StreamPeerConnectionFactory(context)
//        )

//        sessionManager.signalingClient.sessionStateFlow.collectLatest {
//            android.util.Log.d("myTag","rtc session state : $it")
//            uiState.value = uiState.value.copy(
//                webRTCSessionState = it
//            )
//        }

        sessionManager.signalingClient.setPeers(myId, otherUserId)

        uiState.value = uiState.value.copy(
            chatRoom = chatRoom,
            otherUserName = otherUser?.name.orEmpty(),
            isReceiver = isReceiver
            //uiSessionManager = sessionManager
        )



    }

    private fun observerState() = viewModelScope.launch {
        sessionStateFlow.collectLatest { state ->

            uiState.value = uiState.value.copy(
                webRTCSessionState = state
            )

            if (state == WebRTCSessionState.Ready && isReceiver != true) {
                sessionManager.onSessionScreenReady(true)
                callStartTimestamp = Timestamp.now()
            } else if (state == WebRTCSessionState.Creating && isReceiver == true) {
                sessionManager.onSessionScreenReady(false)
                callStartTimestamp = Timestamp.now()
            }
        }
    }

    var callStartTimestamp = Timestamp.now()

    fun callEnded() = viewModelScope.launch {
        android.util.Log.d("myTag","on call ended, save call record to firestore..")

        val myId = firebaseAuth.currentUser!!.uid
        val otherUserId = uiState.value.chatRoom!!.userIds.filterNot { it == myId }.first()
        android.util.Log.d("myTag", "other user id in webRtcSession : $otherUserId")
        val otherUser = firestoreService.getUser(otherUserId)

        firestoreService.saveCallRecord(
            CallRecord(
                callType = if (isReceiver == true) CallType.INCOMING else CallType.OUTGOING,
                //roomId = uiState.value.chatRoom?.id.orEmpty(),
                userId = myId,
                peerName = otherUser?.name.orEmpty(),
                callStart = callStartTimestamp,
                callEnd = Timestamp.now()
            )
        )
    }

    fun onDestory() {
        sessionManager.disconnect()
    }

}
