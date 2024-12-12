package com.example.wls_android.data

import kotlinx.serialization.Serializable

@Serializable
data class Data(
    val data: List<Disturbance>
)
