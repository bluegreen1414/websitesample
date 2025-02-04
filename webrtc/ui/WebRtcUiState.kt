package com.kapirti.social_chat_food_video.webrtc.ui

import com.kapirti.social_chat_food_video.model.ChatRoomFromChatRoom
import com.kapirti.social_chat_food_video.webrtc.WebRTCSessionState

data class WebRtcUiState(
    val chatRoom: ChatRoomFromChatRoom? = null,
    val otherUserName: String = "",
    val isReceiver: Boolean? = null,
    val webRTCSessionState: WebRTCSessionState = WebRTCSessionState.Offline,
    //val uiSessionManager: WebRtcSessionManager? = null
)

