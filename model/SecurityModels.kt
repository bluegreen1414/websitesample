package com.kapirti.social_chat_food_video.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

data class Block(
    @DocumentId val id: String = "id",
    val displayName: String = "",
    val photo: String = "",
    @ServerTimestamp
    val dateOfCreation: Timestamp? = null
)

data class Report(
    @DocumentId val id: String = "",
    val displayName: String = "",
    val photo: String = "",
    @ServerTimestamp
    val dateOfCreation: Timestamp? = null
)

data class Delete(
    val id: String = "",
    val email: String = "",
    val text: String = "",
    @ServerTimestamp val dateOfCreation: Timestamp? = null
)
