package com.jaycefr.gain.steps

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.LocalContext
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Alignment
import androidx.glance.layout.fillMaxSize
import com.jaycefr.gain.MainActivity
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

        val context : Context = LocalContext.current

        androidx.glance.layout.Column(
            modifier = GlanceModifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
//            androidx.glance.text.Text(
//                text = "Step Count : $count",
//                modifier = GlanceModifier.padding(12.dp),
//                style = TextStyle(
//                    fontWeight = FontWeight.Medium,
//                    color = ColorProvider(Color.White),
//                    fontSize = 16.sp
//                )
//            )
            Button(text = count.toString(), onClick = {
                androidx.glance.appwidget.action.actionStartActivity(
                    Intent(context, MainActivity::class.java)
                )
            })
        }
    }

}

class StepWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget
        get() = StepWidget
}

