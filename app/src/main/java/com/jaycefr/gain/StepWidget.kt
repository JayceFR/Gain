package com.jaycefr.gain

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Alignment
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.jaycefr.gain.steps.StepViewModelLinker
import kotlinx.coroutines.runBlocking

object StepWidget : GlanceAppWidget() {

    val stepKey = ActionParameters.Key<Long>("step")

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val stepCount = runBlocking {
            StepViewModelLinker.stepCount.value
        }
        provideContent {
            MyContent(stepCount)
        }
    }



    @Composable
    private fun MyContent(count : Long){
        androidx.glance.layout.Column(
            modifier = GlanceModifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            androidx.glance.text.Text(
                text = "Step Count : $count",
                modifier = GlanceModifier.padding(12.dp),
                style = TextStyle(
                    fontWeight = FontWeight.Medium,
                    color = ColorProvider(Color.White),
                    fontSize = 16.sp
                )
            )
        }
    }

}

class StepWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget
        get() = StepWidget
}

