package com.kapirti.social_chat_food_video.ui.presentation.home.users

import androidx.compose.runtime.mutableStateOf
import com.kapirti.social_chat_food_video.common.stateInUi
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.kapirti.social_chat_food_video.core.datastore.CountryRepository
import com.kapirti.social_chat_food_video.core.datastore.LearnLanguageRepository
import com.kapirti.social_chat_food_video.model.service.AccountService
import com.kapirti.social_chat_food_video.model.service.FirestoreService
import com.kapirti.social_chat_food_video.model.service.LogService
import com.kapirti.social_chat_food_video.ui.presentation.KapirtiViewModel
import com.kapirti.social_chat_food_video.ui.presentation.settings.SettingsUiState
import kotlinx.coroutines.flow.map

@HiltViewModel
class UsersViewModel @Inject constructor(
    private val accountService: AccountService,
    private val firestoreService: FirestoreService,
    private val countryRepository: CountryRepository,
    private val lPLanguageRepository: LearnLanguageRepository,
    logService: LogService,
): KapirtiViewModel(logService) {
    val uiState = accountService.currentUser.map { SettingsUiState(it.isAnonymous) }

    val usersFlirt = firestoreService.usersFlirt.stateInUi(emptyList())
    val usersMarriage = firestoreService.usersMarriage.stateInUi(emptyList())
    val usersLP = firestoreService.usersLP.stateInUi(emptyList())

    val currentUserId = accountService.currentUserId

    private val _country = mutableStateOf<String?>(null)
    val country: String?
        get() = _country.value

    private val _language = mutableStateOf<String?>(null)
    val language: String?
        get() = _language.value


    init {
        launchCatching {
            countryRepository.readCountry().collect { itCountry ->
                _country.value = itCountry
                lPLanguageRepository.readLearnLanguageState().collect { itCountry ->
                    _language.value = itCountry
                }
            }
        }
    }

    fun onLangClick(){
        launchCatching {
            //editTypeRepository.saveEditTypeState(LEARN_LANGUAGE)
        }
    }
    fun onCountryClick(){
        launchCatching {
            //editTypeRepository.saveEditTypeState(EditType.COUNTRY)
        }
    }
}
