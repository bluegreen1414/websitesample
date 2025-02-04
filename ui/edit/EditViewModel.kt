package com.kapirti.social_chat_food_video.ui.presentation.edit

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.kapirti.social_chat_food_video.core.constants.EditType.PRODUCT
import com.kapirti.social_chat_food_video.core.constants.EditType.SELECT_ACCOUNT_TYPE
import com.kapirti.social_chat_food_video.core.constants.SelectType.CAFE
import com.kapirti.social_chat_food_video.core.constants.SelectType.FLIRT
import com.kapirti.social_chat_food_video.core.constants.SelectType.HOTEL
import com.kapirti.social_chat_food_video.core.constants.SelectType.LANGUAGE_PRACTICE
import com.kapirti.social_chat_food_video.core.constants.SelectType.MARRIAGE
import com.kapirti.social_chat_food_video.core.constants.SelectType.RESTAURANT
import com.kapirti.social_chat_food_video.core.datastore.CountryRepository
import com.kapirti.social_chat_food_video.core.datastore.EditTypeRepository
import com.kapirti.social_chat_food_video.core.datastore.LearnLanguageRepository
import com.kapirti.social_chat_food_video.model.service.AccountService
import com.kapirti.social_chat_food_video.model.service.FirestoreService
import com.kapirti.social_chat_food_video.model.service.LogService
import com.kapirti.social_chat_food_video.model.service.StorageService
import com.kapirti.social_chat_food_video.model.service.impl.FirestoreServiceImpl
import com.kapirti.social_chat_food_video.ui.presentation.KapirtiViewModel
import com.kapirti.social_chat_food_video.ui.presentation.add.video.VideoUploadWorker
import com.kapirti.social_chat_food_video.ui.presentation.edit.func.CafeWorker
import com.kapirti.social_chat_food_video.ui.presentation.edit.func.FlirtWorker
import com.kapirti.social_chat_food_video.ui.presentation.edit.func.HotelWorker
import com.kapirti.social_chat_food_video.ui.presentation.edit.func.LanguagePracticeWorker
import com.kapirti.social_chat_food_video.ui.presentation.edit.func.MarriageWorker
import com.kapirti.social_chat_food_video.ui.presentation.edit.func.RestaurantWorker
import com.kapirti.social_chat_food_video.ui.presentation.home.shop.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.ByteArrayOutputStream
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class EditViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val accountService: AccountService,
    private val firestoreService: FirestoreService,
    private val storageService: StorageService,
    private val editTypeRepository: EditTypeRepository,
    private val countryRepository: CountryRepository,
    private val learnLanguageRepository: LearnLanguageRepository,
    logService: LogService,
): KapirtiViewModel(logService) {
    val uid = accountService.currentUserId
    private val _editType = mutableStateOf<String?>(null)

    var uiState = mutableStateOf(EditUiState())
        private set

    private val _country = mutableStateOf<String?>(null)
    val country: String?
        get() = _country.value

    init {
        launchCatching {
           // loadInterstitialAd()
            editTypeRepository.readEditTypeState().collect {
                _editType.value = it
                countryRepository.readCountry().collect{ itc ->
                    _country.value = itc
                }
            }
        }
    }

    private val questionOrder: List<SurveyQuestion> = when (_editType.value) {
        SELECT_ACCOUNT_TYPE -> listOf(SurveyQuestion.SELECT_ACCOUNT_TYPE)
        FLIRT -> listOf(
            SurveyQuestion.COUNTRY,
            SurveyQuestion.USERNAME,
            SurveyQuestion.GENDER,
            SurveyQuestion.BIRTHDAY,
            SurveyQuestion.TAKE_SELFIE,
            SurveyQuestion.FREE_TIME,
            SurveyQuestion.DESCRIPTION,
        )
        MARRIAGE -> listOf(
            SurveyQuestion.COUNTRY,
            SurveyQuestion.USERNAME,
            SurveyQuestion.GENDER,
            SurveyQuestion.BIRTHDAY,
            SurveyQuestion.TAKE_SELFIE,
            SurveyQuestion.FREE_TIME,
            SurveyQuestion.DESCRIPTION,
        )
        LANGUAGE_PRACTICE -> listOf(
            SurveyQuestion.LEARN_LANGUAGE,
            SurveyQuestion.MOTHER_TONGUE,
            SurveyQuestion.COUNTRY,
            SurveyQuestion.USERNAME,
            SurveyQuestion.GENDER,
            SurveyQuestion.BIRTHDAY,
            SurveyQuestion.TAKE_SELFIE,
            SurveyQuestion.FREE_TIME,
            SurveyQuestion.DESCRIPTION,
        )
        HOTEL -> listOf(
            SurveyQuestion.COUNTRY,
            SurveyQuestion.CITY_ADDRESS,
            SurveyQuestion.USERNAME,
            SurveyQuestion.DESCRIPTION,
            SurveyQuestion.TAKE_SELFIE
        )
        RESTAURANT -> listOf(
            SurveyQuestion.COUNTRY,
            SurveyQuestion.CITY_ADDRESS,
            SurveyQuestion.USERNAME,
            SurveyQuestion.DESCRIPTION,
            SurveyQuestion.TAKE_SELFIE
        )
        CAFE -> listOf(
            SurveyQuestion.COUNTRY,
            SurveyQuestion.CITY_ADDRESS,
            SurveyQuestion.USERNAME,
            SurveyQuestion.DESCRIPTION,
            SurveyQuestion.TAKE_SELFIE
        )
        PRODUCT -> listOf(
            SurveyQuestion.TAKE_SELFIE,
            SurveyQuestion.DESCRIPTION,
        )
        else -> emptyList()
    }

    private var questionIndex = 0

    private val _accountType = mutableStateOf<String?>(null)
    val accountType: String?
        get() = _accountType.value

    private val _username = mutableStateOf<String?>(null)
    val username: String?
        get() = _username.value

    private val _gender = mutableStateOf<String?>(null)
    val gender: String?
        get() = _gender.value

    private val _birthday = mutableStateOf<String?>(null)
    val birthday: String?
        get() = _birthday.value

    private val _description = mutableStateOf<String?>(null)
    val description: String?
        get() = _description.value

    private val _freeTimeResponse = mutableStateListOf<String>()
    val freeTimeResponse: List<String>
        get() = _freeTimeResponse

    private val _motherTongue = mutableStateOf<String?>(null)
    val motherTongue: String?
        get() = _motherTongue.value

    private val _learnLanguage = mutableStateOf<String?>(null)
    val learnLanguage: String?
        get() = _learnLanguage.value

    private val _selfieUri = mutableStateOf<Uri?>(null)
    val selfieUri
        get() = _selfieUri.value

    private val _bitmap = mutableStateOf<Bitmap?>(null)
    val bitmap
        get() = _bitmap.value


    // ----- Survey status exposed as State -----

    private val _surveyScreenData = mutableStateOf(createSurveyScreenData())
    val surveyScreenData: SurveyScreenData?
        get() = _surveyScreenData.value

    private val _isNextEnabled = mutableStateOf(false)
    val isNextEnabled: Boolean
        get() = _isNextEnabled.value


    fun onBackPressed(): Boolean {
        if (questionIndex == 0) {
            return false
        }
        changeQuestion(questionIndex - 1)
        return true
    }

    fun onPreviousPressed() {
        if (questionIndex == 0) {
            throw IllegalStateException("onPreviousPressed when on question 0")
        }
        changeQuestion(questionIndex - 1)
    }

    fun onNextPressed() {
        changeQuestion(questionIndex + 1)
    }

    private fun changeQuestion(newQuestionIndex: Int) {
        questionIndex = newQuestionIndex
        _isNextEnabled.value = getIsNextEnabled()
        _surveyScreenData.value = createSurveyScreenData()
    }


    fun onDonePressed(restartAppEdit: () -> Unit, restartAppUsers: () -> Unit, restartAppProduct: () -> Unit) {
        _isNextEnabled.value = false
        onIsDoneBtnWorkingChange(true)
        //showInterstitialAd(context)

        when (_editType.value) {
            SELECT_ACCOUNT_TYPE -> { saveSelectAccountType(restartAppEdit) }
            FLIRT -> { saveFlirt(restartAppUsers) }
            MARRIAGE -> { saveMarriage(restartAppUsers) }
            LANGUAGE_PRACTICE -> { saveLanguagePractice(restartAppUsers) }
            HOTEL -> { saveHotel(restartAppUsers) }
            RESTAURANT -> { saveRestaurant(restartAppUsers) }
            CAFE -> { saveCafe(restartAppUsers) }
            PRODUCT -> { saveProduct(restartAppProduct)}
        }
    }

    fun onAccountTypeChange(newValue: String) {
        _accountType.value = newValue
        _isNextEnabled.value = getIsNextEnabled()
    }

    fun onUsernameChange(newValue: String) {
        _username.value = newValue
        _isNextEnabled.value = getIsNextEnabled()
    }

    fun onBirthdayChange(newValue: String) {
        _birthday.value = newValue
        _isNextEnabled.value = getIsNextEnabled()
    }

    fun onGenderChange(newValue: String) {
        _gender.value = newValue
        _isNextEnabled.value = getIsNextEnabled()
    }
    fun onDescriptionChange(newValue: String) {
        _description.value = newValue
        _isNextEnabled.value = getIsNextEnabled()
    }

    fun onFreeTimeResponse(selected: Boolean, answer: String) {
        if (selected) {
            _freeTimeResponse.add(answer)
        } else {
            _freeTimeResponse.remove(answer)
        }
        _isNextEnabled.value = getIsNextEnabled()
    }
    fun onMotherTongueChange(newValue: String) {
        _motherTongue.value = newValue
        _isNextEnabled.value = getIsNextEnabled()
    }
    fun onLearnLanguageChange(newValue: String) {
        _learnLanguage.value = newValue
        _isNextEnabled.value = getIsNextEnabled()
    }

    fun onSelfieResponse(uri: Uri) {
        _selfieUri.value = uri
        _isNextEnabled.value = getIsNextEnabled()
    }
    fun onCountryResponse(newValue: String) {
        launchCatching {
            countryRepository.saveCountry(newValue)
            _isNextEnabled.value = getIsNextEnabled()
        }
    }


    private fun getIsNextEnabled(): Boolean {
        return when (questionOrder[questionIndex]) {
            SurveyQuestion.SELECT_ACCOUNT_TYPE -> _accountType.value != null
            SurveyQuestion.COUNTRY -> _country.value != null
            SurveyQuestion.USERNAME -> _username.value != null
            SurveyQuestion.GENDER -> _gender.value != null
            SurveyQuestion.BIRTHDAY -> _birthday.value != null
            SurveyQuestion.FREE_TIME -> _freeTimeResponse.isNotEmpty()
            SurveyQuestion.DESCRIPTION -> _description.value != null
            SurveyQuestion.TAKE_SELFIE -> _selfieUri.value != null
            SurveyQuestion.MOTHER_TONGUE -> _motherTongue.value != null
            SurveyQuestion.LEARN_LANGUAGE -> _learnLanguage.value != null
            SurveyQuestion.CITY_ADDRESS -> _gender.value != null && _birthday.value != null
        }
    }
    private fun createSurveyScreenData(): SurveyScreenData {
        return SurveyScreenData(
            questionIndex = questionIndex,
            questionCount = questionOrder.size,
            shouldShowPreviousButton = questionIndex > 0,
            shouldShowDoneButton = questionIndex == questionOrder.size - 1,
            surveyQuestion = questionOrder[questionIndex],
        )
    }

    private fun onIsDoneBtnWorkingChange(newValue: Boolean) { uiState.value = uiState.value.copy(isDoneBtnWorking = newValue) }

    private fun saveSelectAccountType(restartAppEdit: () -> Unit){
        launchCatching {
            editTypeRepository.saveEditTypeState(_accountType.value ?: "nope")
            restartAppEdit()
        }
    }
    private fun saveFlirt(restartAppUsers: () -> Unit) {
        launchCatching {
            _selfieUri?.let {
                val randomUid = UUID.randomUUID().toString()
                val uid = accountService.currentUserId
                val inputData = workDataOf(
                    VideoUploadWorker.RANDOM_ID to randomUid.toString(),
                    VideoUploadWorker.USER_ID to uid.toString(),
                    FlirtWorker.PHOTO_BYTE_ARRAY to it.value.toString(),
                    FirestoreServiceImpl.COUNTRY_FIELD to _country.value.toString(),
                    FirestoreServiceImpl.USERNAME_FIELD to _username.value.toString(),
                    FirestoreServiceImpl.BIRTHDAY_FIELD to _birthday.value.toString(),
                    FirestoreServiceImpl.GENDER_FIELD to _gender.value.toString(),
                    FirestoreServiceImpl.HOBBY_FIELD to _freeTimeResponse.toString(),
                    FirestoreServiceImpl.DESCRIPTION_FIELD to _description.value.toString(),
                )

                val uploadRequest = OneTimeWorkRequestBuilder<FlirtWorker>()
                    .setInputData(inputData)
                    .build()

                WorkManager.getInstance(context).enqueue(uploadRequest)

                restartAppUsers()
            }
        }
    }
    private fun saveMarriage(restartAppUsers: () -> Unit) {
        launchCatching {
            _selfieUri?.let {
                val randomUid = UUID.randomUUID().toString()
                val uid = accountService.currentUserId
                val inputData = workDataOf(
                    VideoUploadWorker.RANDOM_ID to randomUid.toString(),
                    VideoUploadWorker.USER_ID to uid.toString(),
                    FlirtWorker.PHOTO_BYTE_ARRAY to it.value.toString(),
                    FirestoreServiceImpl.COUNTRY_FIELD to _country.value.toString(),
                    FirestoreServiceImpl.USERNAME_FIELD to _username.value.toString(),
                    FirestoreServiceImpl.BIRTHDAY_FIELD to _birthday.value.toString(),
                    FirestoreServiceImpl.GENDER_FIELD to _gender.value.toString(),
                    FirestoreServiceImpl.HOBBY_FIELD to _freeTimeResponse.toString(),
                    FirestoreServiceImpl.DESCRIPTION_FIELD to _description.value.toString(),
                )

                val uploadRequest = OneTimeWorkRequestBuilder<MarriageWorker>()
                    .setInputData(inputData)
                    .build()

                WorkManager.getInstance(context).enqueue(uploadRequest)

                restartAppUsers()
            }
        }
    }
    private fun saveLanguagePractice(restartAppUsers: () -> Unit) {
        launchCatching {
            _selfieUri?.let {
                val randomUid = UUID.randomUUID().toString()
                val uid = accountService.currentUserId
                val inputData = workDataOf(
                    VideoUploadWorker.RANDOM_ID to randomUid.toString(),
                    VideoUploadWorker.USER_ID to uid.toString(),
                    FlirtWorker.PHOTO_BYTE_ARRAY to it.value.toString(),
                    FirestoreServiceImpl.COUNTRY_FIELD to _country.value.toString(),
                    FirestoreServiceImpl.USERNAME_FIELD to _username.value.toString(),
                    FirestoreServiceImpl.BIRTHDAY_FIELD to _birthday.value.toString(),
                    FirestoreServiceImpl.GENDER_FIELD to _gender.value.toString(),
                    FirestoreServiceImpl.HOBBY_FIELD to _freeTimeResponse.toString(),
                    FirestoreServiceImpl.DESCRIPTION_FIELD to _description.value.toString(),
                    FirestoreServiceImpl.LEARN_LANGUAGE_FIELD to _learnLanguage.value.toString(),
                    FirestoreServiceImpl.MOTHER_TONGUE_FIELD to _motherTongue.value.toString(),
                )

                val uploadRequest = OneTimeWorkRequestBuilder<LanguagePracticeWorker>()
                    .setInputData(inputData)
                    .build()

                WorkManager.getInstance(context).enqueue(uploadRequest)

                restartAppUsers()
            }
        }
    }
    private fun saveHotel(restartAppUsers: () -> Unit) {
        launchCatching {
            _selfieUri?.let {
                val randomUid = UUID.randomUUID().toString()
                val uid = accountService.currentUserId
                val inputData = workDataOf(
                    VideoUploadWorker.RANDOM_ID to randomUid.toString(),
                    VideoUploadWorker.USER_ID to uid.toString(),
                    FlirtWorker.PHOTO_BYTE_ARRAY to it.value.toString(),
                    FirestoreServiceImpl.COUNTRY_FIELD to _country.value.toString(),
                    FirestoreServiceImpl.USERNAME_FIELD to _username.value.toString(),
                    FirestoreServiceImpl.CITY_FIELD to _birthday.value.toString(),
                    FirestoreServiceImpl.ADDRESS_FIELD to _gender.value.toString(),
                    FirestoreServiceImpl.DESCRIPTION_FIELD to _description.value.toString(),
                )

                val uploadRequest = OneTimeWorkRequestBuilder<HotelWorker>()
                    .setInputData(inputData)
                    .build()

                WorkManager.getInstance(context).enqueue(uploadRequest)

                restartAppUsers()
            }
        }
    }
    private fun saveRestaurant(restartAppUsers: () -> Unit) {
        launchCatching {
            _selfieUri?.let {
                val randomUid = UUID.randomUUID().toString()
                val uid = accountService.currentUserId
                val inputData = workDataOf(
                    VideoUploadWorker.RANDOM_ID to randomUid.toString(),
                    VideoUploadWorker.USER_ID to uid.toString(),
                    FlirtWorker.PHOTO_BYTE_ARRAY to it.value.toString(),
                    FirestoreServiceImpl.COUNTRY_FIELD to _country.value.toString(),
                    FirestoreServiceImpl.USERNAME_FIELD to _username.value.toString(),
                    FirestoreServiceImpl.CITY_FIELD to _birthday.value.toString(),
                    FirestoreServiceImpl.ADDRESS_FIELD to _gender.value.toString(),
                    FirestoreServiceImpl.DESCRIPTION_FIELD to _description.value.toString(),
                )

                val uploadRequest = OneTimeWorkRequestBuilder<RestaurantWorker>()
                    .setInputData(inputData)
                    .build()

                WorkManager.getInstance(context).enqueue(uploadRequest)

                restartAppUsers()
            }
        }
    }
    private fun saveCafe(restartAppUsers: () -> Unit) {
        launchCatching {
            _selfieUri?.let {
                val randomUid = UUID.randomUUID().toString()
                val uid = accountService.currentUserId
                val inputData = workDataOf(
                    VideoUploadWorker.RANDOM_ID to randomUid.toString(),
                    VideoUploadWorker.USER_ID to uid.toString(),
                    FlirtWorker.PHOTO_BYTE_ARRAY to it.value.toString(),
                    FirestoreServiceImpl.COUNTRY_FIELD to _country.value.toString(),
                    FirestoreServiceImpl.USERNAME_FIELD to _username.value.toString(),
                    FirestoreServiceImpl.CITY_FIELD to _birthday.value.toString(),
                    FirestoreServiceImpl.ADDRESS_FIELD to _gender.value.toString(),
                    FirestoreServiceImpl.DESCRIPTION_FIELD to _description.value.toString(),
                )

                val uploadRequest = OneTimeWorkRequestBuilder<CafeWorker>()
                    .setInputData(inputData)
                    .build()

                WorkManager.getInstance(context).enqueue(uploadRequest)

                restartAppUsers()
            }
        }
    }

    private fun saveProduct(restartApp: () -> Unit) {
        launchCatching {
            _selfieUri?.let {
                if (Build.VERSION.SDK_INT < 28) {
                    _bitmap.value =
                        MediaStore.Images.Media.getBitmap(context.contentResolver, it.value)
                    saveProductBody(restartApp)
                } else {
                    val source = ImageDecoder.createSource(context.contentResolver, it.value!!)
                    _bitmap.value = ImageDecoder.decodeBitmap(source)
                    saveProductBody(restartApp)
                }
            }
        }
    }
    private fun saveProductBody(restartAppProfile: () -> Unit) {
        launchCatching {
            _bitmap.value?.let { bitmapNew ->
                val kucukBitmap = kucukBitmapOlustur(bitmapNew!!, 300)
                val outputStream = ByteArrayOutputStream()
                kucukBitmap.compress(Bitmap.CompressFormat.PNG, 50, outputStream)
                val byteDizisi = outputStream.toByteArray()
                val randomUid = UUID.randomUUID().toString()

                storageService.savePhoto(byteDizisi, uid = randomUid)
                val link = storageService.getPhoto(randomUid)

                firestoreService.saveProduct(
                    Product(
                        photo = link,
                        description = _description.value ?: "",
                    )
                )
                restartAppProfile()
            }
        }
    }
}
/**
    private val _accountTypeCountry = mutableStateOf<String?>(null)
    private val _accountTypeMotherTongue = mutableStateOf<String?>(null)
    private val _mePhoto = mutableStateOf<String?>(null)
    private val _meNameSurname = mutableStateOf<String?>(null)


    //TAKE_VIDEO,


    NAME,




        LEARN_LANGUAGE -> listOf(SurveyQuestion.LEARN_LANGUAGE)

        PHOTOS -> listOf(SurveyQuestion.TAKE_SELFIE)
        REEL -> listOf(SurveyQuestion.TAKE_VIDEO)
        STORY -> listOf(SurveyQuestion.TAKE_SELFIE)


/**

        DISPLAY_NAME -> listOf(SurveyQuestion.DISPLAY_NAME)
        GENDER -> listOf(SurveyQuestion.GENDER)
       */

        else -> emptyList()
    }






    fun onDonePressed(
        popUp: () -> Unit,
        restartApp: () -> Unit,
        restartAppEdit: () -> Unit,
        restartAppProfile: () -> Unit,
       // navigateTimeline: () -> Unit
    ) {
        _isNextEnabled.value = false
        onIsDoneBtnWorkingChange(true)
        //showInterstitialAd(context)


            LEARN_LANGUAGE -> { saveLearnLanguage(restartApp) }


            PHOTOS  -> { accountPhotosBitmapSave(context = context, restartApp = restartApp) }
            STORY -> { saveStory(popUp)}


            /**
            DISPLAY_NAME -> { saveDisplayName(restartApp = restartApp) }
            GENDER -> { saveGender(restartApp = restartApp) } */
        }
    }





    private fun saveStory(restartApp: () -> Unit) {
        launchCatching {
            _selfieUri?.let {
                if (Build.VERSION.SDK_INT < 28) {
                    _bitmap.value =
                        MediaStore.Images.Media.getBitmap(context.contentResolver, it.value)
                    saveStoryBody(restartApp)
                } else {
                    val source = ImageDecoder.createSource(context.contentResolver, it.value!!)
                    _bitmap.value = ImageDecoder.decodeBitmap(source)
                    saveStoryBody(restartApp)
                }
            }
        }
    }
    private fun saveStoryBody(restartAppProfile: () -> Unit) {
        launchCatching {
            _bitmap.value?.let { bitmapNew ->
                val kucukBitmap = kucukBitmapOlustur(bitmapNew!!, 300)
                val outputStream = ByteArrayOutputStream()
                kucukBitmap.compress(Bitmap.CompressFormat.PNG, 50, outputStream)
                val byteDizisi = outputStream.toByteArray()
                val randomUid = UUID.randomUUID().toString()

                storageService.savePhoto(byteDizisi, uid = randomUid)
                val link = storageService.getPhoto(randomUid)


                firestoreService.saveStory(
                    Story(
                        imageUrl = _mePhoto.value ?: "",
                        contentUrl = link,
                        name = _meNameSurname.value ?: "",
                    )
                )
                restartAppProfile()
            }
        }
    }





    private fun saveLearnLanguage(restartApp: () -> Unit) {
        launchCatching {
//            firestoreService.updateAccountTypeLearnLanguage(_learnLanguage.value ?: DEFAULT_LANGUAGE_CODE)
            learnLanguageRepository.saveLearnLanguageState(_learnLanguage.value ?: DEFAULT_LANGUAGE_CODE)
            restartApp()
        }
    }







    private fun accountPhotosBitmapSave(context: Context, restartApp: () -> Unit) {
        launchCatching {
            _selfieUri?.let {
                if (Build.VERSION.SDK_INT < 28) {
                    _bitmap.value =
                        MediaStore.Images.Media.getBitmap(context.contentResolver, it.value)
                    accountPhotosSave(restartApp)
                } else {
                    val source = ImageDecoder.createSource(context.contentResolver, it.value!!)
                    _bitmap.value = ImageDecoder.decodeBitmap(source)
                    accountPhotosSave(restartApp)
                }
            }
        }
    }
    private fun accountPhotosSave(restartAppProfile: () -> Unit) {
        launchCatching {
            _bitmap.value?.let { bitmapNew ->
                val kucukBitmap = kucukBitmapOlustur(bitmapNew!!, 300)
                val outputStream = ByteArrayOutputStream()
                kucukBitmap.compress(Bitmap.CompressFormat.PNG, 50, outputStream)
                val byteDizisi = outputStream.toByteArray()
                val randomUid = UUID.randomUUID().toString()

                storageService.savePhoto(byteDizisi, uid = randomUid)
                val link = storageService.getPhoto(randomUid)

                firestoreService.saveAccountGallery(
                    Gallery(
                        photo = link,
                        dateOfCreation = Timestamp.now()
                    )
                )

                when(_accountType.value) {
                    SelectType.FLIRT -> {
                        firestoreService.updateUserFlirtPhotos(country = _accountTypeCountry.value ?: "", who = accountService.currentUserId, newValue = link)
                        restartAppProfile()
                    }
                    SelectType.MARRIAGE -> {
                        firestoreService.updateUserMarriagePhotos(country = _accountTypeCountry.value ?: "", who = accountService.currentUserId, newValue = link)
                        restartAppProfile()
                    }
                    SelectType.LANGUAGE_PRACTICE -> {
                        firestoreService.updateUserLanguagePracticePhotos(motherTongue = _accountTypeMotherTongue.value ?: "", who = accountService.currentUserId, newValue = link)
                        restartAppProfile()
                    }
                    SelectType.HOTEL -> {
                        firestoreService.updateHotelPhotos(country = _accountTypeCountry.value ?: "", who = accountService.currentUserId, newValue = link)
                        restartAppProfile()
                    }
                    SelectType.RESTAURANT -> {
                        firestoreService.updateRestaurantPhotos(country = _accountTypeCountry.value ?: "", who = accountService.currentUserId, newValue = link)
                        restartAppProfile()
                    }
                    SelectType.CAFE -> {
                        firestoreService.updateCafePhotos(country = _accountTypeCountry.value ?: "", who = accountService.currentUserId, newValue = link)
                        restartAppProfile()
                    }
                    SelectType.NOPE -> { restartAppProfile() }
                }


                /**                firestoreService.saveUserPhotos(
                com.kapirti.social_chat_food_video.model.UserPhotos(
                photo = link,
                date = Timestamp.now()
                )
                )*/
            }
        }
    }


    private fun flirtProfilePhotoSave(context: Context, restartAppProfile: () -> Unit) {
        launchCatching {
            _selfieUri?.let {
                if (Build.VERSION.SDK_INT < 28) {
                    _bitmap.value =
                        MediaStore.Images.Media.getBitmap(context.contentResolver, it.value)
                    flirtPhoto(restartAppProfile)
                } else {
                    val source = ImageDecoder.createSource(context.contentResolver, it.value!!)
                    _bitmap.value = ImageDecoder.decodeBitmap(source)
                    flirtPhoto(restartAppProfile)
                }
            }
        }
    }
    private fun flirtPhoto(restartAppProfile: () -> Unit) {
        launchCatching {
            _bitmap.value?.let { bitmapNew ->
                val kucukBitmap = kucukBitmapOlustur(bitmapNew!!, 300)
                val outputStream = ByteArrayOutputStream()
                kucukBitmap.compress(Bitmap.CompressFormat.PNG, 50, outputStream)
                val byteDizisi = outputStream.toByteArray()
                val randomUid = UUID.randomUUID().toString()

                storageService.savePhoto(byteDizisi, uid = randomUid)
                val link = storageService.getPhoto(randomUid)
                firestoreService.updateFlirtProfilePhoto(country = _country.value ?: DEFAULT_COUNTRY_CODE, photo = link)
                restartAppProfile()
            }
        }
    }
    private fun marriageProfilePhotoSave(context: Context, restartAppProfile: () -> Unit) {
        launchCatching {
            _selfieUri?.let {
                if (Build.VERSION.SDK_INT < 28) {
                    _bitmap.value =
                        MediaStore.Images.Media.getBitmap(context.contentResolver, it.value)
                    marriagePhoto(restartAppProfile)
                } else {
                    val source = ImageDecoder.createSource(context.contentResolver, it.value!!)
                    _bitmap.value = ImageDecoder.decodeBitmap(source)
                    marriagePhoto(restartAppProfile)
                }
            }
        }
    }
    private fun marriagePhoto(restartAppProfile: () -> Unit) {
        launchCatching {
            _bitmap.value?.let { bitmapNew ->
                val kucukBitmap = kucukBitmapOlustur(bitmapNew!!, 300)
                val outputStream = ByteArrayOutputStream()
                kucukBitmap.compress(Bitmap.CompressFormat.PNG, 50, outputStream)
                val byteDizisi = outputStream.toByteArray()
                val randomUid = UUID.randomUUID().toString()

                storageService.savePhoto(byteDizisi, uid = randomUid)
                val link = storageService.getPhoto(randomUid)
                firestoreService.updateMarriageProfilePhoto(country = _country.value ?: DEFAULT_COUNTRY_CODE, photo = link)
                restartAppProfile()
            }
        }
    }



    /**
     *
     *
     *
     *

    private fun profilePhotoBitmapSave(context: Context, restartApp: () -> Unit) {
    launchCatching {
    _selfieUri?.let {
    if (Build.VERSION.SDK_INT < 28) {
    _bitmap.value =
    MediaStore.Images.Media.getBitmap(context.contentResolver, it.value)
    profilePhotoSave(restartApp)
    } else {
    val source = ImageDecoder.createSource(context.contentResolver, it.value!!)
    _bitmap.value = ImageDecoder.decodeBitmap(source)
    profilePhotoSave(restartApp)
    }
    }
    }
    }
    private fun profilePhotoSave(restartAppProfile: () -> Unit) {
    launchCatching {
    _bitmap.value?.let { bitmapNew ->
    val kucukBitmap = kucukBitmapOlustur(bitmapNew!!, 300)
    val outputStream = ByteArrayOutputStream()
    kucukBitmap.compress(Bitmap.CompressFormat.PNG, 50, outputStream)
    val byteDizisi = outputStream.toByteArray()
    val randomUid = UUID.randomUUID().toString()

    storageService.savePhoto(byteDizisi, uid = randomUid)
    val link = storageService.getPhoto(randomUid)
    firestoreService.updateUserProfilePhoto(link)
    restartAppProfile()
    }
    }
    }


     *
     *
     *
     *
     * private fun profilePhotoSave(restartApp: () -> Unit) {
    launchCatching {
    _bitmap.value?.let { bitmapNew ->
    val kucukBitmap = kucukBitmapOlustur(bitmapNew!!, 300)
    val outputStream = ByteArrayOutputStream()
    kucukBitmap.compress(Bitmap.CompressFormat.PNG, 50, outputStream)
    val byteDizisi = outputStream.toByteArray()
    val randomUid = UUID.randomUUID().toString()

    storageService.savePhoto(byteDizisi, uid = randomUid)
    val link = storageService.getPhoto(randomUid)
    firestoreService.saveUserPhotos(
    UserPhotos(
    photo = link,
    date = Timestamp.now()
    )
    )
    restartApp()
    }
    }
    }
     */
/**
    private fun saveDisplayName(restartApp: () -> Unit) {
        launchCatching {
            accountService.displayName(_displayName.value!!)
            firestoreService.updateUserDisplayName(newValue = _displayName.value!!)
            restartApp()
        }
    }


    private fun saveGender(restartApp: () -> Unit) {
        launchCatching {
            firestoreService.updateUserGender(newValue = _gender.value!!)
            restartApp()
        }
    }





*/


    fun onVideoResponse(uri: Uri?) {
        if(uri != null){
            _videoUri.value = uri
            _isNextEnabled.value = getIsNextEnabled()
        }
    }






    //SurveyQuestion.DISPLAY_NAME -> _displayName.value != null

            SurveyQuestion.TAKE_VIDEO -> _videoUri.value != null




*/


enum class SurveyQuestion {
    SELECT_ACCOUNT_TYPE,
    COUNTRY,
    USERNAME,
    GENDER,
    BIRTHDAY,
    FREE_TIME,
    DESCRIPTION,
    TAKE_SELFIE,
    MOTHER_TONGUE,
    LEARN_LANGUAGE,
    CITY_ADDRESS,
}

data class SurveyScreenData(
    val questionIndex: Int,
    val questionCount: Int,
    val shouldShowPreviousButton: Boolean,
    val shouldShowDoneButton: Boolean,
    val surveyQuestion: SurveyQuestion,
)


private fun kucukBitmapOlustur(kullanicininSectigiBitmap: Bitmap, maximumBoyut: Int) : Bitmap {
    var width = kullanicininSectigiBitmap.width
    var height = kullanicininSectigiBitmap.height

    val bitmapOrani : Double = width.toDouble() / height.toDouble()

    if (bitmapOrani > 1) {
        width = maximumBoyut
        val kisaltilmisHeight = width / bitmapOrani
        height = kisaltilmisHeight.toInt()
    } else {
        height = maximumBoyut
        val kisaltilmisWidth = height * bitmapOrani
        width = kisaltilmisWidth.toInt()
    }

    return Bitmap.createScaledBitmap(kullanicininSectigiBitmap,width,height,true)
}
/**
private fun saveMarriage(context: Context, restartApp: () -> Unit) {
    launchCatching {
        _selfieUri?.let {
            if (Build.VERSION.SDK_INT < 28) {
                _bitmap.value =
                    MediaStore.Images.Media.getBitmap(context.contentResolver, it.value)
                saveMarriageBody(restartApp)
            } else {
                val source = ImageDecoder.createSource(context.contentResolver, it.value!!)
                _bitmap.value = ImageDecoder.decodeBitmap(source)
                saveMarriageBody(restartApp)
            }
        }
    }
}
private fun saveMarriageBody(restartAppProfile: () -> Unit) {
    launchCatching {
        _bitmap.value?.let { bitmapNew ->
            val kucukBitmap = kucukBitmapOlustur(bitmapNew!!, 300)
            val outputStream = ByteArrayOutputStream()
            kucukBitmap.compress(Bitmap.CompressFormat.PNG, 50, outputStream)
            val byteDizisi = outputStream.toByteArray()
            val randomUid = UUID.randomUUID().toString()

            storageService.savePhoto(byteDizisi, uid = randomUid)
            val link = storageService.getPhoto(randomUid)
            val ttoken = messagingService.getToken()


            accountService.displayName(_displayName.value ?: "")
            firestoreService.updateAccountType(newValue = MARRIAGE)
            firestoreService.updateAccountTypeLearnLanguage(getCountryLanguage(_country.value ?: DEFAULT_COUNTRY_CODE))
            firestoreService.updateAccountTypeCountry(_country.value ?: DEFAULT_COUNTRY_CODE)
            firestoreService.saveUserMarriage(
                UserMarriage(
                    displayName = _displayName.value ?: "",
                    name = _name.value ?: "",
                    surname = _surname.value ?: "",
                    birthday = _birthday.value ?: "",
                    gender = _gender.value ?: "",
                    photo = link,
                    hobby = _freeTimeResponse,
                    description = _description.value ?: "",
                    country = _country.value ?: DEFAULT_COUNTRY_CODE,
                    online = true,
                    token = ttoken ?: "",
                    dateOfCreation = Timestamp.now()
                )
            )
            restartAppProfile()
        }
    }
}
*/
