package com.jaycefr.gain

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jaycefr.gain.ui.theme.GainTheme
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume


class MainActivity : ComponentActivity() {

    private val sensorManager : SensorManager by lazy {
        getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    private val stepCounterSensor : Sensor? by lazy {
        sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
    }

    private var stepCount :Long= 0

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sensorManager.registerListener(
            stepCounterListener,
            stepCounterSensor,
            SensorManager.SENSOR_DELAY_NORMAL
        )

        enableEdgeToEdge()
        setContent {
            GainTheme {
                val permissionViewModel = viewModel<PermissionManagerViewModel>()
                val requestPermission = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestMultiplePermissions(), onResult = {
                        permmap ->
                        permmap.keys.forEach {
                            permission ->
                            permissionViewModel.notify_permission_granted(permission, permmap[permission] == true, baseContext)
                        }
                    })
                //Handling permissions
                val declined_permissions = permissionViewModel.permission_health_checker(baseContext)

                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Hello World!",
                    )
                    Button(onClick = { requestPermission.launch(declined_permissions) }) {
                        Text(text = "Request permission")
                    }
                    Text(
                        text = "STEPS : $stepCount"
                    )
                }
            }
        }
    }

    override fun onPause() {
        sensorManager.unregisterListener(stepCounterListener)
        super.onPause()
    }

    val stepCounterListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
                val steps = event.values[0].toLong()
                Log.d("StepCounter", "Steps: $steps")
                // Update the step count in your UI or ViewModel
                stepCount = steps
                // Handle the step count (e.g., display it in a TextView)
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            // Handle accuracy changes if needed
        }
    }



}