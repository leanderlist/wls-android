package at.wls_android.app.data

import kotlinx.serialization.Serializable

@Serializable
data class LineData(
    val data: List<Line>
)
