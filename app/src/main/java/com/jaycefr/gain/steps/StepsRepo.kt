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
                println("No data points found for today")
                Log.d("Steps", "found no datapoints for today")
                0
            }
            else -> {
                // Ignore the reboot
                var todaySteps = 0L
                println("Data points found today")
                Log.d("Steps", "found data points today")
                for (x in todayDataPoints.size - 1 downTo 1){
                    if (todayDataPoints[x].steps > todayDataPoints[x-1].steps){
                        todaySteps += todayDataPoints[x].steps - todayDataPoints[x-1].steps
                    }
                }
                todaySteps
            }
        }
    }

    suspend fun getLastStepUpdate() : String = withContext(Dispatchers.IO) {
        val todayAtMidnight = (LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT).toString())
        val todayDataPoints = stepsDao.loadAllStepsFromToday(todayAtMidnight)
        when{
            todayDataPoints.isEmpty() -> {
                println("empty")
                Instant.now().toString()
            }
            else -> {
                var ans = todayDataPoints.first().createdAt;
                for (x in 0..todayDataPoints.size - 2){
                    if(todayDataPoints[x].steps != todayDataPoints[x+1].steps){
                        ans = todayDataPoints[x+1].createdAt;
                    }
                }
                ans
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