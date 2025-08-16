package com.example.rvcopilot.ui.trips.restaurants

import android.widget.Toast
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rvcopilot.R
import com.example.rvcopilot.ui.components.TopBar
import com.example.rvcopilot.data.Restaurant
import com.example.rvcopilot.data.User
import com.example.rvcopilot.model.TripViewModel
import com.example.rvcopilot.ui.theme.Azure

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
fun ReviewRestaurantsScreen(
    selectedRestaurant: Restaurant,
    currentUser: User,
    viewModel: TripViewModel,
    onSaveClick: (String) -> Unit,
    onBackClick: () -> Unit
) {


    val context = LocalContext.current
    var restaurantReview by remember { mutableStateOf("") }

    LaunchedEffect(currentUser) {
        println("ReviewScreen: currentUser loaded = $currentUser")
    }

    LaunchedEffect(selectedRestaurant.firebaseId) {
        viewModel.loadReviewsForRestaurant(selectedRestaurant.firebaseId)
    }

    val allReviews by viewModel.reviewRestaurant.collectAsState()

    Scaffold(
        topBar = {
            Box {
                Image(
                    painter = painterResource(id = R.drawable.rv_avatar_3252),
                    contentDescription = "TopBar Background",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                )
                TopBar(
                    title = "Review: ${selectedRestaurant.name}",
                    onBackClick = onBackClick
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            //Background image for screen body
            Image(
                painter = painterResource(id = R.drawable.rv_avatar_3244),
                contentDescription = "Background Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            //Foreground content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Write a review for: \n ${selectedRestaurant.name}",
                    fontSize = 26.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = restaurantReview,
                    onValueChange = { restaurantReview = it },
                    label = { Text("Write your site review") },
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 4.dp,
                            color = Azure,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .height(260.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            if (restaurantReview.isNotBlank()) {
                                onSaveClick(restaurantReview)
                                // recompose the viewModel
                                viewModel.loadReviewsForRestaurant(selectedRestaurant.firebaseId)
                                Toast.makeText(
                                    context,
                                    "Review saved!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                restaurantReview = ""  // clears all text
                            } else {
                                Toast.makeText(context, "Please write a review", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Azure),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .height(50.dp)
                            //.width(150.dp)
                            .weight(1f)
                            .padding(start = 8.dp)

                    ) {
                        Text(
                            "Save Review",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Button(
                        onClick = {
                            viewModel.deleteReviewsForRestaurant(selectedRestaurant.firebaseId)
                            Toast.makeText(context, "All reviews cleared", Toast.LENGTH_SHORT)
                                .show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .height(50.dp)
                            .weight(1f)
                            //.width(150.dp)
                            .padding(start = 8.dp)
                    ) {
                        Text(
                            "Clear All",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Scrollable Current and Older Reviews:",
                    fontSize = 20.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                LazyColumn (
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)

                ) {

                    items(allReviews) { review ->
                        Text(
                            text = "${review.username}: ${review.text}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Blue,
                            modifier = Modifier
                                .padding(vertical = 6.dp)
                                .fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}