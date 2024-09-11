package com.jaycefr.gain

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StepViewModel(
    private val stepsRepo: StepsRepo
) : ViewModel() {
    var stepCount by mutableLongStateOf(0L)

    @RequiresApi(Build.VERSION_CODES.O)
    fun onEvent(event: StepsEvent){
        when(event){
            StepsEvent.LoadTodaySteps -> {
                viewModelScope.launch {
                    stepCount = stepsRepo.loadTodaySteps()
                }
            }
            is StepsEvent.StoreSteps -> {
                viewModelScope.launch {
                    stepsRepo.storeSteps(event.steps)
                }
            }
        }
    }

}