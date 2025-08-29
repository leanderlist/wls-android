package at.wls_android.app.data

import at.wls_android.app.enums.DisturbanceType
import kotlinx.serialization.Serializable

@Serializable
data class Disturbance(
    val id: String,
    val title: String,
    val type: DisturbanceType,
    val startedAt: String,
    val endedAt: String?,
    val descriptions: List<Description>,
    val lines: List<Line>
)
