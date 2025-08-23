package at.wls_android.app.data

import kotlinx.serialization.Serializable

@Serializable
data class Description(
    val text: String,
    val createdAt: String
)
