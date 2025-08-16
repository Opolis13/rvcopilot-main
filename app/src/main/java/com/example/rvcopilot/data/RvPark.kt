package com.example.rvcopilot.data


import com.example.rvcopilot.R



/** Note1:
 *  data class that represents the RV Park type
 * This acts like a schema for the RV Park class.
 * It defines the struct and attributes of the app.
 *
 */
data class RvPark(
    val firebaseId: String = "",
    val name: String = "",
    val address: String = "",
    val phone: String = "",
    val email: String = "",
    val services: String = "",
    val type: String = "",
    val power: String = "",
    val pad: String = "",
    val pets: String = "",
    val cellular: String = "",
    val wifi: String = "",
    val cable: String = "",
    val amenity: String = "",
    val createdBy: String = "",
    var mailbox: RvParkType = RvParkType.RvParkPage1,
    val imageName: String = "avatar_weather1",
    val coordinates: Coordinates = Coordinates()

)