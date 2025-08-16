package com.example.rvcopilot.data

/** Note:
 *  data class that represents the Restaurant review
 * This acts like a schema for the Restaurant class.
 * It defines the struct and attributes of the app.
 *
 */
data class ReviewRestaurant(

    val restaurantId: String = "",
    val userId: String = "",
    val username: String = "",
    val text: String = "",
    val timestamp: Long = System.currentTimeMillis()


)