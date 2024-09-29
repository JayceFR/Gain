package com.jaycefr.gain.steps.models.usecase

import com.jaycefr.gain.steps.models.StepCount
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface GetDay {
    operator fun invoke(date : LocalDate) : Flow<StepCount>
}