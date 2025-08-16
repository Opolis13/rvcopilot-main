package com.example.rvcopilot.ui.tools

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rvcopilot.ui.components.TopBar
//import com.example.rv_copilot1.ui.theme.FahrenheitButtonColor
import com.example.rvcopilot.ui.theme.*

@Composable
fun TemperatureScreen(
    context: Context,
    onBackClick: () -> Unit
) {
    var celsiusInput by remember { mutableStateOf("") }
    var fahrenheitInput by remember { mutableStateOf("") }
    var fahrenheitResult by remember { mutableStateOf("") }
    var celsiusResult by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopBar(
                title = "Temperature Conversion",
                onBackClick = onBackClick
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Celsius to Fahrenheit",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
                value = celsiusInput,
                onValueChange = { celsiusInput = it },
                label = { Text("Enter °C") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Button(onClick = {
                val celsius = celsiusInput.toFloatOrNull()
                fahrenheitResult = if (celsius != null) {
                    String.format("%.1f °F", (celsius * 9 / 5) + 32)
                } else {
                    "Invalid input"
                }
            },
                colors = ButtonDefaults
                    .buttonColors(containerColor = FahrenheitButtonColor),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .width(200.dp)
                    .padding(vertical = 4.dp)


                ) {
                Text("Convert to Fahrenheit")
            }

            Text(
                text = fahrenheitResult,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold
            )


            Text(
                text = "Fahrenheit to Celsius",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
                value = fahrenheitInput,
                onValueChange = { fahrenheitInput = it },
                label = { Text("Enter °F") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Button(onClick = {
                val fahrenheit = fahrenheitInput.toFloatOrNull()
                celsiusResult = if (fahrenheit != null) {
                    String.format("%.1f °C", (fahrenheit - 32) * 5 / 9)
                } else {
                    "Invalid input"
                }
            },
                colors = ButtonDefaults
                    .buttonColors(containerColor = FahrenheitButtonColor),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .width(200.dp)
                    .padding(vertical = 4.dp)

            ) {
                Text("Convert to Celsius")
            }

            Text(
                text = celsiusResult,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}
