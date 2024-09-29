package com.jaycefr.gain.steps.link

import android.content.Context
import androidx.compose.runtime.rememberCoroutineScope
import androidx.room.Room
import com.jaycefr.gain.steps.models.StepAppDatabase
import com.jaycefr.gain.steps.models.usecase.StepUseCase
import com.jaycefr.gain.steps.serivces.StepCounterEvent
import com.jaycefr.gain.steps.serivces.StepCounterState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import kotlin.coroutines.coroutineContext

class StepViewModelLinker(
    private val coroutineScope: CoroutineScope
){
    private val _stepCount = MutableStateFlow(0L)
    val stepCount : MutableStateFlow<Long> get() = _stepCount

    private val _stepPercentage = MutableStateFlow(0f)
    val stepPercentage : MutableStateFlow<Float> get() = _stepPercentage

    private val _stepGoal = MutableStateFlow(900L)
    val stepGoal : MutableStateFlow<Long> get() = _stepGoal

    private val _gifState = MutableStateFlow(GifState.Standing)
    val gifState : StateFlow<GifState> get() = _gifState

    private val rawStepSensorReading = MutableStateFlow(StepCounterEvent(0, LocalDate.now()))
    private var previousStepCount : Long? = null

    private lateinit var db : StepAppDatabase
    private lateinit var stepsRepo : StepsRepo
    private lateinit var stepUseCase : StepUseCase

    private var getStatsJob : Job? = null

    private val _stats = MutableStateFlow(StepCounterState(LocalDate.now(), 0, 5000))
    val stats : StateFlow<StepCounterState> get() = _stats


    fun initialize(context : Context){
        db  = Room.databaseBuilder(
        context,
        StepAppDatabase::class.java, "stepdb"
        ).build()

        stepsRepo = StepsRepo(db.stepsDao())
        stepUseCase = StepUseCase(stepsRepo)
    }


    init {
        rawStepSensorReading.drop(1).onEach {
            event ->
            val stepCounterDifference = event.stepCount - (previousStepCount ?: event.stepCount)
            previousStepCount = event.stepCount
            stepUseCase.incrementStepCount(event.eventDate, stepCounterDifference.toInt())
        }.launchIn(coroutineScope)
    }

    suspend fun updateStepCount(count : Flow<Long>){
        println("Updating the ui")
        count.collect{
                value -> _stepCount.value = value
                _stepPercentage.value = (value.toFloat() / _stepGoal.value.toFloat())
                println("collected step count : $value")
        }
    }


    fun onStepCountChanged(newStepCount : Long, eventDate : LocalDate){
        rawStepSensorReading.value = StepCounterEvent(newStepCount, eventDate)
        coroutineScope.launch {
            updateStepCount(stepsRepo.getTodaySteps())
        }
        getStats()
    }

    fun getStats(){
        getStatsJob?.cancel()
        getStatsJob = stepUseCase.getDay(LocalDate.now()).onEach {
            day ->
            _stats.value = day.run {
                StepCounterState(
                    date = LocalDate.parse(date),
                    steps = steps,
                    goal = 5000
                )
            }
        }.launchIn(coroutineScope)
    }

    fun loadTodaySteps(){
        coroutineScope.launch {
            stepsRepo.getTodaySteps().collect{
                stepCount ->
                _stepCount.value = stepCount
            }
        }
    }


    fun updateGifState(state: GifState){
        _gifState.value = state
    }
}