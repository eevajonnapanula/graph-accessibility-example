package com.eevajonna.graphexample.ui.screens.utils

import android.util.Log
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.drawText
import androidx.compose.ui.unit.dp
import com.eevajonna.graphexample.ui.data.ApplicantsData

fun applicantsDataToPoint(
    list: List<ApplicantsData>,
    minX: Float,
    minY: Float,
    maxX: Float,
    maxY: Float,
    height: Float,
    width: Float,
): List<Point> {
    return list.map {
        val x = it.year.toFloat().mapValueToDifferentRange(
            minX,
            maxX,
            0f,
            width,
        )

        val y = it.percentage.toFloat().mapValueToDifferentRange(
            minY,
            maxY,
            height,
            0f,
        )

        Point(x, y, it.year, it.percentage.toFloat())
    }
}

data class Point(
    val x: Float,
    val y: Float,
    val year: Int,
    val percentage: Float,
    val isHighlighted: Boolean = false,
) {
    val percentageString = "${percentage.toInt()} %"
}

fun getValues(min: Float, max: Float, width: Float, height: Float): List<Pair<Float, Float>> {
    val steps = (max - min) / 4
    val step = 4
    val values = mutableListOf(Pair(min, min))

    for (i in 1..steps.toInt()) {
        val value = min + i * step

        val mappedValue = value.mapValueToDifferentRange(
            min,
            max,
            height,
            width,
        )

        values.add(Pair(mappedValue, value))
    }
    return values
}

@OptIn(ExperimentalTextApi::class)
fun DrawScope.drawYAxis(
    textMeasurer: TextMeasurer,
    min: Float,
    max: Float,
    width: Float,
    height: Float,
    onPrimaryColor: Color,
) {
    val mappedValues = getValues(min, max, width, height)

    mappedValues.forEachIndexed { index, it ->
        if (index != 0) {
            val path = Path()

            path.moveTo(
                20f,
                it.first,
            )
            path.lineTo(size.width + 32f, it.first)

            drawPath(
                path,
                color = onPrimaryColor,
                style = Stroke(
                    width = 1f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f),
                ),
            )

            val text = AnnotatedString(it.second.toInt().toString())
            val textLayoutResult = textMeasurer.measure(text)
            val textHeight = textLayoutResult.size.height / 2
            val textWidth = textLayoutResult.size.width.toFloat()
            drawText(
                textLayoutResult,
                color = onPrimaryColor,
                topLeft = Offset(-textWidth - 16f, it.first - textHeight),
            )
        }
    }
}

@OptIn(ExperimentalTextApi::class)
fun DrawScope.drawXAxis(
    values: List<Point>,
    width: Float,
    height: Float,
    textMeasurer: TextMeasurer,
    highlightedItemX: Float?,
    onPrimaryColor: Color,
    highlighterColor: Color,
) {
    val xAxisPath = Path()

    xAxisPath.moveTo(20f, height)
    xAxisPath.lineTo(width + 20f, height)

    drawPath(
        xAxisPath,
        color = onPrimaryColor,
        style = Stroke(width = 3f),
    )

    values.forEach {
        val isHighlighted = highlightedItemX?.let { item ->
            it.x.toInt() in item.toInt() - 30..item.toInt() + 30
        } ?: false

        val highlightedOrNotColor = if (isHighlighted) highlighterColor else onPrimaryColor

        val path = Path()
        path.moveTo(it.x + 20f, height)
        path.lineTo(it.x + 20f, height - 20f)
        drawPath(
            path,
            color = highlightedOrNotColor,
            style = Stroke(width = 3f),
        )

        val text = AnnotatedString(it.year.toString())
        val textLayoutResult = textMeasurer.measure(text)
        val textWidth = textLayoutResult.size.width / 2f
        val textXOffset = it.x - textWidth + 20f
        val offset = Offset(textXOffset, height + 32f)
        val rect = Rect(offset, Size(textLayoutResult.size.width.toFloat(), textLayoutResult.size.height.toFloat()))

        if (isHighlighted) {
            val highlighterPath = Path()
            highlighterPath.moveTo(it.x + 20f, height)
            highlighterPath.lineTo(it.x + 20f, 0f)

            Log.v("AAA BBB", "Hey?")
            drawPath(
                highlighterPath,
                color = highlighterColor,
                style = Stroke(
                    width = 2f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f),
                ),
            )
        }
        rotate(60f, rect.center) {
            drawText(
                textLayoutResult,
                color = highlightedOrNotColor,
                topLeft = offset,
            )
        }
    }
}

fun DrawScope.drawData(
    pixelPoints: List<Point>,
    color: Color,
    highlightedItemX: Float?,
    highlighterColor: Color,
) {
    val path = Path()

    path.moveTo(pixelPoints.first().x + 20f, pixelPoints.first().y)

    pixelPoints.forEachIndexed { index, point ->
        val previous = if (index == 0) {
            pixelPoints.first()
        } else {
            pixelPoints[index - 1]
        }

        path.cubicTo(
            (previous.x + 20f + point.x + 20f) / 2,
            previous.y,
            (previous.x + 20f + point.x + 20f) / 2,
            point.y,
            point.x + 20f,
            point.y,
        )
    }

    drawPath(
        path,
        color = color,
        style = Stroke(width = 3f),
    )

    drawPoints(
        pixelPoints.map { Offset(it.x + 20f, it.y) },
        PointMode.Points,
        color = color,
        strokeWidth = 8.dp.toPx(),
        cap = StrokeCap.Round,
    )

    drawPoints(
        filterHighlighted(pixelPoints, highlightedItemX).map { Offset(it.x + 20f, it.y) },
        PointMode.Points,
        color = highlighterColor,
        strokeWidth = 8.dp.toPx(),
        cap = StrokeCap.Square,
    )
}

fun filterHighlighted(values: List<Point>, highlightedItemX: Float?): List<Point> {
    if (highlightedItemX == null) return emptyList()

    val rangeToBothSides = 35

    return values.filter { it.x.toInt() in highlightedItemX.toInt() - rangeToBothSides..highlightedItemX.toInt() + rangeToBothSides }
}
