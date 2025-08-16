package com.example.rvcopilot.data


import com.example.rvcopilot.R



/** Note1:
 *  data class that represents the RV Park type
 * This acts like a schema for the RV Park class.
 * It defines the struct and attributes of the app.
 *
 */
data class Restaurant(
    val firebaseId: String = "",
    val name: String = "",
    val address: String = "",
    val phone: String = "",
    val email: String = "",
    val type: String = "",
    val cellular: String = "",
    val wifi: String = "",
    val foods: String = "",
    var mailbox: RvParkType = RvParkType.RvParkPage1,
    val imageName: String = "p6140016",
    val createdBy: String = ""
)