package com.jaycefr.gain.steps.ui

import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import co.yml.charts.axis.AxisData
import co.yml.charts.ui.linechart.LineChart
import co.yml.charts.ui.linechart.model.GridLines
import co.yml.charts.ui.linechart.model.IntersectionPoint
import co.yml.charts.ui.linechart.model.Line
import co.yml.charts.ui.linechart.model.LineChartData
import co.yml.charts.ui.linechart.model.LinePlotData
import co.yml.charts.ui.linechart.model.LineStyle
import co.yml.charts.ui.linechart.model.SelectionHighlightPoint
import co.yml.charts.ui.linechart.model.SelectionHighlightPopUp
import co.yml.charts.ui.linechart.model.ShadowUnderLine
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import com.jaycefr.gain.PermissionManagerViewModel
import com.jaycefr.gain.R
import com.jaycefr.gain.steps.link.GifState
import com.jaycefr.gain.steps.models.StepAppDatabase
import com.jaycefr.gain.steps.link.StepViewModel
import com.jaycefr.gain.steps.link.StepViewModelLinker
import com.jaycefr.gain.steps.link.StepsRepo
import com.jaycefr.gain.steps.utils.NormalText
import com.jaycefr.gain.steps.utils.getDecimalPlace
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import kotlin.math.min

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun StepCounterScreen(stepViewModel: StepViewModel)
{

    var db by remember { mutableStateOf<StepAppDatabase?>(null) }
    var stepsRepo by remember { mutableStateOf<StepsRepo?>(null) }
    
    var stepWeekList = stepViewModel.stepWeekList.collectAsState()

    val stepCount by stepViewModel.stepCount.collectAsState()
    val lastUpdate by stepViewModel.lastupdate.collectAsState()
    val stepPercentage by stepViewModel.stepPercentage.collectAsState()
    val stepGoal by stepViewModel.stepGoal.collectAsState()
    val stepWeekStreak by stepViewModel.stepWeekStreak.collectAsState()

    val currentlySelectedDate by stepViewModel.currentlySelectedDay.collectAsState()

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    val graphPointData by stepViewModel.graphPointData.collectAsState()
    val lineCharData by stepViewModel.lineChartData.collectAsState()

    val gifState by stepViewModel.gifState.collectAsState()

    val bgColor : Color = MaterialTheme.colorScheme.primaryContainer
    val textColor : Color = MaterialTheme.colorScheme.onBackground

    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

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

        val (weekHistory, streak) = stepsRepo?.loadWeeklyHistory()!!

        stepViewModel.updateStepWeekList(weekHistory)
        stepViewModel.updateStepWeekStreak(streak)
        stepViewModel.updateGraphPointData(stepsRepo?.generateGraphPoints(LocalDate.now().minusDays(0))!!)
    }

    if (graphPointData.size > 0 && lineCharData == null){
        stepViewModel.initLineChart(
            bgColor = bgColor,
            textColor = textColor,
            dayStepCount = stepCount
        )
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

    val scope = rememberCoroutineScope()

    Column (
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        Spacer(modifier = Modifier.height(90.dp))

        CircularProgressBar(percentage = stepPercentage, number = stepCount)

        Spacer(modifier = Modifier.height(20.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
        ) {
            AsyncImage(
                model = if (gifState.toString() == GifState.Walking.toString()) walking_request else request,
                contentDescription = null,
                imageLoader = gifloader,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .width(52.dp)
                    .height(52.dp)
                    .offset(screenWidth * 0.9f * min(stepPercentage, 1f) - 26.dp, 0.dp)
                    .clip(CircleShape),
            )
        }

        Column(
            modifier = Modifier
                .height(40.dp)
                .fillMaxWidth(0.9f)
        ) {

            LinearProgressIndicator(
                progress = {
                    stepPercentage
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(15.dp)
                    .clip(RoundedCornerShape(15.dp)),
                strokeCap = StrokeCap.Round,
                color = Color.Green,
                trackColor = Color.Magenta
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "0",
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "$stepGoal",
                    color = MaterialTheme.colorScheme.onBackground

                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        Column(
            modifier = Modifier
                .height(150.dp)
                .fillMaxWidth(0.9f)
                .clip(shape = RoundedCornerShape(25.dp))
                .shadow(elevation = 4.dp)
                .background(color = MaterialTheme.colorScheme.primaryContainer),

            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,

            ) {
            Text(
                text = "${(stepCount * 0.762).toInt()}m",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = MaterialTheme.typography.displayMedium.fontSize
            )

            Text(
                text = "Distance Covered",
                color = MaterialTheme.colorScheme.onSecondary,
                fontSize = MaterialTheme.typography.titleMedium.fontSize
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
        Column(
            modifier = Modifier
                .height(150.dp)
                .fillMaxWidth(0.9f)
                .clip(shape = RoundedCornerShape(25.dp))
                .shadow(elevation = 4.dp)
                .background(color = MaterialTheme.colorScheme.primaryContainer),

            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,

            ) {
            Text(
                text = "${getDecimalPlace(stepCount * 0.033f)}kcal",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = MaterialTheme.typography.displayMedium.fontSize
            )

            Text(
                text = "Calories Burned",
                color = MaterialTheme.colorScheme.onSecondary,
                fontSize = MaterialTheme.typography.titleMedium.fontSize
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Column(
            modifier = Modifier
                .height(560.dp)
                .fillMaxWidth(0.9f)
                .clip(shape = RoundedCornerShape(25.dp))
                .shadow(elevation = 4.dp)
                .background(color = MaterialTheme.colorScheme.primaryContainer),

            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top,

            ) {

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Text(
                    text = "This Week",
                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.End,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .offset(x = (15).dp)
                )

                Text(
                    text = "History",
                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                    color = MaterialTheme.colorScheme.onSecondary,
                    textAlign = TextAlign.End,
                    modifier = Modifier
                        .offset(x = (-15).dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for(x in 0..<stepWeekList.value.size){
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable {
                                stepViewModel.updateCurrentlySelectedDay(x)
                                scope.launch {
                                    stepViewModel.updateGraphPointData(stepsRepo?.generateGraphPoints(LocalDate.now().minusDays(LocalDate.now().dayOfWeek.value.toLong()).plusDays(x + 1.toLong()))!!)
                                    stepViewModel.initLineChart(
                                        bgColor = bgColor,
                                        textColor = textColor,
                                        dayStepCount = stepCount
                                    )
                                }
                                Toast.makeText(context, LocalDate.now().minusDays(LocalDate.now().dayOfWeek.value.toLong()).plusDays(x + 1.toLong()).toString(), Toast.LENGTH_SHORT).show()
//                                Toast.makeText(context, "Clicked $x", Toast.LENGTH_SHORT).show()
                            }
                    ) {
                        NormalText(
                            text = days[x],
                            color = if (currentlySelectedDate == x) Color.Magenta else MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        day(
                            text = LocalDate.now().minusDays(LocalDate.now().dayOfWeek.value.toLong()).plusDays(
                            (x+1).toLong()).toString().split("-")[2],
                            color = if (stepWeekList.value[x] == 1) Color.Green.copy(alpha = 0.6f) else Color.Red.copy(alpha = 0.6f)
                        )
                    }
                }

            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "$stepWeekStreak day Streak!",
                textAlign = TextAlign.Start,
                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .offset(x = (15).dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                horizontalAlignment = Alignment.CenterHorizontally

            ) {
                lineCharData?.let {
                    LineChart(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        lineChartData = it
                    )
                }
            }



        }

        Button(onClick = { requestPermission.launch(declined_permissions) }) {
            Text(text = "Request permission")
        }

    }

}