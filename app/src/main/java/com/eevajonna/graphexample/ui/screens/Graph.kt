package com.eevajonna.graphexample.ui.screens

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.eevajonna.graphexample.R
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
        verticalArrangement = Arrangement.spacedBy(Graph.innerPadding),
        modifier = modifier,
    ) {
        val graphColors = GraphColors(
            totalColor = MaterialTheme.colorScheme.primary,
            ictColor = MaterialTheme.colorScheme.secondary,
            techColor = MaterialTheme.colorScheme.tertiary,
        )

        Text(
            stringResource(R.string.percentage_of_woman_applicants_in_finnish_higher_education_per_year),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .padding(Graph.topPadding),
        )
        Graph(
            modifier = Modifier
                .padding(Graph.padding)
                .fillMaxWidth()
                .height(Graph.graphHeight),
            total = all,
            tech = tech,
            ict = ict,
            graphColors = graphColors,
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Graph.topPadding),
            verticalArrangement = Arrangement.spacedBy(Graph.topPadding),
            horizontalAlignment = Alignment.Start,
        ) {
            GraphLegend(
                text = stringResource(R.string.all_applicants_in_engineering_and_ict),
                color = graphColors.totalColor,
            )
            GraphLegend(
                text = stringResource(R.string.engineering_degrees_eng),
                color = graphColors.techColor,
            )
            GraphLegend(
                text = stringResource(R.string.information_and_communication_technology_ict),
                color = graphColors.ictColor,
            )
        }

        Text(
            text = stringResource(R.string.data_source),
            modifier = Modifier.padding(horizontal = Graph.topPadding),
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
fun GraphLegend(text: String, color: Color) {
    Row(horizontalArrangement = Arrangement.spacedBy(Graph.padding), verticalAlignment = Alignment.CenterVertically) {
        Column(
            modifier = Modifier
                .size(Graph.GraphLegend.size)
                .clip(Graph.GraphLegend.shape)
                .background(color)
                .border(Graph.GraphLegend.borderWidth, MaterialTheme.colorScheme.onBackground),
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
            .clip(RoundedCornerShape(Graph.topPadding))
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = Graph.topPadding)
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

            val widthBetweenPoints = if (pixelPointsForTotal.isNotEmpty()) pixelPointsForTotal[1].x - pixelPointsForTotal[0].x else 0f

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

            Highlighter(
                modifier = modifier,
                widthBetweenPoints = widthBetweenPoints,
                pixelPointsForTotal = pixelPointsForTotal,
                pixelPointsForTech = pixelPointsForTech,
                pixelPointsForIct = pixelPointsForIct,
                highlightedX = highlightedX,
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
}

@Composable
fun Highlighter(
    modifier: Modifier = Modifier,
    widthBetweenPoints: Float,
    pixelPointsForTotal: List<Point>,
    pixelPointsForTech: List<Point>,
    pixelPointsForIct: List<Point>,
    highlightedX: Float?,
) {
    Box(
        modifier
            .fillMaxSize(),
    ) {
        val sectionWidth = with(LocalDensity.current) {
            widthBetweenPoints.toDp()
        }

        pixelPointsForTotal.forEachIndexed { index, point ->
            val xOffset = ((index + 1) * widthBetweenPoints - widthBetweenPoints * 0.66f).toInt()
            var isHighlighted by remember { mutableStateOf(false) }
            var position by remember { mutableStateOf(Pair(0f, 0f)) }

            if (highlightedX == null) isHighlighted = false

            highlightedX?.let {
                isHighlighted = it > (position.first - widthBetweenPoints) && it < (position.second - widthBetweenPoints)
            }

            val contentDesc = "${point.year}: " +
                "${stringResource(id = R.string.all)} ${point.percentageString}, " +
                "${stringResource(id = R.string.eng)} ${pixelPointsForTech[index].percentageString}, " +
                "${stringResource(id = R.string.ict)} ${pixelPointsForIct[index].percentageString}"

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(sectionWidth)
                    .offset { IntOffset(xOffset, 0) }
                    .border(
                        width = Graph.Highlighter.width,
                        color = if (isHighlighted) MaterialTheme.colorScheme.onBackground else Color.Transparent,
                        shape = RoundedCornerShape(Graph.Highlighter.borderRadius),
                    )
                    .onGloballyPositioned {
                        position =
                            Pair(it.positionInParent().x, it.positionInParent().x + it.size.width)
                    }
                    .focusable()
                    .semantics {
                        contentDescription = contentDesc
                    },
            ) {
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
            .padding(bottom = Graph.GraphLabels.bottomPadding, end = Graph.GraphLabels.endPadding)
            .background(MaterialTheme.colorScheme.background)
            .border(Graph.GraphLabels.borderWidth, MaterialTheme.colorScheme.onBackground),
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
            .padding(horizontal = Graph.padding)
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
    val topPadding = 16.dp
    val innerPadding = 32.dp
    val graphHeight = 300.dp

    object GraphLegend {
        val size = 32.dp
        val shape = RoundedCornerShape(4.dp)
        val borderWidth = 2.dp
    }

    object GraphLabels {
        val bottomPadding = 40.dp
        val endPadding = 8.dp
        val borderWidth = 1.dp
    }

    object Highlighter {
        val width = 2.dp
        val borderRadius = 4.dp
    }
}
