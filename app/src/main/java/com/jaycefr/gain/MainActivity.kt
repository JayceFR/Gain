package com.jaycefr.gain

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jaycefr.gain.steps.serivces.Actions
import com.jaycefr.gain.steps.ui.StepCounterScreen
import com.jaycefr.gain.steps.serivces.StepForegroundService
import com.jaycefr.gain.steps.link.StepViewModel
import com.jaycefr.gain.ui.theme.GainTheme


class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                0
            )
        }

        val viewModel by viewModels<StepViewModel>(
            factoryProducer = {
                object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return StepViewModel() as T
                    }
                }
            }
        )

        val homeViewModel = HomeViewModel()

        Intent(applicationContext, StepForegroundService::class.java).also {
            it.action = Actions.START.toString()
            startService(it)
        }


        enableEdgeToEdge()
        setContent {
            GainTheme {


                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background),

                ){
                    StepCounterScreen(stepViewModel = viewModel)
                }

            }
        }
    }
}