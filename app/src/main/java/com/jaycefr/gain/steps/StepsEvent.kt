package com.jaycefr.gain.steps

sealed interface StepsEvent {
    data class LoadTodaySteps(val steps: Long) : StepsEvent
    data object IncrementTodaySteps : StepsEvent
    data class StoreSteps(val steps: Long) : StepsEvent
}