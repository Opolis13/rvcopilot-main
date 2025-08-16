package com.example.rvcopilot.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.rvcopilot.data.Review
import com.example.rvcopilot.data.RvPark
import com.example.rvcopilot.data.RvParkRepository
import com.example.rvcopilot.data.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


/**
 * ----class extender: The RvParkViewModel inherits (extends) from
 * the ViewModel. RvParkViewModel is a subclass of ViewModel.
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
 * example: currentScreen = RvParkScreen.RvParkPAGE1.  changes screen to RvParkPAGE1.
 * ---the current state was immutable but copy() allows a new instance
 * to be created with updated values.
 *
 *
 * */

class RvParkViewModel(
    private val rvParkRepository: RvParkRepository
) : ViewModel() {

    /**
     * ****************************************************
     * Initializations
     * ****************************************************
     *
     * */
    //init {
    //    loadRvParks()
    //}



    /**
     * setup immutables
     * val _uiState is not mutable.  It is read only
     *
     * setup mutables
     * uiState is mutable
     * */
    private val _uiState = MutableStateFlow(RvParkUiState())
    val uiState: StateFlow<RvParkUiState> = _uiState

    private val _rvParks = MutableStateFlow<List<RvPark>>(emptyList())
    val rvParks: StateFlow<List<RvPark>> = _rvParks

    private val _currentScreen = MutableStateFlow(RvParkScreen.HOME)
    val currentScreen: StateFlow<RvParkScreen> = _currentScreen

    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews: StateFlow<List<Review>> = _reviews

    private val _selectedRvPark = MutableStateFlow<RvPark?>(null)
    val selectedRvPark: StateFlow<RvPark?> = _selectedRvPark

    private val _isViewRvParkSelected = MutableStateFlow(false)
    val isViewRvParkSelected: StateFlow<Boolean> = _isViewRvParkSelected

    private val _navigateToCampgroundDetails = MutableStateFlow(false)
    val navigateToCampgroundDetails: StateFlow<Boolean> = _navigateToCampgroundDetails


    /**
     * ***********************************************
     *
     * CRUD section
     *
     * ************************************************
     * */

    fun loadReviewsForPark(rvParkId: String) {
        viewModelScope.launch {
            _reviews.value = rvParkRepository.getReviews(rvParkId)
        }
    }

    fun loadRvParks(username: String) {
        println("Loading parks for user: $username")
        viewModelScope.launch {
            rvParkRepository.getAllRvParks(username).collect { parks ->
                _rvParks.value = parks
            }
        }
    }
    fun storeSelectedRvPark(RvPark: RvPark?) {
        //println("Storing selected RvPark: ${RvPark.id}")
        _selectedRvPark.value = RvPark

    }

    fun insertRvPark(rvPark: RvPark, username: String) {
        println("Inserting RV Park: $rvPark")
        viewModelScope.launch {
            val id = rvParkRepository.insertRvPark(rvPark)
            //rvParkRepository.insertRvPark(rvPark)
            println("Inserted RV Park '${rvPark.name}' with firebaseId: $id")
            loadRvParks(username)
        }
    }
    fun deleteRvPark(rvPark: RvPark) {
        println("RvParkViewModel deleting RV park: ${rvPark.name}")
        viewModelScope.launch {
            rvParkRepository.deleteRvPark(rvPark.name)
        }
    }
    fun updateRvPark(rvPark: RvPark, username: String) {
        println("RvParkViewModel updateRvPark function called: ${rvPark.name}")
        viewModelScope.launch {
            rvParkRepository.updateRvPark(rvPark.firebaseId, rvPark)
            println("RvParkViewModel: Campsite '${rvPark.name}' updated successfully.")
            loadRvParks(username)

        }
    }

    fun addReviewToPark(rvPark: RvPark, reviewText: String, user: User) {
        viewModelScope.launch {
            val review = Review(
                rvParkId = rvPark.firebaseId,
                userId = user.firebaseId,
                username = user.username,
                text = reviewText
            )
            rvParkRepository.addReview(rvPark.firebaseId, review)
            println("addReviewToPark called for ${rvPark.name}, ${rvPark}")
        }
    }
    fun deleteReviewsForPark(rvParkId: String) {
        viewModelScope.launch {
            rvParkRepository.deleteAllReviews(rvParkId)
            loadReviewsForPark(rvParkId) // refresh the list after deleting
        }
    }

    /**
     * ********************************************
     * Navigation section
     *
     * ********************************************
     * */

    fun navigateToCreateCampsites() {
        _uiState.update {
            it.copy(currentScreen = RvParkScreen.CREATE_CAMPSITES)
        }
    }

    fun navigateToCreateTrip() {
        _uiState.update {
            it.copy(currentScreen = RvParkScreen.TRIPS_SECTION)
        }
    }
    fun navigateToUserBio() {
        _uiState.update {
            it.copy(currentScreen = RvParkScreen.USER_BIO)
        }
    }
    fun navigateToMap() {
        _uiState.update {
            it.copy(currentScreen = RvParkScreen.LOCATION_MAP)
        }
    }
    fun navigateToFavoriteSites() {
        _uiState.update {
            it.copy(currentScreen = RvParkScreen.FAVORITE_SITES)
        }
    }

    fun navigateToHome() {
        _uiState.update {
            it.copy(currentScreen = RvParkScreen.HOME)
        }
    }
    fun navigateToFront() {
        _uiState.value = _uiState.value.copy(currentScreen = RvParkScreen.FRONT_PAGE)
    }

    fun navigateToPage1() {
        _uiState.update {
            it.copy(currentScreen = RvParkScreen.RVPAGE1)
        }
    }

    fun navigateToRvParkGrid() {
        _uiState.value = _uiState.value.copy(currentScreen = RvParkScreen.RVPARK_GRID)
        println("Navigating to: RVPARK_GRID")
    }




    fun quitGame() {
        _uiState.update {
            it.copy(
                currentScreen = RvParkScreen.HOME
            )
        }
    }

    fun navigateToCreateAccount() {
        _uiState.update {
            it.copy(currentScreen = RvParkScreen.CREATE_ACCOUNT)
        }
    }

    fun navigateToTripsSection() {
        _uiState.update { it.copy(currentScreen = RvParkScreen.TRIPS_SECTION) }
    }


    /**
     * *************************************
     * Companion objects
     * *************************************
     * */


    companion object {
        fun provideFactory(repository: RvParkRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    @Suppress("UNCHECKED_CAST")
                    return RvParkViewModel(repository) as T
                }
            }
    }
}