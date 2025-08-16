package com.example.rvcopilot.ui.trips.restaurants

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rvcopilot.R
import com.example.rvcopilot.ui.components.TopBar
import com.example.rvcopilot.data.Restaurant
import com.example.rvcopilot.model.TripViewModel
import com.example.rvcopilot.ui.components.tripImageList
import com.example.rvcopilot.ui.theme.*

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
 * other methods used
 *  * -brush:  linear gradient
 *  * https://developer.android.com/develop/ui/compose/graphics/draw/brush
 *  * val linear = Brush.linearGradient(listOf(Color.Red, Color.Blue))
 * */


@Composable
fun ListRestaurantsScreen(
    viewModel: TripViewModel,
    onRestaurantClick: (Restaurant) -> Unit,
    onBackClick: () -> Unit,
) {
    val context = LocalContext.current
    // change ints to strings
    val tripImageNames = tripImageList.map { context.resources.getResourceEntryName(it) }
    var selectedImageName by remember { mutableStateOf(tripImageNames[0]) }

    val restaurants by viewModel.restaurants.collectAsState(initial = emptyList())
    println("on ListRestaurantsScreen")


    Scaffold(
        topBar = {
            TopBar(
                title = "List of Restaurants",
                onBackClick = onBackClick
            )
        }
    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Button(
                    onClick = { viewModel.navigateToCreateRestaurant() },  // <-- you already have this function
                    modifier = Modifier
                        .fillMaxWidth()
                        //.width(200.dp)
                        .height(60.dp)
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LightBlue)
                ) {
                    Text(
                        text = "Create New Restaurant",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 22.sp,
                        color = Color.Blue
                    )
                }
            }
            // listing of created restaurants
            items(restaurants) { restaurant ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onRestaurantClick(restaurant) }
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    println("Loading Restaurant: ${restaurant.name}, image ID: ${restaurant.imageName}")

                    val resId = context.resources.getIdentifier(
                        restaurant.imageName,
                        "drawable",
                        context.packageName
                    ).takeIf { it != 0 } ?: R.drawable.p6140016

                    Image(
                        painter = painterResource(id = resId),
                        contentDescription = null,
                        modifier = Modifier
                            .size(120.dp)
                            .padding(end = 12.dp)
                    )

                    Box(
                        modifier = Modifier
                            .width(210.dp)
                            .height(60.dp)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        LightOrange,
                                        MediumOrange,
                                        DeepOrange
                                    ),
                                    start = Offset(0f, 0f),
                                    end = Offset(400f, 400f)
                                ),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { onRestaurantClick(restaurant) }
                            .padding(horizontal = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = restaurant.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}