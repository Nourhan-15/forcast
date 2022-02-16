package com.example.forcast247

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.example.forcast247.ApiResponse.Daily

@Entity(
    tableName = "favouriteData"
)
data class FavouriteData(
    @PrimaryKey(autoGenerate = true)
    val id : Int,
    @SerializedName("lat")
    val lat: Double,
    @SerializedName("lon")
    val lon: Double,
    @SerializedName("daily")
    val daily: List<Daily>
    )