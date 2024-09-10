package com.jaycefr.gain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class StepViewModel : ViewModel() {
    var stepCount = 0

    fun updateStepCount(count: Int) {
        stepCount = count
    }

}