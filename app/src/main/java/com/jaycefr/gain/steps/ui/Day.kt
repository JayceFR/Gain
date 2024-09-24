package com.jaycefr.gain.steps.ui

import android.graphics.Paint.Align
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.layout.Box

@Composable
fun day(text : String, color : Color = Color.Red){
    androidx.compose.foundation.layout.Box(
        modifier = Modifier
            .size(30.dp, 30.dp)
            .clip(CircleShape)
            .background(color = color),
        contentAlignment = Alignment.Center

    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = MaterialTheme.typography.bodyLarge.fontSize
        )
    }
}