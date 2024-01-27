package com.example.wls_android.data

import com.google.gson.annotations.SerializedName

data class Description(
    @SerializedName("description")
    val description : String,
    @SerializedName("time")
    val time : String
)