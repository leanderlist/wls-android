package at.wls_android.app.model

import at.wls_android.app.data.Line

data class LineStatePair(
    val line: Line,
    val enabled: Boolean
)