package com.example.rvcopilot.ui.tools

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.rvcopilot.R
import com.example.rvcopilot.ui.components.TopBar
import com.example.rvcopilot.ui.theme.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.rvcopilot.model.RvParkViewModel

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
 * this is related to the composables lifecycle.
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
 * */

/**
 * () -> Unit:  Lambda expression.  ()= function does not accept arguments
 * Unit = 'Void' expression like used in C99.  Function does not return anything.
 * onStartClicked: () -> Unit.   This is a callback for the start button
 * *
 * */
@Composable
fun WeatherScreen(
    viewModel: RvParkViewModel,
    context: Context,  // Required for accessing system services & opening URLs
    onNavigateToTemperature: () -> Unit,
    onBackClick: () -> Unit

) {

    Scaffold(
        topBar = {
            TopBar(
                title = stringResource(R.string.weather),
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
            Image(
                painter = painterResource(id = R.drawable.rv_avatar_weather1),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Title
                Text(
                    text = stringResource(R.string.weather),
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // List of weather resources

                WeatherButton(context, R.string.weather_api)
                WeatherButton(context, R.string.radar)
                WeatherButton(context, R.string.sunset)


                Spacer(modifier = Modifier.height(32.dp))

                WeatherButton(context, R.string.cellular)
                WeatherButton(context, R.string.temperature)

                WeatherDetailButton("Temperature") {
                    println("Button clicked: Navigating to Temperature")
                    onNavigateToTemperature()
                }
            }
        }
    }
}

@Composable
fun WeatherButton(
    context: Context,
    resourceId: Int
) {
    val url = stringResource(resourceId)

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
                            LightBlue,
                            MediumBlue,
                            DeepBlue
                        ),
                        // color gradient
                        start = Offset(0f, 0f),
                        end = Offset(400f, 400f)
                    ),

                    shape = RoundedCornerShape(8.dp),
                )
                .clickable {
                    openUrl(context, url)
                },
            contentAlignment = Alignment.Center
        )
        {
            Text(
                text = url,
                fontSize = 26.sp,
                color = Color.Black,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

// Function to open URL in the browser
fun openUrl(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    context.startActivity(intent)
}

@Composable
fun WeatherDetailButton(
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