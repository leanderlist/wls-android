package at.wls_android.app.data

import kotlinx.serialization.Serializable

@Serializable
data class Subscription(
    val token: String,
    val subscribedLines: List<Line>
)