package com.example.forcast247.ApiResponse

import com.google.gson.annotations.SerializedName

data class Alert(
    @SerializedName("description")
    val description: String?,
    @SerializedName("end")
    val end: Int?,
    @SerializedName("event")
    val event: String?,
    @SerializedName("sender_name")
    val senderName: String?,
    @SerializedName("start")
    val start: Int?
)