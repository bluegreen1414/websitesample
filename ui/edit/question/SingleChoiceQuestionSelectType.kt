package com.kapirti.social_chat_food_video.ui.presentation.edit.question

import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.kapirti.social_chat_food_video.common.composable.KapirtiIcons
import com.kapirti.social_chat_food_video.core.constants.SelectType.FLIRT
import com.kapirti.social_chat_food_video.core.constants.SelectType.HOTEL
import com.kapirti.social_chat_food_video.core.constants.SelectType.MARRIAGE
import com.kapirti.social_chat_food_video.core.constants.SelectType.RESTAURANT
import com.kapirti.social_chat_food_video.ui.presentation.edit.QuestionWrapper
import com.kapirti.social_chat_food_video.R.string as AppText
import androidx.compose.ui.res.stringResource
import com.kapirti.social_chat_food_video.core.constants.SelectType.CAFE
import com.kapirti.social_chat_food_video.core.constants.SelectType.LANGUAGE_PRACTICE

@Composable
fun SingleChoiceQuestionSelectType(
    @StringRes titleResourceId: Int,
    @StringRes directionsResourceId: Int,
    possibleAnswers: List<String>,
    selectedAnswer: String?,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    QuestionWrapper(
        modifier = modifier,
        titleResourceId = titleResourceId,
        directionsResourceId = directionsResourceId,
    ) {
        possibleAnswers.forEach {
            val selected = it == selectedAnswer
            CheckboxRow(
                modifier = Modifier.padding(vertical = 8.dp),
                text = it,
                selected = selected,
                onOptionSelected = { onOptionSelected(it) }
            )
        }
    }
}

@Composable
private fun CheckboxRow(
    text: String,
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
            Icon(iconForAnswer(text), text, Modifier.size(48.dp).padding(end = 16.dp))
            Text(stringResource(textForAccountType(text)), Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
            Box(Modifier.padding(8.dp)) {
                Checkbox(selected, onCheckedChange = null)
            }
        }
    }
}

private fun iconForAnswer(answer: String): ImageVector{
    return when (answer) {
        MARRIAGE -> KapirtiIcons.Favorite
        FLIRT -> KapirtiIcons.Fire
        LANGUAGE_PRACTICE -> KapirtiIcons.Language
        HOTEL -> KapirtiIcons.Hotel
        RESTAURANT -> KapirtiIcons.Restaurant
        CAFE -> KapirtiIcons.Cafe
        else -> KapirtiIcons.Fire
    }
}

fun textForAccountType(answer: String): Int{
    return when (answer) {
        MARRIAGE -> AppText.marriage_title
        FLIRT -> AppText.flirt_title
        LANGUAGE_PRACTICE -> AppText.language_practice_title
        HOTEL -> AppText.hotel
        RESTAURANT -> AppText.restaurant
        CAFE -> AppText.cafe
        else -> AppText.flirt_title
    }
}
