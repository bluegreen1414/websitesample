package com.kapirti.social_chat_food_video.ui.presentation.edit.func

import android.content.Context
import androidx.core.net.toUri
import androidx.work.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import androidx.work.WorkerParameters
import com.google.firebase.Timestamp
import com.kapirti.social_chat_food_video.core.constants.SelectType
import com.kapirti.social_chat_food_video.core.constants.getCountryLanguage
import com.kapirti.social_chat_food_video.model.UserMarriage
import com.kapirti.social_chat_food_video.model.service.impl.FirestoreServiceImpl
import com.kapirti.social_chat_food_video.ui.presentation.add.video.VideoUploadWorker


class MarriageWorker @Inject constructor(
    @ApplicationContext private val context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {
    private val ff = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    private val userCollection = ff.collection(FirestoreServiceImpl.USER_COLLECTION)
    private val userMarriageCollection = ff.collection(FirestoreServiceImpl.MARRIAGE_COLLECTION)


    override fun doWork(): Result {
        val randomid = inputData.getString(VideoUploadWorker.RANDOM_ID) ?: return Result.failure()
        val uid = inputData.getString(VideoUploadWorker.USER_ID) ?: return Result.failure()
        val photoFile = inputData.getString(PHOTO_BYTE_ARRAY) ?: return Result.failure()
        val country = inputData.getString(FirestoreServiceImpl.COUNTRY_FIELD) ?: return Result.failure()
        val username = inputData.getString(FirestoreServiceImpl.USERNAME_FIELD) ?: return Result.failure()
        val birthday = inputData.getString(FirestoreServiceImpl.BIRTHDAY_FIELD) ?: return Result.failure()
        val gender = inputData.getString(FirestoreServiceImpl.GENDER_FIELD) ?: return Result.failure()
        val hobby = inputData.getString(FirestoreServiceImpl.HOBBY_FIELD) ?: return Result.failure()
        val description = inputData.getString(FirestoreServiceImpl.DESCRIPTION_FIELD) ?: return Result.failure()



        val vr = storage.reference.child(VideoUploadWorker.PHOTOS_STORAGE).child(uid).child("${randomid.toUri()}.jpg")
        vr.putFile(photoFile.toUri()).addOnCompleteListener {
            if (!it.isSuccessful) {
                return@addOnCompleteListener
            } else {
                vr.downloadUrl.addOnSuccessListener {
                    val photoDownload = it.toString()

                    userCollection.document(uid)
                        .update(FirestoreServiceImpl.TYPE_FIELD, SelectType.MARRIAGE)
                        .addOnSuccessListener {
                            userCollection.document(uid).update(
                                FirestoreServiceImpl.LEARN_LANGUAGE_FIELD,
                                getCountryLanguage(country)
                            ).addOnSuccessListener {
                                userCollection.document(uid)
                                    .update(FirestoreServiceImpl.COUNTRY_FIELD, country)
                                    .addOnSuccessListener {
                                        userMarriageCollection.document(country)
                                            .collection(country).document(uid).set(
                                                UserMarriage(
                                                    displayName = username,
                                                    photo = photoDownload,
                                                    username = username,
                                                    birthday = birthday,
                                                    gender = gender,
//                                                    hobby = hobby.toList().,
                                                    description = description,
                                                    country = country,
                                                    online = true,
                                                    dateOfCreation = Timestamp.now(),
                                                )
                                            )
                                    }
                            }
                        }
                }
            }
        }

        return Result.success()
    }

    companion object {
        const val PHOTO_BYTE_ARRAY = "photo_byte_array"
    }
}
