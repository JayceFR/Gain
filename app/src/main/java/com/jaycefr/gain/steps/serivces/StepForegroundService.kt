package com.jaycefr.gain.steps.serivces

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.os.IBinder
import android.app.Service
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.glance.appwidget.updateAll
import androidx.room.Room
import com.jaycefr.gain.MainActivity
import com.jaycefr.gain.R
import com.jaycefr.gain.steps.link.GifState
import com.jaycefr.gain.steps.link.StepViewModel
import com.jaycefr.gain.steps.models.StepAppDatabase
import com.jaycefr.gain.steps.link.StepViewModelLinker
import com.jaycefr.gain.steps.link.StepsRepo
import com.jaycefr.gain.steps.models.usecase.StepUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.time.LocalDate

class StepForegroundService : Service() {

    private lateinit var sensorManager: SensorManager
    private lateinit var stepViewModelLinker : StepViewModelLinker
    private var stepCounterSensor: Sensor? = null
    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    companion object{
        private const val PENDING_INTENT_ID = 0x1
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action){
            Actions.START.toString() -> start()
            Actions.STOP.toString() -> stop()
        }
        return START_STICKY
    }

    private val launchApplicationPendingIntent
        get(): PendingIntent{
            val intent = Intent(applicationContext, MainActivity::class.java)
            val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            return PendingIntent.getActivity(this, PENDING_INTENT_ID, intent, flags)
        }
    
    @SuppressLint("ForegroundServiceType")
    private fun start(){
        val notification = NotificationCompat.Builder(this, "running_channel")
            .setContentIntent(launchApplicationPendingIntent)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Gain")
            .setSubText("Tracking Steps")
            .setOnlyAlertOnce(true)
            .setOngoing(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setSilent(true)
            .build()
        startForeground(1, notification)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        val stepCountListener = object : SensorEventListener{
            override fun onSensorChanged(event: SensorEvent?) {
                if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER){
                    val stepCount = event.values[0].toLong()
                    serviceScope.launch {
//                        stepsRepo.storeSteps(stepCount)
                        Log.d("Steps", "Storing Steps : $stepCount")
//                        StepViewModelLinker.updateStepCount(stepsRepo.loadTodaySteps())
                        stepViewModelLinker.onStepCountChanged(stepCount, LocalDate.now())
                        //update the gifstate
                        stepViewModelLinker.updateGifState(GifState.Walking)
                        //update the widget
                        StepWidget.updateAll(applicationContext)
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

            }
        }

        stepViewModelLinker = StepViewModelLinker(serviceScope)
        stepViewModelLinker.initialize(applicationContext)

        sensorManager.registerListener(stepCountListener, stepCounterSensor, SensorManager.SENSOR_DELAY_UI)

    }

    private fun stop(){
        stopSelf()
    }

}

enum class Actions{
    START, STOP
}