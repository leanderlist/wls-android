package com.example.wls_android.data

import com.google.gson.annotations.SerializedName

data class Disturbance(
    @SerializedName("descriptions")
    val descriptions : List<Description>,
    @SerializedName("end_time")
    val end_time : String?,
    @SerializedName("id")
    val id : String,
    @SerializedName("lines")
    val lines : List<Line>,
    @SerializedName("start_time")
    val start_time : String,
    @SerializedName("title")
    val title : String
)