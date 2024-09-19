package com.jaycefr.gain

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.glance.appwidget.updateAll
import com.jaycefr.gain.steps.Actions
import com.jaycefr.gain.steps.StepForegroundService
import com.jaycefr.gain.steps.StepWidget
import com.jaycefr.gain.steps.StepWidgetReceiver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.suspendCoroutine

class BootCompletedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED){
            val serviceIntent = Intent(context, StepForegroundService::class.java)
            serviceIntent.action = Actions.START.toString()
            println("Starting service ")
            context?.startForegroundService(serviceIntent)
            GlobalScope.launch(Dispatchers.Default){
                if (context != null) {
                    StepWidget.updateAll(context)
                }
            }
        }
    }

}