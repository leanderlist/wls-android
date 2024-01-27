package com.example.wls_android.data

import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("data")
    val data : List<Disturbance>
)