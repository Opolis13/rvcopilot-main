package com.example.rvcopilot.ui.details.rvpark

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rvcopilot.model.RvParkViewModel
import com.example.rvcopilot.ui.components.TopBar
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil.compose.rememberAsyncImagePainter
import com.example.rvcopilot.R
import com.example.rvcopilot.data.RvPark
import com.example.rvcopilot.model.PictureViewModel


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
fun CampgroundDetailsScreen(
    viewModel: RvParkViewModel,
    selectedRvPark: RvPark,
    pictureViewModel: PictureViewModel,
    onLocationClick: () -> Unit,
    onBackClick: () -> Unit

) {

    val context = LocalContext.current
    //val selectedRvPark by viewModel.selectedRvPark.collectAsState()
    val pictureList by pictureViewModel.campsitePictures.collectAsState()

    println("currently on CampgroundDetailsScreen")

    selectedRvPark?.let { park ->  // removes requirement for !! operator
        val campgroundPictures = pictureList.filter {
            it.label.equals(park.name, ignoreCase = true)
        }
        Scaffold(
            topBar = {
                TopBar(
                    title = park.name,
                    onBackClick = onBackClick
                  )
            }
        ) { paddingValues ->
            Column( // align the children vertically
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(4.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                val imageResId = remember(park.imageName) {
                    val resId = context.resources.getIdentifier(
                        park.imageName,
                        "drawable",
                        context.packageName
                    )
                    if (resId != 0) resId else R.drawable.avatar_rvcopilot_image
                }

                Image(
                    painter = painterResource(id = imageResId),
                    contentDescription = "Image of ${selectedRvPark!!.name}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(bottom = 8.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                InfoRow("Name", selectedRvPark!!.name)
                InfoRow("Address", selectedRvPark!!.address)
                InfoRow("Phone", selectedRvPark!!.phone)
                InfoRow("Email", selectedRvPark!!.email)
                InfoRow("Services", selectedRvPark!!.services)
                InfoRow("Type of Park", selectedRvPark!!.type)
                InfoRow("Power", selectedRvPark!!.power)
                InfoRow("Pad", selectedRvPark!!.pad)
                InfoRow("Pets", selectedRvPark!!.pets)
                InfoRow("Cellular", selectedRvPark!!.cellular)
                InfoRow("WiFi", selectedRvPark!!.wifi)
                InfoRow("Cable", selectedRvPark!!.cable)
                InfoRow("Amenities", selectedRvPark!!.amenity)
                InfoRow("Coordinates", selectedRvPark!!.coordinates.toString())

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Campground Photos: (scrollable)",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
                )
                if (campgroundPictures.isEmpty()) {
                    Text(
                        text = "No pictures for this campground.",
                        fontSize = 16.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(start = 8.dp, bottom = 12.dp)
                    )
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .padding(horizontal = 8.dp)
                    ) {
                        /**
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
                        items(campgroundPictures) { picture ->
                            val painter = when {
                                picture.imageBase64.isNotBlank() -> {
                                    println("CampgroundDetails: Decoding Base64 for ${picture.label}")
                                    try {
                                        val imageBytes = android.util.Base64.decode(picture.imageBase64, android.util.Base64.DEFAULT)
                                        val bitmap = android.graphics.BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                                        androidx.compose.ui.graphics.painter.BitmapPainter(bitmap.asImageBitmap())
                                    } catch (e: Exception) {
                                        println("CampgroundDetails: Error decoding base64: ${e.localizedMessage}")
                                        painterResource(id = R.drawable.avatar_rvcopilot_image)
                                    }
                                }

                                picture.imageUri.startsWith("http") || picture.imageUri.startsWith("content://") -> {
                                    println("CampgroundDetails: Loading from URI: ${picture.imageUri}")
                                    rememberAsyncImagePainter(picture.imageUri)
                                }

                                else -> {
                                    val resId = context.resources.getIdentifier(
                                        picture.imageUri,
                                        "drawable",
                                        context.packageName
                                    )
                                    println("CampgroundDetails: Drawable ID $resId for ${picture.imageUri}")
                                    if (resId != 0) painterResource(id = resId)
                                    else painterResource(id = R.drawable.avatar_rvcopilot_image)
                                }
                            }

                            Image(
                                painter = painter,
                                contentDescription = picture.label,
                                modifier = Modifier
                                    .padding(4.dp)
                                    .size(80.dp)
                            )
                        }
                    }
                }
            }
        }
    } ?: run {
        Text(
            text = "Loading campground data...",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
fun InfoRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "$label:",
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.width(120.dp)
        )
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold

        )
    }
}


