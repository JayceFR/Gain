package com.jaycefr.gain.steps

sealed interface StepsEvent {
    data object LoadTodaySteps : StepsEvent
    data object IncrementTodaySteps : StepsEvent
    data class StoreSteps(val steps: Long) : StepsEvent
}