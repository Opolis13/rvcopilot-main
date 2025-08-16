package com.example.rvcopilot.ui.trips

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.rvcopilot.model.TripViewModel
import com.example.rvcopilot.ui.components.TopBar
import androidx.compose.runtime.getValue
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import com.example.rvcopilot.data.Trips
import com.example.rvcopilot.model.PictureViewModel
import coil.compose.rememberAsyncImagePainter
import com.example.rvcopilot.R
import com.example.rvcopilot.data.User
import com.example.rvcopilot.ui.theme.LighterGreen

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
 * there may be a deferred function such as images (doesn't run immediately)
 * */

/**
 * () -> Unit:  Lambda expression.  ()= function does not accept arguments
 * Unit = 'Void' expression like used in C99.  Function does not return anything.
 * onStartClicked: () -> Unit.   This is a callback for the start button
 * */
/**
 * Scaffold accepts the topBar composable as a parameter
 * topBar needs Scaffold to run
 * Scaffold also accepts different parts of the user interface ui,
 * in this case topBar with a title, onBackClick
 * see this url for an example:
 * https://developer.android.com/develop/ui/compose/components/scaffold
 *
 * */

@SuppressLint("DiscouragedApi")
@Composable
fun EditTripScreen(
    currentUser: User,
    tripViewModel: TripViewModel,
    pictureViewModel: PictureViewModel,
    trip: Trips,
    username: String,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val pictureList by pictureViewModel.tripPictures.collectAsState(initial = emptyList())

    var destination by remember { mutableStateOf(trip.destination) }
    var destaddress by remember { mutableStateOf(trip.destaddress) }
    var activities by remember { mutableStateOf(trip.activities) }
    var recreation by remember { mutableStateOf(trip.recreation) }
    var restaurants by remember { mutableStateOf(trip.restaurants) }

    var imageIndex by remember { mutableStateOf(0) }
    val currentPicture = if (pictureList.isNotEmpty()) pictureList[imageIndex % pictureList.size] else null

    // default image
    val img1 = R.drawable.avatar_rvcopilot_image

    Scaffold(
        topBar = {
            TopBar(
                title = "Edit Trip",
                onBackClick = onBackClick
            )
        }
    ) { padding ->
        Column( // align the children vertically
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = {
                    try {
                        val newImageName = if (currentPicture?.imageUri == null) {
                            "avatar_rvcopilot_image"
                        } else {

                            currentPicture.imageUri
                                .substringAfterLast("/")
                                .substringBeforeLast(".")
                                .ifBlank { "avatar_rvcopilot_image" }
                        }

                        val updatedTrip = trip.copy(
                            destination = destination,
                            destaddress = destaddress,
                            recreation = recreation,
                            activities = activities,
                            restaurants = restaurants,
                            imageName = newImageName,
                            createdBy = currentUser.username
                        )
                        tripViewModel.updateTrip(updatedTrip, currentUser.username)

                        Toast.makeText(context, "Campsite updated successfully!", Toast.LENGTH_SHORT).show()
                        println("Saved picture: label='${currentPicture?.label}', uri='${currentPicture?.imageUri}'")
                        println("EditTripScreen: Trip object -> '${updatedTrip}' ")
                        onBackClick()
                    } catch (e: Exception) {
                        println("EditTripScreen: Error while saving - ${e.localizedMessage}")
                        Toast.makeText(context, "Error saving campsite: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                    }
                },
                modifier = Modifier
                    .width(220.dp)
                    .padding(4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = LighterGreen,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            )
            {
                Text(
                    "Save Update",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold

                )
            }
            Text(
                text = "Scroll up",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            Text(
                text = "Click image to change",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            /**
             * ----Firestore database
             * stores the images with a prefix of "http" or "content://"
             *
             * ----Images
             * must be given a painter to know what image to draw
             * 'Painter' provides the pixels to draw the image
             * ---when pulling an image from the local resource (res/drawable), use 'painterResource'
             * ---when pulling an image from the internet (Firestore database) then use rememberAsyncImagePainter
             *
             * --rememberAsyncImagePainter
             * defined in 'Coil' for Compose library (not in Jetpack Compose)
             * https://coil-kt.github.io/coil/compose/#rememberasyncimagepainter
             *
             * */
            if (currentPicture != null) {
                Image(
                    painter =
                    if (currentPicture.imageUri.startsWith("http") || currentPicture.imageUri.startsWith("content://")) {
                        rememberAsyncImagePainter(currentPicture.imageUri)
                    } else {
                        /**
                         * ---resID is used to find the real drawable URI for the selected image
                         * uses a standard Android SDK method from the 'Resources' class
                         * example finding an ID:
                         * https://developer.android.com/reference/android/content/res/Resources#getIdentifier(java.lang.String,%20java.lang.String,%20java.lang.String)
                         *
                         * ----getIdentifier supports name strings
                         * other methods use Firebase resource ID int
                         * if the resource id's get out of sync then
                         * firebase will cause the app to crash
                         * */
                        val resId = context.resources.getIdentifier(
                            currentPicture.imageUri,
                            "drawable",
                            context.packageName
                        )
                        painterResource(id = if (resId != 0) resId else img1)
                    },
                    contentDescription = currentPicture.label,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(200.dp)
                        .clickable {
                            imageIndex++
                            val newPic = pictureList[imageIndex % pictureList.size]
                            println("Clicked picture: label='${newPic.label}', uri='${newPic.imageUri}'")

                        }
                        .padding(bottom = 16.dp)
                )
            } else {
                Text(
                    "No pictures available",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            }

            UpdateField("Name of Destination", destination) { destination = it }
            UpdateField("Address", destaddress) { destaddress = it }
            UpdateField("Recreational Opportunities", recreation) { recreation = it }
            UpdateField("Activities", activities) { activities = it }
            UpdateField("Restaurants", restaurants) { restaurants = it }

            Button(
                onClick = {
                    try {
                        val resId2 = if (currentPicture?.imageUri == null) {
                            "avatar_rvcopilot_image"
                        } else {

                            currentPicture.imageUri
                                .substringAfterLast("/")
                                .substringBeforeLast(".")
                                .ifBlank { "avatar_rvcopilot_image" }
                        }
                        val updatedTrip = trip.copy(
                            destination = destination,
                            destaddress = destaddress,
                            recreation = recreation,
                            activities = activities,
                            restaurants = restaurants,
                            imageName = resId2,
                            createdBy = currentUser.username
                        )
                        tripViewModel.updateTrip(updatedTrip, currentUser.username)

                        Toast.makeText(context, "Trip updated successfully!", Toast.LENGTH_SHORT).show()
                        println("Saved picture: label='${currentPicture?.label}', uri='${currentPicture?.imageUri}'")
                        println("EditTripScreen: Trip object '${updatedTrip}' ")
                        onBackClick()

                    } catch (e: Exception) {
                        println("EditTripScreen: Error while saving - ${e.localizedMessage}")
                        Toast.makeText(context, "Error saving campsite: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                    }
                },
                modifier = Modifier
                    .width(220.dp)
                    .padding(4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = LighterGreen,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            )
            {
                Text(
                    "Save Update",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold

                )
            }

        }
    }
}

@Composable
private fun UpdateField(
    label: String,
    value: String,
    onChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    )
}