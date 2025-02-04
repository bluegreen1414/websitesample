package com.kapirti.social_chat_food_video.ui.presentation.login

data class LogInUiState(
    val email: String = "",
    val password: String = "",
    val button: Boolean = true,
    val isErrorEmail: Boolean = false,
    val isErrorPassword: Boolean = false,
)
