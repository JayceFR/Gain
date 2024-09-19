package com.jaycefr.gain

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import com.jaycefr.gain.steps.Actions
import com.jaycefr.gain.steps.StepAppDatabase
import com.jaycefr.gain.steps.StepCounterScreen
import com.jaycefr.gain.steps.StepForegroundService
import com.jaycefr.gain.steps.StepViewModel
import com.jaycefr.gain.steps.StepsRepo
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

        Intent(applicationContext, StepForegroundService::class.java).also {
            it.action = Actions.START.toString()
            startService(it)
        }

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
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.tertiaryContainer,
                                    MaterialTheme.colorScheme.background
                                ),
                                start = Offset.Zero,
                                end = Offset(0f, 1000f)
                            )
                        )
                    ,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(38.dp))

                    Text(
                        text="Home",
                        fontSize = MaterialTheme.typography.titleMedium.fontSize,
                        fontFamily = MaterialTheme.typography.titleMedium.fontFamily,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )

                    StepCounterScreen(viewModel)
                    Spacer(modifier = Modifier.height(80.dp))
                    Text(
                        text = "Hello World!",
                    )
                    Button(onClick = { requestPermission.launch(declined_permissions) }) {
                        Text(text = "Request permission")
                    }
                }
            }
        }
    }
}