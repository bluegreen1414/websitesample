package com.kapirti.social_chat_food_video.ui.presentation.register

data class RegisterUiState(
    val email: String = "",
    val password: String = "",
    val button: Boolean = true,
    val isErrorEmail: Boolean = false,
    val isErrorPassword: Boolean = false,
)
