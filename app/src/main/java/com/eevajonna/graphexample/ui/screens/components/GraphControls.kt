package com.eevajonna.graphexample.ui.screens.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.eevajonna.graphexample.R

@Composable
fun ControlButtons(
    highlightedX: Float?,
    lastIndex: Int,
    setFocus: (Int) -> Unit,
) {
    var selectedIndex by remember {
        mutableStateOf(-1)
    }

    fun setSelectedIndexAndFocus(newIndex: Int) {
        selectedIndex = newIndex
        setFocus(newIndex)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = GraphControls.padding),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        ControlButton(
            type = GraphControlType.Previous,
            currentIndex = selectedIndex,
            lastIndex = lastIndex,
            firstFocusIndex = lastIndex,
            highlightedX = highlightedX,
            setFocus = {
                setSelectedIndexAndFocus(it)
            },
        )
        ControlButton(
            type = GraphControlType.Next,
            currentIndex = selectedIndex,
            lastIndex = lastIndex,
            firstFocusIndex = 0,
            highlightedX = highlightedX,
            setFocus = {
                setSelectedIndexAndFocus(it)
            },
        )
    }
}

@Composable
fun ControlButton(
    type: GraphControlType,
    currentIndex: Int,
    lastIndex: Int,
    firstFocusIndex: Int,
    highlightedX: Float?,
    setFocus: (Int) -> Unit,
) {
    val nextIndexFunc = when (type) {
        GraphControlType.Next -> ::getNextIndex
        GraphControlType.Previous -> ::getPreviousIndex
    }

    OutlinedButton(onClick = {
        val newSelectedIndex = when (highlightedX) {
            null -> firstFocusIndex
            else -> nextIndexFunc(currentIndex, lastIndex)
        }

        setFocus(newSelectedIndex)
    }) {
        if (type == GraphControlType.Previous) ControlButtonIcon(icon = type.icon)
        Text(stringResource(id = type.textResId))
        if (type == GraphControlType.Next) ControlButtonIcon(icon = type.icon)
    }
}

@Composable
fun ControlButtonIcon(icon: ImageVector) {
    Icon(
        imageVector = icon,
        contentDescription = null,
        modifier = Modifier.padding(
            end = GraphControls.buttonContentPadding,
        ),
    )
}

private fun getNextIndex(currentIndex: Int, lastIndex: Int): Int {
    return when (currentIndex) {
        lastIndex -> 0
        else -> currentIndex + 1
    }
}

private fun getPreviousIndex(currentIndex: Int, lastIndex: Int): Int {
    return when (currentIndex) {
        0 -> lastIndex
        else -> currentIndex - 1
    }
}

object GraphControls {
    val buttonContentPadding = 8.dp
    val padding = 12.dp
}

enum class GraphControlType {
    Next {
        override val icon = Icons.Filled.ArrowForward
        override val textResId = R.string.button_next_year
    },
    Previous {
        override val icon = Icons.Filled.ArrowBack
        override val textResId = R.string.button_previous_year
    }, ;

    abstract val icon: ImageVector
    abstract val textResId: Int
}
