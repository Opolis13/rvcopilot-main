package com.example.rvcopilot.ui.details.rvpark

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
import com.example.rvcopilot.data.RvPark
import com.example.rvcopilot.model.RvParkViewModel
import com.example.rvcopilot.ui.theme.*
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.clickable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.example.rvcopilot.R
import com.example.rvcopilot.model.TripViewModel


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
fun RvParkDetailsScreen(
    viewModel: RvParkViewModel,
    tripViewModel: TripViewModel,
    selectedRvPark: RvPark,
    onBackClick: () -> Unit,
    onEditClick: (RvPark) -> Unit
) {
    val context = LocalContext.current
    Scaffold(
        topBar = {
            TopBar(
                title = selectedRvPark.name,
                onBackClick = onBackClick
            )
        }
    ) { paddingValues ->  // lambda expression
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Background Image
            val imageResId = remember(selectedRvPark.imageName) {
                val resId = context.resources.getIdentifier(
                    selectedRvPark.imageName,
                    "drawable",
                    context.packageName
                )
                if (resId != 0) resId else R.drawable.avatar_rvcopilot_image
            }
            Image(
                painter = painterResource(id = imageResId),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "RV park details list",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DetailButton("Campground Details") {
                        println("RvParkDetailsScreen - Button clicked: Navigating to RV park Grid")
                        //viewModel.navigateToCampgroundDetails(selectedRvPark)
                        tripViewModel.storeSelectedRvPark(selectedRvPark)
                        tripViewModel.navigateToCampgroundDetails(selectedRvPark)
                    }

                    DetailButton("Weather") {
                        println("Button clicked: Navigating to Weather")
                        tripViewModel.navigateToWeather()
                    }

                    //DetailButton("Reviews") {
                    //    println("Button clicked: Navigating to Site Review")
                    //    viewModel.navigateToSiteReview()
                    //}
                    DetailButton("Add Pictures") { // page not started yet
                        println("Button clicked: Navigating to Pictures")
                        tripViewModel.navigateToCampPictures()
                    }

                    Button(
                        onClick = {
                            tripViewModel.navigateToEditCampsite()
                        },
                        modifier = Modifier
                            .width(220.dp)
                            .align(Alignment.CenterHorizontally)
                            .padding(top = 16.dp),
                        colors = ButtonDefaults
                            .buttonColors(
                                containerColor = Color.LightGray
                            ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            "Edit Campsite",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Blue
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))

                    Button(
                        onClick = {
                            viewModel.deleteRvPark(selectedRvPark)
                            onBackClick()
                        },
                        modifier = Modifier
                            .width(220.dp)
                            .align(Alignment.CenterHorizontally)
                            .padding(top = 8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            "Delete Campsite",
                            fontSize = 22.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }

                }
            }
        }
    }
}

@Composable
fun DetailButton(
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .wrapContentWidth(align = Alignment.CenterHorizontally)
    ) {

        Box(
            modifier = Modifier
                .width(320.dp)
                .height(60.dp)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            LightOrange,
                            MediumOrange,
                            DeepOrange
                        ),
                        // color gradient
                        start = Offset(0f, 0f),
                        end = Offset(400f, 400f)
                    ),

                    shape = RoundedCornerShape(8.dp),
                )
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        )
        {
            Text(
                text = text,
                fontSize = 26.sp,
                color = Color.Black,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}
