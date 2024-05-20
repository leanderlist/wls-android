package com.example.wls_android.viewmodel

import androidx.lifecycle.ViewModel
import com.example.wls_android.data.Line
import com.example.wls_android.model.LineStatePair

class SettingsData : ViewModel() {
    var selectedLines = mutableListOf<LineStatePair>()

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