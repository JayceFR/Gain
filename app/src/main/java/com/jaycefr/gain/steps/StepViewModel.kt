package com.jaycefr.gain.steps

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class StepViewModel() : ViewModel() {
//    var stepCount by mutableLongStateOf(0L)

    val stepCount : StateFlow<Long> get() = StepViewModelLinker.stepCount


}