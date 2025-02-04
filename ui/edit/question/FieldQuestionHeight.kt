package com.kapirti.social_chat_food_video.ui.presentation.edit.question

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kapirti.social_chat_food_video.common.composable.DescriptionField
import com.kapirti.social_chat_food_video.common.ext.fieldModifier
import com.kapirti.social_chat_food_video.ui.presentation.edit.QuestionWrapper

@Composable
fun FieldQuestionHeight(
    @StringRes titleResourceId: Int,
    @StringRes directionsResourceId: Int,
    @StringRes text: Int,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    QuestionWrapper(
        modifier = modifier,
        titleResourceId = titleResourceId,
        directionsResourceId = directionsResourceId,
    ) {
        val fieldModifier = Modifier.fieldModifier()
        DescriptionField(text, value, onValueChange, fieldModifier)
    }
}
