package com.example.rvcopilot.ui.campsites

import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import androidx.compose.foundation.Image
import com.example.rvcopilot.ui.components.imageList
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rvcopilot.ui.components.TopBar
import com.example.rvcopilot.data.RvPark
import com.example.rvcopilot.data.User
import com.example.rvcopilot.model.RvParkViewModel
import com.example.rvcopilot.ui.theme.LighterGreen

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
fun CreateCampsitesScreen(
    currentUser: User,
    onBackClick: () -> Unit,
    viewModel: RvParkViewModel = viewModel()
) {
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var services by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    var pad by remember { mutableStateOf("") }
    var pets by remember { mutableStateOf("") }
    var power by remember { mutableStateOf("") }
    var cellular by remember { mutableStateOf("") }
    var wifi by remember { mutableStateOf("") }
    var cable by remember { mutableStateOf("") }
    var amenity by remember { mutableStateOf("") }

    var selectedImageRes by remember { mutableStateOf(imageList[0]) }
    var imageIndex by remember { mutableStateOf(0) }


    Scaffold(
        topBar = {
            TopBar(
                title = "Create Favorite Campsite List",
                onBackClick = onBackClick
            )
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top button
                Button(
                    onClick = {
                        try {
                            val assignImage = context.resources.getResourceEntryName(selectedImageRes)

                            val rvPark = RvPark(
                                name = name,
                                address = address,
                                phone = phone,
                                email = email,
                                services = services,
                                type = type,
                                power = power,
                                pad = pad,
                                pets = pets,
                                cellular = cellular,
                                wifi = wifi,
                                cable = cable,
                                amenity = amenity,
                                createdBy = currentUser.username,
                                imageName = assignImage
                            )
                            viewModel.insertRvPark(rvPark, currentUser.username)
                            Toast.makeText(context,
                                "Campsite updated successfully!",
                                Toast.LENGTH_SHORT
                            ).show()
                            println("Saved picture: label='${assignImage}'")
                            println("CreateCampsiteScreen: RvPark object -> '${rvPark}' ")
                            onBackClick()
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Toast.makeText(context, "Error saving campsite: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    },
                    modifier = Modifier
                        .width(220.dp)
                        .padding(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LighterGreen,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                {
                    Text(
                        "Save Campsite",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold

                    )
                }

                Text(
                    text = "Enter RV Park Details (Scroll up)",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Text(
                    text = "Click image to change",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Image(
                    painter = painterResource(id = selectedImageRes),
                    contentDescription = "Selected Image",
                    modifier = Modifier
                        .size(200.dp)
                        .clickable {
                            imageIndex++
                            selectedImageRes = imageList[imageIndex % imageList.size]
                        }
                        .padding(bottom = 16.dp)
                )

                LabeledField("Name of Campground and/or ID", name) { name = it }
                LabeledField("Address", address) { address = it }
                LabeledField("Phone", phone) { phone = it }
                LabeledField("Email", email) { email = it }
                LabeledField("Services Available: dump, water, sewer, hookups", services) { services = it }
                LabeledField("Type: Public land, RV Park, State Park, County Park, ", type) { type = it }
                LabeledField("Power: Watts, Amps, Line Voltage", power) { power = it }
                LabeledField("Pad", pad) { pad = it }
                LabeledField("Pets", pets) { pets = it }
                LabeledField("Cellular, carrier: ave. bars, Wifi, cable", cellular) { cellular = it }
                LabeledField("Wifi", wifi) { wifi = it }
                LabeledField("Cable", cable) { cable = it }
                LabeledField("Amenities: picnic, fire, patio, etc.", amenity) { amenity = it }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        try {
                            val assignImage = context.resources.getResourceEntryName(selectedImageRes)

                            val rvPark = RvPark(
                                name = name,
                                address = address,
                                phone = phone,
                                email = email,
                                services = services,
                                type = type,
                                power = power,
                                pad = pad,
                                pets = pets,
                                cellular = cellular,
                                wifi = wifi,
                                cable = cable,
                                amenity = amenity,
                                createdBy = currentUser.username,
                                imageName = assignImage
                            )
                        viewModel.insertRvPark(rvPark, currentUser.username)
                        Toast.makeText(context, "Campsite updated successfully!", Toast.LENGTH_SHORT).show()
                        println("Saved picture: label='${assignImage}'")
                        println("CreateCampsiteScreen: RvPark object -> '${rvPark}' ")
                        onBackClick()
                            } catch (e: Exception) {
                            e.printStackTrace()
                            Toast.makeText(context, "Error saving campsite: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    },
                    modifier = Modifier
                        .width(220.dp)
                        .padding(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LighterGreen,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                {
                    Text(
                        "Save Campsite",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold

                    )
                }
            }
        }
    }
}

@Composable
fun LabeledField(
    label: String,
    value: String,
    onChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        textStyle = LocalTextStyle.current.copy(
            fontSize = 16.sp
        ),
        shape = RoundedCornerShape(8.dp),

        )
}