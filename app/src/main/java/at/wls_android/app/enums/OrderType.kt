package at.wls_android.app.enums

@kotlinx.serialization.Serializable
enum class OrderType(val text: String) {
    StartedAtDesc("Startzeit - neueste zuerst"),
    StartedAtAsc("Startzeit - älteste zuerst"),
    EndedAtDesc("Endzeit - neueste zuerst"),
    EndedAtAsc("Endzeit - älteste zuerst")
}