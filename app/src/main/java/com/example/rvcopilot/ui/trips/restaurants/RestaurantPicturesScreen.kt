package com.example.rvcopilot.ui.trips.restaurants

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rvcopilot.ui.components.TopBar
import com.example.rvcopilot.data.Restaurant
import com.example.rvcopilot.ui.theme.*
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.clickable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import coil.compose.rememberAsyncImagePainter
import com.example.rvcopilot.R
import com.example.rvcopilot.data.PictureRepository
import com.example.rvcopilot.data.Pictures
import com.example.rvcopilot.model.PictureViewModel
import com.example.rvcopilot.model.RvParkViewModel
import com.example.rvcopilot.model.TripViewModel
import com.example.rvcopilot.ui.components.tripImageList
import com.example.rvcopilot.data.PictureRepository.PictureCategory
import com.example.rvcopilot.ui.details.PictureDetailView


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
 * */


@Composable
fun RestaurantPicturesScreen(
    restaurant: Restaurant,
    pictureViewModel: PictureViewModel,
    category: PictureCategory,
    tripViewModel: TripViewModel,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val pictureList by pictureViewModel.allPictures.collectAsState(initial = emptyList())
    var newLabel by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedPicture by remember { mutableStateOf<Pictures?>(null) }


    println("currently on PicturesScreen")

    LaunchedEffect(Unit) {  // load the stored pictures into db and show on device screen
        pictureViewModel.seedPictures(context)
    }


    /**
     * -- Get the image
     * user selects the image.
     * selectedImageUri stores the image Uri
     *
     * --example:
     * https://developer.android.com/reference/kotlin/androidx/activity/compose/package-summary#rememberLauncherForActivityResult(androidx.activity.result.contract.ActivityResultContract,androidx.activity.result.ActivityResultCallback)
     *
     * --imagePickerLauncher: Launch activity to pick an image
     * this code creates a persistent launcher that picks an image URi
     * it is dependent on  the gradle file for "activity"
     * --rememberLauncherForActivityResult remembers the launcher across recompositions
     * --ActivityResultContracts.GetContent(): built in contract that allows user to pick the content (images) from device
     * --contract specifies conversions to/from intents
     * -- {uri: Uri? ->} user picks the image, block receives a Uri or a null
     *
     *
     * https://developer.android.com/reference/kotlin/androidx/activity/compose/package-summary#rememberLauncherForActivityResult(androidx.activity.result.contract.ActivityResultContract,androidx.activity.result.ActivityResultCallback)
     * example from web link:
     * val launcher =
     *     rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) {
     *         result.value = it}
     *
     * */
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
        }
    }
    Scaffold(
        topBar = {
            TopBar(
                title = "Pictures: ${restaurant.name}",
                onBackClick = onBackClick
            )
        }
    ) { paddingValues ->

        if (selectedPicture == null) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Text(
                        "Add a Picture from phone or tablet",
                        fontSize = 20.sp,
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                    )

                    OutlinedTextField(
                        value = newLabel,
                        onValueChange = { newLabel = it },
                        label = { Text("Label") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))
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

                    if (selectedImageUri != null) {
                        Image(
                            painter = rememberAsyncImagePainter(selectedImageUri),
                            contentDescription = "Selected Image",
                            modifier = Modifier
                                .size(120.dp)
                                .clickable {
                                    imagePickerLauncher.launch("image/*")
                                },
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .background(
                                    Color.LightGray,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { imagePickerLauncher.launch("image/*") },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Click to Upload Images from device",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .padding(
                                        top = 8.dp,
                                        bottom = 12.dp
                                    )

                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {  // save image to Firestore database
                            selectedImageUri?.let { uri ->

                                val labelToSave = if (newLabel.isBlank()) "Untitled ${System.currentTimeMillis()}" else newLabel
                                println("Attempting to save picture: $labelToSave, URI: $uri")
                                // create picture object
                                val picture = Pictures(
                                    label = labelToSave,
                                    imageUri = uri.toString()
                                )
                                println("Attempting to save picture: ${picture.label}, URI: ${picture.imageUri}")
                                pictureViewModel.insertPicture(picture, category)  // call the picture object and store in Firestore
                                Toast.makeText(context, "Picture saved to Firebase", Toast.LENGTH_SHORT).show()
                                println("Picture successfully saved to Firestore: ${picture.label}, URI: ${picture.imageUri}")

                                newLabel = ""
                                selectedImageUri = null
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Azure),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .height(40.dp)
                            .width(200.dp)
                            .padding(start = 8.dp)
                    ) {
                        Text(
                            "Save Picture",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black

                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // header for picture grid
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        //contentAlignment = Alignment.Center
                    )
                    Text(
                        text = "Saved Pictures (tap to view)",
                        fontSize = 20.sp,
                        modifier = Modifier
                            .padding(top = 8.dp, bottom = 12.dp)
                    )
                }

                item {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 600.dp)
                    ) {
                        items(pictureList) { pic ->
                            Column(
                                modifier = Modifier
                                    .padding(6.dp)
                                    .fillMaxWidth()
                                    .clickable { selectedPicture = pic },
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {

                                /**
                                 *****************************************
                                 * use of getIdentifier supports name strings
                                 * other methods use Firebase resource ID int
                                 * if the resource id's get out of sync then
                                 * firebase will cause the app to crash
                                 * uses a standard Android SDK method from the 'Resources' class
                                 * example finding an ID:
                                 * https://developer.android.com/reference/android/content/res/Resources#getIdentifier(java.lang.String,%20java.lang.String,%20java.lang.String)
                                 *
                                 * ----val painter:  if image is a URL or URI then it loads from device (tablet) or Firestore
                                 * if not URL or URI:  loads resource drawable by name
                                 * if no drawable found then uses a default image
                                 * *********************************************
                                 * */
                                /**
                                 *****************************************
                                 * use of getIdentifier supports name strings
                                 * other methods use Firebase resource ID int
                                 * if the resource id's get out of sync then
                                 * firebase will cause the app to crash
                                 * uses a standard Android SDK method from the 'Resources' class
                                 * example finding an ID:
                                 * https://developer.android.com/reference/android/content/res/Resources#getIdentifier(java.lang.String,%20java.lang.String,%20java.lang.String)
                                 *
                                 * ----val painter:  if image is a URL or URI then it loads from device (tablet) or Firestore
                                 * if not URL or URI:  loads resource drawable by name
                                 * if no drawable found then uses a default image
                                 * *********************************************
                                 * */
                                /**
                                 *****************************************
                                 * use of getIdentifier supports name strings
                                 * other methods use Firebase resource ID int
                                 * if the resource id's get out of sync then
                                 * firebase will cause the app to crash
                                 * uses a standard Android SDK method from the 'Resources' class
                                 * example finding an ID:
                                 * https://developer.android.com/reference/android/content/res/Resources#getIdentifier(java.lang.String,%20java.lang.String,%20java.lang.String)
                                 *
                                 * ----val painter:  if image is a URL or URI then it loads from device (tablet) or Firestore
                                 * if not URL or URI:  loads resource drawable by name
                                 * if no drawable found then uses a default image
                                 * *********************************************
                                 * */

                                /**
                                 *****************************************
                                 * use of getIdentifier supports name strings
                                 * other methods use Firebase resource ID int
                                 * if the resource id's get out of sync then
                                 * firebase will cause the app to crash
                                 * uses a standard Android SDK method from the 'Resources' class
                                 * example finding an ID:
                                 * https://developer.android.com/reference/android/content/res/Resources#getIdentifier(java.lang.String,%20java.lang.String,%20java.lang.String)
                                 *
                                 * ----val painter:  if image is a URL or URI then it loads from device (tablet) or Firestore
                                 * if not URL or URI:  loads resource drawable by name
                                 * if no drawable found then uses a default image
                                 * *********************************************
                                 * */
                                val painter = if (pic.imageUri.startsWith("http") || pic.imageUri.startsWith("content://")) {
                                    rememberAsyncImagePainter(pic.imageUri)
                                } else {
                                    val resId = context.resources.getIdentifier(
                                        pic.imageUri,
                                        "drawable",
                                        context.packageName
                                    )

                                    if (resId != 0) {
                                        painterResource(id = resId)
                                    } else {
                                        painterResource(id = R.drawable.avatar_rvcopilot_image)
                                    }
                                }

                                Image(
                                    painter = painter,
                                    contentDescription = pic.label,
                                    modifier = Modifier
                                        .size(100.dp)
                                        .padding(4.dp),
                                    contentScale = ContentScale.Crop
                                )
                                Text(
                                    text = pic.label,
                                    fontSize = 14.sp,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }

            }
        } else {
            val selectedTrip by tripViewModel.selectedTrip.collectAsState()
            val tripLabel = selectedTrip?.destination ?: "Unknown Trip"
            PictureDetailView(
                picture = selectedPicture!!,
                viewModel = pictureViewModel,
                category = PictureCategory.TRIP,
                tripLabel = tripLabel,
                onBack = { selectedPicture = null }
            )
        }
    }
}