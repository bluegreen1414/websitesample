package com.kapirti.social_chat_food_video.ui.presentation.edit.question

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import com.kapirti.social_chat_food_video.common.composable.BasicDivider
import com.kapirti.social_chat_food_video.common.composable.LuccaSurface
import com.kapirti.social_chat_food_video.common.ext.search.NoResults
import com.kapirti.social_chat_food_video.common.ext.search.SearchBar
import com.kapirti.social_chat_food_video.common.ext.search.SearchDisplay
import com.kapirti.social_chat_food_video.common.ext.search.SearchRepo
import com.kapirti.social_chat_food_video.ui.presentation.edit.QuestionWrapper
import com.kapirti.social_chat_food_video.R.drawable as AppIcon
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import com.kapirti.social_chat_food_video.R.string as AppText
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import com.kapirti.social_chat_food_video.common.composable.HomeBackground
import com.kapirti.social_chat_food_video.core.constants.languageText


@Composable
internal fun SingleChoiceQuestionLanguage(
    popUp: () -> Unit,
    @StringRes titleResourceId: Int,
    @StringRes directionsResourceId: Int,
    possibleAnswers: List<String>,
    selectedAnswer: String?,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    state: SearchState = rememberSearchState()
) {
    HomeBackground(modifier = Modifier.fillMaxSize())
    LuccaSurface(modifier = modifier.fillMaxSize()) {
        Column {
            SearchBar(
                query = state.query,
                onQueryChange = { state.query = it },
                searchFocused = state.focused,
                onSearchFocusChange = { state.focused = it },
                onClearQuery = {
                    if (state.query.text.isNotEmpty()) {
                        state.query = TextFieldValue("")
                    } else {
                        popUp()
                    }
                },
                searching = state.searching
            )
            BasicDivider()

            LaunchedEffect(state.query.text) {
                state.searching = true
                state.searchResults =
                    SearchRepo.searchLanguage(state.query.text, items = possibleAnswers)
                state.searching = false
            }
            when (state.searchDisplay) {
                SearchDisplay.Categories -> {
                    SingleChoiceQuestionContent(
                        titleResourceId = titleResourceId,
                        directionsResourceId = directionsResourceId,
                        possibleAnswers = possibleAnswers,
                        onOptionSelected = onOptionSelected,
                        selectedAnswer = selectedAnswer
                    )
                }

                SearchDisplay.Suggestions -> {
                    SingleChoiceQuestionContent(
                        titleResourceId = titleResourceId,
                        directionsResourceId = directionsResourceId,
                        possibleAnswers = possibleAnswers,
                        onOptionSelected = onOptionSelected,
                        selectedAnswer = selectedAnswer
                    )
                }

                SearchDisplay.Results ->
                    SingleChoiceQuestionContent(
                        titleResourceId = titleResourceId,
                        directionsResourceId = directionsResourceId,
                        possibleAnswers = state.searchResults,
                        onOptionSelected = onOptionSelected,
                        selectedAnswer = selectedAnswer
                    )

                SearchDisplay.NoResults -> NoResults(state.query.text)
            }
        }
    }
}


@Composable
private fun SingleChoiceQuestionContent(
    @StringRes titleResourceId: Int,
    @StringRes directionsResourceId: Int,
    possibleAnswers: List<String>,
    selectedAnswer: String?,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    QuestionWrapper(
        titleResourceId = titleResourceId,
        directionsResourceId = directionsResourceId,
        modifier = modifier.selectableGroup(),
    ) {
        possibleAnswers.forEach {
            val selected = it == selectedAnswer
            RadioButtonWithImageRow(
                modifier = Modifier.padding(vertical = 8.dp),
                text = it,
                imageResourceId = AppIcon.logo,
                selected = selected,
                onOptionSelected = { onOptionSelected(it) }
            )
        }
    }
}


@Composable
private fun rememberSearchState(
    query: TextFieldValue = TextFieldValue(""),
    focused: Boolean = false,
    searching: Boolean = false,
    searchResults: List<String> = emptyList()
): SearchState {
    return remember {
        SearchState(
            query = query,
            focused = focused,
            searching = searching,
            searchResults = searchResults
        )
    }
}

@Stable
class SearchState(
    query: TextFieldValue,
    focused: Boolean,
    searching: Boolean,
    searchResults: List<String>
) {
    var query by mutableStateOf(query)
    var focused by mutableStateOf(focused)
    var searching by mutableStateOf(searching)
    var searchResults by mutableStateOf(searchResults)
    val searchDisplay: SearchDisplay
        get() = when {
            !focused && query.text.isEmpty() -> SearchDisplay.Categories
            focused && query.text.isEmpty() -> SearchDisplay.Suggestions
            searchResults.isEmpty() -> SearchDisplay.NoResults
            else -> SearchDisplay.Results
        }
}


@Composable
private fun RadioButtonWithImageRow(
    text: String,
    @DrawableRes imageResourceId: Int,
    selected: Boolean,
    onOptionSelected: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = if (selected) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surface
        },
        border = BorderStroke(
            width = 1.dp,
            color = if (selected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.outline
            }
        ),
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .selectable(
                selected,
                onClick = onOptionSelected,
                role = Role.RadioButton
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = imageResourceId),
                contentDescription = stringResource(AppText.cd_icon),
                modifier = Modifier
                    .size(56.dp)
                    .clip(MaterialTheme.shapes.extraSmall)
                    .padding(start = 0.dp, end = 8.dp)
            )
            Spacer(Modifier.width(8.dp))

            Text(stringResource(languageText(text)), Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
            Box(Modifier.padding(8.dp)) {
                RadioButton(selected, onClick = null)
            }
        }
    }
}
