package com.jaycefr.gain.steps.ui

import android.content.Context
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CircularProgressBar(
    number : Long,
    percentage : Float,
    fontSize: TextUnit = 28.sp,
    radius: Dp = 50.dp,
    color: Color = Color.Green,
    colorHue: Color = Color.Magenta,
    strokeWidth : Dp = 8.dp,
    animDuration : Int = 100,
    animDelay : Int = 0
){
    var animationPlayed by remember {
        mutableStateOf(false)
    }

    val currPercentage = animateFloatAsState(
        targetValue = percentage,
        animationSpec = tween(
            durationMillis = animDuration,
            delayMillis = animDelay
        )
    )

    val context: Context = LocalContext.current

    DisposableEffect(key1 = true) {
        animationPlayed = true
        onDispose {
            animationPlayed = false
        }
//        Toast.makeText(context, "Animation played ${currPercentage.value}", Toast.LENGTH_SHORT).show()
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(radius * 2f)
    ) {
        val sizeModifier = remember(radius) { Modifier.size(radius * 2f) }
        androidx.compose.foundation.Canvas(modifier = sizeModifier) {
            drawArc(
                color = colorHue,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(strokeWidth.toPx(), cap= StrokeCap.Round)
            )
        }
        androidx.compose.foundation.Canvas(modifier = sizeModifier) {
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = 360 * currPercentage.value,
                useCenter = false,
                style = Stroke(strokeWidth.toPx(), cap= StrokeCap.Round)
            )
        }
        
        Text(
            text = "$number",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = fontSize,
            fontWeight = FontWeight.Bold
        )
        
    }


}