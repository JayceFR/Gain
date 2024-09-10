package com.jaycefr.gain

import androidx.lifecycle.ViewModel

class StepViewModel : ViewModel() {
    var stepCount = 0

    fun updateStepCount(count: Int) {
        stepCount = count
    }

}