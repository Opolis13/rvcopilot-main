package com.example.rvcopilot.data

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FacilitiesRepository(
    private val db: FirebaseFirestore
) {
    private val _firebaseCampsitesStateFlow = MutableStateFlow<List<FirebaseCampsite>>(emptyList())
    val firebaseCampsitesStateFlow: StateFlow<List<FirebaseCampsite>> = _firebaseCampsitesStateFlow

    fun fetchCampsitesFromFirebase() {
        println("FIREBASE_CAMPSITE: Starting Firestore fetch...")

        db.collection("db")
            .get()
            .addOnSuccessListener { result ->
                println("FIREBASE_CAMPSITE: Successfully fetched documents from 'db'.")
                val campsiteList = result.mapNotNull { doc ->

                    println("FIREBASE_CAMPSITE: Reading document ID=${doc.id}")
                    try {
                        val campsite = doc.toObject(FirebaseCampsite::class.java)
                        println("FIREBASE_CAMPSITE: Parsed ${campsite.name} at ${campsite.address}")
                        campsite
                    } catch (e: Exception) {
                        println("FIREBASE_CAMPSITE_ERROR: Failed to parse document ID=${doc.id}, Error=${e.message}")
                        null
                    }
                }
                println("FIREBASE_CAMPSITE: Total campsites loaded = ${campsiteList.size}")
                _firebaseCampsitesStateFlow.value = campsiteList
            }
            .addOnFailureListener { exception ->
                println("FIREBASE_CAMPSITE_ERROR: Failed to fetch from Firestore: ${exception.message}")
            }
    }
}