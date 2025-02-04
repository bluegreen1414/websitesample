package com.kapirti.social_chat_food_video.ui.presentation.home.users

import android.text.format.DateUtils
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.google.firebase.Timestamp
import com.kapirti.social_chat_food_video.common.composable.NoSurfaceImage
import com.kapirti.social_chat_food_video.common.ext.getRelativeTime
import com.kapirti.social_chat_food_video.ui.theme.GREEN450

@Composable
fun UserItem(
    photo: String,
    nameSurname: String,
    description: String,
    online: Boolean,
    lastSeen: Timestamp?,
    navigateToAccountDetail: () -> Unit,
  //  navigateToDetail: (Long) -> Unit,
   // toggleSelection: (Long) -> Unit,
    modifier: Modifier = Modifier,
    isOpened: Boolean = false,
    isSelected: Boolean = false,
) {
    Card(
        modifier = modifier
            .padding(horizontal = 4.dp, vertical = 4.dp)
            .semantics { selected = isSelected }
            .clip(CardDefaults.shape)
            .clickable { navigateToAccountDetail() }
          /**  .combinedClickable(
                onClick = { navigateToDetail(email.id) },
                onLongClick = { toggleSelection(email.id) }
            )*/
            .clip(CardDefaults.shape),
        colors = CardDefaults.cardColors(
            containerColor =
            if(online) GREEN450
            else MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                val clickModifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { }//toggleSelection(email.id) }
                AnimatedContent(targetState = isSelected, label = "avatar") { selected ->
                    if (selected) {
                        SelectedProfileImage(clickModifier)
                    } else {
                        ReplyProfileImage(
                            photo,
                            nameSurname,
                            clickModifier
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = nameSurname,
                        style = MaterialTheme.typography.labelMedium
                    )
                    if(!online) {
                        Text(
                            text = lastSeen?.let { "Last seen :${getRelativeTime(lastSeen.toDate().time)}" }
                                ?: "Last seen :",
                            style = MaterialTheme.typography.labelMedium,
                        )
                    }
                }
                IconButton(
                    onClick = { /*TODO*/ },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                ) {
                    Icon(
                        imageVector = Icons.Default.StarBorder,
                        contentDescription = "Favorite",
                        tint = MaterialTheme.colorScheme.outline
                    )
                }
            }

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}


@Composable
private fun SelectedProfileImage(modifier: Modifier = Modifier) {
    Box(
        modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary)
    ) {
        Icon(
            Icons.Default.Check,
            contentDescription = null,
            modifier = Modifier
                .size(24.dp)
                .align(Alignment.Center),
            tint = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Composable
private fun ReplyProfileImage(
    photo: String,
    description: String,
    modifier: Modifier = Modifier
) {
    NoSurfaceImage(
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape),
        imageUrl = photo,
        contentDescription = description,
    )
}
