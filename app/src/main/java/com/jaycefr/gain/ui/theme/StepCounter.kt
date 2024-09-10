package com.jaycefr.gain.ui.theme

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext

@Composable
fun StepCounterScreen(){
    var stepCount by remember {
        mutableStateOf(0)
    }

    val sensorManager = LocalContext.current.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    val sepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

    LaunchedEffect(key1 = sepCounterSensor) {
        val stepCounterListener = object : SensorEventListener{
            override fun onSensorChanged(event: SensorEvent?) {
                if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER){
                    stepCount = event.values[0].toInt()
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                Log.d("StepCounter", "Accuracy Changed")
            }
        }

        sensorManager.registerListener(
            stepCounterListener,
            sepCounterSensor,
            SensorManager.SENSOR_DELAY_UI
        )

    }

    Text(text = "Step Count : $stepCount")

}