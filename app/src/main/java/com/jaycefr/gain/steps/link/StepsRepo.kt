package com.jaycefr.gain.steps.link

import android.util.Log
import co.yml.charts.common.model.Point
import com.jaycefr.gain.steps.models.StepCount
import com.jaycefr.gain.steps.models.StepsDao
import com.jaycefr.gain.steps.models.WeekStep
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

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

    suspend fun loadTodaySteps(date: LocalDate = LocalDate.now() ) : Long = withContext(Dispatchers.IO) {
        val todayAtMidnight = (LocalDateTime.of(date, LocalTime.MIDNIGHT).toString())
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

    suspend fun generateGraphPoints(date: LocalDate = LocalDate.now()) : MutableList<Point> = withContext(Dispatchers.IO){
        val pointList : MutableList<Point> = mutableListOf<Point>()
        val todayAtMidnight = (LocalDateTime.of(date, LocalTime.MIDNIGHT).toString())
        val todayDataPoints = stepsDao.loadAllStepsFromToday(todayAtMidnight)

        var previousValue : Long = 0;

        for (x in todayDataPoints.indices){
            if (todayDataPoints[x].steps > previousValue){
                pointList.add(
                    Point(
                        x = Instant.parse(todayDataPoints[x].createdAt).atZone(ZoneId.systemDefault()).hour.toFloat(),
                        y = todayDataPoints[x].steps.toFloat()
                    )
                )

//                yAxisData.add(todayDataPoints[x].steps)
//                xAxisData.add(Instant.parse(todayDataPoints[x].createdAt).atZone(ZoneId.systemDefault()).hour)
                previousValue = todayDataPoints[x].steps
            }
        }

        pointList
    }

    suspend fun loadWeeklyHistory() : WeekStep = withContext(Dispatchers.IO){
        val day : Int = LocalDate.now().dayOfWeek.value
        val returnList = mutableListOf<Int>(0,0,0,0,0,0,0)
        var streak : Int = 0
        for (x in 1..day){
            val curr_date = LocalDate.now().minusDays(day.toLong()).plusDays(x.toLong())
            val today_steps : Long = loadTodaySteps(curr_date)
            if (today_steps >= StepViewModelLinker.stepGoal.value){
                returnList[x-1] = 1
                streak ++;
            }else{
                streak = 0;
            }
        }
        WeekStep(returnList, streak);
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

