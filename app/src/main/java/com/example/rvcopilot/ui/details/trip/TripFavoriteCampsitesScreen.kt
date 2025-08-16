package com.example.rvcopilot.ui.details.trip

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.rvcopilot.R
import com.example.rvcopilot.ui.components.TopBar
import com.example.rvcopilot.data.User
import com.example.rvcopilot.data.RvPark
import com.example.rvcopilot.model.RvParkViewModel
import com.example.rvcopilot.ui.theme.*

@Composable
fun TripFavoriteCampsitesScreen(
    currentUser: User,
    rvParkViewModel: RvParkViewModel,
    onRvParkClick: (RvPark) -> Unit,
    onBackClick: () -> Unit
) {
    println("on the TripFavoriteCampsitesScreen")
    println("Current user: ${currentUser.username}")

    val rvParks by rvParkViewModel.rvParks.collectAsState(initial = emptyList())

    LaunchedEffect(currentUser.username) {
        rvParkViewModel.loadRvParks(currentUser.username)
    }


    Scaffold(
        topBar = {
            TopBar(title = "Editable Campsites", onBackClick = onBackClick)
        }
    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(rvParks) { rvPark ->
                val context = LocalContext.current
                val imageResId = remember(rvPark.imageName) {
                    context.resources.getIdentifier(
                        rvPark.imageName,
                        "drawable",
                        context.packageName
                    )
                }
                val checkImageResId = if (imageResId != 0) imageResId else R.drawable.avatar_rvcopilot_image

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onRvParkClick(rvPark) }
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = checkImageResId),
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
                                    colors = listOf(LightOrange, MediumOrange, DeepOrange),
                                    start = Offset(0f, 0f),
                                    end = Offset(400f, 400f)
                                ),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { onRvParkClick(rvPark) }
                            .padding(horizontal = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = rvPark.name,
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