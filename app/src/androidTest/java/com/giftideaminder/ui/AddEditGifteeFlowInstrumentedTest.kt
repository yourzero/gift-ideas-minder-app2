package com.giftideaminder.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.giftideaminder.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AddEditGifteeFlowInstrumentedTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun addPerson_flow_showsSnackbarOnSave() {
        // Open Add Person via FAB menu from home
        composeRule.onNodeWithContentDescription("Add Options").performClick()
        composeRule.onNodeWithText("Add Person").performClick()

        // Step 1: Relationship - pick Friend
        composeRule.onNodeWithText("Friend").performClick()
        composeRule.onNodeWithText("Next").performClick()

        // Step 2: Details - enter name (find the single text field)
        composeRule.onNode(hasSetTextAction()).performTextInput("Test User")
        composeRule.onNodeWithText("Next").performClick()

        // Step 3: Dates - skip optional
        composeRule.onNodeWithText("Next").performClick()

        // Step 4: Review - Save
        composeRule.onNodeWithText("Save").performClick()

        // Snackbar should show "was added" for Test User
        composeRule.onNodeWithText("Test User was added").assertIsDisplayed()
    }
}