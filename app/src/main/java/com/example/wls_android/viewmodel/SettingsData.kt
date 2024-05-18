package com.example.wls_android.viewmodel

import androidx.lifecycle.ViewModel
import com.example.wls_android.data.Line

class SettingsData : ViewModel() {
    var selectedLines = mutableSetOf<Line>()
        private set

    fun addLine(line: Line) {
        selectedLines.add(line)
    }

    fun removeLine(line: Line) {
        selectedLines.remove(line)
    }

    fun resetLines() {
        selectedLines.clear()
    }
}