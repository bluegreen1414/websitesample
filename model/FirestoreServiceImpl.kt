package com.kapirti.social_chat_food_video.model.service.impl

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.snapshots
import com.google.firebase.firestore.toObjects
import com.kapirti.social_chat_food_video.model.User
import com.kapirti.social_chat_food_video.model.service.AccountService
import com.kapirti.social_chat_food_video.model.service.FirestoreService
import com.kapirti.social_chat_food_video.model.service.trace
import com.kapirti.social_chat_food_video.ui.presentation.home.shop.Product
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.dataObjects
import com.google.firebase.firestore.toObject
import com.kapirti.social_chat_food_video.core.datastore.CountryRepository
import com.kapirti.social_chat_food_video.core.datastore.LearnLanguageRepository
import com.kapirti.social_chat_food_video.model.Cafe
import com.kapirti.social_chat_food_video.model.CallRecord
import com.kapirti.social_chat_food_video.model.ChatMessageGet
import com.kapirti.social_chat_food_video.model.ChatRoomFromChatRoom
import com.kapirti.social_chat_food_video.model.ChatRoomFromUserChat
import com.kapirti.social_chat_food_video.model.Delete
import com.kapirti.social_chat_food_video.model.Hotel
import com.kapirti.social_chat_food_video.model.Restaurant
import com.kapirti.social_chat_food_video.model.UserFlirt
import com.kapirti.social_chat_food_video.model.UserLP
import com.kapirti.social_chat_food_video.model.UserMarriage

class FirestoreServiceImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: AccountService,
    private val countryRepository: CountryRepository,
    private val learnLanguageRepository: LearnLanguageRepository,
): FirestoreService {
    @OptIn(ExperimentalCoroutinesApi::class)
    override val userChats: Flow<List<ChatRoomFromUserChat>>
        get() = auth.currentUser.flatMapLatest { user ->
            userChatCollection(user.id).orderBy(DATE_OF_CREATION_FIELD, Query.Direction.DESCENDING).dataObjects() }





    override suspend fun getUser(userId: String): User? = suspendCoroutine { cont ->
        userCollection().document(userId).get().addOnSuccessListener {
            val x = it.toObject(User::class.java)
            cont.resume(x)
        }
    }
    override suspend fun getChatRoomFromChatRoom(chatRoomId: String): ChatRoomFromChatRoom? = suspendCoroutine { cont ->
        chatRoomsCollection().document(chatRoomId).get().addOnCompleteListener {
            if (it.isSuccessful) {
                val x = it.result.toObject(ChatRoomFromChatRoom::class.java)
                cont.resume(x)
            } else {
                cont.resume(null)
            }
        }
    }
    override suspend fun getChatMessage(chatRoomId: String): Flow<List<ChatMessageGet>> {
        return getChatRoomMessageReference(chatRoomId).orderBy("timestamp", Query.Direction.DESCENDING).snapshots().map { snapshot -> snapshot.toObjects(ChatMessageGet::class.java) }
    }


    override suspend fun saveUser(user: User) { userCollection().document(user.id).set(user) }
    override suspend fun saveChatRoomFromChatRoom(chatRoomId: String, chatRoom: ChatRoomFromChatRoom) = suspendCoroutine { cont ->
        chatRoomsCollection().document(chatRoomId).set(chatRoom).addOnCompleteListener {
            cont.resume(it.isSuccessful)
        }
    }




/**    override suspend fun setUserOnline() {
        userCollection().document(auth.currentUserId).update("status", "online")
    }

    override suspend fun setUserOffline() {
        //userCollection().document(auth.currentUserId).update("status", Timestamp.now().toDate().time.toString())
        userCollection().document(auth.currentUserId).update("status", System.currentTimeMillis().toString())
        //userCollection().document(auth.currentUserId).update("status", Timestamp.now().seconds)
    }*/

    override suspend fun setUserTyping(typingRoomId: String) {
        userCollection().document(auth.currentUserId).update("typingTo", typingRoomId)
    }

    override suspend fun clearUserTyping(typingRoomId: String) {
        userCollection().document(auth.currentUserId).update("typingTo", "")
    }

    override suspend fun observeOtherChatState(userId: String): Flow<Pair<String, String>> {
        return userCollection().document(userId).snapshots().map { snapshot ->
            snapshot.getString("status").orEmpty() to snapshot.getString("typingTo").orEmpty()
        }

    }


    override suspend fun getChatRoomMessageReference(chatRoomId: String): CollectionReference {
        return chatRoomsCollection().document(chatRoomId).collection("chats")
    }

/**    //@OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getConversations(userId: String): Flow<List<ChatRoom>> {
        return chatRoomsCollection().whereArrayContains("userIds", userId)
            .orderBy("lastMessageTime", Query.Direction.DESCENDING)
            //.addSnapshotListener { value, error -> value.toObjects(ChatRoom::class.java) }
            .snapshotFlow().map { snapshot ->
                android.util.Log.d("myTag","snapshot update triggered..")
                snapshot.toObjects(ChatRoom::class.java)
            }
    }*/




    override fun updateUserFcmToken(token: String) {
        userCollection().document(auth.currentUserId).update(FCM_TOKEN_FIELD, token)
    }

    override suspend fun saveCallRecord(callRecord: CallRecord) {
        callRecordsCollection().add(callRecord)
    }

    override suspend fun getCallRecords(userId: String): Flow<List<CallRecord>> {
        return callRecordsCollection().whereEqualTo("userId", userId)
            .orderBy("callEnd", Query.Direction.DESCENDING)
            //.addSnapshotListener { value, error -> value.toObjects(ChatRoom::class.java) }
            .snapshotFlow().map { snapshot ->
                snapshot.toObjects(CallRecord::class.java)
            }
    }


  /**
    @OptIn(ExperimentalCoroutinesApi::class)
    override val currentUserConversations: Flow<List<ChatRoom>>
        get() = auth.currentUser.flatMapLatest { user ->
            chatRoomsCollection().whereArrayContains("userIds", user.id).orderBy("lastMessageTime", Query.Direction.DESCENDING).snapshots().map { snapshot -> snapshot.toObjects() } }




*/


    @OptIn(ExperimentalCoroutinesApi::class)
    override val products: Flow<List<Product>>
        get() = auth.currentUser.flatMapLatest { user ->
            productCollection().dataObjects() }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val usersFlirt: Flow<List<UserFlirt>>
        get() = countryRepository.readCountry()
            .flatMapLatest { country -> userFlirtCollection(country).dataObjects() }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val usersMarriage: Flow<List<UserMarriage>>
        get() = countryRepository.readCountry()
            .flatMapLatest { country -> userMarriageCollection(country).dataObjects() }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val usersLP: Flow<List<UserLP>>
        get() = learnLanguageRepository.readLearnLanguageState()
            .flatMapLatest { ct -> userLanguagePracticeCollection(ct).dataObjects() }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val restaurants: Flow<List<Restaurant>>
        get() = countryRepository.readCountry().flatMapLatest { country -> restaurantCollection(country).dataObjects() }


    @OptIn(ExperimentalCoroutinesApi::class)
    override val cafes: Flow<List<Cafe>>
        get() = countryRepository.readCountry().flatMapLatest { country -> cafeCollection(country).dataObjects() }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val hotels: Flow<List<Hotel>>
        get() = countryRepository.readCountry().flatMapLatest { ct -> hotelCollection(ct).dataObjects() }



    override suspend fun saveProduct(product: Product): Unit = trace(SAVE_PRODUCT_TRACE){ productCollection().add(product).await()}
    override suspend fun getUserFlirt(country: String, id: String): UserFlirt? = userFlirtDocument(country = country, id = id).get().await().toObject()
    override suspend fun getUserMarriage(country: String, id: String): UserMarriage? = userMarriageDocument(country = country, id = id).get().await().toObject()
    override suspend fun getUserLP(motherTongue: String, id: String): UserLP? = userLanguagePracticeDocument(motherTongue = motherTongue, id = id).get().await().toObject()
    override suspend fun getHotel(country: String, id: String): Hotel? = hotelDocument(country = country, id = id).get().await().toObject()
    override suspend fun getRestaurant(country: String, id: String): Restaurant? = restaurantDocument(country = country, id = id).get().await().toObject()
    override suspend fun getCafe(country: String, id: String): Cafe? = cafeDocument(country = country, id = id).get().await().toObject()
    override suspend fun deleteAccount(delete: Delete): Unit = trace(DELETE_ACCOUNT_TRACE) { deleteCollection().add(delete).await() }



    private fun userCollection(): CollectionReference = firestore.collection(USER_COLLECTION)
    private fun userDocument(id: String): DocumentReference = userCollection().document(id)
    private fun userChatCollection(uid: String): CollectionReference = userDocument(uid).collection(CHAT_COLLECTION)
    private fun userChatDocument(who: String, partnerId: String): DocumentReference = userChatCollection(who).document(partnerId)


    private fun chatRoomsCollection(): CollectionReference = firestore.collection(CHATROOM_COLLECTION)
    private fun callRecordsCollection(): CollectionReference = firestore.collection(CALL_RECORDS_COLLECTION)
    private fun productCollection(): CollectionReference = firestore.collection(PRODUCT_COLLECTION)
    private fun userFlirtCollection(country: String): CollectionReference = firestore.collection(FLIRT_COLLECTION).document(country).collection(country)
    private fun userFlirtDocument(country: String, id: String): DocumentReference = userFlirtCollection(country).document(id)
    private fun userMarriageCollection(country: String): CollectionReference = firestore.collection(MARRIAGE_COLLECTION).document(country).collection(country)
    private fun userMarriageDocument(country: String, id: String): DocumentReference = userMarriageCollection(country).document(id)
    private fun userLanguagePracticeCollection(motherTongue: String): CollectionReference = firestore.collection(LANGUAGE_PRACTICE_COLLECTION).document(motherTongue).collection(motherTongue)
    private fun userLanguagePracticeDocument(motherTongue: String, id: String): DocumentReference = userLanguagePracticeCollection(motherTongue).document(id)
    private fun hotelCollection(country: String): CollectionReference = firestore.collection(HOTEL_COLLECTION).document(country).collection(HOTEL_COLLECTION)
    private fun hotelDocument(country: String, id: String): DocumentReference = hotelCollection(country).document(id)
    private fun restaurantCollection(country: String): CollectionReference = firestore.collection(RESTAURANT_COLLECTION).document(country).collection(RESTAURANT_COLLECTION)
    private fun restaurantDocument(country: String, id: String): DocumentReference = restaurantCollection(country).document(id)
    private fun cafeCollection(country: String): CollectionReference = firestore.collection(CAFE_COLLECTION).document(country).collection(CAFE_COLLECTION)
    private fun cafeDocument(country: String, id: String): DocumentReference = cafeCollection(country).document(id)
    private fun deleteCollection(): CollectionReference = firestore.collection(DELETE_COLLECTION)


    fun Query.snapshotFlow(): Flow<QuerySnapshot> = callbackFlow {
        val listenerRegistration = addSnapshotListener { value, error ->
            if (error != null) {
                android.util.Log.d("myTag2","close it!, error is $error")
                close()
                return@addSnapshotListener
            }
            if (value != null) {
                android.util.Log.d("myTag2","send it!")
                trySend(value)
            }
        }
        awaitClose {
            android.util.Log.d("myTag2","remove it!")
            listenerRegistration.remove()
        }
    }


    companion object {
        const val FLIRT_COLLECTION = "Flirt"
        const val MARRIAGE_COLLECTION = "Marriage"
        const val LANGUAGE_PRACTICE_COLLECTION = "LanguagePractice"
        const val RESTAURANT_COLLECTION = "Restaurant"
        const val CAFE_COLLECTION = "Cafe"
        const val HOTEL_COLLECTION = "Hotel"
        const val ONLINE_FIELD = "online"
        const val LAST_SEEN_FIELD = "lastSeen"
        const val GENDER_FIELD = "gender"
        const val HOBBY_FIELD = "hobby"
        const val PHOTO_FIELD = "photo"
        const val DESCRIPTION_FIELD = "description"
        const val USERNAME_FIELD = "username"
        const val BIRTHDAY_FIELD = "birthday"
        const val CITY_FIELD = "city"
        const val ADDRESS_FIELD = "address"
        const val TYPE_FIELD = "type"
        const val COUNTRY_FIELD = "country"
        const val LEARN_LANGUAGE_FIELD = "learnLanguage"
        const val MOTHER_TONGUE_FIELD = "motherTongue"
        const val FCM_TOKEN_FIELD = "fcmToken"
        private const val PRODUCT_COLLECTION = "Product"
        const val CHAT_COLLECTION = "Chat"
        private const val SAVE_USER_FLIRT_TRACE = "saveUserFlirt"
        private const val SAVE_USER_MARRIAGE_TRACE = "saveUserMarriage"
        private const val SAVE_USER_LP_TRACE = "saveUserLP"
        private const val SAVE_HOTEL_TRACE = "saveHotel"
        private const val SAVE_RESTAURANT_TRACE = "saveRestaurant"
        private const val SAVE_CAFE_TRACE = "saveCafe"
        private const val SAVE_FEEDBACK_TRACE = "saveFeedback"
        private const val SAVE_TIMELINE_TRACE = "saveTimeline"
        private const val SAVE_ACCOUNT_GALLERY_TRACE = "saveAccountGallery"
        private const val SAVE_ACCOUNT_CHAT_TRACE = "saveAccountChat"
        private const val SAVE_ACCOUNT_ARCHIVE_TRACE = "saveAccountArchive"
        private const val SAVE_CHAT_MESSAGE_TRACE = "saveChatMessage"
        private const val SAVE_BLOCK_USER = "saveBlockUser"
        private const val SAVE_REPORT = "saveReport"
        private const val SAVE_TIMELINE_REPORT_TRACE = "saveTimelineReport"
        private const val SAVE_HOTEL_REPORT_TRACE = "saveHotelReport"
        private const val SAVE_CAFE_REPORT_TRACE = "saveCafeReport"
        private const val SAVE_RESTAURANT_REPORT_TRACE = "saveRestaurantReport"
        private const val SAVE_USER_FLIRT_REPORT_TRACE = "saveUserFlirtReport"
        private const val SAVE_USER_LP_REPORT_TRACE = "saveUserLPReport"
        private const val SAVE_USER_MARRIAGE_REPORT_TRACE = "saveUserMarriageReport"
        private const val SAVE_PRODUCT_TRACE = "saveProduct"
        private const val SAVE_STORY_TRACE = "saveStory"
        private const val DELETE_COLLECTION = "Delete"
        private const val DELETE_ACCOUNT_TRACE = "deleteAccount"

        private const val CALL_RECORDS_COLLECTION = "CallRecords"
        private const val CHATROOM_COLLECTION = "ChatRooms"
        const val USER_COLLECTION = "User"
        private const val DATE_OF_CREATION_FIELD = "dateOfCreation"

        fun getChatRoomId(firstUserId: String, secondUserId: String) : String {
            return if(firstUserId.hashCode() < secondUserId.hashCode()) {
                firstUserId + "_" + secondUserId
            } else {
                secondUserId + "_" + firstUserId
            }
        }
    }
}
