package com.example.myapplication.data.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.http.GET
import retrofit2.http.Query

interface NominatimApi {
    @GET("search")
    suspend fun search(
        @Query("q") q: String,
        @Query("format") format: String = "json",
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): List<NominatimPlace>
}

@Serializable
data class NominatimPlace(
    @SerialName("place_id") val placeId: Long,
    @SerialName("display_name") val displayName: String,
    @SerialName("lat") val lat: String,
    @SerialName("lon") val lon: String
)

interface OpenMeteoApi {
    @GET("forecast")
    suspend fun forecast(
        @Query("latitude") lat: Double,
        @Query("longitude") lon: Double,
        @Query("current") current: String = "temperature_2m,wind_speed_10m"
    ): OpenMeteoResp
}

@Serializable
data class OpenMeteoResp(@SerialName("current") val current: OpenMeteoCurrent)

@Serializable
data class OpenMeteoCurrent(
    @SerialName("temperature_2m") val temp: Double,
    @SerialName("wind_speed_10m") val wind: Double
)
