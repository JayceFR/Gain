package com.jaycefr.gain.steps

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class StepsRepo (
    private val stepsDao: StepsDao
) {
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun storeSteps(steps: Long) = withContext(Dispatchers.IO) {
        val stepCount = StepCount(
            steps = steps,
            createdAt = Instant.now().toString()
        )
        Log.d("STEPS", "storing steps")
        stepsDao.insertAll(stepCount)
    }

    suspend fun loadTodaySteps(steps: Long) : Long = withContext(Dispatchers.IO) {
        val todayAtMidnight = (LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT).toString())
        val todayDataPoints = stepsDao.loadAllStepsFromToday(todayAtMidnight)
        when{
            todayDataPoints.isEmpty() -> {
                storeSteps(steps)
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