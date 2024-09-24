package com.jaycefr.gain.steps.utils

import java.time.LocalDate

fun getDecimalPlace(number : Float, decimal_places : Int = 2) : String{
    val numbers = number.toString().split('.')
    return numbers[0] + "." + numbers[1].take(decimal_places)
}

fun main(){
    val day : Int = LocalDate.now().dayOfWeek.value
    for( x in 1..day){
        println(LocalDate.now().minusDays(day.toLong()).plusDays(x.toLong()).toString())
    }
}