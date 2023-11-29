package com.eevajonna.graphexample.ui.screens

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eevajonna.graphexample.R
import com.eevajonna.graphexample.ui.data.all
import com.eevajonna.graphexample.ui.data.ict
import com.eevajonna.graphexample.ui.data.tech
import com.eevajonna.graphexample.ui.screens.components.GraphComponent
import com.eevajonna.graphexample.ui.theme.GraphExampleTheme

@Composable
fun GraphScreen(modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(GraphScreen.innerPadding),
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
                .padding(GraphScreen.topPadding),
        )
        GraphComponent(
            modifier = Modifier
                .padding(GraphScreen.padding)
                .fillMaxWidth()
                .height(GraphScreen.graphHeight),
            total = all,
            tech = tech,
            ict = ict,
            graphColors = graphColors,
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = GraphScreen.topPadding),
            verticalArrangement = Arrangement.spacedBy(GraphScreen.topPadding),
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
            modifier = Modifier.padding(horizontal = GraphScreen.topPadding),
            style = MaterialTheme.typography.labelMedium,
        )
    }
}

@Composable
fun GraphLegend(text: String, color: Color) {
    Row(horizontalArrangement = Arrangement.spacedBy(GraphScreen.padding), verticalAlignment = Alignment.CenterVertically) {
        Column(
            modifier = Modifier
                .size(GraphScreen.GraphLegend.size)
                .clip(GraphScreen.GraphLegend.shape)
                .background(color)
                .border(GraphScreen.GraphLegend.borderWidth, MaterialTheme.colorScheme.onBackground),
        ) {}
        Text(text)
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

object GraphScreen {
    val padding = 8.dp
    val topPadding = 16.dp
    val innerPadding = 32.dp
    val graphHeight = 300.dp

    object GraphLegend {
        val size = 32.dp
        val shape = RoundedCornerShape(4.dp)
        val borderWidth = 2.dp
    }
}
