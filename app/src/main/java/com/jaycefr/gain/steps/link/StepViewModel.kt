package com.jaycefr.gain.steps.link

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.Point
import co.yml.charts.ui.linechart.model.GridLines
import co.yml.charts.ui.linechart.model.IntersectionPoint
import co.yml.charts.ui.linechart.model.Line
import co.yml.charts.ui.linechart.model.LineChartData
import co.yml.charts.ui.linechart.model.LinePlotData
import co.yml.charts.ui.linechart.model.LineStyle
import co.yml.charts.ui.linechart.model.LineType
import co.yml.charts.ui.linechart.model.SelectionHighlightPoint
import co.yml.charts.ui.linechart.model.SelectionHighlightPopUp
import co.yml.charts.ui.linechart.model.ShadowUnderLine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate

class StepViewModel() : ViewModel() {
//    var stepCount by mutableLongStateOf(0L)

    val stepCount : StateFlow<Long> get() = StepViewModelLinker.stepCount

    val stepPercentage : StateFlow<Float> get() = StepViewModelLinker.stepPercentage

    val stepGoal : StateFlow<Long> get() = StepViewModelLinker.stepGoal

    private val _stepWeekList = MutableStateFlow(mutableListOf<Int>())
    val stepWeekList : StateFlow<MutableList<Int>> get() = _stepWeekList

    private val _stepWeekStreak = MutableStateFlow(0)
    val stepWeekStreak : StateFlow<Int> get() = _stepWeekStreak

    private val _currentlySelectedDay = MutableStateFlow(0)
    val currentlySelectedDay : StateFlow<Int> get() = _currentlySelectedDay

    private val _selectedDaySteps = MutableStateFlow(0L)
    val selectedDaySteps : StateFlow<Long> get() = _selectedDaySteps

    private val _lastupdate = MutableStateFlow("");
    val lastupdate : StateFlow<String> get() = _lastupdate

    private val _graphPointData = MutableStateFlow(mutableListOf<Point>())
    val graphPointData : StateFlow<MutableList<Point>> get() = _graphPointData

    private val _lineChartData = MutableStateFlow<LineChartData?>(null)
    val lineChartData : StateFlow<LineChartData?> get() = _lineChartData

    val gifState : StateFlow<GifState> get() = StepViewModelLinker.gifState

    init {
        _currentlySelectedDay.value = LocalDate.now().dayOfWeek.value - 1
    }

    fun updateLastUpdate(update: String){
        _lastupdate.value = update
    }

    fun updateStepWeekList(list: MutableList<Int>){
        _stepWeekList.value = list
    }

    fun updateStepWeekStreak(streak: Int){
        _stepWeekStreak.value = streak
    }

    fun updateCurrentlySelectedDay(day: Int){
        _currentlySelectedDay.value = day
    }

    fun updateGraphPointData(data: MutableList<Point>){
        _graphPointData.value = data
    }

    fun updateSelectedDaySteps(steps: Long){
        _selectedDaySteps.value = steps
    }


    fun initLineChart(bgColor : Color, textColor : Color, dayStepCount : Long){
        val xAxisData = AxisData.Builder()
            .axisStepSize(50.dp)
            .backgroundColor(bgColor)
            .steps(24)
            .labelData { i -> i.toString() }
            .labelAndAxisLinePadding(15.dp)
            .axisLineColor(textColor.copy(alpha = 0.5f))
            .axisLabelColor(textColor)
            .build()

        val steps = 5

        val yAxisData = AxisData.Builder()
            .steps(steps)
            .backgroundColor(bgColor)
            .labelAndAxisLinePadding(20.dp)
            .axisLineColor(textColor.copy(alpha = 0.5f))
            .axisLabelColor(textColor)
            .labelData { i ->
                val yScale = dayStepCount / steps
                (i * yScale).toString()
            }.build()

        _lineChartData.value = LineChartData(
            linePlotData = LinePlotData(
                lines = listOf(
                    Line(
                        dataPoints = _graphPointData.value,
                        LineStyle(
                            color = textColor,
                            lineType = LineType.SmoothCurve(isDotted = false)
                        ),
                        IntersectionPoint(
                            color = Color.Transparent
                        ),
                        SelectionHighlightPoint(),
                        ShadowUnderLine(
                            alpha = 0.5f,
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color.Magenta,
                                    bgColor
                                )
                            )
                        ),
                        SelectionHighlightPopUp()
                    )
                ),
            ),
            isZoomAllowed = false,
            xAxisData = xAxisData,
            yAxisData = yAxisData,
            gridLines = null,
            paddingTop = 13.dp,
            paddingRight = 0.dp,
            containerPaddingEnd = 0.dp,
            bottomPadding = 0.dp,
            backgroundColor = Color.Transparent
        )
    }

}

enum class GifState{
    Standing,
    Walking
}

