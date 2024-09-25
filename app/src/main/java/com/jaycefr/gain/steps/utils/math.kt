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
//    println(Instant.parse(Instant.now().toString()).atZone(ZoneId.systemDefault()).hour)

    val mp = hashMapOf<Int, MutableList<Long>>()
    mp[8] = mutableListOf(30,46,57)
    mp[10] = mutableListOf(40, 50, 60, 70)
    mp[9] = mutableListOf(70, 80, 0, 10, 20)

    for (key in mp.keys.sorted()){
        var steps : Long = 0;
        for (x in mp[key]!!.size - 1 downTo 1){
            if (mp[key]!![x] > mp[key]!![x-1]){
                steps += mp[key]!![x] - mp[key]!![x-1]
            }
        }
        println("$key $steps")
    }

}