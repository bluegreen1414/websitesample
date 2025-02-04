package com.kapirti.social_chat_food_video.ui.presentation.login

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.kapirti.social_chat_food_video.common.composable.AdsBannerToolbar
import com.kapirti.social_chat_food_video.core.constants.ConsAds.ADS_LOG_IN_BANNER_ID
import com.kapirti.social_chat_food_video.R.string as AppText
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kapirti.social_chat_food_video.common.composable.BasicButton
import com.kapirti.social_chat_food_video.common.composable.BasicTextButton
import com.kapirti.social_chat_food_video.common.composable.EmailField
import com.kapirti.social_chat_food_video.common.composable.HeaderText
import com.kapirti.social_chat_food_video.common.composable.HomeBackground
import com.kapirti.social_chat_food_video.common.composable.PasswordField
import com.kapirti.social_chat_food_video.common.ext.basicButton
import com.kapirti.social_chat_food_video.common.ext.fieldModifier
import com.kapirti.social_chat_food_video.common.ext.smallSpacer
import com.kapirti.social_chat_food_video.common.ext.textButton
import com.kapirti.social_chat_food_video.core.constants.Cons.SPLASH_TIMEOUT
import kotlinx.coroutines.delay

@Composable
fun LogInScreen(
    navigateAndPopUpLoginToHome: () -> Unit,
    navigateAndPopUpLoginToRegister: () -> Unit,
    navigateAndPopUpLoginToEdit: () -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    viewModel: LogInViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState
    val emailError = stringResource(id = AppText.email_error)
    val emptyPasswordError = stringResource(id = AppText.empty_password_error)
    val wrongPasswordError = stringResource(id = AppText.wrong_password_error)
    val recoveryEmailSent = stringResource(id = AppText.recovery_email_sent)

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = { AdsBannerToolbar(ADS_LOG_IN_BANNER_ID) }
    ) { innerPadding ->
        HomeBackground(modifier = Modifier.fillMaxSize())

        Column(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxWidth()
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                HeaderText(text = AppText.welcome_back)
                Spacer(modifier = Modifier.width(5.dp))
                Icon(imageVector = Icons.AutoMirrored.Filled.Login, contentDescription = stringResource(AppText.log_in))
            }

            EmailField(
                value = uiState.email,
                onNewValue = viewModel::onEmailChange,
                modifier = Modifier.fieldModifier(),
                isError = uiState.isErrorEmail
            )
            PasswordField(
                value = uiState.password,
                onNewValue = viewModel::onPasswordChange,
                modifier = Modifier.fieldModifier(),
                isError = uiState.isErrorPassword
            )

            BasicButton(text = AppText.log_in, Modifier.basicButton(), uiState.button) {
                viewModel.onLogInClick(
                    restartApp = navigateAndPopUpLoginToHome,
                    navigateAndPopUpLoginToRegister = navigateAndPopUpLoginToRegister,
                    navigateAndPopUpLoginToEdit = navigateAndPopUpLoginToEdit,
                    snackbarHostState = snackbarHostState,
                    emailError = emailError,
                    emptyPasswordError = emptyPasswordError,
                    wrongPasswordError = wrongPasswordError,
                )
            }

            BasicTextButton(AppText.forgotten_password, Modifier.textButton()) {
                viewModel.onForgotPasswordClick(
                    snackbarHostState = snackbarHostState,
                    emailError = emailError,
                    recoveryEmailSent = recoveryEmailSent,
                )
            }
            Spacer(modifier = Modifier.smallSpacer())

            BasicTextButton(AppText.create_new_account, Modifier.textButton()) {
                navigateAndPopUpLoginToRegister()
            }
        }
    }
}
