package com.kapirti.social_chat_food_video.ui.presentation.edit

import android.content.Context
import android.content.ContextWrapper
import androidx.activity.compose.BackHandler
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.tween
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.hilt.navigation.compose.hiltViewModel
import com.kapirti.social_chat_food_video.common.composable.DialogCancelButton
import com.kapirti.social_chat_food_video.common.composable.DialogConfirmButton
import com.kapirti.social_chat_food_video.common.composable.PasswordField
import com.kapirti.social_chat_food_video.common.ext.fieldModifier
import com.kapirti.social_chat_food_video.R.string as AppText
import androidx.compose.foundation.layout.fillMaxSize
import com.kapirti.social_chat_food_video.common.composable.HomeBackground

private const val CONTENT_ANIMATION_DURATION = 300


@Composable
fun EditRoute(
    popUp: () -> Unit,
    restartAppUsers: () -> Unit,
    restartAppEdit: () -> Unit,
    restartAppProduct: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditViewModel = hiltViewModel()
) {
    val surveyScreenData = viewModel.surveyScreenData ?: return
    val uiState by viewModel.uiState

    BackHandler {
        if (!viewModel.onBackPressed()) {
            popUp()
        }
    }

    EditScreen(
        surveyScreenData = surveyScreenData,
        isNextEnabled = viewModel.isNextEnabled,
        onClosePressed = { popUp() },
        onPreviousPressed = { viewModel.onPreviousPressed() },
        onNextPressed = { viewModel.onNextPressed() },
        onDonePressed = { viewModel.onDonePressed(
            restartAppEdit = restartAppEdit,
            restartAppUsers = restartAppUsers,
            restartAppProduct = restartAppProduct
        ) },
        isDoneBtnWorking = uiState.isDoneBtnWorking,
    ) { paddingValues ->
        HomeBackground(modifier = Modifier.fillMaxSize())

        val modifier = Modifier.padding(paddingValues)

        AnimatedContent(
            targetState = surveyScreenData,
            transitionSpec = {
                val animationSpec: TweenSpec<IntOffset> = tween(CONTENT_ANIMATION_DURATION)

                val direction = getTransitionDirection(
                    initialIndex = initialState.questionIndex,
                    targetIndex = targetState.questionIndex,
                )

                slideIntoContainer(
                    towards = direction,
                    animationSpec = animationSpec,
                ) togetherWith slideOutOfContainer(
                    towards = direction,
                    animationSpec = animationSpec
                )
            },
            label = "surveyScreenDataAnimation"
        ) { targetState ->

            when (targetState.surveyQuestion) {
                SurveyQuestion.SELECT_ACCOUNT_TYPE -> {
                    SelectTypeQuestion(
                        selectedAnswer = viewModel.accountType,
                        onOptionSelected = viewModel::onAccountTypeChange,
                        modifier = modifier,
                    )
                }
                SurveyQuestion.COUNTRY -> CountryQuestion(
                    selectedAnswer = viewModel.country,
                    onOptionSelected = viewModel::onCountryResponse,
                    popUp = popUp,
                    modifier = modifier
                )
                SurveyQuestion.USERNAME -> {
                    UsernameQuestion(
                        username = viewModel.username ?: "",
                        onUsernameChange = viewModel::onUsernameChange,
                        modifier = modifier,
                    )
                }
                SurveyQuestion.GENDER -> GenderQuestion(
                    selectedAnswer = viewModel.gender,
                    onOptionSelected = viewModel::onGenderChange,
                    modifier = modifier,
                )
                SurveyQuestion.BIRTHDAY -> BirthdayQuestion(
                    age = viewModel.birthday ?: "",
                    onAgeChange = viewModel::onBirthdayChange,
                    modifier = modifier
                )
                SurveyQuestion.FREE_TIME -> {
                    FreeTimeQuestion(
                        selectedAnswers = viewModel.freeTimeResponse,
                        onOptionSelected = viewModel::onFreeTimeResponse,
                        modifier = modifier,
                    )
                }
                SurveyQuestion.DESCRIPTION -> {
                    DescriptionQuestion(
                        description = viewModel.description ?: "",
                        onDescriptionChange = viewModel::onDescriptionChange,
                        modifier = modifier
                    )
                }
                SurveyQuestion.MOTHER_TONGUE -> MotherTongueQuestion(
                    selectedAnswer = viewModel.motherTongue,
                    onOptionSelected = viewModel::onMotherTongueChange,
                    popUp = popUp,
                    modifier = modifier,
                )

                SurveyQuestion.LEARN_LANGUAGE -> LearnLanguageQuestion(
                    selectedAnswer = viewModel.learnLanguage,
                    onOptionSelected = viewModel::onLearnLanguageChange,
                    popUp = popUp,
                    modifier = modifier,
                )
                SurveyQuestion.TAKE_SELFIE -> TakeSelfieQuestion(
                    imageUri = viewModel.selfieUri,
                    onPhotoTaken = viewModel::onSelfieResponse,
                    modifier = modifier,
                )
                SurveyQuestion.CITY_ADDRESS -> CityAddressQuestion(
                    city = viewModel.birthday ?: "",
                    onCityChange = viewModel::onBirthdayChange,
                    address = viewModel.gender ?: "",
                    onAddressChange = viewModel::onGenderChange,
                    modifier = modifier
                )
            }
        }
    }
}

private fun getTransitionDirection(
    initialIndex: Int,
    targetIndex: Int
): AnimatedContentTransitionScope.SlideDirection {
    return if (targetIndex > initialIndex) {
        AnimatedContentTransitionScope.SlideDirection.Left
    } else {
        AnimatedContentTransitionScope.SlideDirection.Right
    }
}

private tailrec fun Context.findActivity(): AppCompatActivity =
    when (this) {
        is AppCompatActivity -> this
        is ContextWrapper -> this.baseContext.findActivity()
        else -> throw IllegalArgumentException("Could not find activity!")
    }

