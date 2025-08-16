package com.example.rvcopilot.model

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.rvcopilot.data.Restaurant
import com.example.rvcopilot.data.Review
import com.example.rvcopilot.data.ReviewRestaurant
import com.example.rvcopilot.data.ReviewTrip
import com.example.rvcopilot.data.RvPark
import com.example.rvcopilot.data.Trips
import com.example.rvcopilot.data.TripRepository
import com.example.rvcopilot.data.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


/**
 * ----class extender: The TripViewModel inherits (extends) from
 * the ViewModel. TripViewModel is a subclass of ViewModel.
 * ----the state persists when there is a configuration change
 * The ViewModel keeps data from being lost when there is a configuration change
 * it allows the ui components to automatically change when there are state updates.
 *
 * */
/**
 * NOTE:  when a lambda expression is used in Kotlin
 * with a single parameter and this parameter is not
 * explicitly named, the compiler assigns an implicit name "it"
 * */

/**
 * The _uiState is a MutableStateFlow which holds the current ui state
 *  * .update allows modification of the current state
 * 'it' represents the current state of the _uiState.
 * '.copy' creates a new instance of a _uiState.
 * example: currentScreen = TripScreen.TripPAGE1.  changes screen to TripPAGE1.
 * ---the current state was immutable but copy() allows a new instance
 * to be created with updated values.
 *
 *
 * */

class TripViewModel(
    private val tripRepository: TripRepository
) : ViewModel() {

    /**
     * ****************************************************
     * Initializations
     * ****************************************************
     *
     * */
    //init {
     //   loadTrips()
    //}



    /**
     * setup immutables
     * val _uiState is not mutable.  It is read only
     *
     * setup mutables
     * uiState is mutable
     * */
    private val _uiState = MutableStateFlow(TripUiState())
    val uiState: StateFlow<TripUiState> = _uiState

    private val _trips = MutableStateFlow<List<Trips>>(emptyList())
    val trips: StateFlow<List<Trips>> = _trips

    private val _reviewTrip = MutableStateFlow<List<ReviewTrip>>(emptyList())
    val reviewTrip: StateFlow<List<ReviewTrip>> = _reviewTrip

    private val _selectedTrip = MutableStateFlow<Trips?>(null)
    val selectedTrip: StateFlow<Trips?> = _selectedTrip

    private val _isViewTripSelected = MutableStateFlow(false)
    val isViewTripSelected: StateFlow<Boolean> = _isViewTripSelected

    private val _restaurants = MutableStateFlow<List<Restaurant>>(emptyList())
    val restaurants: StateFlow<List<Restaurant>> = _restaurants

    private val _reviewRestaurant = MutableStateFlow<List<ReviewRestaurant>>(emptyList())
    val reviewRestaurant: StateFlow<List<ReviewRestaurant>> = _reviewRestaurant

    private val _selectedRestaurant = MutableStateFlow<Restaurant?>(null)
    val selectedRestaurant: StateFlow<Restaurant?> = _selectedRestaurant

    private val _isViewRestaurantSelected = MutableStateFlow(false)
    val isViewRestaurantSelected: StateFlow<Boolean> = _isViewRestaurantSelected

    private val _currentScreen = MutableStateFlow<TripScreen>(TripScreen.TRIP_LIST)
    val currentScreen: StateFlow<TripScreen> = _currentScreen

    private val _tripUiState = MutableStateFlow(TripUiState())
    val tripUiState: StateFlow<TripUiState> = _tripUiState

    private val _selectedRvPark = MutableStateFlow<RvPark?>(null)
    val selectedRvPark: StateFlow<RvPark?> = _selectedRvPark

    /**
     * ***********************************************
     * ***********************************************
     * Trips CRUD section
     * ***********************************************
     * ************************************************
     * */

    fun loadReviewsForTrip(tripId: String) {
        viewModelScope.launch {
            _reviewTrip.value = tripRepository.getReviewTrip(tripId)
        }
    }

    fun loadTrips(username: String) {
        viewModelScope.launch {
            tripRepository.getAllTrips(username).collect { trips ->
                _trips.value = trips
            }
        }
    }
    fun storeSelectedTrip(Trip: Trips?) {
        //println("Storing selected Trip: ${Trip.id}")
        _selectedTrip.value = Trip
    }
    fun storeSelectedRvPark(rvPark: RvPark) {
        _selectedRvPark.value = rvPark
    }

    fun insertTrip(trip: Trips) {
        println("TripViewModel: Inserting trip for user ${trip.createdBy}")
        viewModelScope.launch {
            tripRepository.insertTrip(trip)
        }
    }
    fun deleteTrip(trip: Trips) {
        println("TripViewModel deleting Trip: ${trip.destination}")
        viewModelScope.launch {
            tripRepository.deleteTrip(trip.destination)
        }
    }
    fun updateTrip(trip: Trips, username: String) {
        println("TripViewModel updateTrip function called: ${trip.destination}")
        viewModelScope.launch {
            tripRepository.updateTrip(trip.firebaseId, trip)
            println("TripViewModel: Campsite '${trip.destination}' updated successfully.")
            loadTrips(username)

        }
    }

    fun addReviewToTrip(trip: Trips, reviewText: String, user: User) {
        viewModelScope.launch {
            val reviewTrip = ReviewTrip(
                tripId = trip.firebaseId,
                userId = user.firebaseId,
                username = user.username,
                text = reviewText
            )
            tripRepository.addReviewTrip(trip.firebaseId, reviewTrip)
            println("addReviewToPark called for ${trip.destination}, ${trip}")
        }
    }
    fun deleteReviewsForTrip(tripId: String) {
        viewModelScope.launch {
            tripRepository.deleteAllReviewTrips(tripId)
            loadReviewsForTrip(tripId) // refresh the list after deleting
        }
    }

    /**
     * *******************************************************
     * *******************************************************
     * TRIP NAVIGATION section
     * *******************************************************
     * */
    fun navigateToFavoriteCampsites(trip: Trips) {
        _uiState.value = _uiState.value.copy(
            currentScreen = TripScreen.TRIP_FAVORITE_CAMPSITES,
            selectedTrip = trip
        )
    }
    fun navigateToRvParkDetailsScreen() {
        _uiState.value = _uiState.value.copy(currentScreen = TripScreen.RVPARK_DETAILS)
    }
    fun navigateToCampgroundDetails(rvPark: RvPark) {
        println("TripViewModel: navigating to: CAMPGROUND_DETAILS for ${rvPark.name}")
        storeSelectedRvPark(rvPark)
        _selectedRvPark.value = rvPark
        _uiState.value = _uiState.value.copy(currentScreen = TripScreen.TRIP_DETAILS)
        _uiState.value = _uiState.value.copy(
            currentScreen = TripScreen.CAMPGROUND_DETAILS
        )
    }
    fun navigateToEditCampsite() {
        _uiState.value = _uiState.value.copy(currentScreen = TripScreen.EDIT_CAMPSITE)
    }
    fun navigateToCampPictures() {
        _uiState.update { it.copy(currentScreen = TripScreen.CAMP_PICTURES_SCREEN) }
    }
    fun navigateToWeather() {
        _uiState.update { it.copy(currentScreen = TripScreen.WEATHER) }
    }
    fun navigateToTemperature() {
        _uiState.update { it.copy(currentScreen = TripScreen.TEMPERATURE) }
    }

    /**
     * *******************************************************
     * *******************************************************
     * Restaurants CRUD section
     * *******************************************************
     * */
    fun loadReviewsForRestaurant(restaurantId: String) {
        viewModelScope.launch {
            _reviewRestaurant.value = tripRepository.getReviewRestaurant(restaurantId)
        }
    }

    private fun loadRestaurants(username: String) {
        viewModelScope.launch {
            tripRepository.getAllRestaurants(username).collect { restaurants ->
                _restaurants.value = restaurants
            }
        }
    }
    fun storeSelectedRestaurant(Restaurant: Restaurant) {
        //println("Storing selected Restaurant: ${Restaurant.id}")
        _selectedRestaurant.value = Restaurant
    }

    fun insertRestaurant(restaurant: Restaurant) {
        println("TripViewModel: Inserting trip for user ${restaurant.createdBy}")
        viewModelScope.launch {
            tripRepository.insertRestaurant(restaurant)
        }
    }
    fun deleteRestaurant(restaurant: Restaurant) {
        println("RestaurantViewModel deleting Restaurant: ${restaurant.name}")
        viewModelScope.launch {
            tripRepository.deleteRestaurant(restaurant.name)
        }
    }
    fun updateRestaurant(restaurant: Restaurant, username: String) {
        println("RestaurantViewModel updateRestaurant function called: ${restaurant.name}")
        viewModelScope.launch {
            tripRepository.updateRestaurant(restaurant.firebaseId, restaurant)
            println("RestaurantViewModel: Campsite '${restaurant.name}' updated successfully.")
            loadRestaurants(username)

        }
    }

    fun addReviewToRestaurant(restaurant: Restaurant, reviewText: String, user: User) {
        viewModelScope.launch {
            val reviewRestaurant = ReviewRestaurant(
                restaurantId = restaurant.firebaseId,
                userId = user.firebaseId,
                username = user.username,
                text = reviewText
            )
            tripRepository.addReviewRestaurant(restaurant.firebaseId, reviewRestaurant)
            println("addReviewToRestaurant TripViewModel called for ${restaurant.name}, ${restaurant}")
        }
    }
    fun deleteReviewsForRestaurant(restaurantId: String) {
        viewModelScope.launch {
            tripRepository.deleteAllReviewRestaurants(restaurantId)
            loadReviewsForRestaurant(restaurantId) // refresh the list after deleting
        }
    }

    /**
     * *******************************************************
     * *******************************************************
     * Recreational section
     * *******************************************************
     * */

    /**
     * *******************************************************
     * *******************************************************
     * Activities section
     * *******************************************************
     * */




    /**
     * ********************************************
     * Navigation section for Trips
     * ********************************************
     * ********************************************
     * */
    fun navigateToTripList() {
        _uiState.update {
            it.copy(currentScreen = TripScreen.TRIP_LIST)
        }
    }
    fun navigateToTripDetails(trip: Trips) {
        if (_uiState.value.selectedTrip != trip || _uiState.value.currentScreen != TripScreen.TRIP_DETAILS) {
            _uiState.value = _uiState.value.copy(
                selectedTrip = trip,
                currentScreen = TripScreen.TRIP_DETAILS
            )
            println("Navigating to 1: ${_uiState.value.currentScreen} (${trip.firebaseId})")
        } else {
            println("Already on TRIP_DETAILS with same park — no UI update needed.")
        }
    }
    fun navigateToTripInfoDetails(trip: Trips) {
        if (_uiState.value.selectedTrip != trip || _uiState.value.currentScreen != TripScreen.TRIP_INFO) {
            _uiState.value = _uiState.value.copy(
                selectedTrip = trip,
                currentScreen = TripScreen.TRIP_INFO
            )
            println("Navigating to TRIP INFO screen: ${_uiState.value.currentScreen} (${trip.firebaseId})")
        } else {
            println("Already on TRIP_INFO with same park — no UI update needed.")
        }
    }

    fun navigateToTripReview() {
        _uiState.update { it.copy(currentScreen = TripScreen.TRIP_REVIEW)}
    }

    fun navigateToCreateTrip() {
        _uiState.update {it.copy(currentScreen = TripScreen.CREATE_TRIP)}
    }

    fun navigateToEditTrip() {
        _uiState.value = _uiState.value.copy(currentScreen = TripScreen.EDIT_TRIPS)
    }

    fun navigateToTripPictures(trip: Trips) {
        println("Button clicked: Navigating to Trip Pictures")
        println("navigateToTripPictures: trip = ${trip.destination}")
        _uiState.update {
            it.copy(  // copy causes the change in screen selection
                selectedTrip = trip,
                currentScreen = TripScreen.TRIP_PICTURES_SCREEN
            )
        }
    }
    fun navigateToRvParkDetails(rvPark: RvPark) {
        _selectedRvPark.value = rvPark
        _uiState.update { it.copy(currentScreen = TripScreen.RVPARK_DETAILS) }
    }

    /**
     * *******************************************************
     * *******************************************************
     * Navigation section for Restaurants
     * *******************************************************
     * */
    fun navigateToCreateRestaurant() {
        _uiState.update {it.copy(currentScreen = TripScreen.CREATE_REST)}
    }
    fun navigateToEditRestaurant() {
        _uiState.value = _uiState.value.copy(currentScreen = TripScreen.EDIT_REST)
    }
    fun navigateToRestaurantReview() {
        _uiState.update { it.copy(currentScreen = TripScreen.REST_REVIEW)}
    }
    fun navigateToRestList(username: String) {
        loadRestaurants(username)
        _uiState.update {
            it.copy(currentScreen = TripScreen.REST_LIST)
        }
    }
    fun navigateToRestDetails(restaurant: Restaurant) {
        if (_uiState.value.selectedRestaurant != restaurant || _uiState.value.currentScreen != TripScreen.TRIP_DETAILS) {
            _uiState.value = _uiState.value.copy(
                selectedRestaurant = restaurant,
                currentScreen = TripScreen.REST_DETAILS
            )
            println("Navigating to 1: ${_uiState.value.currentScreen} (${restaurant.firebaseId})")
        } else {
            println("Already on Restaurant_DETAILS with same park — no UI update needed.")
        }
    }
    fun navigateToRestInfoDetails(restaurant: Restaurant) {
        if (_uiState.value.selectedRestaurant != restaurant || _uiState.value.currentScreen != TripScreen.REST_INFO) {
            _uiState.value = _uiState.value.copy(
                selectedRestaurant = restaurant,
                currentScreen = TripScreen.REST_INFO
            )
            println("Navigating to TRIP INFO screen: ${_uiState.value.currentScreen} (${restaurant.firebaseId})")
        } else {
            println("Already on TRIP_INFO with same park — no UI update needed.")
        }
    }
    fun navigateToRestaurantPictures(restaurant: Restaurant) {
        println("Button clicked: Navigating to Restaurant Pictures")
        println("navigateToRestaurantPictures: restaurant = ${restaurant.name}")
        _uiState.update {
            it.copy(  // copy causes the change in screen selection
                selectedRestaurant = restaurant,
                currentScreen = TripScreen.RESTAURANT_PICTURES
            )
        }
    }
    /**
     * *******************************************************
     * *******************************************************
     * Navigation section for Recreation
     * *******************************************************
     * */


     /**
     * *************************************
     * Companion objects
     * *************************************
     * */


    companion object {
        fun provideFactory(repository: TripRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    @Suppress("UNCHECKED_CAST")
                    return TripViewModel(repository) as T
                }
            }
    }
}