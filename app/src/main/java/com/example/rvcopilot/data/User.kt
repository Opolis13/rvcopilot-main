package com.example.rvcopilot.data

//import androidx.room.ColumnInfo
//import androidx.room.Entity
//import androidx.room.PrimaryKey

/** Note:
 *  data class that represents the User type
 * This acts like a schema for the User class.
 * It defines the struct and attributes of the app.
 *
 */


data class User(
    val firebaseId: String = "",
    val username: String = "",
    val password: String = "",
    val userBio: String = "",
    val reviewBio: String = ""
)