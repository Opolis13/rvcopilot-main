package com.example.rvcopilot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.rvcopilot.RvCopilotApp
import com.example.rvcopilot.ui.theme.RvcopilotTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import android.location.Location
import com.google.android.gms.location.Priority
import androidx.activity.enableEdgeToEdge


class MainActivity : ComponentActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // global function for uncaught exceptions
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            println("Uncaught exception on ${thread.name}: ${throwable.message}")
            throwable.printStackTrace()
        }


        // init FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        enableEdgeToEdge()
        // init firebase
        FirebaseApp.initializeApp(this)
        FirebaseFirestore.setLoggingEnabled(true)

        setContent {
            RvcopilotTheme {

                RvCopilotApp()
            }
        }
    }
}