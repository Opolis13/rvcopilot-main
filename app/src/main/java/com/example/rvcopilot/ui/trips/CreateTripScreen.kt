package com.example.rvcopilot.ui.trips

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import com.example.rvcopilot.ui.components.TopBar
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rvcopilot.R
import com.example.rvcopilot.data.Trips
import com.example.rvcopilot.data.User
import com.example.rvcopilot.model.TripViewModel
import com.example.rvcopilot.ui.components.tripImageList
import com.example.rvcopilot.ui.theme.LighterGreen


@Composable
fun CreateTripScreen(
    currentUser: User,
    viewModel: TripViewModel,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current

    var destination by remember { mutableStateOf("") }
    var destaddress by remember { mutableStateOf("") }
    var activities by remember { mutableStateOf("") }
    var recreation by remember { mutableStateOf("") }
    var restaurants by remember { mutableStateOf("") }

    var imageIndex by remember { mutableStateOf(0) }
    // change ints to strings
    val tripImageNames = tripImageList.map { context.resources.getResourceEntryName(it) }
    var selectedImageName by remember { mutableStateOf(tripImageNames[0]) }
    println("on CreateTripScreen: selectedImageName: $selectedImageName")


    Scaffold(
        topBar = {
            TopBar(
                title = "Create Trips",
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
                        val assignImage = selectedImageName
                        println("CreateTripScreen button click: assignImage: $assignImage")

                        val trip = Trips(
                            destination = destination,
                            destaddress = destaddress,
                            recreation = recreation,
                            activities = activities,
                            restaurants = restaurants,
                            imageName = assignImage,
                            createdBy = currentUser.username
                        )
                        viewModel.insertTrip(trip)
                        Toast.makeText(context, "Trip saved: $destination", Toast.LENGTH_SHORT).show()
                        println("CreateTripScreen: Trip saved with destination = $destination and address = $destaddress")
                        onBackClick()
                    },
                    modifier = Modifier
                        .width(220.dp)
                        .padding(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LighterGreen,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        "Save Trip",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        //modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                Text(
                    text = "Enter Trip Details (Scroll up)",
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
                val resId = context.resources.getIdentifier(
                    selectedImageName,
                    "drawable",
                    context.packageName
                ).takeIf { it != 0 } ?: R.drawable.p6140016

                Image(
                    painter = painterResource(id = resId),
                    contentDescription = "Selected Image",
                    modifier = Modifier
                        .size(200.dp)
                        .clickable {
                            imageIndex++
                            val drawableId = tripImageList[imageIndex % tripImageList.size]
                            selectedImageName = context.resources.getResourceEntryName(drawableId)
                        }
                        .padding(bottom = 16.dp)
                )

                LabeledField1("Trip Destination ", destination) { destination = it }
                LabeledField1("Destination Address", destaddress) { destaddress = it }
                LabeledField1("Recreation opportunities", recreation) { recreation = it }
                LabeledField1("Local Activities ", activities) { activities = it }
                LabeledField1("Restaurants", restaurants) { restaurants = it }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val assignImage = selectedImageName

                        val trip = Trips(
                            destination = destination,
                            destaddress = destaddress,
                            recreation = recreation,
                            activities = activities,
                            restaurants = restaurants,
                            imageName = assignImage,
                            createdBy = currentUser.username
                        )
                        viewModel.insertTrip(trip)
                        Toast.makeText(context, "Trip saved: $destination", Toast.LENGTH_SHORT).show()
                        onBackClick()
                    },
                    modifier = Modifier
                        .width(220.dp)
                        .padding(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LighterGreen,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        "Save Trip",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        //modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun LabeledField1(
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