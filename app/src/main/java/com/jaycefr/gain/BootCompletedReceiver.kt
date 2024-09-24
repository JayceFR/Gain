package com.jaycefr.gain

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.jaycefr.gain.steps.serivces.Actions
import com.jaycefr.gain.steps.serivces.StepForegroundService

class BootCompletedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent!!.action == Intent.ACTION_BOOT_COMPLETED){

            val serviceIntent = Intent(context, StepForegroundService::class.java)
            serviceIntent.action = Actions.START.toString()
            context!!.startForegroundService(serviceIntent)

        }
    }

}