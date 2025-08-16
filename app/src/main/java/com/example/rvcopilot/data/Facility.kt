package com.example.rvcopilot.data

import com.google.gson.annotations.SerializedName


data class Facility(
    @SerializedName("FacilityID") val facilityId: String?,
    @SerializedName("FacilityName") val facilityName: String?,
    @SerializedName("Loop") val loop: String?,
    @SerializedName("FacilityTypeDescription") val facilityType: String?,
    @SerializedName("FacilityLatitude") val facilityLatitude: Double?,
    @SerializedName("FacilityLongitude") val facilityLongitude: Double?,
    @SerializedName("FacilityDescription") val facilityDescription: String?
)

data class FacilityResponse(
    @SerializedName("RECDATA") val facilities: List<Facility>
)
