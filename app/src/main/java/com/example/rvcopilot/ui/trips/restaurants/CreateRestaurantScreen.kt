package com.example.rvcopilot.ui.trips.restaurants

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
import com.example.rvcopilot.data.Restaurant
import com.example.rvcopilot.data.User
import com.example.rvcopilot.model.TripViewModel
import com.example.rvcopilot.ui.components.tripImageList
import com.example.rvcopilot.ui.theme.LighterGreen


@Composable
fun CreateRestaurantScreen(
    currentUser: User,
    viewModel: TripViewModel,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    var cellular by remember { mutableStateOf("") }
    var wifi by remember { mutableStateOf("") }
    var foods by remember { mutableStateOf("") }

    // change ints to strings
    val tripImageNames = tripImageList.map { context.resources.getResourceEntryName(it) }
    var selectedImageName by remember { mutableStateOf(tripImageNames[0]) }
    var imageIndex by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TopBar(
                title = "Create Restaurants",
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

                        val restaurant = Restaurant(
                            name = name,
                            address = address,
                            phone = phone,
                            email = email,
                            type = type,
                            cellular = cellular,
                            wifi = wifi,
                            foods = foods,
                            imageName = assignImage,
                            createdBy = currentUser.username
                        )
                        viewModel.insertRestaurant(restaurant)
                        Toast.makeText(context, "Restaurant saved: $name", Toast.LENGTH_SHORT).show()
                        println("CreateRestaurantScreen: Restaurant saved = $name and foods = $foods")
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
                        "Save Restaurant",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
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
                val resId2 = context.resources.getIdentifier(
                    selectedImageName,
                    "drawable",
                    context.packageName
                ).takeIf { it != 0 } ?: R.drawable.p6140016

                Image(
                    painter = painterResource(id = resId2),
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

                LabeledFieldRest("Name of Restaurant", name) { name = it }
                LabeledFieldRest("Address", address) { address = it }
                LabeledFieldRest("Phone", phone) { phone = it }
                LabeledFieldRest("Email", email) { email = it }
                LabeledFieldRest("Type of restaurant", type) { type = it }
                LabeledFieldRest("Cellular", email) { email = it }
                LabeledFieldRest("WiFi", wifi) { wifi = it }
                LabeledFieldRest("Foods", foods) { foods = it }





                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val assignImage = selectedImageName

                        val restaurant = Restaurant(
                            name = name,
                            address = address,
                            phone = phone,
                            email = email,
                            type = type,
                            cellular = cellular,
                            wifi = wifi,
                            foods = foods,
                            imageName = assignImage,
                            createdBy = currentUser.username
                        )
                        viewModel.insertRestaurant(restaurant)
                        Toast.makeText(context, "Restaurant saved: $name", Toast.LENGTH_SHORT).show()
                        println("CreateRestaurantScreen: Restaurant saved = $name and foods = $foods")
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
                        "Save Restaurant",
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
fun LabeledFieldRest(
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

