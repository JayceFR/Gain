package com.jaycefr.gain.steps

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.room.Room

@Composable
fun StepCounterScreen(stepViewModel: StepViewModel)
{

    var db by remember { mutableStateOf<StepAppDatabase?>(null) }
    var stepsRepo by remember { mutableStateOf<StepsRepo?>(null) }

    val stepCount by stepViewModel.stepCount.collectAsState()
    val lastUpdate by stepViewModel.lastupdate.collectAsState()
    val context : Context = LocalContext.current
    LaunchedEffect(context) {
        db = Room.databaseBuilder(
            context,
            StepAppDatabase::class.java, "stepdb"
        ).build()

        stepsRepo = StepsRepo(db!!.stepsDao())

        stepViewModel.updateLastUpdate(stepsRepo!!.getLastStepUpdate())
        StepViewModelLinker.updateStepCount(stepsRepo!!.loadTodaySteps())

    }
    Column(
        modifier = Modifier
            .height(150.dp)
            .fillMaxWidth(0.85f)
            .clip(shape = RoundedCornerShape(15.dp))
            .shadow(elevation = 4.dp)
            .background(color = MaterialTheme.colorScheme.primaryContainer),

        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,

        ){
        Text(
            text = "$stepCount",
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            fontSize = MaterialTheme.typography.displayLarge.fontSize
        )

        Text(
            text = "Steps Taken",
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            fontSize = MaterialTheme.typography.titleMedium.fontSize
        )
    }
    Spacer(modifier = Modifier.height(20.dp))
    Column(
        modifier = Modifier
            .height(150.dp)
            .fillMaxWidth(0.85f)
            .clip(shape = RoundedCornerShape(15.dp))
            .shadow(elevation = 4.dp)
            .background(color = MaterialTheme.colorScheme.primaryContainer),

        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,

        ){
        Text(
            text = "${(stepCount * 0.762).toInt()}m",
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            fontSize = MaterialTheme.typography.displayLarge.fontSize
        )

        Text(
            text = "Distance Covered",
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            fontSize = MaterialTheme.typography.titleMedium.fontSize
        )
        Text(
            text = "Last updated: $lastUpdate",
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            fontSize = MaterialTheme.typography.titleMedium.fontSize
        )
    }




}