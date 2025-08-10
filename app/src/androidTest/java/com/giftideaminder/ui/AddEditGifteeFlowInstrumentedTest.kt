package com.threekidsinatrenchcoat.giftideaminder.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.threekidsinatrenchcoat.giftideaminder.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AddEditGifteeFlowInstrumentedTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun addPerson_flow_showsSnackbarOnSave() {
        composeRule.onNodeWithContentDescription("Add Options").performClick()
        composeRule.onNodeWithText("Add Person").performClick()
        composeRule.onNodeWithText("Friend").performClick()
        composeRule.onNodeWithText("Next").performClick()
        composeRule.onNode(hasSetTextAction()).performTextInput("Test User")
        composeRule.onNodeWithText("Next").performClick()
        composeRule.onNodeWithText("Next").performClick()
        composeRule.onNodeWithText("Save").performClick()
        composeRule.onNodeWithText("Test User was added").assertIsDisplayed()
    }

    @Test
    fun datesStep_addCustomLabel_and_remove() {
        composeRule.onNodeWithContentDescription("Add Options").performClick()
        composeRule.onNodeWithText("Add Person").performClick()

        // Relationship -> Details
        composeRule.onNodeWithText("Friend").performClick()
        composeRule.onNodeWithText("Next").performClick()
        composeRule.onNode(hasSetTextAction()).performTextInput("Custom Date User")
        composeRule.onNodeWithText("Next").performClick()

        // Dates: add custom label
        composeRule.onNodeWithText("Custom date label").performTextInput("Graduation")
        composeRule.onNodeWithText("Pick date & add").performClick()
        composeRule.onNodeWithText("Done").performClick()

        // Header appears when a non-prompt date is present (best-effort)
        composeRule.onNodeWithText("Other dates").assertIsDisplayed()

        // Remove the custom date and finish
        composeRule.onNodeWithText("Clear").performClick()
        composeRule.onNodeWithText("Next").performClick()
        composeRule.onNodeWithText("Save").performClick()
        composeRule.onNodeWithText("Custom Date User was added").assertIsDisplayed()
    }
}