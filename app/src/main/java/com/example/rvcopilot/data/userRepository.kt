package com.example.rvcopilot.data

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * --userRepository class:
 * data layer that interacts with the data access objects (dao).
 * manage database interactions for airport and flight data.
 * provides the interface between the viewModel and each dao.
 * --the suspend coroutine functions force Room operations
 * to be done in the background.
 * operations can be in the front but the preference for Room is background
 * */

class UserRepository(
    private val db: FirebaseFirestore

) {
    suspend fun update(user: User) {
        db.collection("users").add(user).await()
    }

    /**
    // Firebase needs val referenceId = db.collection to retrieve the firebaseId
     * https://firebase.google.com/docs/firestore/query-data/get-data#kotlin_2
     *
     *
     * - Kotlin coroutine dispatcher
     * https://kotlinlang.org/docs/coroutine-context-and-dispatchers.html#dispatchers-and-threads
     * The coroutine context includes a coroutine dispatcher
     * determines what thread or threads the corresponding coroutine uses
     * for its execution. The coroutine dispatcher can confine
     * coroutine execution to a specific thread, dispatch it
     * to a thread pool, or let it run unconfined.
     *
     * withContext:  https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/with-context.html
     *
     */
    suspend fun insertUser(user: User) {
        println("userRepository insertUser() User: $user ")
        try {
            withContext(Dispatchers.IO) {
                /** ---insertUser(context: CoroutineContext)
                 * withContext calls the specified suspending block
                 * with a given coroutine context, suspends until it completes
                 * and returns the result.
                 *
                 * code below:  creates document reference id first then puts it into object
                 * */
                val referenceId = db.collection("users").document() // generates Firestore ID
                val userWithId = user.copy(firebaseId = referenceId.id)
                // .await() suspends the coroutine. similar to javascript promise
                // waits for this task to complete
                println("before coroutine suspension using .await()")
                referenceId.set(userWithId).await()
                println("User inserted with firebaseId: ${userWithId.firebaseId}")
            }
        }
        catch (e: Exception) {
            println("Error inserting user: ${e.message}")
            e.printStackTrace()
        }
    }

    fun getAllUsers(): Flow<List<User>> = flow {
        val snapshot = db.collection("users").get().await()
        val users = snapshot.documents.mapNotNull { it.toObject(User::class.java) }
        emit(users)
    }
    /**
    * Firebase needs val doc = db.collection to retrieve the firebaseId
     * https://firebase.google.com/docs/firestore/query-data/get-data#kotlin_2
     *
     * - see getUser example ->
     * https://firebase.blog/posts/2021/02/improve-app-stability-with-firebase-crashlytics-and-kotlin
     * function getUserById "fetches a document from Firestore" see link for example
     *
     */
    suspend fun getUserById(firebaseId: String): User? {
        return try {
            val doc = db.collection("users").document(firebaseId).get().await()
            doc.toObject(User::class.java)
        } catch (e: Exception) {
            println("Error getting user by ID: ${e.message}")
            null
        }
    }


    suspend fun getUserByUsername(username: String): User? {
        val snapshot = db.collection("users")
            .whereEqualTo("username", username)
            .get().await()

        return if (!snapshot.isEmpty) {
            snapshot.documents.first().toObject(User::class.java)
        } else null
    }
    suspend fun updateUserBio(userId: String, newBio: String) {
        db.collection("users")
            .document(userId)
            .update("userBio", newBio)
            .await()
    }
    suspend fun updateUserReview(userId: String, newReview: String) {
        db.collection("users")
            .document(userId)
            .update("reviewBio", newReview)
            .await()
    }
}
