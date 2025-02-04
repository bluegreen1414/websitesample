package com.kapirti.social_chat_food_video.model.service.impl

import com.google.firebase.storage.FirebaseStorage
import com.kapirti.social_chat_food_video.model.service.AccountService
import com.kapirti.social_chat_food_video.model.service.StorageService
import javax.inject.Inject
import android.net.Uri
import androidx.compose.ui.util.trace
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await

class StorageServiceImpl @Inject constructor(
    private val auth: AccountService,
    private val storage: FirebaseStorage
): StorageService {
    override suspend fun getPhoto(uid: String): String = photoReference(uid).downloadUrl.await().toString()

    override suspend fun savePhoto(photo: ByteArray, uid: String): Unit =
        trace(SAVE_USER_PHOTOS) {
            photoReference(uid).putBytes(photo).await()
        }

    private fun storageReference(): StorageReference = storage.reference.child(PHOTOS_STORAGE).child(auth.currentUserId)
    private fun photoReference(uid: String): StorageReference = storageReference().child("${uid}.jpg")
    private fun videoReference(uid: String): StorageReference = storageReference().child("${uid}.mp4")

    companion object {
        private const val PHOTOS_STORAGE = "Photos"
        private const val SAVE_USER_PHOTOS = "storageUserPhotos"
        private const val SAVE_VIDEO = "storageVideo"
    }
}

/**
override suspend fun getVideo(uid: String): String = videoReference(uid).downloadUrl.await().toString()

override suspend fun saveVideo(uri: Uri, uid: String): Unit =
trace(SAVE_VIDEO) {
videoReference(uid).putFile(uri).await()
}



/** override suspend fun getProfile(): String =
override suspend fun saveBytes(photo: ByteArray): Unit = trace(SAVE_PHOTO_TRACE) {



private const val SAVE_PHOTO_TRACE = "savePhoto"
}*/
}

 * */
