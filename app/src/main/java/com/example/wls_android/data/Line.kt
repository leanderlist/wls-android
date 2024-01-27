package com.example.wls_android.data

import com.google.gson.annotations.SerializedName

data class Line(
    @SerializedName("id")
    val id : String,
    @SerializedName("type")
    val type : Int
)