package com.example.rvcopilot.data


import com.example.rvcopilot.R

data class Pictures(
    val id: String = "",
    val label: String = "",
    val imageUri: String = "", // Firebase Storage path or public URI
    val imageBase64: String = ""
)