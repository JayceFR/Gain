package com.jaycefr.gain.steps

import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.runtime.MutableState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.coroutines.coroutineContext

class StepsRepo (
    private val stepsDao: StepsDao
) {
    suspend fun storeSteps(steps: Long) = withContext(Dispatchers.IO) {
        val stepCount = StepCount(
            steps = steps,
            createdAt = Instant.now().toString()
        )
        Log.d("STEPS", "storing steps")
        stepsDao.insertAll(stepCount)
    }

    suspend fun loadTodaySteps() : Long = withContext(Dispatchers.IO) {
        val todayAtMidnight = (LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT).toString())
        val todayDataPoints = stepsDao.loadAllStepsFromToday(todayAtMidnight)
        when{
            todayDataPoints.isEmpty() -> {
                0
            }
            else -> {
                val firstDataPointOfDay = todayDataPoints.first()
                val lastDataPointOfDay = todayDataPoints.last()
                val todaySteps = lastDataPointOfDay.steps - firstDataPointOfDay.steps
                todaySteps
            }
        }
    }

}

object StepViewModelLinker{
    private val _stepCount = MutableStateFlow(0L)
    val stepCount : MutableStateFlow<Long> get() = _stepCount

    fun updateStepCount(count : Long){
        _stepCount.value = count
    }


}