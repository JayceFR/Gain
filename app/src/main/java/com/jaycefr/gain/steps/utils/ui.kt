package com.jaycefr.gain.steps.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun NormalText(text : String, color : Color = MaterialTheme.colorScheme.onSecondary){
    Text(
        text = text,
        fontSize = MaterialTheme.typography.bodyLarge.fontSize,
        color = color
    )
}