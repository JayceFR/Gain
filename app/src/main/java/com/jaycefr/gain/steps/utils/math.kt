package com.jaycefr.gain.steps.utils

import okhttp3.internal.UTC
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

fun getDecimalPlace(number : Float, decimal_places : Int = 2) : String{
    val numbers = number.toString().split('.')
    return numbers[0] + "." + numbers[1].take(decimal_places)
}

fun main(){
    println(Instant.parse(Instant.now().toString()).atZone(ZoneId.systemDefault()).hour)
}