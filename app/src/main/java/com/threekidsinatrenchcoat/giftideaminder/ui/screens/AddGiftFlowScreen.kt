// app/src/main/java/com/threekidsinatrenchcoat/giftideaminder/ui/screens/AddGiftFlowScreen.kt
package com.threekidsinatrenchcoat.giftideaminder.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import java.util.*

/**
 * Add Gift Flow
 * Fixes:
 * - Next disabled until a person is selected
 * - "Pick Date" opens a DatePickerDialog and commits selection
 * - Gift idea field is editable
 * - Primary button label is "Save" on final step
 * - Back/Save aligned like Add/Edit Recipient flow (weighted row on final step)
 * - Clears transient state when opened from FAB by calling [onResetForCreate] once at first composition
 *
 * Note: Wire these callbacks to your ViewModel. This composable keeps minimal local state,
 * delegating persistence to the VM via parameters/callbacks.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGiftFlowScreen(
    // State from your VM
    selectedPersonName: String?,
    selectedPersonId: Int?,
    eventDateMillis: Long?,
    ideaText: String,
    stepIndex: Int,
    // VM callbacks
    onSelectPersonClick: () -> Unit,
    onDateSelected: (Long?) -> Unit,
    onIdeaChange: (String) -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit,
    onSave: () -> Unit,
    onResetForCreate: () -> Unit,
    // Whether this screen was opened fresh from the "+" FAB
    openedFromFab: Boolean,
    modifier: Modifier = Modifier
) {
    // Reset once if opened from FAB
    LaunchedEffect(openedFromFab) {
        if (openedFromFab) onResetForCreate()
    }

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = eventDateMillis ?: Date().time
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDateSelected(datePickerState.selectedDateMillis)
                        showDatePicker = false
                        // If you want to auto-advance after picking:
                        onNext()
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // Simple header / step indicator (replace with your own)
    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Add Gift", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = ((stepIndex + 1).coerceAtMost(3)) / 3f,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))

        when (stepIndex) {
            0 -> StepSelectPerson(
                selectedPersonName = selectedPersonName,
                onSelectPersonClick = onSelectPersonClick
            )
            1 -> StepPickDate(
                dateMillis = eventDateMillis,
                onPickClick = { showDatePicker = true }
            )
            else -> StepDetails(
                ideaText = ideaText,
                onIdeaChange = onIdeaChange
            )
        }

        Spacer(Modifier.height(24.dp))

        // Bottom buttons
        if (stepIndex < 2) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(onClick = onBack) { Text("Back") }
                val nextEnabled = if (stepIndex == 0) selectedPersonId != null else true
                Button(onClick = onNext, enabled = nextEnabled) { Text("Next") }
            }
        } else {
            // Final step: align like Add/Edit Recipient flow (Back + Save in one row)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier.weight(1f)
                ) { Text("Back") }
                Button(
                    onClick = onSave,
                    modifier = Modifier.weight(1f)
                ) { Text("Save") }
            }
        }
    }
}

@Composable
private fun StepSelectPerson(
    selectedPersonName: String?,
    onSelectPersonClick: () -> Unit
) {
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
        Text("Select Person", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        val label = selectedPersonName ?: "Choose..."
        Button(onClick = onSelectPersonClick) { Text(label) }
        Spacer(Modifier.height(8.dp))
        Text(
            text = "You must choose a person before continuing.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

@Composable
private fun StepPickDate(
    dateMillis: Long?,
    onPickClick: () -> Unit
) {
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
        Text("Pick Date", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        val label = dateMillis?.let { Date(it).toString() } ?: "No date selected"
        OutlinedButton(onClick = onPickClick) { Text("Pick Date") }
        Spacer(Modifier.height(8.dp))
        Text(label, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun StepDetails(
    ideaText: String,
    onIdeaChange: (String) -> Unit
) {
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
        Text("Gift Details", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = ideaText,
            onValueChange = onIdeaChange,
            label = { Text("Gift idea") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
