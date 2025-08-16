package com.example.rvcopilot.ui.campsites

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
import com.example.rvcopilot.model.RvParkViewModel
import com.example.rvcopilot.ui.components.TopBar
import androidx.compose.runtime.getValue
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import com.example.rvcopilot.data.RvPark
import com.example.rvcopilot.model.PictureViewModel
import coil.compose.rememberAsyncImagePainter
import com.example.rvcopilot.R
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
fun EditCampsiteScreen(
    rvParkViewModel: RvParkViewModel,
    pictureViewModel: PictureViewModel,
    rvPark: RvPark,
    username: String,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val pictureList by pictureViewModel.campsitePictures.collectAsState(initial = emptyList())


    var name by remember { mutableStateOf(rvPark.name) }
    var address by remember { mutableStateOf(rvPark.address) }
    var phone by remember { mutableStateOf(rvPark.phone) }
    var email by remember { mutableStateOf(rvPark.email) }
    var services by remember { mutableStateOf(rvPark.services) }
    var type by remember { mutableStateOf(rvPark.type) }
    var pad by remember { mutableStateOf(rvPark.pad) }
    var pets by remember { mutableStateOf(rvPark.pets) }
    var power by remember { mutableStateOf(rvPark.power) }
    var cellular by remember { mutableStateOf(rvPark.cellular) }
    var wifi by remember { mutableStateOf(rvPark.wifi) }
    var cable by remember { mutableStateOf(rvPark.cable) }
    var amenity by remember { mutableStateOf(rvPark.amenity) }

    var imageIndex by remember { mutableStateOf(0) }
    val currentPicture = if (pictureList.isNotEmpty()) pictureList[imageIndex % pictureList.size] else null

    // default image
    val img1 = R.drawable.avatar_rvcopilot_image

    Scaffold(
        topBar = {
            TopBar(
                title = "Edit Campsite",
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


                    val updatedRvPark = rvPark.copy(
                        name = name,
                        address = address,
                        phone = phone,
                        email = email,
                        services = services,
                        type = type,
                        power = power,
                        pad = pad,
                        pets = pets,
                        cellular = cellular,
                        wifi = wifi,
                        cable = cable,
                        amenity = amenity,
                        imageName = newImageName
                    )
                    rvParkViewModel.updateRvPark(updatedRvPark, username)

                    Toast.makeText(context, "Campsite updated successfully!", Toast.LENGTH_SHORT).show()
                    println("Saved picture: label='${currentPicture?.label}', uri='${currentPicture?.imageUri}'")
                    println("EditCampsiteScreen: RvPark object -> '${updatedRvPark}' ")
                    onBackClick()
                } catch (e: Exception) {
                        println("EditCampsiteScreen: Error while saving - ${e.localizedMessage}")
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
             *
             * ---resID is used to find the real drawable ID for the selected image
             *    uses a standard Android SDK method from the 'Resources' class
             *      example finding an ID:
             *      https://developer.android.com/reference/android/content/res/Resources#getIdentifier(java.lang.String,%20java.lang.String,%20java.lang.String)
             *
             *
             * the picture is tested for base64 string existing in database
             * base64 string must be decoded into a bitmap
             * the bitmap must be converted into a BitmapPainter
             * if the decode fails then the default image is shown (app should not crash)
             *
             * previously the imageUri was referenced to an actual picture within the
             * Android imageLoader used with an Android camera or tablet
             * The Coil app (rememberAsyncImagePainter) is used to load the image
             * from a URI or the internet
             *
             * * ----Firestore database
             *  stores the images with a prefix of "http" or "content://"
             *
             *  ----Images
             *  must be given a painter to know what image to draw
             *  'Painter' provides the pixels to draw the image
             *  ---when pulling an image from the local resource (res/drawable), use 'painterResource'
             *  ---when pulling an image from the internet (Firestore database) then use rememberAsyncImagePainter
             *
             *  --rememberAsyncImagePainter
             *  defined in 'Coil' for Compose library (not in Jetpack Compose)
             *  https://coil-kt.github.io/coil/compose/#rememberasyncimagepainter
             *
             *  -----base64 Loading images
             *  https://code.luasoftware.com/tutorials/android/jetpack-compose-load-n-images-per-row
             *  https://stackoverflow.com/questions/68041157/how-can-i-display-a-base64-encoded-image-in-jetpack-compose-without-using-extern
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
                        println("Clicked picture: label='${currentPicture.label}', uri='${currentPicture.imageUri}'")
                        imageIndex++
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

            UpdateField("Name of Campground and/or ID", name) { name = it }
            UpdateField("Address", address) { address = it }
            UpdateField("Phone", phone) { phone = it }
            UpdateField("Email", email) { email = it }
            UpdateField("Services Available: dump, water, sewer, hookups", services) { services = it }
            UpdateField("Type: Public land, RV Park, State Park, County Park,", type) { type = it }
            UpdateField("Power: Watts, Amps, Line Voltage", power) { power = it }
            UpdateField("Pad: ", pad) { pad = it }
            UpdateField("Pets", pets) { pets = it }
            UpdateField("Cellular & carrier: ave. bars", cellular) { cellular = it }
            UpdateField("WiFi", wifi) { wifi = it }
            UpdateField("Cable", cable) { cable = it }
            UpdateField("Amenities: picnic, fire, patio, etc.", amenity) { amenity = it }

            Button(
                onClick = {
                   try {
                    val newImageName = if (currentPicture?.imageUri == null) {
                        "avatar_rvcopilot_image"
                    } else {
                        // Extract clean drawable name
                        currentPicture.imageUri
                            .substringAfterLast("/")
                            .substringBeforeLast(".")
                            .ifBlank { "avatar_rvcopilot_image" }
                    }

                    val updatedRvPark = rvPark.copy(
                        name = name,
                        address = address,
                        phone = phone,
                        email = email,
                        services = services,
                        type = type,
                        power = power,
                        pad = pad,
                        pets = pets,
                        cellular = cellular,
                        wifi = wifi,
                        cable = cable,
                        amenity = amenity,
                        imageName = newImageName
                    )
                    rvParkViewModel.updateRvPark(updatedRvPark, username)

                    Toast.makeText(context, "Campsite updated successfully!", Toast.LENGTH_SHORT).show()
                    println("Saved picture: label='${currentPicture?.label}', uri='${currentPicture?.imageUri}'")
                    println("EditCampsiteScreen: RvPark object '${updatedRvPark}' ")
                    onBackClick()

                } catch (e: Exception) {
                println("EditCampsiteScreen: Error while saving - ${e.localizedMessage}")
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