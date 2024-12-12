package com.example.wls_android.data

import kotlinx.serialization.Serializable

@Serializable
data class Disturbance(
    val descriptions: List<Description>,
    val end_time: String?,
    val id: String,
    val lines: List<Line>,
    val start_time: String,
    val title: String
)
