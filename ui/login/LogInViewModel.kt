package com.kapirti.social_chat_food_video.ui.presentation.login

import android.content.Context
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.mutableStateOf
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.messaging.FirebaseMessaging
import com.kapirti.social_chat_food_video.common.ext.isValidEmail
import com.kapirti.social_chat_food_video.core.constants.EditType
import com.kapirti.social_chat_food_video.core.constants.SelectType
import com.kapirti.social_chat_food_video.ui.presentation.KapirtiViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import com.kapirti.social_chat_food_video.core.datastore.CountryRepository
import com.kapirti.social_chat_food_video.core.datastore.EditTypeRepository
import com.kapirti.social_chat_food_video.core.datastore.LearnLanguageRepository
import com.kapirti.social_chat_food_video.model.service.AccountService
import com.kapirti.social_chat_food_video.model.service.FirestoreService
import com.kapirti.social_chat_food_video.model.service.LogService
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class LogInViewModel @Inject constructor(
    @ApplicationContext private val application: Context,
    private val accountService: AccountService,
    private val firestoreService: FirestoreService,
    private val editTypeRepository: EditTypeRepository,
    private val countryRepository: CountryRepository,
    private val learnLanguageRepository: LearnLanguageRepository,
    logService: LogService,
): KapirtiViewModel(logService) {
    var uiState = mutableStateOf(LogInUiState())
        private set

    private val email
        get() = uiState.value.email
    private val password
        get() = uiState.value.password
    private val button
        get() = uiState.value.button


    fun onEmailChange(newValue: String) {
        uiState.value = uiState.value.copy(email = newValue)
    }

    fun onPasswordChange(newValue: String) {
        uiState.value = uiState.value.copy(password = newValue)
    }

    fun onLogInClick(
        restartApp: () -> Unit,
        navigateAndPopUpLoginToRegister: () -> Unit,
        navigateAndPopUpLoginToEdit: () -> Unit,
        snackbarHostState: SnackbarHostState,
        emailError: String,
        emptyPasswordError: String, wrongPasswordError: String,
    ) {
        onButtonChange()
        if (!email.isValidEmail()) {
            launchCatching {
                onIsErrorEmailChange(true)
                snackbarHostState.showSnackbar(emailError)
                onButtonChange()
            }
            return
        }

        if (password.isBlank()) {
            launchCatching {
                onIsErrorPasswordChange(true)
                snackbarHostState.showSnackbar(emptyPasswordError)
                onButtonChange()
            }
            return
        }

        launchCatching {
            try {
                accountService.authenticate(email, password)
                firestoreService.getUser(accountService.currentUserId)?.let { at ->
                    when (at.type) {
                        SelectType.NOPE -> { nopeBody(navigateAndPopUpLoginToEdit = navigateAndPopUpLoginToEdit) }
                        SelectType.FLIRT -> { saveCountry(country = at.country, restartApp = restartApp) }
                        SelectType.MARRIAGE -> { saveCountry(country = at.country, restartApp = restartApp) }
                        SelectType.LANGUAGE_PRACTICE -> { lp(country = at.country, learnLanguage = at.learnLanguage, restartApp = restartApp) }
                        SelectType.HOTEL -> { saveCountry(country = at.country, restartApp = restartApp) }
                        SelectType.RESTAURANT -> { saveCountry(country = at.country, restartApp = restartApp) }
                        SelectType.CAFE -> { saveCountry(country = at.country, restartApp = restartApp) }
                    }
                }
            } catch (ex: FirebaseAuthException) {
                if (ex.errorCode == "ERROR_WRONG_PASSWORD"){
                    launchCatching {
                        onIsErrorPasswordChange(true)
                        snackbarHostState.showSnackbar(wrongPasswordError)
                        onButtonChange()
                    }
                } else if (ex.errorCode == "ERROR_USER_NOT_FOUND"){
                    navigateAndPopUpLoginToRegister()
                } else if (ex.errorCode == "ERROR_INVALID_CREDENTIAL") {
                    navigateAndPopUpLoginToRegister()
                }

                throw ex
            }
        }
    }


    private fun lp(country: String, learnLanguage: String, restartApp: () -> Unit){
        launchCatching {
            learnLanguageRepository.saveLearnLanguageState(learnLanguage)
            saveCountry(country, restartApp = restartApp)
        }
    }

    private fun saveCountry(country: String, restartApp: () -> Unit) {
        launchCatching {
            saveCurrentFcmToken()
            countryRepository.saveCountry(country)
            bodyContent(restartApp)
        }
    }
    private fun bodyContent(restartApp: () -> Unit){
        launchCatching {
            restartApp()
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




    fun onForgotPasswordClick(
        snackbarHostState: SnackbarHostState,
        emailError: String, recoveryEmailSent: String,
    ) {
        if (!email.isValidEmail()) {
            launchCatching {
                onIsErrorEmailChange(true)
                snackbarHostState.showSnackbar(emailError)
            }
            return
        }

        launchCatching {
            accountService.sendRecoveryEmail(email)
            onIsErrorEmailChange(false)
            snackbarHostState.showSnackbar(recoveryEmailSent)
        }
    }

    private fun nopeBody(navigateAndPopUpLoginToEdit: () -> Unit){
        launchCatching {
            editTypeRepository.saveEditTypeState(EditType.SELECT_ACCOUNT_TYPE)
            navigateAndPopUpLoginToEdit()
        }
    }

    private fun onButtonChange() { uiState.value = uiState.value.copy(button = !button) }
    private fun onIsErrorEmailChange(newValue: Boolean) { uiState.value = uiState.value.copy(isErrorEmail = newValue) }
    private fun onIsErrorPasswordChange(newValue: Boolean) { uiState.value = uiState.value.copy(isErrorPassword = newValue) }
}
