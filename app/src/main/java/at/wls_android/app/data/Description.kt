package at.wls_android.app.data

import kotlinx.serialization.Serializable

@Serializable
data class Description(
    val description: String,
    val time: String
)
