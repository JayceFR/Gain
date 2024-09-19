package com.jaycefr.gain.steps

import android.annotation.SuppressLint
import android.content.Intent
import android.os.IBinder
import android.app.Service
import android.content.ComponentName
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.glance.appwidget.updateAll
import androidx.room.Room
import com.jaycefr.gain.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class StepForegroundService : Service() {

    private lateinit var sensorManager: SensorManager
    private var stepCounterSensor: Sensor? = null
    private lateinit var db : StepAppDatabase
    private lateinit var stepsRepo: StepsRepo
    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action){
            Actions.START.toString() -> start()
            Actions.STOP.toString() -> stop()
        }
        return START_STICKY
    }
    
    @SuppressLint("ForegroundServiceType")
    private fun start(){
        val notification = NotificationCompat.Builder(this, "running_channel")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Gain")
            .setSubText("Tracking Steps")
            .build()
        startForeground(1, notification)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        val stepCountListener = object : SensorEventListener{
            override fun onSensorChanged(event: SensorEvent?) {
                if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER){
                    val stepCount = event.values[0].toLong()
                    serviceScope.launch {
                        stepsRepo.storeSteps(stepCount)
                        Log.d("Steps", "Storing Steps : $stepCount")
                        StepViewModelLinker.updateStepCount(stepsRepo.loadTodaySteps())
                        //update the widget
                        StepWidget.updateAll(applicationContext)
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

            }
        }

        db = Room.databaseBuilder(
            applicationContext,
            StepAppDatabase::class.java, "stepdb"
        ).build()

        stepsRepo = StepsRepo(db.stepsDao())

        sensorManager.registerListener(stepCountListener, stepCounterSensor, SensorManager.SENSOR_DELAY_UI)

    }

    private fun stop(){
        stopSelf()
    }

}

enum class Actions{
    START, STOP
}