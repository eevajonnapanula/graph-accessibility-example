package com.eevajonna.graphexample.ui.screens.components

import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.eevajonna.graphexample.R
import com.eevajonna.graphexample.ui.screens.utils.Point

@Composable
fun Highlighter(
    modifier: Modifier = Modifier,
    widthBetweenPoints: Float,
    pixelPointsForTotal: List<Point>,
    pixelPointsForTech: List<Point>,
    pixelPointsForIct: List<Point>,
    highlightedX: Float?,
    setFocus: (Float) -> Unit,
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
            var color by remember { mutableStateOf(Color.Transparent) }

            val focusedColor = MaterialTheme.colorScheme.onBackground

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
                        width = Highlighter.width,
                        color = color,
                        shape = RoundedCornerShape(Highlighter.borderRadius),
                    )
                    .onGloballyPositioned {
                        position =
                            Pair(it.positionInParent().x, it.positionInParent().x + it.size.width)
                    }
                    .onFocusChanged {
                        color = if (it.isFocused) focusedColor else Color.Transparent

                        if (it.isFocused) {
                            setFocus(point.x)
                        }
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
object Highlighter {
    val width = 2.dp
    val borderRadius = 4.dp
}
