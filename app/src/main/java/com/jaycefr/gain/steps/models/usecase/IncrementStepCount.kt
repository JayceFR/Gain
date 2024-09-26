package com.jaycefr.gain.steps.models.usecase

import java.time.LocalDate

interface IncrementStepCount {
    suspend operator fun invoke(date : LocalDate, by : Int)
}