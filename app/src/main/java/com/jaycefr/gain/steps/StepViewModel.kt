package com.jaycefr.gain.steps

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class StepViewModel(
    private val stepsRepo: StepsRepo
) : ViewModel() {
    var stepCount by mutableLongStateOf(0L)

    fun onEvent(event: StepsEvent){
        when(event){
            is StepsEvent.StoreSteps -> {
                viewModelScope.launch {
                    stepsRepo.storeSteps(event.steps)
                }
            }

            StepsEvent.IncrementTodaySteps -> {
                stepCount ++;
            }

            is StepsEvent.LoadTodaySteps -> {
                viewModelScope.launch {
                    stepCount = stepsRepo.loadTodaySteps(event.steps)
                }
            }
        }
    }

}