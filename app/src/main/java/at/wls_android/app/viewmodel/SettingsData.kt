package at.wls_android.app.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import at.wls_android.app.data.Line
import at.wls_android.app.model.LineStatePair

class SettingsData : ViewModel() {
    var selectedLines = mutableListOf<LineStatePair>()
    var theme = mutableStateOf("standard")

    fun setTheme(theme: String) {
        if (theme != "standard" && theme != "dynamic") {
            throw IllegalArgumentException("Invalid theme: $theme")
        }
        this.theme.value = theme
    }

    fun getTheme(): String {
        return theme.value
    }

    fun addLine(line: LineStatePair) {
        selectedLines.add(line)
    }

    fun removeLine(line: LineStatePair) {
        selectedLines.remove(line)
    }

    fun resetLines() {
        selectedLines.clear()
    }

    @Override
    operator fun contains(line: Line): Boolean {
        for (lineStatePair in selectedLines) {
            if (lineStatePair.line == line) {
                return true
            }
        }
        return false
    }

    fun isEnabled(line: Line): Boolean {
        for (lineStatePair in selectedLines) {
            if (lineStatePair.line == line) {
                return lineStatePair.enabled
            }
        }
        return false
    }

    fun hasNoEnabledLines(): Boolean {
        for (lineStatePair in selectedLines) {
            if (lineStatePair.enabled) {
                return false
            }
        }
        return true
    }
}