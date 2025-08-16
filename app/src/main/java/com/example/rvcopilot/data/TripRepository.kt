package com.example.rvcopilot.data

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow


class TripRepository(
    private val db: FirebaseFirestore
) {

    /**
     * *******************************************************
     * *******************************************************
     * Trips section
     * *******************************************************
     * */
    suspend fun insertTrip(trips: Trips) {
        try {
            val docRef = db.collection("trips").add(trips).await()
            val id = docRef.id
            docRef.update("firebaseId", id).await()
            println("Inserted Trip '${trips.destination}' with firebaseId: $id")
        } catch (e: Exception) {
            println("Insert error: ${e.message}")
        }
    }

    fun getAllTrips(username: String): Flow<List<Trips>> = callbackFlow {
        val listener = db.collection("trips")
            .whereEqualTo("createdBy", username)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    println("Firestore listener error: ${error.message}")
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val trips = snapshot?.documents?.mapNotNull { doc ->
                    val trip = doc.toObject(Trips::class.java)
                    trip?.copy(firebaseId = doc.id) // Assign unique ID
                } ?: emptyList()
                println("Firestore sent updated trips list: ${trips.size} trips")
                trySend(trips)
            }
        awaitClose {
            println("Firestore Trips listener closed")
            listener.remove()
        }
    }

    suspend fun updateTrip(firebaseId: String, updatedTrip: Trips) {
        println("inside updateTrip with firebaseId: '$firebaseId'")
        println("Trip object:  '${updatedTrip}'")
        try {
            if (firebaseId.isNotBlank()) {
                db.collection("trips")
                    .document(firebaseId)
                    .set(updatedTrip)
                    .await()
                println("Updated Trip: ${updatedTrip.destination}")
            } else {
                println("firebaseId is blank — can't update.")
            }
        } catch (e: Exception) {
            println("Error updating Trip: ${e.message}")
        }
    }
    suspend fun deleteTrip(tripDestination: String) {
        println("inside of TripRepository function deleteTrip")
        try {
            val snapshot = db.collection("trips")
                .whereEqualTo("destination", tripDestination)
                .get()
                .await()
            if (!snapshot.isEmpty) {
                for (document in snapshot.documents) {
                    document.reference.delete().await()
                    println("delete RV Park: $tripDestination")
                }
            } else {
                println("Rv Park name not found: $tripDestination")
            }
        } catch (e: Exception) {
            println("Error deleting RV Park: ${e.message}")
        }
    }

    suspend fun addReviewTrip(tripId: String, reviewTrip: ReviewTrip) {
        try {
            db.collection("trips")
                .document(tripId)
                .collection("reviewTrips")
                .add(reviewTrip)
                .await()

            println("Review successfully saved to /trips/$tripId/reviewTrip")
        } catch (e: Exception) {
            println("Firestore save failed: ${e.message}")
        }
    }
    suspend fun getReviewTrip(tripId: String): List<ReviewTrip> {
        return try {
            val snapshot = db.collection("trips")
                .document(tripId)
                .collection("reviewTrips")
                .get()
                .await()

            snapshot.documents.mapNotNull { it.toObject(ReviewTrip::class.java) }
        } catch (e: Exception) {
            println("Failed to load reviewTrip: ${e.message}")
            emptyList()
        }
    }
    suspend fun deleteAllReviewTrips(tripId: String) {
        try {
            val snapshot = db.collection("trips")
                .document(tripId)
                .collection("reviewTrips")
                .get()
                .await()

            snapshot.documents.forEach { it.reference.delete().await() }
            println("All reviews deleted for RV park: $tripId")
        } catch (e: Exception) {
            println("Failed to delete reviews: ${e.message}")
        }
    }

    /**
     * *******************************************************
     * *******************************************************
     * Restaurants section
     * *******************************************************
     * */
    suspend fun insertRestaurant(restaurants: Restaurant) {
        try {
            val docRef = db.collection("restaurants").add(restaurants).await()
            val id = docRef.id
            docRef.update("firebaseId", id).await()
            println("Inserted Restaurant '${restaurants.name}' with firebaseId: $id")
        } catch (e: Exception) {
            println("Insert error: ${e.message}")
        }
    }

    fun getAllRestaurants(username: String): Flow<List<Restaurant>> = callbackFlow {
        val listener = db.collection("restaurants")
            .whereEqualTo("createdBy", username)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    println("Firestore listener error: ${error.message}")
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val restaurants = snapshot?.documents?.mapNotNull { doc ->
                    val restaurant = doc.toObject(Restaurant::class.java)
                    restaurant?.copy(firebaseId = doc.id) // Assign unique ID
                } ?: emptyList()
                println("Firestore sent updated restaurants list: ${restaurants.size} restaurants")
                trySend(restaurants)
            }
        awaitClose {
            println("Firestore Restaurants listener closed")
            listener.remove()
        }
    }

    suspend fun updateRestaurant(firebaseId: String, updatedRestaurant: Restaurant) {
        println("inside updateRestaurant with firebaseId: '$firebaseId'")
        println("Restaurant object:  '${updatedRestaurant}'")
        try {
            if (firebaseId.isNotBlank()) {
                db.collection("restaurants")
                    .document(firebaseId)
                    .set(updatedRestaurant)
                    .await()
                println("Updated Restaurant: ${updatedRestaurant.name}")
            } else {
                println("firebaseId is blank — can't update.")
            }
        } catch (e: Exception) {
            println("Error updating Restaurant: ${e.message}")
        }
    }
    suspend fun deleteRestaurant(restaurant: String) {
        println("inside of RestaurantRepository function deleteRestaurant")
        try {
            val snapshot = db.collection("restaurants")
                .whereEqualTo("name", restaurant)
                .get()
                .await()
            if (!snapshot.isEmpty) {
                for (document in snapshot.documents) {
                    document.reference.delete().await()
                    println("delete RV Park: $restaurant")
                }
            } else {
                println("Rv Park name not found: $restaurant")
            }
        } catch (e: Exception) {
            println("Error deleting RV Park: ${e.message}")
        }
    }
    suspend fun addReviewRestaurant(restaurantId: String, reviewRestaurant: ReviewRestaurant) {
        try {
            db.collection("restaurants")
                .document(restaurantId)
                .collection("reviewRestaurants")
                .add(reviewRestaurant)
                .await()

            println("Review successfully saved to /restaurants/$restaurantId/reviewRestaurant")
        } catch (e: Exception) {
            println("Firestore save failed: ${e.message}")
        }
    }
    suspend fun getReviewRestaurant(restaurantId: String): List<ReviewRestaurant> {
        return try {
            val snapshot = db.collection("restaurants")
                .document(restaurantId)
                .collection("reviewRestaurants")
                .get()
                .await()

            snapshot.documents.mapNotNull { it.toObject(ReviewRestaurant::class.java) }
        } catch (e: Exception) {
            println("Failed to load reviewRestaurant: ${e.message}")
            emptyList()
        }
    }
    suspend fun deleteAllReviewRestaurants(restaurantId: String) {
        try {
            val snapshot = db.collection("restaurants")
                .document(restaurantId)
                .collection("reviewRestaurants")
                .get()
                .await()

            snapshot.documents.forEach { it.reference.delete().await() }
            println("All reviews deleted for RV park: $restaurantId")
        } catch (e: Exception) {
            println("Failed to delete reviews: ${e.message}")
        }
    }
    /**
     * *******************************************************
     * *******************************************************
     * Recreational section
     * *******************************************************
     * */


    /**
     * *******************************************************
     * *******************************************************
     * Activities section
     * *******************************************************
     * */

}