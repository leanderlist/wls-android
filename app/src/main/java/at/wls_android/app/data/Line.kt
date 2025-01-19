package at.wls_android.app.data

import kotlinx.serialization.Serializable

@Serializable
data class Line(
    val id: String,
    val type: Int
)
