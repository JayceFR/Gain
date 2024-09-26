package com.jaycefr.gain.steps.models.usecase

import com.jaycefr.gain.steps.link.StepsRepo
import com.jaycefr.gain.steps.models.StepCount
import kotlinx.coroutines.flow.first
import java.time.LocalDate

class IncrementStepCountImpl(
    private val stepsRepo: StepsRepo
) : IncrementStepCount {
    override suspend fun invoke(date: LocalDate, by: Int) {
        val day = stepsRepo.getDay().first()
        if (day == null){
            stepsRepo.upsertDay(StepCount(LocalDate.now().toString(), by.toLong()))
        }
        else{
            val updatedDay = day.copy(steps = day.steps + by)
            stepsRepo.upsertDay(updatedDay)
        }
    }
}