package com.example.rvcopilot.data

import android.os.Parcel
import android.os.Parcelable
import com.example.rvcopilot.R



/** Note1:
 *  data class that represents the Trip creation page
 * This acts like a schema for the Trip class.
 * It defines the struct and attributes of the app.
 *
 */
data class Trips(
    val firebaseId: String = "",
    val destination: String = "",
    val destaddress: String = "",
    val recreation: String = "",
    val activities: String = "",
    val restaurants: String = "",
    val imageName: String = "p6140016",
    val createdBy: String = ""
)

