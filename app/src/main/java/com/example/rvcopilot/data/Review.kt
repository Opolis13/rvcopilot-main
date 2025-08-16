package com.example.rvcopilot.data



/** Note:
 *  data class that represents the RV Park review
 * This acts like a schema for the RV Park class.
 * It defines the struct and attributes of the app.
 *
 */
data class Review(

    val rvParkId: String = "",
    val userId: String = "",
    val username: String = "",
    val text: String = "",
    val timestamp: Long = System.currentTimeMillis()


)