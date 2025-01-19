package at.wls_android.app.data

import kotlinx.serialization.Serializable

@Serializable
data class Data(
    val data: List<Disturbance>
)
