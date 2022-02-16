package com.example.forcast247

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.forcast247.ApiResponse.Alert
import com.example.forcast247.ApiResponse.Current
import com.example.forcast247.ApiResponse.Daily
import com.example.forcast247.ApiResponse.Hourly
import com.google.gson.annotations.SerializedName


@Entity(
    tableName = "weatherData"
)
data class WeatherResponse(
    @PrimaryKey
    val id : Int,
    @SerializedName("alerts")
    val alerts: List<Alert>?,
    @SerializedName("current")
    val current: Current,
    @SerializedName("daily")
    val daily: List<Daily>,
    @SerializedName("hourly")
    val hourly: List<Hourly>,
    @SerializedName("lat")
    val lat: Double,
    @SerializedName("lon")
    val lon: Double,
    @SerializedName("timezone")
    val timezone: String,
    @SerializedName("timezone_offset")
    val timezoneOffset: Double
    )