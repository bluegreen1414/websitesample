package com.kapirti.social_chat_food_video.ui.presentation.register

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.kapirti.social_chat_food_video.R.string as AppText
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAddAlt1
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kapirti.social_chat_food_video.common.composable.AdsBannerToolbar
import com.kapirti.social_chat_food_video.common.composable.BasicButton
import com.kapirti.social_chat_food_video.common.composable.BasicTextButton
import com.kapirti.social_chat_food_video.common.composable.EmailField
import com.kapirti.social_chat_food_video.common.composable.HeaderText
import com.kapirti.social_chat_food_video.common.composable.HomeBackground
import com.kapirti.social_chat_food_video.common.composable.HyperlinkText
import com.kapirti.social_chat_food_video.common.composable.PasswordField
import com.kapirti.social_chat_food_video.common.ext.basicButton
import com.kapirti.social_chat_food_video.common.ext.fieldModifier
import com.kapirti.social_chat_food_video.common.ext.smallSpacer
import com.kapirti.social_chat_food_video.common.ext.textButton
import com.kapirti.social_chat_food_video.core.constants.Cons.SPLASH_TIMEOUT
import com.kapirti.social_chat_food_video.core.constants.ConsAds.ADS_REGISTER_BANNER_ID
import kotlinx.coroutines.delay

@Composable
fun RegisterScreen(
    navigateAndPopUpRegisterToEdit: () -> Unit,
    navigateAndPopUpRegisterToLogin: () -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState
    val fieldModifier = Modifier.fieldModifier()
    val email_error = stringResource(AppText.email_error)
    val password_error = stringResource(AppText.password_error)

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = { AdsBannerToolbar(ADS_REGISTER_BANNER_ID) }
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
                HeaderText(text = AppText.register)
                Spacer(modifier = Modifier.width(5.dp))
                Icon(
                    imageVector = Icons.Default.PersonAddAlt1,
                    contentDescription = stringResource(AppText.register),
                )
            }

            EmailField(
                value = uiState.email,
                onNewValue = viewModel::onEmailChange,
                modifier = fieldModifier,
                isError = uiState.isErrorEmail
            )
            PasswordField(
                value = uiState.password,
                onNewValue = viewModel::onPasswordChange,
                modifier = fieldModifier,
                isError = uiState.isErrorPassword
            )

            Spacer(modifier = Modifier.smallSpacer())

            HyperlinkText(
                fullText = "By clicking Register, you are accepting the Terms of use and Privacy Policy.",
                linkText = listOf("Terms of use", "Privacy Policy"),
                hyperlink = listOf(
                    "https://kapirti.com/app/quickchat/termsandconditions.html",
                    "https://kapirti.com/app/quickchat/privacypolicy.html"
                ),
                fontSize = MaterialTheme.typography.bodyLarge.fontSize
            )

            BasicButton(text = AppText.register, Modifier.basicButton(), uiState.button) {
                viewModel.onRegisterClick(
                    snackbarHostState = snackbarHostState,
                    navigateAndPopUpRegisterToEdit = navigateAndPopUpRegisterToEdit,
                    email_error = email_error,
                    password_error = password_error,
                    navigateAndPopUpRegisterToLogin = navigateAndPopUpRegisterToLogin,
                )
            }
            Spacer(modifier = Modifier.smallSpacer())


            BasicTextButton(AppText.already_have_an_account, Modifier.textButton()) {
                navigateAndPopUpRegisterToLogin()
            }
        }
    }
}
