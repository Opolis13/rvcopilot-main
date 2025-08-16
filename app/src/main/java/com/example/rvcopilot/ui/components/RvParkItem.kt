package com.example.rvcopilot.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rvcopilot.data.RvPark


/**
 * this function is used to display a list of icons and buttons with
 * RV Park names.
 * icon and button are side by side
 * */
@Composable
fun RvParkItem(
    rvpark: RvPark,
    onClick: () -> Unit
) {

    val context = LocalContext.current
    val imageResId = remember(rvpark.imageName) {
        val resId = context.resources.getIdentifier(
            rvpark.imageName,
            "drawable",
            context.packageName
        )
        if (resId != 0) resId else android.R.drawable.ic_menu_report_image
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = imageResId),
            contentDescription = rvpark.address,
            modifier = Modifier
                .size(100.dp)
                .padding(end = 16.dp)
        )

        Button(
            onClick =  {
                //println("Button clicked for: $airplaneName")
                onClick()
                //onNavigateToAirplaneDetails(airplane)
            },

            colors = ButtonDefaults.buttonColors(
                containerColor = Color.LightGray
            ),
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier.width(300.dp)
        ) {
            Text(
                text = rvpark.name,
                fontSize = 20.sp,
                color = Color.Blue,
                fontWeight = FontWeight.Bold
            )
        }
    }
}