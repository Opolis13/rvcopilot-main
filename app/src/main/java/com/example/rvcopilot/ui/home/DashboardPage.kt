package com.example.rvcopilot.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.ui.Alignment
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rvcopilot.R
import com.example.rvcopilot.ui.components.TopBar
import com.example.rvcopilot.data.RvPark
import com.example.rvcopilot.model.RvParkViewModel
import com.example.rvcopilot.ui.components.RvParkItem


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
fun HomePage(
    viewModel: RvParkViewModel,
    onViewRvPark: (RvPark) -> Unit = {},
    onNavigateToRvParkGrid: () -> Unit,
    onNavigateToTrips: () -> Unit,
    onNavigateToUserBio: () -> Unit,
    onNavigateToMap: () -> Unit,
    onNavigateToFavoriteSites: () -> Unit,
    onBackClick: (() -> Unit)? = null
) {

    val rvParks by viewModel.rvParks.collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopBar(
                title = "Dashboard",
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Background Image
            Image(
                painter = painterResource(id = R.drawable.avatar_rvcopilot_image),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Dashboard: Click on the Image",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)

                )
                val buttons = listOf(
                    //"All Campsites" to onNavigateToCreateCampsites,
                    "Campsite database" to onNavigateToRvParkGrid,
                    "Trips" to onNavigateToTrips,
                    //"List Favorite Sites" to onNavigateToFavoriteSites,
                    "User Bio" to onNavigateToUserBio,
                    "Location Map" to onNavigateToMap,
                    //"Trips" to onNavigateToTrips,
                    //"RV Parks Grid" to onNavigateToRvParkGrid,

                    )
                Spacer(modifier = Modifier.height(16.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(1f), // enough to show 3 rows
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(buttons) { (label, action) ->
                        val iconRes = when (label) {
                            //"Create Campsites" -> R.drawable.rv_avatar_3240
                            "Campsite database" -> R.drawable.rv_avatar_3240
                            "RV Parks Grid" -> R.drawable.rv_avatar_koa_albany_corvallis
                            "Trips" -> R.drawable.rv_avatar_3244
                            "User Bio" -> R.drawable.rv_avatar_3250
                            "Location Map" -> R.drawable.rv_avatar_glacier_bay1
                            //"List Favorite Sites" -> R.drawable.avatar_list

                            else -> R.drawable.rv_avatar_3244
                        }

                        Button(
                            onClick = action,
                            modifier = Modifier
                                //.aspectRatio(1f)
                                .size(150.dp)
                                //.height(100.dp)
                                .fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                            shape = RoundedCornerShape(16.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.BottomCenter
                                //horizontalAlignment = Alignment.CenterHorizontally,
                                //verticalArrangement = Arrangement.Center
                            ) {
                                Image(
                                    painter = painterResource(id = iconRes),
                                    contentDescription = label,
                                    modifier = Modifier
                                        //.size(150.dp)
                                        //.align(Alignment.Center)
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(16.dp))
                                    //.padding(bottom = 4.dp)
                                )
                                Surface(
                                    color = Color.Black.copy(alpha = 0.6f),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(4.dp),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = label,
                                        modifier = Modifier
                                            .padding(4.dp)
                                            .fillMaxWidth(),
                                        textAlign = TextAlign.Center,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp

                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                /**
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(rvParks) { rvpark ->
                        val parkName = rvpark.name
                        println("Displaying RV Park: $parkName")

                        RvParkItem(
                            rvpark = rvpark,
                            onClick = {
                                println("Button clicked for: $parkName")
                                tripViewModel.navigateToCampgroundDetails(rvpark)
                            }
                        )
                    }
                } */
            }
        }
    }
}



