package com.kapirti.social_chat_food_video.ui.presentation.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.kapirti.social_chat_food_video.R.string as AppText
import com.kapirti.social_chat_food_video.R.drawable as AppIcon
import androidx.compose.material3.AlertDialog
import androidx.compose.ui.tooling.preview.Preview
import com.kapirti.social_chat_food_video.common.composable.BasicButton
import com.kapirti.social_chat_food_video.common.composable.DialogCancelButton
import com.kapirti.social_chat_food_video.common.composable.DialogConfirmButton
import com.kapirti.social_chat_food_video.common.ext.basicButton
import com.kapirti.social_chat_food_video.core.constants.Cons.SPLASH_TIMEOUT
import kotlinx.coroutines.delay

@Composable
internal fun SplashScreen(
    navigateAndPopUpSplashToWelcome: () -> Unit,
    navigateAndPopUpSplashToLogin: () -> Unit,
    navigateAndPopUpSplashToHome: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SplashViewModel = hiltViewModel()
) {
    LogosuzPhoto(modifier = Modifier.fillMaxSize())

    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (viewModel.showError.value) {
            Text(text = stringResource(AppText.generic_error))

            BasicButton(AppText.try_again, Modifier.basicButton(), true) {
                viewModel.onAppStart(navigateAndPopUpSplashToHome = navigateAndPopUpSplashToHome,
                    navigateAndPopUpSplashToWelcome = navigateAndPopUpSplashToWelcome,
                    navigateAndPopUpSplashToLogin = navigateAndPopUpSplashToLogin)
            }
        }
    }
    if (viewModel.showReviewDialog.value) {
        AlertDialog(
            title = { Text(stringResource(AppText.review_title)) },
            text = { Text(stringResource(AppText.review_description)) },
            dismissButton = { DialogCancelButton(AppText.cancel) { viewModel.showDialogFalse(
                navigateAndPopUpSplashToHome) } },
            confirmButton = {
                DialogConfirmButton(AppText.ok) {
                    viewModel.showDialogTrue(navigateAndPopUpSplashToHome)
                }
            },
            onDismissRequest = { viewModel.showDialogFalse(navigateAndPopUpSplashToHome) }
        )
    }
    /** if (viewModel.showPayScreen.value) {
    PayScreen(
    onPayClicked = { chooseSubscription.checkSubscriptionStatus("monthly") },
    onCloseClicked = { viewModel.showPayScreenFalse(
    navigateAndPopUpSplashToHome = navigateAndPopUpSplashToHome,
    navigateAndPopUpSplashToWelcome = navigateAndPopUpSplashToWelcome,
    navigateAndPopUpSplashToLogin = navigateAndPopUpSplashToLogin
    ) }
    )
    }*/


    LaunchedEffect(true) {
        delay(SPLASH_TIMEOUT)
        viewModel.onAppStart(navigateAndPopUpSplashToHome = navigateAndPopUpSplashToHome,
            navigateAndPopUpSplashToWelcome = navigateAndPopUpSplashToWelcome,
            navigateAndPopUpSplashToLogin = navigateAndPopUpSplashToLogin)
    }
}

@Preview(showBackground = true)
@Composable
private fun LogosuzPhoto(
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        Image(
            painter = painterResource(id = AppIcon.logosuz),
            contentDescription = stringResource(id = AppText.cd_logo,),
            modifier = Modifier.fillMaxSize(),
        )
    }
}

