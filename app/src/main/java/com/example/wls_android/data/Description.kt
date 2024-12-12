package com.example.wls_android.data

import kotlinx.serialization.Serializable

@Serializable
data class Description(
    val description: String,
    val time: String
)
