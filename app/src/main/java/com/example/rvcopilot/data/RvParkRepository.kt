package com.example.rvcopilot.data

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow


class RvParkRepository(
    private val db: FirebaseFirestore
) {
    suspend fun insertRvPark(rvPark: RvPark) {
        try {
            val docRef = db.collection("rv_parks").add(rvPark).await()
            val id = docRef.id
            docRef.update("firebaseId", id).await()
            println("Inserted RV Park '${rvPark.name}' with firebaseId: $id")
        } catch (e: Exception) {
            println("Insert error: ${e.message}")
        }
    }

    fun getAllRvParks(username: String): Flow<List<RvPark>> = callbackFlow {
        val listener = db.collection("rv_parks")
            .whereEqualTo("createdBy", username)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    println("Firestore listener error: ${error.message}")
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val parks = snapshot?.documents?.mapNotNull { doc ->
                    val park = doc.toObject(RvPark::class.java)
                    park?.copy(firebaseId = doc.id) // assigns a firebase ID
                } ?: emptyList()
                println("Firestore sent updated park list: ${parks.size} parks")
                trySend(parks)
            }
        awaitClose {
            println("Firestore RV park listener for $username is closed")
            listener.remove()
        }
    }

    suspend fun updateRvPark(firebaseId: String, updatedRvPark: RvPark) {
        println("inside updateRvPark with firebaseId: '$firebaseId'")
        println("RvPark object:  '${updatedRvPark}'")
        try {
            if (firebaseId.isNotBlank()) {
                db.collection("rv_parks")
                    .document(firebaseId)
                    .set(updatedRvPark)
                    .await()
                println("Updated RV Park: ${updatedRvPark.name}")
            } else {
                println("firebaseId is blank — can't update.")
            }
        } catch (e: Exception) {
            println("Error updating RV Park: ${e.message}")
        }
    }
    suspend fun deleteRvPark(rvParkName: String) {
        println("inside of RvParkRepository function deleteRvPark")
        try {
            val snapshot = db.collection("rv_parks")
                .whereEqualTo("name", rvParkName)
                .get()
                .await()
            if (!snapshot.isEmpty) {
                for (document in snapshot.documents) {
                    document.reference.delete().await()
                    println("delete RV Park: $rvParkName")
                }
            } else {
                println("Rv Park name not found: $rvParkName")
            }
        } catch (e: Exception) {
            println("Error deleting RV Park: ${e.message}")
        }
    }

    suspend fun addReview(rvParkId: String, review: Review) {
        try {
            db.collection("rvParks")
                .document(rvParkId)
                .collection("reviews")
                .add(review)
                .await()

            println("Review successfully saved to /rvParks/$rvParkId/reviews")
        } catch (e: Exception) {
            println("Firestore save failed: ${e.message}")
        }
    }
    suspend fun getReviews(rvParkId: String): List<Review> {
        return try {
            val snapshot = db.collection("rvParks")
                .document(rvParkId)
                .collection("reviews")
                .get()
                .await()

            snapshot.documents.mapNotNull { it.toObject(Review::class.java) }
        } catch (e: Exception) {
            println("Failed to load reviews: ${e.message}")
            emptyList()
        }
    }
    suspend fun deleteAllReviews(rvParkId: String) {
        try {
            val snapshot = db.collection("rvParks")
                .document(rvParkId)
                .collection("reviews")
                .get()
                .await()

            snapshot.documents.forEach { it.reference.delete().await() }
            println("All reviews deleted for RV park: $rvParkId")
        } catch (e: Exception) {
            println("Failed to delete reviews: ${e.message}")
        }
    }

}