package com.example.rvcopilot.ui.details.trip

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.Image
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rvcopilot.ui.components.TopBar
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.example.rvcopilot.R
import com.example.rvcopilot.model.TripViewModel
import com.example.rvcopilot.model.PictureViewModel
import com.example.rvcopilot.ui.components.tripImageList


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


@Composable
fun TripInfoDetailsScreen(
    viewModel: TripViewModel,
    pictureViewModel: PictureViewModel,
    onLocationClick: () -> Unit,
    onBackClick: () -> Unit

) {
    val context = LocalContext.current

    val selectedTrip by viewModel.selectedTrip.collectAsState()

    if (selectedTrip == null) {
        Text(
            text = "Loading campground data...",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )
        return
    }

    println("TripInfoDetailsScreen: selectedTrip = ${selectedTrip!!.destination}")

    val allPictures by pictureViewModel.allPictures.collectAsState()
    println("TripInfoDetailsScreen: allPictures = ${allPictures.size} total")
    allPictures.forEach {
        println("Picture label = '${it.label}', imageUri = '${it.imageUri}'")
        val match = it.label.contains(selectedTrip!!.destination, ignoreCase = true)
        println("Label: '${it.label}' vs '${selectedTrip!!.destination}' → match=$match")
    }

    val tripPictures = allPictures.filter {
        it.label.contains(selectedTrip!!.destination, ignoreCase = true)
    }
    println("TripInfoDetailsScreen: after the filter tripPictures = ${tripPictures.size}")

    val selectedImageName = selectedTrip!!.imageName

    tripPictures.forEach {
        println("Picture label = ${it.label}, imageUri = ${it.imageUri}")
    }


    Scaffold(
        topBar = {
            TopBar(
                title = selectedTrip!!.destination,
                onBackClick = onBackClick
                //onBackClick = { viewModel.navigateToRvParkList() }
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

            val resId = context.resources.getIdentifier(
                selectedImageName,
                "drawable",
                context.packageName
            ).takeIf { it != 0 } ?: R.drawable.p6140016

            println("TripInfoDetailsScreen: picture: '${selectedImageName}', resId:'${resId}'")

            Image(
                painter = painterResource(id = resId),
                contentDescription = "Image of ${selectedTrip!!.destination}",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(bottom = 8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))


            TripInfoRow("destination", selectedTrip!!.destination)
            TripInfoRow("destination address", selectedTrip!!.destaddress)
            TripInfoRow("Recreation Opportunities", selectedTrip!!.recreation)
            TripInfoRow("Activities", selectedTrip!!.activities)
            TripInfoRow("Restaurants", selectedTrip!!.restaurants)


            /**
             * small grid of pictures
             * */
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Trip Photos:",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp, top = 16.dp, bottom = 8.dp)
            )
            if (tripPictures.isEmpty()) {
                Text(
                    text = "No pictures saved yet for this trip.",
                    fontSize = 18.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(start = 8.dp, bottom = 12.dp)
                )
            } else {

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(horizontal = 8.dp),
                ) {
                    items(tripPictures) { picture ->
                        val resId2 = context.resources.getIdentifier(
                            picture.imageUri,
                            "drawable",
                            context.packageName
                        ).takeIf { it != 0 } ?: R.drawable.trip_p6150089

                        println("TripInfoDetails image: ${picture.imageUri}, resId: $resId2")

                        Image(
                            painter = painterResource(id = resId2),
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
}

@Composable
fun TripInfoRow(
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




