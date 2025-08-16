package com.example.rvcopilot.model

import com.example.rvcopilot.data.Restaurant
import com.example.rvcopilot.data.Trips
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


//  a data class holds the data
data class TripUiState(
    val currentScreen: TripScreen = TripScreen.TRIP_LIST,
    val trips: List<Trips> = emptyList(),
    val selectedTrip: Trips? = null,
    val selectedRestaurant: Restaurant? = null,
    val isLoading: Boolean = false,

    )



enum class TripScreen {
    CREATE_TRIP,
    TRIP_INFO,
    TRIP_LIST,
    TRIP_DETAILS,
    EDIT_TRIPS,
    TRIP_PICTURES_SCREEN,
    CAMP_PICTURES_SCREEN,
    TRIP_REVIEW,
    REST_LIST,
    CREATE_REST,
    EDIT_REST,
    REST_DETAILS,
    REST_INFO,
    REST_REVIEW,
    RESTAURANT_PICTURES,
    RVPARK_DETAILS,
    TRIP_FAVORITE_CAMPSITES,
    CAMPGROUND_DETAILS,
    EDIT_CAMPSITE,
    TEMPERATURE,
    WEATHER,
    FAVORITE_SITES



}