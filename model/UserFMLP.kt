package com.kapirti.social_chat_food_video.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

data class UserFlirt(
    @DocumentId val id: String = "id",
    val photo: String = "Photo",
    val displayName: String = "Display name",
    val username: String = "Name",
    val birthday: String = "Birthday",
    val gender: String = "Gender",
    val description: String = "Description",
    val hobby: List<String> = listOf(),
    val country: String = "United State",
    val online: Boolean = false,
    @ServerTimestamp val lastSeen: Timestamp? = null,
    @ServerTimestamp var dateOfCreation: Timestamp? = null
)

data class UserMarriage(
    @DocumentId val id: String = "id",
    val photo: String = "Photo",
    val displayName: String = "Display name",
    val username: String = "Name",
    val birthday: String = "Birthday",
    val gender: String = "Gender",
    val description: String = "Description",
    val hobby: List<String> = listOf(),
    val country: String = "United State",
    val online: Boolean = false,
    @ServerTimestamp val lastSeen: Timestamp? = null,
    @ServerTimestamp var dateOfCreation: Timestamp? = null
)

data class UserLP(
    @DocumentId val id: String = "id",
    val photo: String = "Photo",
    val displayName: String = "Display name",
    val username: String = "Name",
    val birthday: String = "Birthday",
    val gender: String = "Gender",
    val description: String = "Description",
    val hobby: List<String> = listOf(),
    val country: String = "United State",
    val motherTongue: String = "Mother tongue",
    val learnLanguage: String = "Learn language",
    val online: Boolean = false,
    @ServerTimestamp val lastSeen: Timestamp? = null,
    @ServerTimestamp var dateOfCreation: Timestamp? = null
)


/**
data class UserLP(
    @DocumentId val id: String = "id",
    val displayName: String = "Display name",
    val name: String = "Name",
    val surname: String = "Surname",
    val photo: String = "Photo",
    val birthday: String = "Birthday",
    val gender: String = "Gender",
    val description: String = "Description",

    val country: String = "United State",
    val motherTongue: String = "Mother tongue",
    val learnLanguage: String = "Learn language",

    val hobby: List<String> = listOf(),


    val token: String = "Token",
    val online: Boolean = false,
    @ServerTimestamp var lastSeen: Timestamp? = null,
    @ServerTimestamp var dateOfCreation: Timestamp? = null,

    val galleryLiked: Int = 0,
    val canLike: Int = 0,
)
*/
data class UserCall(
    @DocumentId val id: String = "id",
    val photo: String = "Photo",
    val displayName: String = "Display name",
    @ServerTimestamp var dateOfCreation: Timestamp? = null
)
