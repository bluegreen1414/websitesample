package com.kapirti.social_chat_food_video.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

data class Hotel(
    @DocumentId val id: String = "-1",
    val displayName: String = "Display name",
    val country: String = "",
    val city: String = "",
    val address: String = "",
    val username: String = "",
    val description: String = "",
    val photo: String = "",
    val online: Boolean = false,
    @ServerTimestamp val lastSeen: Timestamp? = null,
    @ServerTimestamp var dateOfCreation: Timestamp? = null
)

data class Restaurant(
    @DocumentId val id: String = "-1",
    val displayName: String = "Display name",
    val country: String = "",
    val city: String = "",
    val address: String = "",
    val username: String = "",
    val description: String = "",
    val photo: String = "",
    val online: Boolean = false,
    @ServerTimestamp val lastSeen: Timestamp? = null,
    @ServerTimestamp var dateOfCreation: Timestamp? = null


//    val type: String = "",
    //   val website: String = "",
    //  @DrawableRes val drawable: Int = -1
)

data class Cafe(
    @DocumentId val id: String = "-1",
    val displayName: String = "Display name",
    val country: String = "",
    val city: String = "",
    val address: String = "",
    val username: String = "",
    val description: String = "",
    val photo: String = "",
    val online: Boolean = false,
    @ServerTimestamp val lastSeen: Timestamp? = null,
    @ServerTimestamp var dateOfCreation: Timestamp? = null
)
