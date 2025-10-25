package com.example.demo

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainViewModel: ViewModel() {

    var isReady by mutableStateOf(false)
        private set

    init {
        viewModelScope.launch {
            delay(3000L)
            isReady = true
        }
    }
}