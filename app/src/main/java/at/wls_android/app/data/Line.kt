package at.wls_android.app.data

import at.wls_android.app.enums.LineType
import kotlinx.serialization.Serializable

@Serializable
data class Line(
    val id: String,
    val type: LineType,
    val displayName: String
)
