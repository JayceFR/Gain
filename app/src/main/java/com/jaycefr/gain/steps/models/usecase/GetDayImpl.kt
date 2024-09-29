package com.jaycefr.gain.steps.models.usecase

import com.jaycefr.gain.steps.link.StepsRepo
import com.jaycefr.gain.steps.models.StepCount
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class GetDayImpl(
    private val stepsRepo: StepsRepo
) : GetDay {
    override fun invoke(date: LocalDate): Flow<StepCount> {
        val dayFlow : Flow<StepCount?> = stepsRepo.getDay()
        return dayFlow.map {
            it ?: StepCount(
                date = date.toString(),
                steps = 0
            )
        }
    }
}