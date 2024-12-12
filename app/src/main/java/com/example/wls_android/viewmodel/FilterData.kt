package com.example.wls_android.viewmodel

import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel

class FilterData : ViewModel() {
    var filters = mutableStateMapOf<String, String>()
        private set

    fun addFilter(key: String, value: String) {
        filters[key] = value
    }

    fun resetFilters() {
        filters.clear()
    }
}