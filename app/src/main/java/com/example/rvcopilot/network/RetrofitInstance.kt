package com.example.rvcopilot.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.Interceptor
import okhttp3.Response
import com.example.rvcopilot.BuildConfig


object RetrofitInstance {
    private const val BASE_URL = "https://ridb.recreation.gov/api/v1/"

    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("apikey", BuildConfig.RIDB_API_KEY)  //gradle.properties
                .build()
            chain.proceed(request)
        }
        .build()

    val api: RidbApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RidbApiService::class.java)
    }
}