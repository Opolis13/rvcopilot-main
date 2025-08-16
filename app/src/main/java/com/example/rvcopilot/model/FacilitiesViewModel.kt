package com.example.rvcopilot.model

import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.example.rvcopilot.data.Facility
import com.example.rvcopilot.data.Campsite
import com.example.rvcopilot.data.FacilitiesRepository
import com.example.rvcopilot.data.FirebaseCampsite
import com.example.rvcopilot.data.RvPark
import com.example.rvcopilot.data.RvParkRepository
import com.example.rvcopilot.network.RetrofitInstance
import kotlinx.coroutines.launch
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class FacilitiesViewModel(
    private val facilitiesRepository: FacilitiesRepository
) : ViewModel() {

    private val _facilities = MutableLiveData<List<Facility>>()
    val facilities: LiveData<List<Facility>> = _facilities

    private val _campsites = MutableLiveData<List<Campsite>>()
    val campsites: LiveData<List<Campsite>> = _campsites

    private val db = FirebaseFirestore.getInstance()

    //private val _firebaseCampsites = MutableLiveData<List<FirebaseCampsite>>()
    //val firebaseCampsites: LiveData<List<FirebaseCampsite>> = _firebaseCampsites

    val firebaseCampsites: StateFlow<List<FirebaseCampsite>> =
        facilitiesRepository.firebaseCampsitesStateFlow

    fun loadCampsites() {
        println("facilitiesViewModel:  loading campsites")
        facilitiesRepository.fetchCampsitesFromFirebase()
    }

    fun fetchFacilities(state: String = "OR") {
        viewModelScope.launch {
            val response = RetrofitInstance.api.getFacilities(state = state)

            if (response.isSuccessful) {
                val body = response.body()
                if (body == null) {
                    println("FACILITY_TEST, Response body is null")
                } else {
                    println("FACILITY_TEST, Response body received")
                    val facilities = body.facilities
                    if (facilities.isEmpty()) {
                        println("FACILITY_TEST, No facilities found in response")
                    } else {
                        println("FACILITY_TEST, Number facilities found: ${facilities.size}")
                        facilities.forEachIndexed { index, it ->
                            println(
                                "FACILITY_TEST, " +
                                        "[$index] ID=${it.facilityId}, " +
                                        "Name=${it.facilityName}, " +
                                        "Type: ${it.facilityType}, " +
                                        "Latitude: ${it.facilityLatitude}," +
                                        "Longitude: ${it.facilityLongitude}"
                            )
                        }
                        _facilities.value = facilities
                    }
                }
            } else {
                val errorRaw = response.errorBody()?.string()
                println("FACILITY_TEST, Response not successful: ${response.code()}")
                println("RAW_ERROR_JSON:\n${errorRaw ?: "No error body"}")
            }
        }
    }

    fun saveFavoriteCampsite(
        campsite: FirebaseCampsite,
        username: String,
        onSuccess: ()-> Unit,
    ) {
        val rvPark = RvPark(
            firebaseId = campsite.name.replace(" ", "_"),
            name = campsite.name,
            address = campsite.address,
            phone = campsite.phone,
            email = "",
            services = campsite.amenities.joinToString(", "),
            type = "Campground",
            power = campsite.hookups.joinToString(", "),
            pad = "",
            pets = if (campsite.pets) "Yes" else "No",
            cellular = "",
            wifi = if (campsite.wifi) "Yes" else "No",
            cable = "",
            amenity = campsite.amenities.joinToString(", "),
            createdBy = username,
            imageName = "avatar_weather1",
            coordinates = campsite.coordinates
        )

        FirebaseFirestore.getInstance()
            .collection("rv_parks")
            //.document(rvPark.firebaseId)
            //.set(rvPark)
            .add(rvPark)
            .addOnSuccessListener {
                println("FIREBASE_STAR: Saved favorite '${rvPark.name}' for $username")
                onSuccess()
            }
            .addOnFailureListener { e ->
                println("FIREBASE_STAR_ERROR: ${e.message}")
            }
    }

    companion object {
        fun provideFactory(repository: FacilitiesRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    @Suppress("UNCHECKED_CAST")
                    return FacilitiesViewModel(repository) as T
                }
            }
    }
}