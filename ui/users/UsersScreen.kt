package com.kapirti.social_chat_food_video.ui.presentation.home.users

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.kapirti.social_chat_food_video.common.composable.NotificationPermissionCard
import com.kapirti.social_chat_food_video.model.UserFlirt
import com.kapirti.social_chat_food_video.model.UserLP
import com.kapirti.social_chat_food_video.model.UserMarriage

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun FlirtContent(users: List<UserFlirt>, onItemClicked: (UserFlirt) -> Unit, modifier: Modifier = Modifier) {
    @SuppressLint("InlinedApi") // Granted at install time on API <33.
    val notificationPermissionState = rememberPermissionState(
        android.Manifest.permission.POST_NOTIFICATIONS,
    )
    LazyColumn(
        modifier = modifier//.LazyColumnPadding(),
    ){
        if (!notificationPermissionState.status.isGranted) {
            item {
                NotificationPermissionCard(
                    shouldShowRationale = notificationPermissionState.status.shouldShowRationale,
                    onGrantClick = {
                        notificationPermissionState.launchPermissionRequest()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                )
            }
        }
        items(users){archive ->
            UserItem(
                photo = archive.photo,
                nameSurname = archive.username,
                description = archive.description,
                online = archive.online,
                lastSeen = archive.lastSeen,
                navigateToAccountDetail = { onItemClicked(archive) }
            )
        }
    }
}


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MarriageContent(users: List<UserMarriage>, onItemClicked: (UserMarriage) -> Unit, modifier: Modifier = Modifier) {
    @SuppressLint("InlinedApi") // Granted at install time on API <33.
    val notificationPermissionState = rememberPermissionState(
        android.Manifest.permission.POST_NOTIFICATIONS,
    )
    LazyColumn(
        modifier = modifier//.LazyColumnPadding(),
    ){
        if (!notificationPermissionState.status.isGranted) {
            item {
                NotificationPermissionCard(
                    shouldShowRationale = notificationPermissionState.status.shouldShowRationale,
                    onGrantClick = {
                        notificationPermissionState.launchPermissionRequest()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                )
            }
        }
        items(users){archive ->
            UserItem(
                photo = archive.photo,
                nameSurname = archive.username,
                description = archive.description,
                online = archive.online,
                lastSeen = archive.lastSeen,
                navigateToAccountDetail = { onItemClicked(archive) }
            )
        }
    }
}


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LanguageContent(users: List<UserLP>, onItemClicked: (UserLP) -> Unit, modifier: Modifier = Modifier) {
    @SuppressLint("InlinedApi") // Granted at install time on API <33.
    val notificationPermissionState = rememberPermissionState(
        android.Manifest.permission.POST_NOTIFICATIONS,
    )

    LazyColumn(
        modifier = modifier//.LazyColumnPadding(),
    ){
        if (!notificationPermissionState.status.isGranted) {
            item {
                NotificationPermissionCard(
                    shouldShowRationale = notificationPermissionState.status.shouldShowRationale,
                    onGrantClick = {
                        notificationPermissionState.launchPermissionRequest()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                )
            }
        }
        items(users){archive ->
            UserItem(
                photo = archive.photo,
                nameSurname = archive.username,
                description = archive.description,
                online = archive.online,
                lastSeen = archive.lastSeen,
                navigateToAccountDetail = { onItemClicked(archive) }
            )
        }
    }
}















/**








/**
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.kapirti.ira.R
import com.kapirti.ira.core.datastore.ChatIdRepository
import com.kapirti.ira.core.viewmodel.IncludeChatViewModel
import com.kapirti.ira.model.Chat

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ChatsScreen(
chats: List<Chat>,
contentPadding: PaddingValues,
onChatClick: (Chat) -> Unit,
modifier: Modifier = Modifier,
) {
@SuppressLint("InlinedApi") // Granted at install time on API <33.
val notificationPermissionState = rememberPermissionState(
android.Manifest.permission.POST_NOTIFICATIONS,
)
LazyColumn(
modifier = modifier,
contentPadding = contentPadding,
) {
if (!notificationPermissionState.status.isGranted) {
item {
NotificationPermissionCard(
shouldShowRationale = notificationPermissionState.status.shouldShowRationale,
onGrantClick = {
notificationPermissionState.launchPermissionRequest()
},
modifier = Modifier
.fillMaxWidth()
.padding(16.dp),
)
}
}
items(items = chats) { chat ->
ChatRow(
chat = chat,
onClick = { onChatClick(chat) }
)
}
}
}


@Composable
private fun NotificationPermissionCard(
shouldShowRationale: Boolean,
onGrantClick: () -> Unit,
modifier: Modifier = Modifier,
) {
Card(
modifier = modifier,
) {
Text(
text = stringResource(R.string.permission_message),
modifier = Modifier.padding(16.dp),
)
if (shouldShowRationale) {
Text(
text = stringResource(R.string.permission_rationale),
modifier = Modifier.padding(horizontal = 16.dp),
)
}
Box(
modifier = Modifier
.fillMaxWidth()
.padding(16.dp),
contentAlignment = Alignment.TopEnd,
) {
Button(onClick = onGrantClick) {
Text(text = stringResource(R.string.permission_grant))
}
}
}
}
*/

/**

floatingActionButton = {
Button(onClick = chatsToArchive){
Text(stringResource(AppText.archive))
}
},


}
*/





/**
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import com.kapirti.social_chat_food_video.common.composable.BasicDivider
import com.kapirti.social_chat_food_video.common.composable.LuccaSurface
import com.kapirti.social_chat_food_video.common.ext.search.SearchBar
import androidx.compose.runtime.Stable
import com.kapirti.social_chat_food_video.common.composable.AdsMediumRectangleToolbar
import com.kapirti.social_chat_food_video.common.ext.search.NoResults
import com.kapirti.social_chat_food_video.common.ext.search.SearchDisplay
import com.kapirti.social_chat_food_video.common.ext.search.SearchRepo
import com.kapirti.social_chat_food_video.core.constants.ConsAds
import com.kapirti.social_chat_food_video.model.UserFlirt


@Composable
internal fun UsersScreen(
popUp: () -> Unit,
items: List<UserFlirt>,
onItemClick: (UserFlirt) -> Unit,
showInterstitialAd: () -> Unit,
modifier: Modifier = Modifier,
state: SearchState = rememberSearchState()
) {
LuccaSurface(modifier = modifier.fillMaxSize()) {
Column {
SearchBar(
query = state.query,
onQueryChange = { state.query = it },
searchFocused = state.focused,
onSearchFocusChange = { state.focused = it },
onClearQuery = {
if(state.query.text.isNotEmpty()){
state.query = TextFieldValue("")
} else { popUp() }
},
searching = state.searching
)
BasicDivider()

LaunchedEffect(state.query.text) {
state.searching = true
state.searchResults = SearchRepo.searchUserFlirt(state.query.text, items = items)
state.searching = false
}
when (state.searchDisplay) {
SearchDisplay.Categories -> { UsersFlirtContent(items, onItemClick, showInterstitialAd) }
SearchDisplay.Suggestions -> { UsersFlirtContent(items, onItemClick, showInterstitialAd) }
SearchDisplay.Results -> UsersFlirtContent(state.searchResults, onItemClick, showInterstitialAd)
SearchDisplay.NoResults -> NoResults(state.query.text)
}
}
}
}

@Composable
private fun UsersFlirtContent(
items: List<UserFlirt>,
onItemClick: (UserFlirt) -> Unit,
showInterstitialAd: () -> Unit,
modifier: Modifier = Modifier,
) {
val adFrequency = 3
val scrollState = rememberLazyListState()
var showAd by remember { mutableStateOf(false) }

LaunchedEffect(key1 = scrollState.firstVisibleItemIndex) {
if (scrollState.firstVisibleItemIndex > 0 && scrollState.firstVisibleItemIndex % 3 == 0 && !showAd) {
showAd = true
showInterstitialAd()
}
}

Column(
modifier = modifier.fillMaxSize()
) {
LazyColumn(
state = scrollState,
modifier = Modifier.fillMaxSize()
) {
val totalItems = items.size + (items.size / adFrequency)

items(totalItems) { index ->
if ((index + 1) % (adFrequency + 1) == 0) {
AdsMediumRectangleToolbar(ConsAds.ADS_USERS_FLIRT_BANNER_ID)
} else {
val itemIndex = index - (index / (adFrequency + 1))
UserFlirtItem(
userFlirt = items[itemIndex],
onItemClick = {
onItemClick(items[itemIndex])
}
)
}
}
}
}
}


 */
*/
