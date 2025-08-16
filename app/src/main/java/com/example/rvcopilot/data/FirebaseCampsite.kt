package com.example.rvcopilot.data

data class FirebaseCampsite(
    val name: String = "",
    val address: String = "",
    val phone: String = "",
    val state: String = "",
    val zipCode: String = "",
    val wifi: Boolean = false,
    val pets: Boolean = false,
    val rating: Double = 0.0,
    val numReviews: Int = 0,
    val amenities: List<String> = emptyList(),
    val hookups: List<String> = emptyList(),
    val coordinates: Coordinates = Coordinates()
)

data class Coordinates(
    val lat: Double = 0.0,
    val lng: Double = 0.0
)
