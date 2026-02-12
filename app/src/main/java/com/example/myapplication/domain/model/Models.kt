package com.example.myapplication.domain.model

data class Place(
    val id: String,
    val name: String,
    val lat: Double,
    val lon: Double
)

data class Forecast(
    val lat: Double,
    val lon: Double,
    val tempC: Double,
    val wind: Double
)

data class BoardItem(
    val id: String = "",
    val text: String = "",
    val createdAt: Long = 0L,
    val createdBy: String = ""
)
