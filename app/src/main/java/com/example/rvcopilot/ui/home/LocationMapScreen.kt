package com.example.rvcopilot.ui.home

import android.location.Location
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rvcopilot.data.FirebaseCampsite
import com.example.rvcopilot.model.FacilitiesViewModel
import com.example.rvcopilot.ui.components.TopBar
import com.example.rvcopilot.model.LocationViewModel
import com.example.rvcopilot.ui.theme.LightOrange
import com.example.rvcopilot.ui.tools.HaversineApp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.GoogleMapComposable
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

/**
 * Map and markers adapted from Codelab found at
 * https://developers.google.com/codelabs/maps-platform/maps-platform-101-compose#0
 */

@Composable
fun LocationMapScreen(
    onBackClick: () -> Unit,
    onHomeClick: () -> Unit,
    locationViewModel: LocationViewModel,
    facilitiesViewModel: FacilitiesViewModel
) {
    //    Current location of user
    val location by locationViewModel.location.collectAsState()
    println("on LocationMapScreen")

    val cameraPositionState = rememberCameraPositionState()

    // All campsites list
    val campsites by facilitiesViewModel.firebaseCampsites.collectAsState()

    // Campsites within radius list
    val nearbyCampsites by locationViewModel.nearbyCampsites.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        println("LocationMapScreen launch: loadCampsites")
        facilitiesViewModel.loadCampsites()
    }

    LaunchedEffect(cameraPositionState.isMoving) {
        println("LocationMapScreen launch: filterNearbyCampsites")
        if (!cameraPositionState.isMoving) {
            locationViewModel.filterNearbyCampsites(cameraPositionState.position.target)
        }
    }
    // Load initial location nearby campsites
    LaunchedEffect(location) {
        if (location != null) {
            cameraPositionState.move(
                CameraUpdateFactory.newLatLngZoom(
                    location!!, 8f
                )
            )
            locationViewModel.filterNearbyCampsites(center = location!!, radius = 80.46)
        }
    }

    Scaffold(
        topBar = {
            TopBar(
                title = "Location Map",
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
                    .padding(16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PermissionHandler(
                    onPermissionGranted = {
                        println("LocationMapScreen:  PermissionHandler() call")
                        locationViewModel.getUserLocation()
                    }
                )
                Text(
                    text = "Location Map",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                )

                // Search bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Search Location") },
                    singleLine = true,
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                locationViewModel.searchAndMoveCamera(
                                    searchQuery,
                                    cameraPositionState
                                )
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search"
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
                // Map and list views
                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    if (isVisible) {
                        LazyColumn(
                            contentPadding = paddingValues,
                            modifier = Modifier
                                .fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            item {
                                if (nearbyCampsites.isEmpty()) {
                                    Text("No campsites loaded yet.", color = Color.Red)
                                } else {
                                    Text(
                                        "Loaded ${nearbyCampsites.size} campsites:\nList is Scrollable",
                                        color = Color.Blue,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 20.sp,
                                        modifier = Modifier
                                            .padding(8.dp)
                                    )
                                }
                            }
                            items(nearbyCampsites) { campsite ->
                                val distKm = HaversineApp.calculateDistance(
                                    location!!.latitude,
                                    location!!.longitude,
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
                                        Column(modifier = Modifier.padding(16.dp)) {
                                            Text(
                                                "Name: ${campsite.name}",
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text("Address: ${campsite.address}")
                                            Text("Phone: ${campsite.phone}")
                                            Text("State: ${campsite.state}, ZIP: ${campsite.zipCode}")
                                            Text("WiFi: ${if (campsite.wifi) "Yes" else "No"}, Pets Allowed: ${if (campsite.pets) "Yes" else "No"}")
                                            Text("Rating: ${campsite.rating} (${campsite.numReviews} reviews)")
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text("Amenities: ${campsite.amenities.joinToString()}")
                                            Text("Hookups: ${campsite.hookups.joinToString()}")
                                            Text(
                                                "Distance from user: ${"%.1f".format(distMiles)} Miles",
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = Color.Blue
                                            )
                                        }
                                    }

                                }
                            }
                        }
                    } else {
                        if (location != null) {
                            LocationMap(
                                location = location!!,
                                cameraPositionState = cameraPositionState,
                                campsites = nearbyCampsites
                            )
                        } else {
                            Column(
                                modifier = Modifier,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("Getting your location...")
                                CircularProgressIndicator()
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = onHomeClick,
                        modifier = Modifier.width(220.dp)
                    ) {
                        Text(
                            "Go to Dashboard",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    // List of nearby campsites
                    Button(
                        onClick = { isVisible = !isVisible }
                    ) {
                        Text(
                            if (isVisible) "Show Map" else "Show List"
                        )
                    }
                }
            }
        }
    }
}
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionHandler(
    onPermissionGranted: () -> Unit
) {
    val locationPermissionState = rememberPermissionState(
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )

    LaunchedEffect(Unit) {
        locationPermissionState.launchPermissionRequest()
    }

    LaunchedEffect(locationPermissionState.status) {
        if (locationPermissionState.status.isGranted) {
            println("LocationMapScreen- PermissionHandler LaunchedEffect: Location permission granted")
            onPermissionGranted()
        } else {
            println("LocationMapScreen- PermissionHandler LaunchedEffect: Location permission NOT granted")
        }
    }

    if(!locationPermissionState.status.isGranted) {
        Text("Location permission is required for local information.")
    }
}

@Composable
fun LocationMap(
    location: LatLng,
    cameraPositionState: CameraPositionState,
    campsites: List<FirebaseCampsite>
) {
    val center = cameraPositionState.position.target
    LaunchedEffect(location) {
        cameraPositionState.move(
            CameraUpdateFactory.newLatLngZoom(location, 8f)
        )
    }
    GoogleMap(
        modifier = Modifier
            .width(400.dp)
            .height(400.dp),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(isMyLocationEnabled = true)
    ) {
        CampsiteMarkers(campsites)
        Circle(
            center = center,
            radius = 80467.0,
            fillColor = Color(0x3300BFFF),
            strokeColor = Color(0xFF00BFFF),
            strokeWidth = 2f
        )
    }
}


@Composable
@GoogleMapComposable
fun CampsiteMarkers(
    campsites: List<FirebaseCampsite>
) {
    campsites.forEach { campsite ->
        val location = LatLng(campsite.coordinates.lat, campsite.coordinates.lng)
        Marker(
            state = rememberMarkerState(position = location),
            title = campsite.name,
            tag = campsite
        )
    }
}