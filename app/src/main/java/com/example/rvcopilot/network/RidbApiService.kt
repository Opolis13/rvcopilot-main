package com.example.rvcopilot.network

import com.example.rvcopilot.data.CampsiteResponse
import com.example.rvcopilot.data.FacilityResponse

import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Response


interface RidbApiService {

    @GET("facilities")
    suspend fun getFacilities(
        @Query("limit") limit: Int = 35,
        @Query("offset") offset: Int = 0,
        @Query("state") state: String
    ): Response<FacilityResponse>

    @GET("campsites")
    suspend fun getCampsites(
        @Query("facilityId") facilityId: Int,
        @Query("limit") limit: Int = 35,
        @Query("offset") offset: Int = 0
    ): Response<CampsiteResponse>
}