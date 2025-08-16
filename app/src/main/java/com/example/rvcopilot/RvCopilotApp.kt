package com.example.rvcopilot

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rvcopilot.data.PictureRepository
import com.example.rvcopilot.data.PictureRepository.PictureCategory
import com.example.rvcopilot.model.UserViewModel
import com.example.rvcopilot.data.UserRepository
import com.example.rvcopilot.data.RvParkRepository
import com.example.rvcopilot.data.TripRepository
import com.example.rvcopilot.model.PictureViewModel
import com.example.rvcopilot.model.RvParkScreen
import com.example.rvcopilot.model.RvParkViewModel
import com.example.rvcopilot.model.TripViewModel
import com.example.rvcopilot.ui.details.rvpark.CampgroundDetailsScreen
import com.example.rvcopilot.ui.home.HomePage
import com.example.rvcopilot.ui.user.CreateAccountScreen
import com.example.rvcopilot.ui.details.PicturesScreen
import com.example.rvcopilot.ui.details.rvpark.RvParkDetailsScreen
import com.example.rvcopilot.ui.details.rvpark.RvParkGridScreen
import com.example.rvcopilot.ui.campsites.CreateCampsitesScreen
import com.example.rvcopilot.ui.trips.CreateTripScreen
import com.example.rvcopilot.ui.campsites.EditCampsiteScreen
import com.example.rvcopilot.ui.user.UserBioScreen
import com.example.rvcopilot.ui.user.FrontPageScreen
import com.example.rvcopilot.ui.campsites.ListFavoriteSitesScreen
import com.example.rvcopilot.ui.home.LocationMapScreen
import com.example.rvcopilot.ui.campsites.ReviewScreen
import com.example.rvcopilot.ui.tools.TemperatureScreen
import com.example.rvcopilot.ui.tools.WeatherScreen
import androidx.compose.runtime.key
import com.example.rvcopilot.data.FacilitiesRepository
import com.example.rvcopilot.data.RvPark
import com.example.rvcopilot.model.FacilitiesViewModel
import com.example.rvcopilot.model.LocationViewModel
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
fun RvCopilotApp()  // reference the Android 'reply app' example
{

    // initialize the viewModel instance
    val context = LocalContext.current
    val firestore = remember { FirebaseFirestore.getInstance() }
    val db = FirebaseFirestore.getInstance()
    val rvParkRepository = remember { RvParkRepository(firestore) }
    val userRepository = remember { UserRepository(firestore) }
    val pictureRepository = remember { PictureRepository(firestore) }
    val tripRepository = remember { TripRepository(firestore) }
    val facilitiesRepository = remember { FacilitiesRepository(firestore) }


    /**
     *  **************************************************************
     * viewModels with companion objects
     * must have this definition or the system will crash
     * * *************************************************************
     * */
    val pictureViewModel: PictureViewModel = viewModel(
        factory = PictureViewModel.provideFactory(pictureRepository)
    )

    val viewModel: RvParkViewModel = viewModel(
        factory = RvParkViewModel.provideFactory(rvParkRepository)
    )
    // shared instance of the viewModel
    val userViewModel: UserViewModel = viewModel(
        factory = UserViewModel.provideFactory(userRepository)
    )
    val rvParkViewModel: RvParkViewModel = viewModel(
        factory = RvParkViewModel.provideFactory(rvParkRepository)
    )

    // convert flow of data into a state value
    val rvParkUiState = viewModel.uiState.collectAsState().value
    // note: .value extracts the current value of the State object

    val tripViewModel: TripViewModel = viewModel(
        factory = TripViewModel.provideFactory(tripRepository)
    )

    val facilitiesViewModel: FacilitiesViewModel = viewModel(
        factory = FacilitiesViewModel.provideFactory(facilitiesRepository)
    )
    val application = context.applicationContext as Application
    val locationViewModel: LocationViewModel = viewModel(
        factory = LocationViewModel.provideFactory(application, facilitiesRepository)
    )

    // print current screen before starting the "when" if block
    println("Current screen: ${rvParkUiState.currentScreen}")

    when (rvParkUiState.currentScreen) {
        RvParkScreen.HOME -> {
            println("RvCopilotApp: Navigating to: HOME")
            HomePage(
                viewModel = viewModel,
                onNavigateToRvParkGrid = { viewModel.navigateToRvParkGrid() },
                onNavigateToTrips = { viewModel.navigateToTripsSection() },
                onNavigateToUserBio = { viewModel.navigateToUserBio() },
                onNavigateToMap = { viewModel.navigateToMap() },
                onNavigateToFavoriteSites = {viewModel.navigateToFavoriteSites()}
            )
        }
        RvParkScreen.FRONT_PAGE -> {
            println("RvCopilotApp: Navigating to: FRONT_PAGE")
            FrontPageScreen(
                userViewModel = userViewModel,
                onBackClick = { viewModel.quitGame() },
                onStartClick = { viewModel.navigateToMap() },
                //onStartClick = { viewModel.navigateToHome() }, // for development
                onCreateAccountClick = {
                    viewModel.navigateToCreateAccount()

                }
            )
        }
        RvParkScreen.CREATE_ACCOUNT -> {
            println("RvCopilotApp: Navigating to: CREATE_ACCOUNT")
            CreateAccountScreen(
                onBackClick = { viewModel.navigateToFront() },
                onAccountCreated = { viewModel.navigateToFront() }
            )
        }
        RvParkScreen.USER_BIO -> {
            println("RvCopilotApp: Current screen:  USER_BIO")
            UserBioScreen(
                userViewModel = userViewModel,
                onSaveClick = { updatedBio ->
                    userViewModel.updateBio(updatedBio)
                },
                onBackClick = { viewModel.navigateToHome() }
            )
        }
        RvParkScreen.RVPARK_GRID -> {
            println("RvCopilotApp: Navigating to: RV Park GRID SCREEN")
            //val facilitiesViewModel: FacilitiesViewModel = viewModel()
            val currentUser by userViewModel.currentUser.collectAsState()
            //val locationViewModel: LocationViewModel = viewModel()

            currentUser?.let { user -> // currentUser could be null
                RvParkGridScreen(
                    currentUser = user,
                    viewModel = viewModel,
                    tripViewModel = tripViewModel,
                    facilitiesViewModel = facilitiesViewModel,
                    locationViewModel = locationViewModel,
                    onBackClick = { viewModel.navigateToPage1() }
                )
            } ?: println("from Rvcopilot RVPARK_GRID, currentUser is null")
        }
        RvParkScreen.LOCATION_MAP -> {
            println("RvCopilotApp: Navigating to: LOCATION_MAP")
            LocationMapScreen(
                onBackClick = { viewModel.navigateToHome() },
                onHomeClick = { viewModel.navigateToHome() },
                locationViewModel = locationViewModel,
                facilitiesViewModel = facilitiesViewModel
            )
        }
        RvParkScreen.CREATE_TRIP -> {
            println("RvCopilotApp: Navigating to: CREATE_TRIP")
            val currentUser by userViewModel.currentUser.collectAsState()
            currentUser?.let {
                CreateTripScreen(
                    currentUser = it,
                    viewModel = tripViewModel,
                    onBackClick = { viewModel.navigateToHome() },
                )
            } ?: Text("Loading user $currentUser")
        }
        RvParkScreen.TRIPS_SECTION -> {
            println("Navigating to Trip Section")
            TripApp(
                onExitTripApp = {
                    rvParkViewModel.navigateToHome()
                }
            )
        }

/**
        RvParkScreen.RVPARK_DETAILS -> {
            println("inside RVPARK_DETAILS")
            println("Navigating to: PAGE1_DETAILS")
            val selectedRvPark = rvParkUiState.selectedRvPark
            if (selectedRvPark != null) {
                key(selectedRvPark.firebaseId) {
                    RvParkDetailsScreen(
                        viewModel = viewModel,
                        selectedRvPark = selectedRvPark,
                        tripViewModel = tripViewModel,
                        onBackClick = { viewModel.navigateToFavoriteSites() },
                        onEditClick = { rvPark ->
                            viewModel.storeSelectedRvPark(rvPark)
                            viewModel.navigateToEditCampsite()
                        }
                    )
                }
            } else {
                println("Warning: selectedRvPark is null. Navigating back to PAGE1.")
                viewModel.navigateToPage1()
            }
        } */


/**
        RvParkScreen.TEMPERATURE -> {  // Calculator screen
            rvParkUiState.selectedRvPark?.let { selectedRvPark ->
                TemperatureScreen(
                    context = context,
                    onBackClick = { viewModel.navigateToRvParkDetails(selectedRvPark) }
                )
            }
        }
        RvParkScreen.PICTURES_SCREEN -> {
            PicturesScreen(
                category = PictureCategory.CAMPSITE,
                viewModel = pictureViewModel,
                tripViewModel = tripViewModel,
                tripLabel = rvParkUiState.selectedRvPark?.name ?: "Unknown Campsite",
                onBackClick = {
                    val selectedRvPark = rvParkUiState.selectedRvPark
                    if (selectedRvPark != null) {
                        viewModel.navigateToRvParkDetails(selectedRvPark)
                    } else {
                        println("PICTURES_SCREEN: selectedRvPark is null. Cannot navigate back.")
                        viewModel.navigateToFavoriteSites()
                    }
                    //viewModel.navigateToRvParkDetails(
                     //   rvParkUiState.selectedRvPark!!
                    //)
                }

            )
        }
        RvParkScreen.WEATHER -> {
            println("RvCopilotApp: RvParkScreen.Weather")
            rvParkUiState.selectedRvPark?.let { selectedRvPark ->
                WeatherScreen(
                    viewModel = viewModel,
                    context = context,
                    onBackClick = { viewModel.navigateToRvParkDetails(selectedRvPark) }
                )
            }
        }*/


         /**
        RvParkScreen.CREATE_CAMPSITES -> {
            val currentUser by userViewModel.currentUser.collectAsState()
            currentUser?.let {
                CreateCampsitesScreen(
                    currentUser = it,
                    onBackClick = { viewModel.navigateToHome() },
                )
            } ?: Text("Loading user $currentUser")
        } */


/**
        RvParkScreen.FAVORITE_SITES -> {
            val currentUser by userViewModel.currentUser.collectAsState()
            currentUser?.let {
                ListFavoriteSitesScreen(
                    currentUser = it,
                    viewModel = viewModel,
                    onRvParkClick = { rvPark -> viewModel.navigateToRvParkDetails(rvPark) },
                    onBackClick = { viewModel.navigateToHome() }
                )
            } ?: Text("")
        }
        RvParkScreen.SITE_REVIEW -> {
            rvParkUiState.selectedRvPark?.let { selectedRvPark ->
                val currentUser = userViewModel.currentUser.value
                if (currentUser != null) {
                    key(selectedRvPark.firebaseId) {
                        ReviewScreen(
                            selectedRvPark = selectedRvPark,
                            currentUser = currentUser,
                            viewModel = viewModel,
                            onSaveClick = { reviewText ->
                                println("Review saved: $reviewText")
                                viewModel.addReviewToPark(
                                    selectedRvPark,
                                    reviewText,
                                    currentUser
                                )
                            },
                            onBackClick = { viewModel.navigateToRvParkDetails(selectedRvPark) }
                        )
                    }
                }
            }
        }

        RvParkScreen.CAMPGROUND_DETAILS -> {
            println("Navigating to: CAMPGROUND_DETAILS")
            rvParkUiState.selectedRvPark?.let { selectedRvPark ->
                key(selectedRvPark.firebaseId) {
                    CampgroundDetailsScreen(
                        viewModel = viewModel,
                        selectedRvPark = selectedRvPark,
                        pictureViewModel = pictureViewModel,
                        onLocationClick = { },
                        onBackClick = { viewModel.navigateToRvParkDetails(selectedRvPark) }
                    )
                }
            }
        }

        RvParkScreen.EDIT_CAMPSITE -> {
            val currentUser by userViewModel.currentUser.collectAsState()
            rvParkUiState.selectedRvPark?.let { selectedRvPark ->
                key(selectedRvPark.firebaseId) {
                    currentUser?.let {
                        EditCampsiteScreen(
                            rvParkViewModel = rvParkViewModel,
                            pictureViewModel = pictureViewModel,
                            rvPark = selectedRvPark,
                            username = it.username,
                            onBackClick = { viewModel.navigateToRvParkDetails(selectedRvPark) }
                        )
                    } ?: Text("")
                }
            }
        }
         */


        else -> {
            println("Unknown screen, navigating to HOME")
            viewModel.navigateToHome()

        }
    }
}