package com.jaycefr.gain.steps.models.usecase

import com.jaycefr.gain.steps.link.StepsRepo
import com.jaycefr.gain.steps.models.StepCount
import kotlinx.coroutines.flow.first
import java.time.LocalDate

class IncrementStepCountImpl(
    private val stepsRepo: StepsRepo,
    private val getDayUseCase: GetDay
) : IncrementStepCount {
    override suspend fun invoke(date: LocalDate, by: Int) {
        val day = getDayUseCase(date).first()
        val updatedDay = day.copy(steps = day.steps + by)
        stepsRepo.upsertDay(updatedDay)
    }
}