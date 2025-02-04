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

package com.kapirti.social_chat_food_video.webrtc.ui.screens.stage

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.kapirti.social_chat_food_video.webrtc.WebRTCSessionState

@Composable
fun StageScreen(
    state: WebRTCSessionState,
    waitingName: String,
    onJoinCall: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        var enabledCall by remember { mutableStateOf(false) }

        val text = when (state) {
            WebRTCSessionState.Offline -> {
                enabledCall = false
                "Start Session"
            }

            WebRTCSessionState.Impossible -> {
                enabledCall = false
                "Session Impossible"
            }

            WebRTCSessionState.Ready -> {
                enabledCall = true
                "Session Ready"
            }

            WebRTCSessionState.Creating -> {
                enabledCall = true
                "Session Creating"
            }

            WebRTCSessionState.Active -> {
                enabledCall = false
                "Session Active"
            }
        }
        Column(
            modifier = Modifier.align(Alignment.Center),
        ) {
            Text(
                text = "Call with : $waitingName",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
            )
            Button(
                enabled = enabledCall,
                onClick = { onJoinCall.invoke() },
            ) {
                Text(
                    text = text,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }

    }
}
