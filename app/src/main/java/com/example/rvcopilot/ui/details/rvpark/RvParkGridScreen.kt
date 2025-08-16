package com.example.rvcopilot.ui.details.rvpark

import android.location.Geocoder
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rvcopilot.ui.components.TopBar
import com.example.rvcopilot.data.RvPark
import com.example.rvcopilot.data.User
import com.example.rvcopilot.model.RvParkViewModel
import com.example.rvcopilot.model.FacilitiesViewModel
import com.example.rvcopilot.data.FirebaseCampsite
import com.example.rvcopilot.model.LocationViewModel
import com.example.rvcopilot.model.TripViewModel
import com.example.rvcopilot.ui.theme.LightOrange
import com.example.rvcopilot.ui.tools.HaversineApp

@Composable
fun RvParkGridScreen(
    currentUser: User,
    viewModel: RvParkViewModel,
    tripViewModel: TripViewModel,
    facilitiesViewModel: FacilitiesViewModel,
    locationViewModel: LocationViewModel,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val locationState by locationViewModel.location.collectAsState()
    var locationName by remember { mutableStateOf<String?>(null) }
    print("on RvParkGridScreen: before launch coordinates")
    val (currentLat, currentLon) = locationState?.let {
        LaunchedEffect(it) {
            try {
                val geocoder = Geocoder(context)
                val addresses = geocoder.getFromLocation(it.latitude, it.longitude, 1)
                if (!addresses.isNullOrEmpty()) {
                    val city = addresses[0].locality ?: ""
                    val state = addresses[0].adminArea ?: ""
                    locationName = "$city, $state"
                } else {
                    locationName = "Unknown Location"
                }
            } catch (e: Exception) {
                locationName = "Unknown Location"
            }
        }
        println("latitude: ${it.latitude}, longitude: ${it.longitude}")
        Pair(it.latitude, it.longitude)// similar to a tuple of data: pair.first = latitude
    } ?: run {
        println("can't find the location")
        locationName = "Bend, Oregon"
        Pair(44.06, -121.31)
    }


    // Load campsites automatically
    LaunchedEffect(Unit) {
        //facilitiesViewModel.fetchCampsites()
        facilitiesViewModel.loadCampsites()
    }

    Scaffold(
        topBar = {
            TopBar(
                title = "Campsite List From\nDatabase (non-editable)",
                onBackClick = onBackClick
            )
        }
    )
    { paddingValues ->

        val campsiteList by facilitiesViewModel.firebaseCampsites.collectAsState()

        LazyColumn(
            contentPadding = paddingValues,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                if (campsiteList.isEmpty()) {
                    Text("No campsites loaded yet.", color = Color.Red)
                } else {
                    Text(
                        "Loaded ${campsiteList.size} campsites:\nList is Scrollable, click star to favorite",
                        color = Color.Blue,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp,
                        modifier = Modifier
                            .padding(8.dp)
                    )
                    //"Current Location: $currentLocationName\n($currentLat, $currentLon)",
                    Text(
                        "Current Location: ${locationName ?: "Loading..."}\n($currentLat, $currentLon)",
                        color = Color.Black,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(start = 8.dp, bottom = 12.dp)
                    )
                }
            }
                /**
            Button(
            onClick = {
            facilitiesViewModel.fetchCampsites()
            //facilitiesViewModel.fetchCampsitesFromFirebase()
            facilitiesViewModel.loadCampsites()
            },
            modifier = Modifier
            .fillMaxWidth()
            //.width(200.dp)
            .height(60.dp)
            .padding(vertical = 8.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = LightBlue)
            ) {
            Text(
            text = "Load Campsites from DB",
            fontWeight = FontWeight.ExtraBold,
            fontSize = 22.sp,
            color = Color.Blue
            )
            }
            }
            items(campsiteList) { campsite ->
            Card(
            modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
            elevation = CardDefaults.cardElevation(4.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = LightOrange)
            ) {
            Column(modifier = Modifier.padding(16.dp)) {
            Text(
            text = "Name: ${campsite.campsiteName}",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
            )
            Text(text = "Type: ${campsite.campsiteType}")
            Text(text = "Loop: ${campsite.loop}")
            Text(text = "Lat: ${campsite.campsiteLatitude}, Lon: ${campsite.campsiteLongitude}")
            }*/


            items(campsiteList) { campsite ->
                var isStarred by remember { mutableStateOf(false) }
                println("CAMPSITE_COORDINATES: lat=${campsite.coordinates.lat}, lon=${campsite.coordinates.lng}")
                // use the Haversine Singleton Object: does not hold a state
                val distKm = HaversineApp.calculateDistance(
                    currentLat,
                    currentLon,
                    campsite.coordinates.lat,
                    campsite.coordinates.lng
                )
                val distMiles = distKm * 0.621371 // note: a 10k = 6.21 SM

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    elevation = CardDefaults.cardElevation(4.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = LightOrange)
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        // inside the box scope are the star and DB items
                        IconButton( // for the star
                            onClick = {
                                isStarred = !isStarred
                                if (isStarred) {
                                    facilitiesViewModel.saveFavoriteCampsite(
                                        campsite,
                                        currentUser.username,
                                        onSuccess={
                                            Toast.makeText(
                                                context,
                                                "Saved '${campsite.name}' to favorites",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    )
                                }
                            },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = "Favorite",
                                tint = if (isStarred) Color.Red else Color.Gray
                            )
                        }

                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Name: ${campsite.name}", fontWeight = FontWeight.Bold)
                            Text("Address: ${campsite.address}")
                            Text("Phone: ${campsite.phone}")
                            Text("State: ${campsite.state}, ZIP: ${campsite.zipCode}")
                            Text("WiFi: ${if (campsite.wifi) "Yes" else "No"}, Pets Allowed: ${if (campsite.pets) "Yes" else "No"}")
                            Text("Rating: ${campsite.rating} (${campsite.numReviews} reviews)")
                            Text("Coordinates: ${campsite.coordinates.lat}, ${campsite.coordinates.lng}")
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Amenities: ${campsite.amenities.joinToString()}")
                            Text("Hookups: ${campsite.hookups.joinToString()}")
                            Text(
                                "Distance from Current Location --> ${"%.1f".format(distMiles)} Miles",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.Blue
                            )
                        }
                    }

                }
            }
        }
    }
}