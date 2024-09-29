package com.jaycefr.gain.steps.serivces

import java.time.LocalDate

data class StepCounterState(
    val date : LocalDate,
    val steps : Long,
        val goal : Int
    )
