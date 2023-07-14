package com.eevajonna.graphexample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.Modifier
import com.eevajonna.graphexample.ui.screens.GraphScreen
import com.eevajonna.graphexample.ui.theme.GraphExampleTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GraphExampleTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(title = {})
                    },
                ) {
                    GraphScreen(modifier = Modifier.padding(it))
                }
            }
        }
    }
}
