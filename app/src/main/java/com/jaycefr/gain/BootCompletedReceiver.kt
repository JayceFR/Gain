package com.jaycefr.gain

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootCompletedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent!!.action == Intent.ACTION_BOOT_COMPLETED){
            val i = Intent(context, MainActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context!!.startActivity(i)
//            context?.startForegroundService(serviceIntent)
//            GlobalScope.launch(Dispatchers.Default){
//                if (context != null) {
//                    StepWidget.updateAll(context)
//                }
//            }
        }
    }

}