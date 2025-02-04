package com.kapirti.social_chat_food_video.ui.presentation.register

import android.content.Context
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.mutableStateOf
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.messaging.FirebaseMessaging
import com.kapirti.social_chat_food_video.common.ext.isValidEmail
import com.kapirti.social_chat_food_video.common.ext.isValidPassword
import com.kapirti.social_chat_food_video.core.constants.EditType.SELECT_ACCOUNT_TYPE
import com.kapirti.social_chat_food_video.core.constants.SelectType.NOPE
import com.kapirti.social_chat_food_video.core.datastore.EditTypeRepository
import com.kapirti.social_chat_food_video.model.User
import com.kapirti.social_chat_food_video.model.service.AccountService
import com.kapirti.social_chat_food_video.model.service.FirestoreService
import com.kapirti.social_chat_food_video.model.service.LogService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.kapirti.social_chat_food_video.ui.presentation.KapirtiViewModel
import dagger.hilt.android.qualifiers.ApplicationContext

@HiltViewModel
class RegisterViewModel @Inject constructor(
    @ApplicationContext private val application: Context,
    logService: LogService,
    private val accountService: AccountService,
    private val firestoreService: FirestoreService,
    private val editTypeRepository: EditTypeRepository,
): KapirtiViewModel(logService) {
    var uiState = mutableStateOf(RegisterUiState())
        private set

    private val email
        get() = uiState.value.email
    private val password
        get() = uiState.value.password
    private val button
        get() = uiState.value.button

    fun onEmailChange(newValue: String) { uiState.value = uiState.value.copy(email = newValue) }
    fun onPasswordChange(newValue: String) { uiState.value = uiState.value.copy(password = newValue) }

    fun onRegisterClick(
        snackbarHostState: SnackbarHostState,
        navigateAndPopUpRegisterToEdit: () -> Unit,
        navigateAndPopUpRegisterToLogin: () -> Unit,
        email_error: String,
        password_error: String
    ) {
        onButtonChange()
        if (!email.isValidEmail()) {
            launchCatching { snackbarHostState.showSnackbar(email_error) }
            onButtonChange()
            return
        }

        if (!password.isValidPassword()) {
            launchCatching { snackbarHostState.showSnackbar(password_error) }
            onButtonChange()
            return
        }

        launchCatching {
            try {
                accountService.linkAccount(email, password)
                firestoreService.saveUser(
                    User(
                        id = accountService.currentUserId,
                        name = accountService.currentUserEmail,
                        type = NOPE
                    )
                )
                saveCurrentFcmToken()
                editTypeRepository.saveEditTypeState(SELECT_ACCOUNT_TYPE)
                navigateAndPopUpRegisterToEdit()
            } catch (ex: FirebaseAuthException) {
                if (ex.errorCode == "ERROR_EMAIL_ALREADY_IN_USE") {
                    navigateAndPopUpRegisterToLogin()
                }

                launchCatching { snackbarHostState.showSnackbar(ex.localizedMessage ?: "") }
                onButtonChange()
                throw ex
            }
        }
    }

    private fun saveCurrentFcmToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                firestoreService.updateUserFcmToken(token)
            }
        }
    }


    private fun onButtonChange() { uiState.value = uiState.value.copy(button = !button) }
    private fun onIsErrorEmailChange(newValue: Boolean) { uiState.value = uiState.value.copy(isErrorEmail = newValue) }
    private fun onIsErrorPasswordChange(newValue: Boolean) { uiState.value = uiState.value.copy(isErrorPassword = newValue) }
}
