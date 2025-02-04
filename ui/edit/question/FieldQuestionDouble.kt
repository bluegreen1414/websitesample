/*
 * Copyright (C) 2024 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kapirti.social_chat_food_video.ui.presentation.edit.question

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kapirti.social_chat_food_video.common.composable.BasicField
import com.kapirti.social_chat_food_video.common.ext.fieldModifier
import com.kapirti.social_chat_food_video.ui.presentation.edit.QuestionWrapper

@Composable
fun FieldQuestionDouble(
    @StringRes titleResourceId: Int,
    @StringRes directionsResourceId: Int,
    @StringRes textFirst: Int,
    @StringRes textSecond: Int,
    valueFirst: String,
    valueSecond: String,
    onFirstChange: (String) -> Unit,
    onSecondChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    QuestionWrapper(
        modifier = modifier,
        titleResourceId = titleResourceId,
        directionsResourceId = directionsResourceId,
    ) {
        val fieldModifier = Modifier.fieldModifier()
        BasicField(textFirst, valueFirst, onFirstChange, fieldModifier)
        BasicField(textSecond, valueSecond, onSecondChange, fieldModifier)
    }
}
