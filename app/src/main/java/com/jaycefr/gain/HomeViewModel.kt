package com.jaycefr.gain

import androidx.lifecycle.ViewModel
import java.util.Calendar

class HomeViewModel : ViewModel() {
    fun getGreetingMessage(): String {
        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when (currentHour) {
            in 0..11 -> GreetingMessage.GoodMorning.toString()
            in 12..16 -> GreetingMessage.GoodAfternoon.toString()
            else -> GreetingMessage.GoodEvening.toString()
        }
    }
}

enum class GreetingMessage{
    GoodMorning,
    GoodAfternoon,
    GoodEvening,
}