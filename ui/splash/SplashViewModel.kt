package com.kapirti.social_chat_food_video.ui.presentation.splash

import androidx.compose.runtime.mutableStateOf
import com.google.firebase.firestore.FirebaseFirestoreException
import javax.inject.Inject
import com.google.firebase.messaging.FirebaseMessaging
import com.kapirti.social_chat_food_video.core.datastore.IsReviewDataStore
import com.kapirti.social_chat_food_video.core.datastore.OnBoardingRepository
import com.kapirti.social_chat_food_video.core.repository.SettingsRepository
import com.kapirti.social_chat_food_video.model.service.AccountService
import com.kapirti.social_chat_food_video.model.service.ConfigurationService
import com.kapirti.social_chat_food_video.model.service.FirestoreService
import com.kapirti.social_chat_food_video.model.service.LogService
import com.kapirti.social_chat_food_video.ui.presentation.KapirtiViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val accountService: AccountService,
    private val firestoreService: FirestoreService,
    private val onBoardingRepository: OnBoardingRepository,
    private val isReviewDataStore: IsReviewDataStore,
    private val settingsRepository: SettingsRepository,
    configurationService: ConfigurationService,
    logService: LogService,
): KapirtiViewModel(logService) {
    val showError = mutableStateOf(false)
    var showReviewDialog = mutableStateOf(false)

    init {
        launchCatching { configurationService.fetchConfiguration() }
    }


    fun onAppStart(
        navigateAndPopUpSplashToHome: () -> Unit,
        navigateAndPopUpSplashToWelcome: () -> Unit,
        navigateAndPopUpSplashToLogin: () -> Unit,
    ) {
        showError.value = false
        launchCatching {
            onBoardingRepository.readOnBoardingState().collect {
                if (it) {
                    if (accountService.hasUser) {
                        saveCurrentFcmToken()
                        askReview(navigateAndPopUpSplashToHome)
                    } else{ navigateAndPopUpSplashToLogin() }
                } else {  navigateAndPopUpSplashToWelcome() }
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


    private fun askReview(navigateAndPopUpSplashToTimeline: () -> Unit) {
        launchCatching {
            isReviewDataStore.readIsReviewState().collect {
                if (!it) { showReviewDialog.value = true }
                else {
                    navigateAndPopUpSplashToTimeline()
                }
            }
        }
    }

    private var job: Job? = null
    fun showDialogTrue(restartAppFlirt: () -> Unit) {
        launchCatching {
            job?.cancel()
            job = launchCatching {
                try {
                    isReviewDataStore.saveIsReviewState(true)
                    settingsRepository.rate()
                    showDialogFalse(restartAppFlirt)
                } catch (e: FirebaseFirestoreException) {
                }
            }
        }
    }
    fun showDialogFalse(restartAppFlirt: () -> Unit){
        launchCatching {
            restartAppFlirt() }
    }
}
