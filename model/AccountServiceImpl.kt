package com.kapirti.social_chat_food_video.model.service.impl

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.kapirti.social_chat_food_video.model.UserIsAnonymous
import com.kapirti.social_chat_food_video.model.service.AccountService
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class AccountServiceImpl @Inject constructor(private val auth: FirebaseAuth) : AccountService {
    override val hasUser: Boolean
        get() = auth.currentUser != null

    override val currentUserId: String
        get() = auth.currentUser?.uid.orEmpty()

    override val currentUserEmail: String
        get() = auth.currentUser?.email.orEmpty()

    override val currentUserDisplayName: String
        get() = auth.currentUser?.displayName.orEmpty()


    override val currentUser: Flow<UserIsAnonymous>
        get() = callbackFlow {
            val listener =
                FirebaseAuth.AuthStateListener { auth ->
                    this.trySend(auth.currentUser?.let { UserIsAnonymous(it.uid, it.isAnonymous, it.email.orEmpty()) } ?: UserIsAnonymous())
                }
            auth.addAuthStateListener(listener)
            awaitClose { auth.removeAuthStateListener(listener) }
        }


    override suspend fun authenticate(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).await()
    }

    override suspend fun sendRecoveryEmail(email: String) {
        auth.sendPasswordResetEmail(email).await()
    }

/**    override suspend fun createAnonymousAccount() {
        auth.signInAnonymously().await()
    }*/

    override suspend fun linkAccount(email: String, password: String): FirebaseUser = suspendCoroutine { cont ->
        //trace(LINK_ACCOUNT_TRACE) {
        val x = auth.createUserWithEmailAndPassword(email, password)
        x.addOnSuccessListener { result ->
            cont.resume(result.user!!)
        }
        //val credential = EmailAuthProvider.getCredential(email, password)
        //auth.currentUser!!.linkWithCredential(credential).await()
        //}
    }


    override suspend fun displayName(newValue: String){
        val profileUpdates = userProfileChangeRequest {
            displayName = newValue
        }
        auth.currentUser?.let { it.updateProfile(profileUpdates).await() }
    }

    override suspend fun signOut() {
        if (auth.currentUser!!.isAnonymous) {
            auth.currentUser!!.delete()
        }
        auth.signOut()

        // Sign the user back in anonymously.
        //createAnonymousAccount()
    }

    override suspend fun deleteAccount() {
        auth.currentUser!!.delete().await()
    }

    companion object {
        private const val LINK_ACCOUNT_TRACE = "linkAccount"
    }
}
