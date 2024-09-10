package com.jaycefr.gain

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class TodayScreenViewModel () {
    val currentScreenState : MutableStateFlow<TodayScreenState> = MutableStateFlow(TodayScreenState.Loading)

}

sealed class TodayScreenState {
    data object Loading : TodayScreenState()
    data class Content(var steps : Int, var dailyGoal: Int) : TodayScreenState()
    data object Error : TodayScreenState()
}

