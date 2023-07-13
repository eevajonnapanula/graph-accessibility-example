package com.eevajonna.graphexample.ui.screens

import android.content.res.Configuration.UI_MODE_NIGHT_YES
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eevajonna.graphexample.ui.data.ApplicantsData
import com.eevajonna.graphexample.ui.data.all
import com.eevajonna.graphexample.ui.data.ict
import com.eevajonna.graphexample.ui.data.tech
import com.eevajonna.graphexample.ui.screens.utils.Point
import com.eevajonna.graphexample.ui.screens.utils.applicantsDataToPoint
import com.eevajonna.graphexample.ui.screens.utils.drawData
import com.eevajonna.graphexample.ui.screens.utils.drawXAxis
import com.eevajonna.graphexample.ui.screens.utils.drawYAxis
import com.eevajonna.graphexample.ui.screens.utils.filterHighlighted
import com.eevajonna.graphexample.ui.theme.GraphExampleTheme
import kotlinx.coroutines.delay

@Composable
fun GraphScreen(modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(32.dp),
        modifier = modifier,
    ) {
        val graphColors = GraphColors(
            totalColor = MaterialTheme.colorScheme.primary,
            ictColor = MaterialTheme.colorScheme.secondary,
            techColor = MaterialTheme.colorScheme.tertiary,
        )

        Text(
            "Percentage of woman applicants in Finnish higher education per year",
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .padding(horizontal = Graph.padding)
                .padding(top = 16.dp),
        )
        Graph(
            modifier = Modifier
                .padding(Graph.padding)
                .fillMaxWidth()
                .height(300.dp),
            total = all,
            tech = tech,
            ict = ict,
            graphColors = graphColors,
        )

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            GraphLegend(
                text = "Total",
                color = graphColors.totalColor,
            )
            GraphLegend(
                text = "Tech",
                color = graphColors.techColor,
            )
            GraphLegend(
                text = "Ict",
                color = graphColors.ictColor,
            )
        }
    }
}

@Composable
fun GraphLegend(text: String, color: Color) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(color)
                .border(2.dp, MaterialTheme.colorScheme.onBackground),
        ) {}
        Text(text)
    }
}

@OptIn(ExperimentalTextApi::class)
@Composable
fun Graph(
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

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
            .padding(bottom = Graph.padding),
    ) {
        var pixelPointsForTotal by remember { mutableStateOf(emptyList<Point>()) }
        var pixelPointsForTech by remember { mutableStateOf(emptyList<Point>()) }
        var pixelPointsForIct by remember { mutableStateOf(emptyList<Point>()) }

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

            LaunchedEffect(dragInProgress) {
                if (dragInProgress.not()) {
                    delay(1000L)
                    highlightedX = null
                }
            }

            Canvas(
                modifier = Modifier
                    .padding(
                        bottom = Graph.innerPadding,
                        start = Graph.innerPadding,
                        end = Graph.innerPadding / 2,
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

                selectedTotal = if (highlightedTotal.isNotEmpty()) highlightedTotal.first().percentageString else selectedTotal
                selectedTech = if (highlightedTech.isNotEmpty()) highlightedTech.first().percentageString else selectedTech
                selectedIct = if (highlightedIct.isNotEmpty()) highlightedIct.first().percentageString else selectedIct

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
                )
                drawData(
                    pixelPointsForIct,
                    color = graphColors.ictColor,
                    highlightedItemX = highlightedX,
                    highlighterColor = highlighterColor,
                )
                drawData(
                    pixelPointsForTotal,
                    color = graphColors.totalColor,
                    highlightedItemX = highlightedX,
                    highlighterColor = highlighterColor,
                )
            }

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
            .padding(bottom = 40.dp, end = 8.dp)
            .background(MaterialTheme.colorScheme.background)
            .border(1.dp, MaterialTheme.colorScheme.onBackground),
    ) {
        LabelText(listOf(selectedYear))
        LabelText(listOf("Total", selectedTotal))
        LabelText(listOf("Tech:", selectedTech))
        LabelText(listOf("Ict:", selectedIct))
    }
}

@Composable
fun LabelText(texts: List<String>) {
    Row(
        modifier = Modifier
            .padding(horizontal = 8.dp)
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

@Preview(showSystemUi = true)
@Composable
fun GraphPreview() {
    GraphExampleTheme {
        GraphScreen()
    }
}

@Preview(showSystemUi = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun DarkGraphPreview() {
    GraphExampleTheme {
        GraphScreen(modifier = Modifier.background(MaterialTheme.colorScheme.surface))
    }
}

data class GraphColors(
    val totalColor: Color,
    val techColor: Color,
    val ictColor: Color,
)

object Graph {
    val padding = 8.dp
    val innerPadding = 32.dp
}
