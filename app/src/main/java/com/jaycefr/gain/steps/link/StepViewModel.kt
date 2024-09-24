package com.jaycefr.gain.steps.link

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

    fun initLineChart(){
        val xAxisData = AxisData.Builder()
            .axisStepSize(20.dp)
            .backgroundColor(Color.Blue)
            .steps(6)
            .labelData { i -> i.toString() }
            .labelAndAxisLinePadding(15.dp)
            .build()

        val steps = 5

        val yAxisData = AxisData.Builder()
            .steps(steps)
            .backgroundColor(Color.Red)
            .labelAndAxisLinePadding(20.dp)
            .labelData { i ->
                val yScale = 100 / steps
                (i * yScale).toString()
            }.build()

        _lineChartData.value = LineChartData(
            linePlotData = LinePlotData(
                lines = listOf(
                    Line(
                        dataPoints = _graphPointData.value,
                        LineStyle(),
                        IntersectionPoint(),
                        SelectionHighlightPoint(),
                        ShadowUnderLine(),
                        SelectionHighlightPopUp()
                    )
                ),
            ),
            xAxisData = xAxisData,
            yAxisData = yAxisData,
            gridLines = GridLines(),
            backgroundColor = Color.White
        )
    }

}

enum class GifState{
    Standing,
    Walking
}

