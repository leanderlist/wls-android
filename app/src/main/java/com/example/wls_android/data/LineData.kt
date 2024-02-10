package com.example.wls_android.data

import kotlinx.serialization.Serializable

@Serializable
data class LineData(
    val data : List<Line>
)
