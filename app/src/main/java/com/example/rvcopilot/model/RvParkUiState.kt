package com.example.rvcopilot.model

import com.example.rvcopilot.data.RvPark
import com.example.rvcopilot.data.RvParkType


//  a data class holds the data
data class RvParkUiState(
    val currentScreen: RvParkScreen = RvParkScreen.FRONT_PAGE,   // start at FrontPage to get permissions
    //val currentScreen: RvParkScreen = RvParkScreen.HOME, // for development only
    val isShowingFrontPage: Boolean = true,
    val currentCategory: String? = null,
    val RvParkPage1: List<RvPark> = emptyList(),
    val RvParkPage2: List<RvPark> = emptyList(),
    val currentRvPark1: RvParkType = RvParkType.RvParkPage1,
    val currentRvPark2: RvParkType = RvParkType.RvParkPage2,
    val selectedRvPark: RvPark? = null,
    val isShowingHomepage: Boolean = true,
    val RvParksByType: List<RvPark> = emptyList(),
    val foundCorrectLocation: Boolean = false,
    val isLoading: Boolean = false,

    )


enum class RvParkScreen {
    FRONT_PAGE,
    HOME,
    RVPAGE1,
    RVPARK_DETAILS,
    RVPARK_GRID,
    TEMPERATURE,
    WEATHER,
    CREATE_ACCOUNT,
    USER_BIO,
    TRIPS_SECTION,
    CREATE_TRIP,
    LOCATION_MAP,
    CREATE_CAMPSITES,
    FAVORITE_SITES,
    CAMPGROUND_DETAILS,
    PICTURES_SCREEN,
    SITE_REVIEW


}