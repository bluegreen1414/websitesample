package com.kapirti.social_chat_food_video.ui.presentation.edit

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kapirti.social_chat_food_video.R.string as AppText
import android.graphics.Bitmap
import com.kapirti.social_chat_food_video.model.LuccaRepo
import com.kapirti.social_chat_food_video.ui.presentation.edit.question.FieldQuestion
import com.kapirti.social_chat_food_video.ui.presentation.edit.question.FieldQuestionDouble
import com.kapirti.social_chat_food_video.ui.presentation.edit.question.FieldQuestionHeight
import com.kapirti.social_chat_food_video.ui.presentation.edit.question.MultipleChoiceQuestion
import com.kapirti.social_chat_food_video.ui.presentation.edit.question.PhotoQuestion
import com.kapirti.social_chat_food_video.ui.presentation.edit.question.SingleChoiceQuestionCountry
import com.kapirti.social_chat_food_video.ui.presentation.edit.question.SingleChoiceQuestionGender
import com.kapirti.social_chat_food_video.ui.presentation.edit.question.SingleChoiceQuestionLanguage
import com.kapirti.social_chat_food_video.ui.presentation.edit.question.SingleChoiceQuestionSelectType


@Composable
fun SelectTypeQuestion(
    selectedAnswer: String?,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    SingleChoiceQuestionSelectType(
        titleResourceId = AppText.select_type_question_title,
        directionsResourceId = AppText.select_type_question_description,
        possibleAnswers = LuccaRepo.getSelectType(),
        selectedAnswer = selectedAnswer,
        onOptionSelected = onOptionSelected,
        modifier = modifier,
    )
}

@Composable
fun UsernameQuestion(
    username: String,
    onUsernameChange: (String) -> Unit,
    modifier: Modifier = Modifier,
){
    FieldQuestion(
        titleResourceId = AppText.name_and_surname,
        directionsResourceId = AppText.name_and_surname,
        text = AppText.name_and_surname,
        value = username,
        onValueChange = onUsernameChange,
        modifier = modifier,
    )
}

@Composable
fun GenderQuestion(
    selectedAnswer: String?,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    SingleChoiceQuestionGender(
        possibleAnswers = LuccaRepo.getGender(),
        selectedAnswer = selectedAnswer,
        onOptionSelected = onOptionSelected,
        modifier = modifier,
    )
}

@Composable
fun BirthdayQuestion(
    age: String,
    onAgeChange: (String) -> Unit,
    modifier: Modifier = Modifier,
){
    FieldQuestion(
        titleResourceId = AppText.age,
        directionsResourceId = AppText.age,
        text = AppText.age,
        value = age,
        onValueChange = onAgeChange,
        modifier = modifier,
    )
}

/**
@Composable
fun BirthdayQuestion(
    birthday: String?,
    onBirthdayChange: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    /** DateQuestion(
    titleResourceId = AppText.birthday,
    directionsResourceId = AppText.select_date,
    birthday = birthday,
    onBirthdayChange = onBirthdayChange,
    modifier = modifier,
    )*/
}*/

@Composable
fun FreeTimeQuestion(
    selectedAnswers: List<String>,
    onOptionSelected: (selected: Boolean, answer: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    MultipleChoiceQuestion(
        titleResourceId = AppText.in_my_free_time,
        directionsResourceId = AppText.select_all,
        possibleAnswers = LuccaRepo.getHobbies(),
        selectedAnswers = selectedAnswers,
        onOptionSelected = onOptionSelected,
        modifier = modifier,
    )
}

@Composable
fun DescriptionQuestion(
    description: String,
    onDescriptionChange: (String) -> Unit,
    modifier: Modifier = Modifier,
){
    FieldQuestionHeight(
        titleResourceId = AppText.tell_us_about_you,
        directionsResourceId = AppText.tell_us_about_you,
        text = AppText.description,
        value = description,
        onValueChange = onDescriptionChange,
        modifier = modifier,
    )
}

@Composable
fun TakeSelfieQuestion(
    imageUri: Uri?,
    onPhotoTaken: (Uri) -> Unit,
    modifier: Modifier = Modifier,
) {
    PhotoQuestion(
        titleResourceId = AppText.selfie_skills,
        imageUri = imageUri,
        onPhotoTaken = onPhotoTaken,
        modifier = modifier,
    )
}

@Composable
fun MotherTongueQuestion(
    selectedAnswer: String?,
    onOptionSelected: (String) -> Unit,
    popUp: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SingleChoiceQuestionLanguage(
        titleResourceId = AppText.mother_tongue,
        directionsResourceId = AppText.select_one,
        possibleAnswers = LuccaRepo.getLanguages(),
        selectedAnswer = selectedAnswer,
        onOptionSelected = onOptionSelected,
        popUp = popUp,
        modifier = modifier,
    )
}

@Composable
fun LearnLanguageQuestion(
    selectedAnswer: String?,
    onOptionSelected: (String) -> Unit,
    popUp: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SingleChoiceQuestionLanguage(
        titleResourceId = AppText.learn_language,
        directionsResourceId = AppText.select_one,
        possibleAnswers = LuccaRepo.getLanguages(),
        selectedAnswer = selectedAnswer,
        onOptionSelected = onOptionSelected,
        popUp = popUp,
        modifier = modifier,
    )
}


@Composable
fun CountryQuestion(
    selectedAnswer: String?,
    onOptionSelected: (String) -> Unit,
    popUp: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SingleChoiceQuestionCountry(
        popUp = popUp,
        possibleAnswers = LuccaRepo.getCountries(),
        selectedAnswer = selectedAnswer,
        onOptionSelected = onOptionSelected,
        modifier = modifier,
    )
}

@Composable
fun CityAddressQuestion(
    city: String,
    address: String,
    onCityChange: (String) -> Unit,
    onAddressChange: (String) -> Unit,
    modifier: Modifier = Modifier,
){
    FieldQuestionDouble(
        titleResourceId = AppText.city_and_address,
        directionsResourceId = AppText.city_and_address,
        textFirst = AppText.city,
        textSecond = AppText.address,
        valueFirst = city,
        valueSecond = address,
        onFirstChange = onCityChange,
        onSecondChange = onAddressChange,
        modifier = modifier,
    )
}
