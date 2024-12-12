package com.example.wls_android.data

import kotlinx.serialization.Serializable

@Serializable
data class Line(
    val id: String,
    val type: Int
)
