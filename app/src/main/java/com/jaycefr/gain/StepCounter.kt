package com.jaycefr.gain

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.room.Room

@Composable
fun StepCounterScreen(stepsRepo: StepsRepo){
    var stepCount by remember {
        mutableStateOf(0L)
    }

    var count by remember {
        mutableStateOf(0)
    }

    val sensorManager = LocalContext.current.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    val stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

    LaunchedEffect(key1 = stepCounterSensor) {
        val stepCounterListener = object : SensorEventListener{
            override fun onSensorChanged(event: SensorEvent?) {
                if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER){
                    stepCount = event.values[0].toLong()
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                Log.d("StepCounter", "Accuracy Changed")
            }
        }

        sensorManager.registerListener(
            stepCounterListener,
            stepCounterSensor,
            SensorManager.SENSOR_DELAY_UI
        )

    }

    Text(text = "Step Count : $stepCount")

}