package com.example.rvcopilot.data

import com.google.gson.annotations.SerializedName

data class Campsite(
    @SerializedName("CampsiteID") val campsiteID: String?,
    @SerializedName("FacilityID") val facilityId: String?,
    @SerializedName("CampsiteName") val campsiteName: String?,
    @SerializedName("Loop") val loop: String?,
    @SerializedName("CampsiteType") val campsiteType: String?,
    @SerializedName("CampsiteLatitude") val campsiteLatitude: Double?,
    @SerializedName("CampsiteLongitude") val campsiteLongitude: Double?
)

data class CampsiteResponse(
    @SerializedName("RECDATA") val campsites: List<Campsite>
)
