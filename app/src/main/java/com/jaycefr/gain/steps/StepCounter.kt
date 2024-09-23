package com.jaycefr.gain.steps

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.Image
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.paint
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.room.Room
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import com.jaycefr.gain.PermissionManagerViewModel
import com.jaycefr.gain.R
import java.time.Instant

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun StepCounterScreen(stepViewModel: StepViewModel)
{

    var db by remember { mutableStateOf<StepAppDatabase?>(null) }
    var stepsRepo by remember { mutableStateOf<StepsRepo?>(null) }

    val stepCount by stepViewModel.stepCount.collectAsState()
    val lastUpdate by stepViewModel.lastupdate.collectAsState()

    val gifState by stepViewModel.gifState.collectAsState()

    var colors = mutableListOf<Color>()

    if (!isSystemInDarkTheme()){
        colors = mutableListOf(
            Color(185, 212, 220, 100),
            Color(200, 227, 233, 100),
            Color(215, 240, 252, 100))
    }
    else{
        colors = mutableListOf(Color(27, 54, 66, 100), Color(42, 69, 75, 100), Color(41, 72, 97, 100))
    }

    val context : Context = LocalContext.current
    LaunchedEffect(context) {
        db = Room.databaseBuilder(
            context,
            StepAppDatabase::class.java, "stepdb"
        ).build()

        stepsRepo = StepsRepo(db!!.stepsDao())

        stepViewModel.updateLastUpdate(stepsRepo!!.getLastStepUpdate())
        StepViewModelLinker.updateStepCount(stepsRepo!!.loadTodaySteps())

        val last_update = Instant.parse(lastUpdate)
        val current_time = Instant.now().minusSeconds(300)
        if (last_update.isBefore(current_time)){
            StepViewModelLinker.updateGifState(GifState.Standing)
        } else{
            StepViewModelLinker.updateGifState(GifState.Walking)
        }


    }

    val gifloader = ImageLoader.Builder(LocalContext.current)
        .components {
            if (Build.VERSION.SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }
        .build()

    val request = ImageRequest.Builder(LocalContext.current)
        .data(R.drawable.standing)
        .size(480, 480)
        .build()

    val walking_request = ImageRequest.Builder(LocalContext.current)
        .data(R.drawable.walking)
        .size(480, 480)
        .build()

    val infiniteTransition = rememberInfiniteTransition(label = "background")
    val targetOffset = with(LocalDensity.current){
        1000.dp.toPx()
    }
    val offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = targetOffset,
        animationSpec = infiniteRepeatable(
            tween(50000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse),
        label = "offset"
    )
    val brush = Brush.linearGradient(
        colors = colors,
        start = Offset(offset, offset),
        end = Offset(offset + 500, offset + 500),
        tileMode = TileMode.Mirror
    )
    val permissionViewModel = viewModel<PermissionManagerViewModel>()
    val requestPermission = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(), onResult = {
                permmap ->
            permmap.keys.forEach {
                    permission ->
                permissionViewModel.notify_permission_granted(permission, permmap[permission] == true, context)
            }
        })
    //Handling permissions
    val declined_permissions = permissionViewModel.permission_health_checker(LocalContext.current)

    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(brush),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            modifier = Modifier
                .height(300.dp)

        ){

//            Spacer(
//                modifier = Modifier
//                    .fillMaxHeight(0.8f)
//                    .fillMaxWidth()
//                    .blur(0.dp)
////                    .paint(
////                        painterResource(id = R.drawable.hexagon),
////                        contentScale = ContentScale.FillBounds
////                    )
//                    .drawWithCache {
//                        onDrawBehind { drawRect(brush) }
//                    }
//            )
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Box(
                    modifier = Modifier
                        .offset(y = 180.dp)
                        .clip(CircleShape)
                        .size(100.dp)
                ){
                    AsyncImage(
                        model = if (gifState.toString() == GifState.Walking.toString()) walking_request else request,
                        contentDescription = null ,
                        imageLoader = gifloader,
                        contentScale = ContentScale.FillBounds

                        ,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(90.dp))

        Column(
            modifier = Modifier
                .height(150.dp)
                .fillMaxWidth(0.85f)
                .clip(shape = RoundedCornerShape(15.dp))
                .background(color = Color.Transparent)
                .shadow(elevation = 4.dp)
                ,

            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {

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
        }

        Button(onClick = { requestPermission.launch(declined_permissions) }) {
            Text(text = "Request permission")
        }

    }







}