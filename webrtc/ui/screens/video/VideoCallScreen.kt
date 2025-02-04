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

package com.kapirti.social_chat_food_video.webrtc.ui.screens.video

import android.app.Activity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.kapirti.social_chat_food_video.webrtc.WebRTCSessionState
import com.kapirti.social_chat_food_video.webrtc.ui.components.VideoRenderer
import com.kapirti.social_chat_food_video.webrtc.sessions.LocalWebRtcSessionManager
import com.kapirti.social_chat_food_video.webrtc.ui.WebRtcUiState

@Composable
fun VideoCallScreen(
    //isReceiver: Boolean?
    state: WebRtcUiState,
    onLeaveCall: () -> Unit
) {
    val sessionManager = LocalWebRtcSessionManager.current

//    LaunchedEffect(key1 = Unit) {
//        sessionManager.onSessionScreenReady(/*isReceiver == true*/)
//    }

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        var parentSize: IntSize by remember { mutableStateOf(IntSize(0, 0)) }

        val remoteVideoTrackState by sessionManager.remoteVideoTrackFlow.collectAsState(null)
        val remoteVideoTrack = remoteVideoTrackState

        val localVideoTrackState by sessionManager.localVideoTrackFlow.collectAsState(null)
        val localVideoTrack = localVideoTrackState

        var callMediaState by remember { mutableStateOf(CallMediaState()) }

        if (state.webRTCSessionState != WebRTCSessionState.Active) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = "Waiting for ${state.otherUserName} to join")
            }
        }

        if (remoteVideoTrack != null) {
            VideoRenderer(
                videoTrack = remoteVideoTrack,
                modifier = Modifier
                    .fillMaxSize()
                    .onSizeChanged { parentSize = it },
            )
        }

        if (localVideoTrack != null && callMediaState.isCameraEnabled) {
            FloatingVideoRenderer(
                modifier = Modifier
                    .size(width = 150.dp, height = 210.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .align(Alignment.TopEnd),
                videoTrack = localVideoTrack,
                parentBounds = parentSize,
                paddingValues = PaddingValues(0.dp),
            )
        }

        val activity = (LocalContext.current as? Activity)

        VideoCallControls(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            callMediaState = callMediaState,
            onCallAction = {
                when (it) {
                    is CallAction.ToggleMicroPhone -> {
                        val enabled = callMediaState.isMicrophoneEnabled.not()
                        callMediaState = callMediaState.copy(isMicrophoneEnabled = enabled)
                        sessionManager.enableMicrophone(enabled)
                    }

                    is CallAction.ToggleCamera -> {
                        val enabled = callMediaState.isCameraEnabled.not()
                        callMediaState = callMediaState.copy(isCameraEnabled = enabled)
                        sessionManager.enableCamera(enabled)
                    }

                    CallAction.FlipCamera -> sessionManager.flipCamera()
                    CallAction.LeaveCall -> {
                        onLeaveCall.invoke()
                        sessionManager.disconnect()
                        activity?.finish()
                    }
                }
            },
        )
    }
}
