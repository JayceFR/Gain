package com.jaycefr.gain.steps.serivces

import java.time.LocalDate

data class StepCounterEvent(
    val stepCount : Long,
    val eventDate : LocalDate
)
