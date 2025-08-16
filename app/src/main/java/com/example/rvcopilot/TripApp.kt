package com.example.rvcopilot

import android.widget.Toast
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rvcopilot.data.PictureRepository
import com.example.rvcopilot.data.Restaurant
import com.example.rvcopilot.data.PictureRepository.PictureCategory
import com.example.rvcopilot.model.UserViewModel
import com.example.rvcopilot.data.UserRepository
import com.example.rvcopilot.data.TripRepository
import com.example.rvcopilot.model.PictureViewModel
import com.example.rvcopilot.model.RvParkViewModel
import com.example.rvcopilot.model.TripScreen
import com.example.rvcopilot.model.TripViewModel
import com.example.rvcopilot.ui.details.PicturesScreen
import com.example.rvcopilot.ui.details.trip.TripDetailsScreen
import com.example.rvcopilot.ui.trips.CreateTripScreen
import com.example.rvcopilot.ui.trips.EditTripScreen
import com.example.rvcopilot.ui.trips.ListTripsScreen
import com.example.rvcopilot.ui.trips.restaurants.CreateRestaurantScreen
import com.example.rvcopilot.ui.trips.restaurants.ListRestaurantsScreen
import com.example.rvcopilot.data.RvParkRepository
import androidx.compose.runtime.getValue
import com.example.rvcopilot.data.RvPark
import com.example.rvcopilot.data.User
import com.example.rvcopilot.model.FacilitiesViewModel
import com.example.rvcopilot.model.LocationViewModel
import com.example.rvcopilot.model.RvParkScreen
import com.example.rvcopilot.ui.campsites.EditCampsiteScreen
import com.example.rvcopilot.ui.campsites.ListFavoriteSitesScreen
import com.example.rvcopilot.ui.details.rvpark.CampgroundDetailsScreen
import com.example.rvcopilot.ui.details.rvpark.RvParkDetailsScreen
import com.example.rvcopilot.ui.details.rvpark.RvParkGridScreen
import com.example.rvcopilot.ui.details.trip.RestaurantDetailsScreen
import com.example.rvcopilot.ui.details.trip.RestaurantInfoDetailsScreen
import com.example.rvcopilot.ui.details.trip.TripFavoriteCampsitesScreen
import com.example.rvcopilot.ui.details.trip.TripInfoDetailsScreen
import com.example.rvcopilot.ui.tools.TemperatureScreen
import com.example.rvcopilot.ui.tools.WeatherScreen
import com.example.rvcopilot.ui.trips.EditRestaurantScreen
import com.example.rvcopilot.ui.trips.ReviewTripsScreen
import com.example.rvcopilot.ui.trips.restaurants.RestaurantPicturesScreen
import com.example.rvcopilot.ui.trips.restaurants.ReviewRestaurantsScreen
import com.google.firebase.firestore.FirebaseFirestore



/**
 * ----viewModel exposes the state to the ui,
 * it persists the state through config changes.
 * viewModel handles user actions and updates the state
 * the updated state is fed back to the ui to render.
 * Note: this viewModel works with 'CluesViewModel', 'CluesUiState' and 'CluesApp'
 * (which contain the use case class) to get data and transform
 * it into the ui state.  (data class --> viewModel --> ui elements)
 * https://developer.android.com/jetpack/androidx/releases/collection?hl=en
 * https://developer.android.com/topic/architecture/ui-layer
 *
 * --state variables
 * 'by' is used to delegate the get and set operations to state object
 * 'remember' ensures that the state value persists across recompositions
 * 'false' is used to make sure that all the buttons start out with an initial state of not selected.
 *
 * --coroutines
 * this is related to the Composable lifecycle.
 * it is used to delay the action of the button so that the highlighting is visible
 *
 * --context
 * an interface that provides access to application-specific
 * resources and system services.
 * for example: manage ui related components dynamically,
 * accessing string resources.
 * - stringResource() requires 'context'
 * - openUrl(context, url) uses the 'Intent.ACTION_VIEW'
 * to launch on external browser.
 */

/**
 * @Composable marks this function as a
 * Jetpack Compose User Interface function
 *
 * some functions don't allow try-catch within the composable because
 *  * there may be a deferred function such as images (doesn't run immediately)
 * */

/**
 * - lambdas: anonymous function.  block of code that acts like a variable
 *  * () -> Unit:  Lambda expression.  ()= function does not accept arguments
 *  *  * Unit = 'Void' expression. Function does not return anything.
 * */
/**
 * Scaffold accepts the topBar composable as a parameter
 * topBar needs Scaffold to run
 * Scaffold also accepts different parts of the user interface ui,
 * in this case topBar with a title, onBackClick
 * see this url for an example:
 * https://developer.android.com/develop/ui/compose/components/scaffold
 *
 *
 * --- let scope function: executes the block only if the object is non-null
 *     https://kotlinlang.org/docs/null-safety.html#safe-call-operator
 *     safe call operator ?: example from nullability notes: var number: Int? = 10
 *     ----selectedClue?.let,  executes if not a null
 *
 *
 *
 * */

@Composable
fun TripApp(
    onExitTripApp: () -> Unit
)  // app controller for Trips
{

    // initialize the viewModel instance
    val context = LocalContext.current
    val firestore = remember { FirebaseFirestore.getInstance() }
    val db = FirebaseFirestore.getInstance()
    val userRepository = remember { UserRepository(firestore) }
    val rvParkRepository = remember { RvParkRepository(firestore) }
    val tripRepository = remember { TripRepository(firestore) }
    val pictureRepository = remember { PictureRepository(firestore) }

    val userViewModel: UserViewModel = viewModel(
        factory = UserViewModel.provideFactory(userRepository)
    )
    val rvParkViewModel: RvParkViewModel = viewModel(
        factory = RvParkViewModel.provideFactory(rvParkRepository)
    )


    /**
     *  **************************************************************
     * viewModels with companion objects
     * must have this definition or the system will crash
     * * *************************************************************
     * */
    val pictureViewModel: PictureViewModel = viewModel(
        factory = PictureViewModel.provideFactory(pictureRepository)
    )

    val tripViewModel: TripViewModel = viewModel(
        factory = TripViewModel.provideFactory(tripRepository)
    )


    // tripUiState now defined as a 'type' TripUiState not as a State<TripUiState>
    // 'by' lets Compose recompose when the state changes
    val tripUiState by tripViewModel.uiState.collectAsState()

    val currentScreen by tripViewModel.currentScreen.collectAsState()

    // print current screen before starting the "when" if block
    println("Current screen: ${tripUiState.currentScreen}")
    /**
     * *******************************************************
     * *******************************************************
     * TRIPS section
     * *******************************************************
     * */
    when (tripUiState.currentScreen) {
        TripScreen.TRIP_LIST -> {
            println("Navigating to: TRIP_LIST")
            val currentUser by userViewModel.currentUser.collectAsState()
            currentUser?.let { // only execute this block when currentUser is not null
                ListTripsScreen(
                    currentUser = it,  // 'it' is the lambda referred to be let which is 'currentUser'
                    viewModel = tripViewModel,
                    onBackClick = { onExitTripApp() },
                    onTripClick = { clickedTrip ->
                        Toast.makeText(
                            context,
                            "Opening ${clickedTrip.destination}",
                            Toast.LENGTH_SHORT
                        ).show()
                        println("Trip clicked: ${clickedTrip.destination}")
                        tripViewModel.storeSelectedTrip(clickedTrip)
                        tripViewModel.navigateToTripDetails(clickedTrip)
                    }
                )
            } ?: Text("")
        }


        TripScreen.TRIP_DETAILS -> {
            println("inside TripApp TRIP_DETAILS")
            println("Navigating to: TRIP_DETAILS")
            val selectedTrip = tripUiState.selectedTrip
            val currentUser by userViewModel.currentUser.collectAsState()
            currentUser?.let {// only execute this block when currentUser is not null
                if (selectedTrip != null) {
                    TripDetailsScreen(
                        currentUser = it,  // 'it' is the lambda referred to be let which is 'currentUser'
                        viewModel = tripViewModel,
                        selectedTrip = selectedTrip,
                        rvParkViewModel = rvParkViewModel,
                        onBackClick = { tripViewModel.navigateToTripList() },
                        onTripEditClick = { tripViewModel.navigateToEditTrip() }
                    )
                } else {
                    println("Warning: selectedRvPark is null. Navigating back to PAGE1.")
                    tripViewModel.navigateToTripList()
                }
            }
        }

        TripScreen.TRIP_INFO -> {
            println("inside TripApp TRIP_INFO")
            println("Navigating to: TRIP_INFO")
            val selectedTrip = tripUiState.selectedTrip
            if (selectedTrip != null) {
                TripInfoDetailsScreen(
                    viewModel = tripViewModel,
                    pictureViewModel = pictureViewModel,
                    onBackClick = { tripViewModel.navigateToTripDetails(selectedTrip) },
                    onLocationClick = { tripViewModel.navigateToTripInfoDetails(selectedTrip) }
                )
            } else {
                println("Warning: selectedRvPark is null. Navigating back to PAGE1.")
                tripViewModel.navigateToTripList()
            }
        }

        TripScreen.TRIP_PICTURES_SCREEN -> {
            val tripLabel = tripUiState.selectedTrip?.destination ?: "Unknown Trip"
            PicturesScreen(
                category = PictureRepository.PictureCategory.TRIP,
                viewModel = pictureViewModel,
                tripViewModel = tripViewModel,
                tripLabel = tripLabel,
                onBackClick = {
                    tripViewModel.navigateToTripDetails(
                        tripUiState.selectedTrip!!
                    )
                }
            )
        }


        TripScreen.CREATE_TRIP -> {
            val currentUser by userViewModel.currentUser.collectAsState()
            currentUser?.let { // only execute this block when currentUser is not null
                CreateTripScreen(
                    currentUser = it,  // 'it' is the lambda referred to be let which is 'currentUser'
                    viewModel = tripViewModel,
                    onBackClick = { tripViewModel.navigateToTripList() },
                )
            } ?: Text("Loading user $currentUser")
        }

        TripScreen.TRIP_REVIEW -> {
            tripUiState.selectedTrip?.let { selectedTrip ->
                val currentUser = userViewModel.currentUser.value
                if (currentUser != null) {
                    ReviewTripsScreen(
                        selectedTrip = selectedTrip,
                        currentUser = currentUser,
                        viewModel = tripViewModel,
                        onSaveClick = { reviewText ->
                            println("Review saved: $reviewText")
                            tripViewModel.addReviewToTrip(
                                selectedTrip,
                                reviewText,
                                currentUser
                            )
                        },
                        onBackClick = { tripViewModel.navigateToTripDetails(selectedTrip) }
                    )
                }
            }
        }

        TripScreen.EDIT_TRIPS -> {
            val currentUser by userViewModel.currentUser.collectAsState()
            tripUiState.selectedTrip?.let { selectedTrip ->
                currentUser?.let {  // only execute this block when currentUser is not null
                    EditTripScreen(
                        currentUser = it,  // 'it' is the lambda referred to be let which is 'currentUser'
                        tripViewModel = tripViewModel,
                        pictureViewModel = pictureViewModel,
                        trip = selectedTrip,
                        username = it.username,
                        onBackClick = { tripViewModel.navigateToTripDetails(selectedTrip) }
                    )
                } ?: Text("")
            }
        }
        /**
         * *******************************************************
         * *******************************************************
         * CAMPSITES section
         * *******************************************************
         * */

        TripScreen.CAMP_PICTURES_SCREEN -> {
            val selectedRvPark by rvParkViewModel.selectedRvPark.collectAsState()
            val campgroundLabel = selectedRvPark?.name ?: "Unknown Campground"

            selectedRvPark?.let { rvPark ->
                PicturesScreen(
                    category = PictureRepository.PictureCategory.CAMPSITE,
                    viewModel = pictureViewModel,
                    tripViewModel = tripViewModel,
                    tripLabel = campgroundLabel,
                    onBackClick = {
                        tripViewModel.navigateToRvParkDetailsScreen()
                    }
                )
            } ?: run {
                println("Warning: selectedRvPark is null. Navigating back to Trip Details.")
                tripViewModel.navigateToTripDetails(tripViewModel.uiState.value.selectedTrip!!)
            }
        }

        TripScreen.RVPARK_DETAILS -> {
            val selectedRvPark by rvParkViewModel.selectedRvPark.collectAsState()
            selectedRvPark?.let { rvPark ->
                RvParkDetailsScreen(
                    viewModel = rvParkViewModel,
                    tripViewModel = tripViewModel,
                    selectedRvPark = rvPark,
                    onBackClick = {
                        tripViewModel.navigateToFavoriteCampsites(tripUiState.selectedTrip!!)
                    },
                    onEditClick = {
                        rvParkViewModel.storeSelectedRvPark(it)
                        tripViewModel.navigateToEditCampsite()
                    }
                )
            }
        }

        TripScreen.EDIT_CAMPSITE -> {
            val selectedRvPark by rvParkViewModel.selectedRvPark.collectAsState()
            val currentUser by userViewModel.currentUser.collectAsState()
            selectedRvPark?.let { rvPark ->
                currentUser?.let { user ->
                EditCampsiteScreen(
                    rvParkViewModel = rvParkViewModel,
                    pictureViewModel = pictureViewModel,
                    rvPark = rvPark,
                    username = user.username,
                    onBackClick = {
                        tripViewModel.navigateToRvParkDetailsScreen()
                    }
                )
            } ?: run {
                println("TripApp: Warning: currentUser is null")
                tripViewModel.navigateToRvParkDetailsScreen()
            }
        } ?: run {
                println("TripApp: Warning: selectedRvPark is null")
                tripViewModel.navigateToRvParkDetailsScreen()
            }
    }

        TripScreen.TRIP_FAVORITE_CAMPSITES -> {
            println("Navigating to: Trip Favorite Campsites SCREEN")
            val facilitiesViewModel: FacilitiesViewModel = viewModel()
            val currentUser by userViewModel.currentUser.collectAsState()
            val selectedRvPark by rvParkViewModel.selectedRvPark.collectAsState()

            currentUser?.let { user -> // currentUser could be null
                    TripFavoriteCampsitesScreen(
                        currentUser = user,
                        rvParkViewModel = rvParkViewModel,
                        onBackClick = { tripViewModel.navigateToTripDetails(tripUiState.selectedTrip!!) },
                        onRvParkClick = { clickedRvPark ->
                            println("Clicked favorited RV Park: ${clickedRvPark.name}")
                            rvParkViewModel.storeSelectedRvPark(clickedRvPark)
                            tripViewModel.navigateToRvParkDetailsScreen()
                        }
                    )
                } ?: println("from TripScreen, currentUser is null")
        }
        TripScreen.CAMPGROUND_DETAILS -> {
            println("TripApp when statement - Navigating to: CAMPGROUND_DETAILS")
            println("TripApp when statement: re-rendering the page")
            //val selectedRvPark by rvParkViewModel.selectedRvPark.collectAsState()
            //val selectedRvPark = tripViewModel.selectedRvPark.value
            val selectedRvPark by tripViewModel.selectedRvPark.collectAsState()
            //if (selectedRvPark != null) {
            //    println("Tripapp: selectedRvPark: ${selectedRvPark!!.name}")
            //} else {
            //    println("Tripapp: selectedRvPark is NULL")
            //}
            selectedRvPark?.let { rvPark ->
                println("TripApp when statement: Rendering CampgroundDetailsScreen for ${rvPark.name}")
                CampgroundDetailsScreen(
                    selectedRvPark = rvPark,
                    viewModel = rvParkViewModel,
                    pictureViewModel = pictureViewModel,
                    onLocationClick = { tripViewModel.navigateToWeather() },
                    onBackClick = {
                        println(" back out of campground details")
                        tripViewModel.navigateToRvParkDetailsScreen()}
                )
            } ?: run {
                println("TripApp when statement: selectedRvPark is null in CAMPGROUND_DETAILS, navigating back")
                tripViewModel.navigateToTripDetails(tripViewModel.uiState.value.selectedTrip!!)
            }
        }
        TripScreen.TEMPERATURE -> {  // Calculator screen
            println("TripApp when statement: Rendering temperature details")
            val selectedRvPark by rvParkViewModel.selectedRvPark.collectAsState()

            selectedRvPark?.let { rvPark ->
                TemperatureScreen(
                    context = context,
                    onBackClick = { tripViewModel.navigateToRvParkDetails(rvPark) }
                )
            } ?: run {
                println("Warning: selectedRvPark is null. Navigating back to Trip Details.")
                tripViewModel.navigateToTripDetails(tripViewModel.uiState.value.selectedTrip!!)
            }
        }
        TripScreen.WEATHER -> {
            println("TripApp: RvParkScreen.Weather")
            val selectedRvPark by rvParkViewModel.selectedRvPark.collectAsState()
            selectedRvPark?.let { rvPark ->
            //rvParkUiState.selectedRvPark?.let { selectedRvPark ->
                WeatherScreen(
                    viewModel = rvParkViewModel,
                    context = context,
                    onNavigateToTemperature = {
                        tripViewModel.navigateToTemperature()
                    },
                    onBackClick = { tripViewModel.navigateToRvParkDetails(rvPark) }
                )
            } ?: run {
                println("Warning: selectedRvPark is null. Returning to Trip Details.")
                tripViewModel.navigateToTripDetails(tripViewModel.uiState.value.selectedTrip!!)
            }
        }


        TripScreen.FAVORITE_SITES -> {
            val currentUser by userViewModel.currentUser.collectAsState()
            currentUser?.let { user ->
                ListFavoriteSitesScreen(
                    currentUser = user,
                    viewModel = rvParkViewModel,
                    onRvParkClick = { clickedRvPark ->
                        rvParkViewModel.storeSelectedRvPark(clickedRvPark)
                        tripViewModel.navigateToRvParkDetailsScreen()
                                    },
                    onBackClick = { rvParkViewModel.navigateToHome() }
                )
            } ?: Text("")
        }
        /**
         * *******************************************************
         * *******************************************************
         * RESTAURANTS section
         * *******************************************************
         * */
        TripScreen.REST_LIST -> {
            ListRestaurantsScreen(
                viewModel = tripViewModel,
                onBackClick = { tripViewModel.navigateToTripDetails(tripUiState.selectedTrip!!) },
                onRestaurantClick = { restaurant ->
                    println("Restaurant clicked: ${restaurant.name}")
                    tripViewModel.storeSelectedRestaurant(restaurant)
                    tripViewModel.navigateToRestDetails(restaurant)

                }
            )
        }

        TripScreen.CREATE_REST -> {
            val currentUser by userViewModel.currentUser.collectAsState()
            currentUser?.let {  // only execute this block when currentUser is not null
                CreateRestaurantScreen(
                    currentUser = it,  // 'it' is the lambda referred to be let which is 'currentUser'
                    viewModel = tripViewModel,
                    onBackClick = { tripViewModel.navigateToRestList(it.username) }
                )
            } ?: Text("")
        }

        TripScreen.EDIT_REST -> {
            val selectedRestaurant = tripUiState.selectedRestaurant
            val currentUser by userViewModel.currentUser.collectAsState()

            if (selectedRestaurant != null && currentUser != null) {
                EditRestaurantScreen(
                    currentUser = currentUser!!,
                    tripViewModel = tripViewModel,
                    trip = tripUiState.selectedTrip!!,
                    restaurant = selectedRestaurant,
                    pictureViewModel = pictureViewModel,
                    onBackClick = { tripViewModel.navigateToRestDetails(selectedRestaurant) }
                )
            } else {
                println("Warning: selectedRestaurant is null. Navigating back to Rest list.")
                tripViewModel.navigateToRestList(currentUser?.username ?: "")
            }
        }

        TripScreen.REST_REVIEW -> {
            tripUiState.selectedTrip?.let { selectedTrip ->
                val currentUser = userViewModel.currentUser.value
                val selectedRestaurant = tripUiState.selectedRestaurant

                if (currentUser != null && selectedRestaurant != null) {
                    ReviewRestaurantsScreen(
                        selectedRestaurant = selectedRestaurant,
                        currentUser = currentUser,
                        viewModel = tripViewModel,
                        onSaveClick = { reviewText ->
                            println("TripApp Restaurant Review saved: $reviewText")
                            tripViewModel.addReviewToRestaurant(
                                selectedRestaurant,
                                reviewText,
                                currentUser
                            )
                        },
                        onBackClick = { tripViewModel.navigateToRestDetails(selectedRestaurant) }
                    )
                }
            }

        }

        TripScreen.REST_DETAILS -> {
            println("Navigating to: REST_DETAILS")
            val selectedRestaurant = tripUiState.selectedRestaurant
            val currentUser by userViewModel.currentUser.collectAsState()
            currentUser?.let { // only execute this block when currentUser is not null
                if (selectedRestaurant != null) {
                    RestaurantDetailsScreen(
                        viewModel = tripViewModel,
                        rvParkViewModel = rvParkViewModel,
                        selectedRestaurant = selectedRestaurant,
                        // 'it' is the lambda referred to be let which is 'currentUser'
                        onBackClick = { tripViewModel.navigateToRestList(it.username) },
                        onRestaurantEditClick = { tripViewModel.navigateToEditTrip() }
                    )
                } else {
                    println("Warning: selectedRestaurant is null. Navigating back to Rest list.")
                    tripViewModel.navigateToRestList(it.username)
                }
            }
        }

        TripScreen.REST_INFO -> {
            val selectedRestaurant = tripUiState.selectedRestaurant
            val currentUser by userViewModel.currentUser.collectAsState()
            currentUser?.let { // only execute this block when currentUser is not null
                if (selectedRestaurant != null) {
                    RestaurantInfoDetailsScreen(
                        viewModel = tripViewModel,
                        // 'it' is the lambda referred to be let which is 'currentUser'
                        onBackClick = { tripViewModel.navigateToRestList(it.username) },
                        onLocationClick = { tripViewModel.navigateToRestDetails(selectedRestaurant) }
                    )
                } else {
                    println("Warning: selectedRestaurant is null. Navigating back to Rest list.")
                    tripViewModel.navigateToRestList(it.username)
                }
            }
        }

        TripScreen.RESTAURANT_PICTURES -> {
            println("TripApp: Entered RESTAURANT_PICTURES branch")
            val selectedRestaurant = tripUiState.selectedRestaurant
            println("TripApp:  inside the TripsScreen.RESTAURANT_PICTURES after val selectedRestaurant")
            selectedRestaurant?.let { restaurant ->
                // 'restaurant' is the lambda referred to be let which is 'restaurant'
                         RestaurantPicturesScreen(
                    restaurant = restaurant,
                    pictureViewModel = pictureViewModel,
                    category = PictureCategory.TRIP,
                    tripViewModel = tripViewModel,
                    onBackClick = { tripViewModel.navigateToRestDetails(restaurant) }
                )
            }
        }
        /**
         * *******************************************************
         * *******************************************************
         * Recreation section
         * *******************************************************
         * */


        else -> {
            println("Unknown screen, navigating to Trip HOME")
            val currentUser by userViewModel.currentUser.collectAsState()
            currentUser?.let { // only execute this block when currentUser is not null
                // 'it' is the lambda referred to be let which is 'currentUser'
                ListTripsScreen(
                    currentUser = it,
                    viewModel = tripViewModel,
                    onBackClick = { onExitTripApp() },
                    onTripClick = { clickedTrip ->
                        Toast.makeText(
                            context,
                            "Opening ${clickedTrip.destination}",
                            Toast.LENGTH_SHORT
                        ).show()
                        println("Trip clicked: ${clickedTrip.destination}")
                        tripViewModel.storeSelectedTrip(clickedTrip)
                        tripViewModel.navigateToTripDetails(clickedTrip)
                    }
                )
            } ?: Text("")
        }
    }
}