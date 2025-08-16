package com.example.rvcopilot.ui.details

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.foundation.lazy.LazyColumn
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.rvcopilot.R
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.example.rvcopilot.data.PictureRepository
import com.example.rvcopilot.ui.components.TopBar
import com.example.rvcopilot.data.Pictures
import com.example.rvcopilot.model.PictureViewModel
import com.example.rvcopilot.ui.theme.Azure
import com.example.rvcopilot.data.PictureRepository.PictureCategory
import com.example.rvcopilot.model.TripViewModel
import java.io.ByteArrayOutputStream
import com.example.rvcopilot.ui.tools.uriToBase64

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

@SuppressLint("DiscouragedApi")
@Composable
fun PicturesScreen(
    category: PictureCategory,
    viewModel: PictureViewModel,
    tripViewModel: TripViewModel,
    tripLabel: String,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    //val pictureList by viewModel.allPictures.collectAsState(initial = emptyList())
    var newLabel by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedPicture by remember { mutableStateOf<Pictures?>(null) }

    //val selectedRvPark by viewModel.selectedRvPark.collectAsState()
    //val tripLabel = selectedRvPark?.name ?: "Unknown Campsite"

    val pictureList by when (category) {
        PictureCategory.TRIP -> viewModel.tripPictures.collectAsState()
        PictureCategory.CAMPSITE -> viewModel.campsitePictures.collectAsState()
    }
    println("PictureScreen: Showing ${pictureList.size} pictures for $category")
    //println("currently on PicturesScreen")

    LaunchedEffect(Unit) {  // load the stored pictures into db and show on device screen
        viewModel.seedPictures(context)

        launch {
            viewModel.tripPictures.collectLatest {
                println("Updated tripPictures size: ${it.size}")
            }
            launch {
                viewModel.campsitePictures.collectLatest {
                    println("Updated campsitePictures size: ${it.size}")
                }
            }
        }
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
    val selectedTrip by tripViewModel.selectedTrip.collectAsState()
    val tripTitle = selectedTrip?.destination ?: "none selected:"  // default
    key(tripLabel) {
        Scaffold(
            topBar = {
                TopBar(
                    //title = "$tripTitle Pictures",
                    title = "",
                    onBackClick = onBackClick
                )
            }
        ) { padding ->
            if (selectedPicture == null) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
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

                                    val labelToSave =
                                        if (newLabel.isBlank()) "Untitled ${System.currentTimeMillis()}" else newLabel
                                    println("Attempting to save picture: $labelToSave, URI: $uri")

                                    val base64Image = uriToBase64(context, uri)

                                    // create picture object
                                    val picture = Pictures(
                                        label = labelToSave,
                                        imageBase64 = base64Image
                                        //imageUri = uri.toString()
                                    )
                                    println("Saving picture with label: ${picture.label}, URI: ${picture.imageUri}")
                                    viewModel.insertPicture(picture,category)
                                    println("Inserted picture: ${picture.label}, imageUri: ${picture.imageUri}")


                                    // call the picture object and store in Firestore
                                    Toast.makeText(
                                        context,
                                        "Picture saved to Firebase",
                                        Toast.LENGTH_SHORT
                                    ).show()
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



                                    Image(
                                        painter = when {
                                    pic.imageBase64.isNotBlank() -> {
                                        println("Decoding image from Base64 for label: ${pic.label}")
                                        try {
                                            val imageBytes = Base64.decode(pic.imageBase64, Base64.DEFAULT)
                                            val bitmap = BitmapFactory.decodeByteArray(
                                                imageBytes,
                                                0,
                                                imageBytes.size
                                            )
                                            BitmapPainter(bitmap.asImageBitmap())
                                        } catch (e: Exception) {
                                            println("Error decoding base64: ${e.localizedMessage}")
                                            painterResource(id = R.drawable.avatar_rvcopilot_image)
                                        }
                                    }
                                    // lambda block
                                    pic.imageUri.startsWith("http") || pic.imageUri.startsWith("content://") -> {
                                        println("Loading image with URI: ${pic.imageUri}")
                                        println("Loading image from URI for label: ${pic.label}, URI: ${pic.imageUri}")
                                        rememberAsyncImagePainter(pic.imageUri)
                                    }

                                    else -> {
                                        val resId = context.resources.getIdentifier(
                                            pic.imageUri,
                                            "drawable",
                                            context.packageName
                                        )
                                        println("Using drawable resource ID: $resId for imageUri: ${pic.imageUri}")
                                        if (resId != 0) painterResource(id = resId)
                                        else painterResource(id = R.drawable.avatar_rvcopilot_image)
                                        }
                                    },


                                    //painter = painter,
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
                //

                //val selectedTrip by tripViewModel.selectedTrip.collectAsState()
                //val tripLabel = selectedTrip?.destination ?: "Unknown Trip"

                if (selectedPicture != null) {
                    PictureDetailView(
                        picture = selectedPicture!!,
                        viewModel = viewModel,
                        category = category,
                        tripLabel = tripLabel,
                        onBack = { selectedPicture = null }
                    )
                }
            }
        }
    }
}
