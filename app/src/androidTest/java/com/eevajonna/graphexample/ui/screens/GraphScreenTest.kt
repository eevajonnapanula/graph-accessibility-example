package com.eevajonna.graphexample.ui.screens

import androidx.compose.ui.input.key.Key
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertAny
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.test.swipeRight
import com.eevajonna.graphexample.ui.theme.GraphExampleTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class GraphScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun touchInteractionsWorkCorrectly() {
        val labels = composeTestRule.onNode(hasTestTag(TestTags.labelsTestTag))
        val chart = composeTestRule.onNode(hasTestTag(TestTags.chartTestTag))

        labels.assertIsNotDisplayed()

        // Navigate forward
        chart.performTouchInput {
            swipeRight(startX = 0f, endX = 30f)
        }

        labels.assertIsDisplayed()
        labels.onChildren().assertAny(hasText("2015"))

        // Navigate forward
        chart.performTouchInput {
            swipeRight(startX = 30f, endX = 70f)
        }

        labels.onChildren().assertAny(hasText("2016"))
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun keyboardNavigationWorksCorrectly() {
        val labels = composeTestRule.onNode(hasTestTag(TestTags.labelsTestTag))
        val chart = composeTestRule.onNode(hasTestTag(TestTags.chartTestTag))

        labels.assertIsNotDisplayed()

        // Navigate forward
        chart.performKeyInput {
            pressKey(Key.DirectionRight)
            pressKey(Key.DirectionRight)
            pressKey(Key.DirectionRight)
            pressKey(Key.DirectionRight)
            pressKey(Key.DirectionRight)
            pressKey(Key.DirectionRight)
        }

        labels.onChildren().assertAny(hasText("2020"))

        // Navigate back
        chart.performKeyInput {
            pressKey(Key.DirectionLeft)
            pressKey(Key.DirectionLeft)
            pressKey(Key.DirectionLeft)
        }

        labels.onChildren().assertAny(hasText("2017"))
    }

    @Test
    fun buttonNavigationWorksCorrectly() {
        val labels = composeTestRule.onNode(hasTestTag(TestTags.labelsTestTag))
        val leftButton = composeTestRule.onNode(hasTestTag(TestTags.leftButtonTestTag))
        val rightButton = composeTestRule.onNode(hasTestTag(TestTags.rightButtonTestTag))

        labels.assertIsNotDisplayed()

        // Navigate forward
        rightButton.performClick()

        labels.assertIsDisplayed()

        labels.onChildren().assertAny(hasText("2015"))

        // Navigate forward
        rightButton.performClick()
        rightButton.performClick()
        rightButton.performClick()
        rightButton.performClick()

        // Navigate back
        leftButton.performClick()
        leftButton.performClick()

        labels.onChildren().assertAny(hasText("2017"))
    }

    @Before
    fun setupTests() {
        composeTestRule.setContent {
            GraphExampleTheme {
                GraphScreen()
            }
        }
    }
}
