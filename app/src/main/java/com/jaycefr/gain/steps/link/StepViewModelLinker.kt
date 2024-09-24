package com.jaycefr.gain.steps.link

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object StepViewModelLinker{
    private val _stepCount = MutableStateFlow(0L)
    val stepCount : MutableStateFlow<Long> get() = _stepCount

    private val _stepPercentage = MutableStateFlow(0f)
    val stepPercentage : MutableStateFlow<Float> get() = _stepPercentage

    private val _stepGoal = MutableStateFlow(3000L)
    val stepGoal : MutableStateFlow<Long> get() = _stepGoal

    private val _gifState = MutableStateFlow(GifState.Standing)
    val gifState : StateFlow<GifState> get() = _gifState

    fun updateStepCount(count : Long){
        _stepCount.value = count
        _stepPercentage.value = count/stepGoal.value.toFloat()
    }

    fun updateGifState(state: GifState){
        _gifState.value = state
    }
}