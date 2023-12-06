package com.eevajonna.graphexample.ui.screens.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import com.eevajonna.graphexample.R
import com.eevajonna.graphexample.ui.data.ApplicantsData
import com.eevajonna.graphexample.ui.screens.GraphColors
import com.eevajonna.graphexample.ui.screens.utils.Point
import com.eevajonna.graphexample.ui.screens.utils.applicantsDataToPoint
import com.eevajonna.graphexample.ui.screens.utils.drawData
import com.eevajonna.graphexample.ui.screens.utils.drawXAxis
import com.eevajonna.graphexample.ui.screens.utils.drawYAxis
import com.eevajonna.graphexample.ui.screens.utils.filterHighlighted
import kotlinx.coroutines.delay

@OptIn(ExperimentalTextApi::class)
@Composable
fun GraphComponent(
    modifier: Modifier = Modifier,
    total: List<ApplicantsData>,
    tech: List<ApplicantsData>,
    ict: List<ApplicantsData>,
    graphColors: GraphColors,
) {
    var highlightedX by remember { mutableStateOf<Float?>(null) }

    var selectedTotal by remember {
        mutableStateOf("")
    }

    var selectedTech by remember {
        mutableStateOf("")
    }

    var selectedIct by remember {
        mutableStateOf("")
    }

    var selectedYear by remember {
        mutableStateOf("")
    }

    val highlighterColor = MaterialTheme.colorScheme.onBackground
    var pixelPointsForTotal by remember { mutableStateOf(emptyList<Point>()) }
    var pixelPointsForTech by remember { mutableStateOf(emptyList<Point>()) }
    var pixelPointsForIct by remember { mutableStateOf(emptyList<Point>()) }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Column(
            modifier = modifier
                .clip(RoundedCornerShape(GraphComponent.topPadding))
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = GraphComponent.topPadding)
                .padding(bottom = GraphComponent.padding),
        ) {
            Box(
                contentAlignment = Alignment.Center,
            ) {
                val minX = total.first().year.toFloat()
                val maxX = total.last().year.toFloat()
                val minY = 14f
                val maxY = total.maxOf { it.percentage }.toFloat() + 5f

                val textMeasurer = rememberTextMeasurer()
                val onBackgroundColor = MaterialTheme.colorScheme.onBackground

                var dragInProgress by remember { mutableStateOf(false) }

                val widthBetweenPoints =
                    if (pixelPointsForTotal.isNotEmpty()) pixelPointsForTotal[1].x - pixelPointsForTotal[0].x else 0f

                LaunchedEffect(dragInProgress) {
                    if (dragInProgress.not()) {
                        delay(1000L)
                        highlightedX = null
                    }
                }

                Canvas(
                    modifier = Modifier
                        .padding(
                            bottom = GraphComponent.innerPadding,
                            start = GraphComponent.innerPadding,
                            end = GraphComponent.innerPadding / 2,
                        )
                        .pointerInput(Unit) {
                            detectHorizontalDragGestures(
                                onDragStart = {
                                    dragInProgress = true
                                },
                                onDragCancel = {
                                    dragInProgress = false
                                },
                                onDragEnd = {
                                    dragInProgress = false
                                },
                            ) { change, _ ->
                                change.consume()
                                highlightedX = change.position.x
                            }
                        }
                        .fillMaxSize(),
                ) {
                    pixelPointsForTotal = applicantsDataToPoint(
                        total,
                        minX,
                        minY,
                        maxX,
                        maxY,
                        size.height,
                        size.width,
                    )

                    pixelPointsForTech = applicantsDataToPoint(
                        tech,
                        minX,
                        minY,
                        maxX,
                        maxY,
                        size.height,
                        size.width,
                    )

                    pixelPointsForIct = applicantsDataToPoint(
                        ict,
                        minX,
                        minY,
                        maxX,
                        maxY,
                        size.height,
                        size.width,
                    )

                    val highlightedTotal = filterHighlighted(pixelPointsForTotal, highlightedX)
                    val highlightedTech = filterHighlighted(pixelPointsForTech, highlightedX)
                    val highlightedIct = filterHighlighted(pixelPointsForIct, highlightedX)

                    selectedYear =
                        if (highlightedTotal.isNotEmpty()) highlightedTotal.first().year.toString() else selectedYear

                    selectedTotal =
                        if (highlightedTotal.isNotEmpty()) highlightedTotal.first().percentageString else selectedTotal
                    selectedTech =
                        if (highlightedTech.isNotEmpty()) highlightedTech.first().percentageString else selectedTech
                    selectedIct =
                        if (highlightedIct.isNotEmpty()) highlightedIct.first().percentageString else selectedIct

                    drawYAxis(
                        textMeasurer = textMeasurer,
                        min = minY,
                        max = maxY,
                        height = size.height,
                        width = 0f,
                        onPrimaryColor = onBackgroundColor,
                    )

                    drawXAxis(
                        pixelPointsForTotal,
                        size.width,
                        size.height,
                        textMeasurer = textMeasurer,
                        highlightedItemX = highlightedX,
                        onPrimaryColor = onBackgroundColor,
                        highlighterColor = highlighterColor,
                    )

                    drawData(
                        pixelPointsForTech,
                        color = graphColors.techColor,
                        highlightedItemX = highlightedX,
                        highlighterColor = highlighterColor,
                        strokeCap = StrokeCap.Round,
                    )
                    drawData(
                        pixelPointsForIct,
                        color = graphColors.ictColor,
                        highlightedItemX = highlightedX,
                        highlighterColor = highlighterColor,
                        strokeCap = StrokeCap.Square,
                        dashed = true,
                    )
                    drawData(
                        pixelPointsForTotal,
                        color = graphColors.totalColor,
                        highlightedItemX = highlightedX,
                        highlighterColor = highlighterColor,
                        strokeCap = StrokeCap.Butt,
                        strokeWidth = 12.dp.toPx(),
                    )
                }

                Highlighter(
                    modifier = modifier,
                    widthBetweenPoints = widthBetweenPoints,
                    pixelPointsForTotal = pixelPointsForTotal,
                    pixelPointsForTech = pixelPointsForTech,
                    pixelPointsForIct = pixelPointsForIct,
                    highlightedX = highlightedX,
                    setFocus = { newX ->
                        highlightedX = newX
                    },
                )

                if (highlightedX != null) {
                    Labels(
                        Modifier.align(
                            Alignment.BottomEnd,
                        ),
                        selectedTotal,
                        selectedTech,
                        selectedIct,
                        selectedYear,
                    )
                }
            }
        }
        ControlButtons(
            highlightedX = highlightedX,
            lastIndex = pixelPointsForTotal.count() - 1,
        ) { selectedIndex ->
            highlightedX = pixelPointsForTotal[selectedIndex].x
        }
    }
}

@Composable
fun Labels(
    modifier: Modifier,
    selectedTotal: String,
    selectedTech: String,
    selectedIct: String,
    selectedYear: String,
) {
    Column(
        modifier = modifier
            .wrapContentSize()
            .padding(
                bottom = GraphComponent.GraphLabels.bottomPadding,
                end = GraphComponent.GraphLabels.endPadding,
            )
            .background(MaterialTheme.colorScheme.background)
            .border(GraphComponent.GraphLabels.borderWidth, MaterialTheme.colorScheme.onBackground),
    ) {
        LabelText(listOf(selectedYear))
        LabelText(listOf(stringResource(R.string.all), selectedTotal))
        LabelText(listOf(stringResource(R.string.eng), selectedTech))
        LabelText(listOf(stringResource(R.string.ict), selectedIct))
    }
}

@Composable
fun LabelText(texts: List<String>) {
    Row(
        modifier = Modifier
            .padding(horizontal = GraphComponent.padding)
            .fillMaxWidth(0.35f),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        texts.map {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }
    }
}

object GraphComponent {
    val padding = 8.dp
    val topPadding = 16.dp
    val innerPadding = 32.dp

    object GraphLabels {
        val bottomPadding = 40.dp
        val endPadding = 8.dp
        val borderWidth = 1.dp
    }
}
