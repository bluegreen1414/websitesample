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
import com.kapirti.social_chat_food_video.R.string as AppText
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import com.kapirti.social_chat_food_video.common.composable.HomeBackground
import com.kapirti.social_chat_food_video.common.composable.NoSurfaceImage
import com.kapirti.social_chat_food_video.model.CountryFlag


@Composable
internal fun SingleChoiceQuestionCountry(
    popUp: () -> Unit,
    possibleAnswers: List<CountryFlag>,
    selectedAnswer: String?,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    state: SearchStateCountry = rememberSearchState()
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
                    SearchRepo.searchCountry(state.query.text, items = possibleAnswers)
                state.searching = false
            }
            when (state.searchDisplay) {
                SearchDisplay.Categories -> {
                    SingleChoiceQuestionContent(
                        possibleAnswers = possibleAnswers,
                        onOptionSelected = onOptionSelected,
                        selectedAnswer = selectedAnswer
                    )
                }

                SearchDisplay.Suggestions -> {
                    SingleChoiceQuestionContent(
                        possibleAnswers = possibleAnswers,
                        onOptionSelected = onOptionSelected,
                        selectedAnswer = selectedAnswer
                    )
                }

                SearchDisplay.Results ->
                    SingleChoiceQuestionContent(
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
    possibleAnswers: List<CountryFlag>,
    selectedAnswer: String?,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberLazyListState()

    QuestionWrapper(
        modifier = modifier,
        titleResourceId = AppText.country,
        directionsResourceId = AppText.country
    ) {
        possibleAnswers.forEach { userItem ->
            val selected = userItem.country == selectedAnswer
            CheckboxRow(
                modifier = Modifier.padding(vertical = 8.dp),
                countryFlag = userItem,
                selected = selected,
                onOptionSelected = { onOptionSelected(userItem.country) }
            )
        }
    }
}


@Composable
private fun rememberSearchState(
    query: TextFieldValue = TextFieldValue(""),
    focused: Boolean = false,
    searching: Boolean = false,
    searchResults: List<CountryFlag> = emptyList()
): SearchStateCountry {
    return remember {
        SearchStateCountry(
            query = query,
            focused = focused,
            searching = searching,
            searchResults = searchResults
        )
    }
}

@Stable
class SearchStateCountry(
    query: TextFieldValue,
    focused: Boolean,
    searching: Boolean,
    searchResults: List<CountryFlag>
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
private fun CheckboxRow(
    countryFlag: CountryFlag,
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
            .clickable(onClick = onOptionSelected)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            NoSurfaceImage(
                imageUrl = countryFlag.flag,
                contentDescription = countryFlag.country,
                modifier = Modifier
                    .size(60.dp)
                    .padding(end = 16.dp))
            Text(stringResource(countryFlag.text), Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
            Box(Modifier.padding(8.dp)) {
                Checkbox(selected, onCheckedChange = null)
            }
        }
    }
}





/**

import com.kapirti.social_chat_food_video.R.string as AppText
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import com.kapirti.social_chat_food_video.common.composable.NoSurfaceImage
import com.kapirti.social_chat_food_video.model.CountryFlag
import com.kapirti.social_chat_food_video.ui.presentation.edit.QuestionWrapper

@Composable
fun SingleChoiceQuestionCountry(
    possibleAnswers: List<CountryFlag>,
    selectedAnswer: String?,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    QuestionWrapper(
        modifier = modifier,
        titleResourceId = AppText.country,
        directionsResourceId = AppText.country
    ) {
        possibleAnswers.forEach {
            val selected = it.country == selectedAnswer
            CheckboxRow(
                modifier = Modifier.padding(vertical = 8.dp),
                countryFlag = it,
                selected = selected,
                onOptionSelected = { onOptionSelected(it.country) }
            )
        }
    }
}

@Composable
private fun CheckboxRow(
    countryFlag: CountryFlag,
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
            .clickable(onClick = onOptionSelected)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            NoSurfaceImage(
                imageUrl = countryFlag.flag,
                contentDescription = countryFlag.country,
                modifier = Modifier
                    .size(60.dp)
                    .padding(end = 16.dp))
            Text(stringResource(countryFlag.text), Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
            Box(Modifier.padding(8.dp)) {
                Checkbox(selected, onCheckedChange = null)
            }
        }
    }
}
*/
