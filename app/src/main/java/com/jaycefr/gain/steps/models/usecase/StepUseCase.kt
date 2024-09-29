package com.jaycefr.gain.steps.models.usecase

import com.jaycefr.gain.steps.link.StepsRepo

class StepUseCase(
    stepsRepo: StepsRepo
) {
    val getDay = GetDayImpl(stepsRepo)
    val incrementStepCount = IncrementStepCountImpl(stepsRepo, getDay)
}