package com.jaycefr.gain.steps.utils

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.onEach
import okhttp3.internal.UTC
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

fun getDecimalPlace(number : Float, decimal_places : Int = 2) : String{
    val numbers = number.toString().split('.')
    return numbers[0] + "." + numbers[1].take(decimal_places)
}

val stepreading = MutableStateFlow(0)


fun main(){
//    println(Instant.parse(Instant.now().toString()).atZone(ZoneId.systemDefault()).hour)
    println( LocalDate.now().toString())
    var date : LocalDate = LocalDate.parse(LocalDate.now().toString())

}