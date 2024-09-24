package com.jaycefr.gain.steps.data

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class StepViewModel() : ViewModel() {
//    var stepCount by mutableLongStateOf(0L)

    val stepCount : StateFlow<Long> get() = StepViewModelLinker.stepCount

    val stepPercentage : StateFlow<Float> get() = StepViewModelLinker.stepPercentage

    val stepGoal : StateFlow<Long> get() = StepViewModelLinker.stepGoal

    private val _stepWeekList = MutableStateFlow(mutableListOf<Int>())
    val stepWeekList : StateFlow<MutableList<Int>> get() = _stepWeekList

    private val _lastupdate = MutableStateFlow("");
    val lastupdate : StateFlow<String> get() = _lastupdate

    val gifState : StateFlow<GifState> get() = StepViewModelLinker.gifState

    fun updateLastUpdate(update: String){
        _lastupdate.value = update
    }

    fun updateStepWeekList(list: MutableList<Int>){
        _stepWeekList.value = list
    }

}

enum class GifState{
    Standing,
    Walking
}

