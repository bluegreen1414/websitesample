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

package com.kapirti.social_chat_food_video.webrtc.ui

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.kapirti.social_chat_food_video.webrtc.ui.screens.video.VideoCallScreen
import com.kapirti.social_chat_food_video.webrtc.WebRtcViewModel
import com.kapirti.social_chat_food_video.webrtc.sessions.LocalWebRtcSessionManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WebRtcActivity : ComponentActivity() {

    private val viewModel: WebRtcViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val uiState by viewModel.uiState

        requestPermissions(arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO), 0)

        //val roomId = intent.getStringExtra("roomId")
        //android.util.Log.d("myTag","room id from webrtc activity : $roomId")

//        val sessionManager: WebRtcSessionManager = WebRtcSessionManagerImpl(
//            context = this,
//            signalingClient = SignalingClient(),
//            peerConnectionFactory = StreamPeerConnectionFactory(this),
//        )
//
//        if (roomId.isNullOrEmpty().not()){
//            viewModel.setRoomId(roomId!!)
//        }

        //uiState.chatRoom?.let {
            setContent {
                //uiState.uiSessionManager?.let {
                    CompositionLocalProvider(LocalWebRtcSessionManager provides viewModel.sessionManager) {
                    //CompositionLocalProvider(LocalWebRtcSessionManager provides uiState.uiSessionManager) {
                        // A surface container using the 'background' color from the theme
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.background,
                        ) {
                            var onCallScreen by remember { mutableStateOf(false) }
                            val state by viewModel.sessionStateFlow.collectAsState()
                            /*
                            if (!onCallScreen && state != WebRTCSessionState.Ready) {
                                StageScreen(
                                    state = state, //uiState.webRTCSessionState,
                                    waitingName = uiState.otherUserName
                                ) { onCallScreen = true }
                            } else {
                                VideoCallScreen(/*uiState.isReceiver*/)
                            }

                             */

                            //if (state == WebRTCSessionState.Active) {
                                //VideoCallScreen(/*uiState.isReceiver*/)
                                VideoCallScreen(
                                    state = uiState,
                                    onLeaveCall = {
                                        viewModel.callEnded()
                                    }
                                )
//                            } else {
//                                Box(modifier = Modifier.fillMaxSize(),
//                                    contentAlignment = Alignment.Center) {
//                                    Text(text = "Waiting for ${uiState.otherUserName} to join...")
//                                }
//                            }

                        }
                    }
                //}
            }
        //}

    }

    override fun onDestroy() {
        super.onDestroy()
        //viewModel.onDestory()
    }
}
