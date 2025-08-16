package com.example.rvcopilot.ui.details

import android.annotation.SuppressLint
// graphics
import android.graphics.BitmapFactory
import android.util.Base64
import android.net.Uri
import android.widget.Toast
import androidx.compose.ui.res.painterResource


// Jetpack
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight



// coil for URI/URL image loading asynchronously
import coil.compose.rememberAsyncImagePainter

// models and classes
import com.example.rvcopilot.R
import com.example.rvcopilot.data.Pictures
import com.example.rvcopilot.model.PictureViewModel
import com.example.rvcopilot.data.PictureRepository.PictureCategory

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
fun PictureDetailView(
    picture: Pictures,
    viewModel: PictureViewModel,
    category: PictureCategory,
    tripLabel: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    println("on PictureDetailView")
    /**
     *
     * if it does not start with "http" and does not start with "content://"
     * then it is a drawable resource image
     * */

    val isDrawableImage = !picture.imageUri.startsWith("http") && !picture.imageUri.startsWith("content://")

    val imageUri = remember(picture) {
        if (isDrawableImage) {
            val resId = context.resources.getIdentifier(
                picture.imageUri ?: "",  // when imageUri is null it returns empty string
                "drawable",
                context.packageName
            )
            if (resId != 0) {
                viewModel.getImageUriFromDrawable(context, resId) ?: Uri.EMPTY
            } else {
                null // no drawable found
            }

        } else {
            // when picture.imageUri is null it will return a null, otherwise, parse 'it'
            picture.imageUri?.let { Uri.parse(it) } ?: Uri.EMPTY
        }
    }


    val painter = if (isDrawableImage && imageUri != null) {
        val resId = context.resources.getIdentifier(
            picture.imageUri,
            "drawable",
            context.packageName
        )
        painterResource(id = resId)
    } else {
        rememberAsyncImagePainter(picture.imageUri ?: R.drawable.avatar_rvcopilot_image)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
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
        Image(
            painter = when {
                picture.imageBase64.isNotBlank() -> {
                    println("PictureDetailView: Decoding image from Base64 for label: ${picture.label}")
                    try {
                        val imageBytes = Base64.decode(picture.imageBase64, Base64.DEFAULT)
                        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                        BitmapPainter(bitmap.asImageBitmap())
                    } catch (e: Exception) {
                        println("PictureDetailView: Error decoding base64: ${e.localizedMessage}")
                        painterResource(id = R.drawable.avatar_rvcopilot_image)
                    }
                }
                picture.imageUri.startsWith("http") || picture.imageUri.startsWith("content://") -> {
                    println("PictureDetailView: Loading image from URI: ${picture.imageUri}")
                    rememberAsyncImagePainter(picture.imageUri)
                }

                else -> {
                    val resId = context.resources.getIdentifier(
                        picture.imageUri,
                        "drawable",
                        context.packageName
                    )
                    println("PictureDetailView: Using drawable resource ID: $resId for imageUri: ${picture.imageUri}")
                    if (resId != 0) painterResource(id = resId)
                    else painterResource(id = R.drawable.avatar_rvcopilot_image)
                }
            },

            contentDescription = picture.label,
            modifier = Modifier
                .fillMaxWidth()
                .height(360.dp)
                .clickable { onBack() },
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = picture.label,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {

        Button(
            onClick = {

                //viewModel.insertPicture(picture, category)
                val pictureWithTripLabel = picture.copy(label = tripLabel)
                println("PictureDetailView: saving picture: ${pictureWithTripLabel.label}, URI: ${pictureWithTripLabel.imageUri}")

                viewModel.insertPicture(pictureWithTripLabel, category)
                Toast.makeText(context, "Saved to Firebase!", Toast.LENGTH_SHORT).show()
                println("Picture saved to Firebase!!!!!")
                onBack()
           },
            modifier = Modifier
                .padding(4.dp)
                //.width(200.dp)
                .weight(1f)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.LightGray,
                contentColor = Color.White
            )
        ) {
            Text(
                "Save to Firebase",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
            Button(
                onClick = {
                    println("Attempting to delete: ${picture.label}, URI: ${picture.imageUri}")
                    viewModel.deletePicture(picture, context, category)
                    Toast.makeText(context, "Deleted from Firebase!", Toast.LENGTH_SHORT).show()
                    onBack()
                },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
                    .padding(4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red,
                    contentColor = Color.White
                )
            ) {
                Text(
                    "Delete",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onBack,
            modifier = Modifier
                .padding(8.dp)
                .width(200.dp)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.LightGray,
                contentColor = Color.White
            )
        ) {
            Text(
                "Back to All Pictures",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}