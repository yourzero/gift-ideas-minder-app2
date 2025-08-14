package com.threekidsinatrenchcoat.giftideaminder.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
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
        // Dates -> Preferences
        composeRule.onNodeWithText("Next").performClick()
        // Preferences -> Review
        composeRule.onNodeWithText("Next").performClick()
        composeRule.onNodeWithText("Save").performClick()
        composeRule.onNodeWithText("Test User was added").assertIsDisplayed()
    }

    @Test
    fun datesStep_addTypedDate_and_remove() {
        composeRule.onNodeWithContentDescription("Add Options").performClick()
        composeRule.onNodeWithText("Add Person").performClick()

        // Relationship -> Details
        composeRule.onNodeWithText("Friend").performClick()
        composeRule.onNodeWithText("Next").performClick()
        composeRule.onNode(hasSetTextAction()).performTextInput("Typed Date User")
        composeRule.onNodeWithText("Next").performClick()

        // Dates: add a typed date row, select a known type, pick a date
        composeRule.onNodeWithText("Add Date").performClick()
        // Open type dropdown using testTag to avoid ambiguity
        composeRule.onNodeWithTag("date-type-selector").performClick()
        composeRule.onNodeWithText("Graduation").performClick()
        // Pick date
        composeRule.onNodeWithText("Pick Date").performClick()
        composeRule.onNodeWithText("Done").performClick()

        // Remove the date row and finish
        composeRule.onNodeWithText("Delete").performClick()
        // Dates -> Preferences
        composeRule.onNodeWithText("Next").performClick()
        // Preferences -> Review
        composeRule.onNodeWithText("Next").performClick()
        composeRule.onNodeWithText("Save").performClick()
        composeRule.onNodeWithText("Typed Date User was added").assertIsDisplayed()
    }
}