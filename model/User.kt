package com.kapirti.social_chat_food_video.model

import android.net.Uri
import androidx.core.net.toUri
import com.google.firebase.firestore.DocumentId

private const val SHORTCUT_PREFIX = "contact_"

data class User(
    @DocumentId val id: String = "id",
    val name: String = "displayName",
    val country: String = "",
    val type: String = "nope",
    val learnLanguage: String = "",
    val motherTongue: String = "",

    //   val photo: String = "Photo",
//    val description: String = "Description",
//  val surname: String = "surname",
//    val online: Boolean = false,
//    @ServerTimestamp var lastSeen: Timestamp? = null,
 //   @ServerTimestamp var dateOfCreation: Timestamp? = null,


    var fcmToken: String = "",
    var status: String = "",
    var typingTo: String = ""
) {
    val shortcutId: String
        get() = "$SHORTCUT_PREFIX$id"

    val contentUri: Uri
        get() = "https://socialite.google.com/chat/$id".toUri()

    val iconUri: Uri
        get() = "".toUri()
}

fun extractChatId(shortcutId: String): String {
    if (!shortcutId.startsWith(SHORTCUT_PREFIX)) return ""
    return try {
        shortcutId.substring(SHORTCUT_PREFIX.length)
    } catch (e: NumberFormatException) {
        ""
    }
}

