package com.kapirti.social_chat_food_video.ui.presentation.home.users

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kapirti.social_chat_food_video.common.composable.CraneTabBar
import com.kapirti.social_chat_food_video.common.composable.CraneTabsUsers
import com.kapirti.social_chat_food_video.core.viewmodel.IncludeAccountViewModel
import com.kapirti.social_chat_food_video.model.UserFlirt
import com.kapirti.social_chat_food_video.model.UserLP
import com.kapirti.social_chat_food_video.model.UserMarriage

enum class CraneScreen {
    Flirt, Marriage, Language
}

@Composable
internal fun HomeRoute(
    includeAccountViewModel: IncludeAccountViewModel,
    //onItemClicked: (User, String) -> Unit,
    onItemClicked: (String, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    CraneHomeContent(
        modifier = modifier,
        navigateUser = onItemClicked,
        includeAccountViewModel = includeAccountViewModel
    )
}

//    val myId = viewModel.currentUserId
/**
    HomeScreen(users.filterNot { it.id == myId }, onItemClicked = {
        onItemClicked.invoke(it, myId!!)
    })*/




@Composable
private fun CraneHomeContent(
    includeAccountViewModel: IncludeAccountViewModel,
    navigateUser: (String, String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: UsersViewModel = hiltViewModel()
) {
    val usersFlirt by viewModel.usersFlirt.collectAsStateWithLifecycle()
    val usersMarriage by viewModel.usersMarriage.collectAsStateWithLifecycle()
    val languageUsers by viewModel.usersLP.collectAsStateWithLifecycle()
    var tabSelected by remember { mutableStateOf(CraneScreen.Flirt) }
    val myId = viewModel.currentUserId

    Scaffold(
        modifier = modifier,
        topBar = { HomeTabBar(tabSelected, onTabSelected = { tabSelected = it }) },
    ) { innerPadding ->
        Column {
           /** StoryContent(
                mePhoto = mePhoto,
                stories = stories,
                onAddStoryClicked = { viewModel.onAddStoryClicked(navigateEdit) },
                onWatchStoryClicked = {
                    photoStock = it.contentUrl
                    activeId = it.contentUrl
                }
            )*/

            SearchContent(
                tabSelected,
                usersFlirt,//.filterNot { it.id == myId },
                usersMarriage,
                languageUsers,
                onItemFlirtClicked = {
                    includeAccountViewModel.addFlirt(it)
                    navigateUser(it.id, myId) },
                onItemMarriageClicked = {
                    includeAccountViewModel.addMarriage(it)
                    navigateUser(it.id, myId) },
                onItemLanguageClicked = {
                    includeAccountViewModel.addLanguage(it)
                    navigateUser(it.id, myId) },
                modifier = modifier.padding(innerPadding),
            )

        }
    }
}



@Composable
private fun HomeTabBar(
    tabSelected: CraneScreen,
    onTabSelected: (CraneScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    CraneTabBar(
        modifier = modifier,
    ) { tabBarModifier ->
        CraneTabsUsers(
            modifier = tabBarModifier,
            titles = CraneScreen.values().map { it.name },
            tabSelected = tabSelected,
            onTabSelected = { newTab -> onTabSelected(CraneScreen.values()[newTab.ordinal]) }
        )
    }
}


@Composable
private fun SearchContent(
    tabSelected: CraneScreen,
    flirtUsers: List<UserFlirt>,
    marriageUsers: List<UserMarriage>,
    languageUsers: List<UserLP>,
    onItemFlirtClicked: (UserFlirt) -> Unit,
    onItemMarriageClicked: (UserMarriage) -> Unit,
    onItemLanguageClicked: (UserLP) -> Unit,
    modifier: Modifier = Modifier
) {
    when (tabSelected) {
        CraneScreen.Flirt -> FlirtContent(flirtUsers, onItemFlirtClicked, modifier)
        CraneScreen.Marriage -> MarriageContent(marriageUsers, onItemMarriageClicked, modifier)
        CraneScreen.Language -> LanguageContent(languageUsers, onItemLanguageClicked, modifier)
    }
}
