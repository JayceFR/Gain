package com.jaycefr.gain

sealed interface StepsEvent {
    data object LoadTodaySteps : StepsEvent
    data class StoreSteps(val steps: Long) : StepsEvent
}