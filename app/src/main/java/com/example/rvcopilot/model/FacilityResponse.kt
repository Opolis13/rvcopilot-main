package com.example.rvcopilot.model


import com.google.gson.annotations.SerializedName
import com.example.rvcopilot.data.Facility

data class FacilityResponse(
    @SerializedName("RECDATA") val facilities: List<Facility>
)
