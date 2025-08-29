package at.wls_android.app.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import at.wls_android.app.data.Line

class SettingsData : ViewModel() {

    private val subscribedLines = mutableStateListOf<Line>()
    private var theme = mutableStateOf("standard")
    private var baseUrl = mutableStateOf("https://wls.byleo.net")

    fun setTheme(theme: String) {
        if (theme != "standard" && theme != "dynamic") {
            throw IllegalArgumentException("Invalid theme: $theme")
        }
        this.theme.value = theme
    }

    fun getTheme(): String {
        return theme.value
    }

    fun setBaseUrl(baseUrl: String) {
        this.baseUrl.value = baseUrl.trim().let {
            it.ifEmpty { "https://wls.byleo.net" }
        }
    }

    fun getBaseUrl(): String {
        return baseUrl.value
    }

    fun setSubscribedLines(lines: List<Line>) {
        resetSubscribedLines()
        subscribedLines.addAll(lines)
    }

    fun addSubscribedLine(line: Line) {
        if (!subscribedLines.contains(line)) {
            subscribedLines.add(line)
        }
    }

    fun getSubscribedLines(): List<Line> {
        return subscribedLines.toList()
    }

    fun resetSubscribedLines() {
        subscribedLines.clear()
    }
}