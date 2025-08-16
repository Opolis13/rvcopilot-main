package com.example.rvcopilot.model

import android.annotation.SuppressLint
import android.app.Application
import android.location.Geocoder
import android.location.Location
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.rvcopilot.data.FacilitiesRepository
import com.example.rvcopilot.data.FirebaseCampsite
import com.example.rvcopilot.ui.tools.HaversineApp
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.streetview.StreetViewCameraPositionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LocationViewModel(
    application: Application,
    private val facilitiesRepository: FacilitiesRepository
) : AndroidViewModel(application) {
    private val context = application.applicationContext

    private val _location = MutableStateFlow<LatLng?>(null)
    val location: StateFlow<LatLng?> = _location

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(application)

    val firebaseCampsites: StateFlow<List<FirebaseCampsite>> = facilitiesRepository.firebaseCampsitesStateFlow

    private val _nearbyCampsites = MutableStateFlow<List<FirebaseCampsite>>(emptyList())
    val nearbyCampsites: StateFlow<List<FirebaseCampsite>> = _nearbyCampsites

    @SuppressLint("MissingPermission")
    fun getUserLocation() {
        val permission = android.Manifest.permission.ACCESS_FINE_LOCATION
        val permGranted =
            androidx.core.content.ContextCompat.checkSelfPermission(context, permission)
        if (permGranted == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { loc ->
                    if (loc != null) {
                        _location.value = LatLng(loc.latitude, loc.longitude)
                        println("LOCATION: Got user location: ${loc.latitude}, ${loc.longitude}")
                    } else {
                        println("LOCATION: Last location was null.  Trying to get current location")
                        fusedLocationClient
                            .getCurrentLocation(
                                com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY,
                                null
                            )
                            .addOnSuccessListener { currentLoc ->
                                if (currentLoc != null) {
                                    _location.value =
                                        LatLng(currentLoc.latitude, currentLoc.longitude)
                                    println("LOCATION: Got user location (current): ${currentLoc.latitude}, ${currentLoc.longitude}")
                                } else {
                                    println("LOCATION: getCurrentLocation() also returned null.")
                                }
                            }
                            .addOnFailureListener { err ->
                                println("LOCATION: Failed to get CURRENT location: ${err.localizedMessage}")
                            }
                    }
                }
                .addOnFailureListener { e ->
                    println("LOCATION: Failed to get LAST location: ${e.localizedMessage}")
                }
        } else {
            println("LOCATION: Location permission not granted. skipped location request.")
        }
    }

    fun searchAndMoveCamera(
        query: String,
        cameraPositionState: CameraPositionState
    ) {
        viewModelScope.launch {
            try {
                val geocoder = Geocoder(context)
                val addresses = geocoder.getFromLocationName(query, 1)

                if (!addresses.isNullOrEmpty()) {
                    val searchLocation = addresses[0]
                    val latLng = LatLng(searchLocation.latitude, searchLocation.longitude)
                    cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(latLng, 8f))
                }
            } catch (e: Exception) {
                println("LocationViewModel MapSearch: Geocoding failed: ${e.message}")
            }
        }
    }

    // 50 mile radius in km = 80.46
    // Sorts by distance
    fun filterNearbyCampsites(
        center: LatLng,
        radius: Double = 80.46) {
        val nearby = firebaseCampsites.value.filter { campsite ->
            HaversineApp.calculateDistance(
                center.latitude, center.longitude,
                campsite.coordinates.lat, campsite.coordinates.lng) <= radius
        }.sortedBy { campsite ->
            HaversineApp.calculateDistance(center.latitude, center.longitude,
                campsite.coordinates.lat, campsite.coordinates.lng)
        }
        _nearbyCampsites.value = nearby
    }

    companion object {
        fun provideFactory(application: Application, repository: FacilitiesRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    @Suppress("UNCHECKED_CAST")
                    return LocationViewModel(application, repository) as T
                }
            }
    }
}