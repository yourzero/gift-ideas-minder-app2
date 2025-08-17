// app/src/main/java/com/threekidsinatrenchcoat/giftideaminder/ui/screens/AddGiftFlowScreen.kt
package com.threekidsinatrenchcoat.giftideaminder.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.threekidsinatrenchcoat.giftideaminder.data.model.ImportantDate
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
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
    // Important dates for the selected person
    personImportantDates: List<ImportantDate>,
    // VM callbacks
    onSelectPersonClick: () -> Unit,
    onDateSelected: (Long?) -> Unit,
    onIdeaChange: (String) -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit,
    onSave: () -> Unit,
    onResetForCreate: () -> Unit,
    onAddCustomDate: (String, LocalDate) -> Unit,
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
            progress = { ((stepIndex + 1).coerceAtMost(3)) / 3f },
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
                personImportantDates = personImportantDates,
                onPickClick = { showDatePicker = true },
                onDateSelected = onDateSelected,
                onAddCustomDate = onAddCustomDate
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
    personImportantDates: List<ImportantDate>,
    onPickClick: () -> Unit,
    onDateSelected: (Long?) -> Unit,
    onAddCustomDate: (String, LocalDate) -> Unit
) {
    var showAddDateDialog by remember { mutableStateOf(false) }
    var customDateLabel by remember { mutableStateOf("") }
    var selectedLocalDate by remember { mutableStateOf(LocalDate.now()) }
    var saveToProfile by remember { mutableStateOf(true) }
    
    // Check if current dateMillis matches any saved date for visual indication
    val selectedImportantDate = dateMillis?.let { millis ->
        val selectedDate = Date(millis).toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
        personImportantDates.find { it.date == selectedDate }
    }
    
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
        Text("Pick Date", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        
        if (personImportantDates.isNotEmpty()) {
            Text(
                "Choose from saved dates:",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
            Spacer(Modifier.height(8.dp))
            
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.heightIn(max = 200.dp)
            ) {
                items(personImportantDates) { importantDate ->
                    val isSelected = selectedImportantDate?.id == importantDate.id
                    Card(
                        onClick = {
                            val millis = importantDate.date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                            onDateSelected(millis)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                MaterialTheme.colorScheme.surface
                            }
                        ),
                        border = if (isSelected) {
                            CardDefaults.outlinedCardBorder().copy(
                                width = 2.dp,
                                brush = SolidColor(MaterialTheme.colorScheme.primary)
                            )
                        } else {
                            CardDefaults.outlinedCardBorder()
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = importantDate.label,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (isSelected) {
                                        MaterialTheme.colorScheme.onPrimaryContainer
                                    } else {
                                        MaterialTheme.colorScheme.primary
                                    }
                                )
                                Text(
                                    text = importantDate.date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isSelected) {
                                        MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                    } else {
                                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    }
                                )
                            }
                            if (isSelected) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
            
            Spacer(Modifier.height(12.dp))
            HorizontalDivider()
            Spacer(Modifier.height(12.dp))
        }
        
        // Single "Add Date" button
        OutlinedButton(
            onClick = { showAddDateDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Add Date")
        }
        
        Spacer(Modifier.height(8.dp))
        
        // Show selected date status
        val selectedLabel = if (selectedImportantDate != null) {
            "${selectedImportantDate.label} - ${selectedImportantDate.date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))}"
        } else if (dateMillis != null) {
            val date = Date(dateMillis)
            "Custom date - ${date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                .format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))}"
        } else {
            "No date selected"
        }
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (dateMillis != null) {
                    MaterialTheme.colorScheme.tertiaryContainer
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                }
            )
        ) {
            Text(
                text = "Selected: $selectedLabel",
                style = MaterialTheme.typography.bodyMedium,
                color = if (dateMillis != null) {
                    MaterialTheme.colorScheme.onTertiaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                modifier = Modifier.padding(12.dp)
            )
        }
    }
    
    if (showAddDateDialog) {
        AddDateDialog(
            customDateLabel = customDateLabel,
            selectedDate = selectedLocalDate,
            saveToProfile = saveToProfile,
            onLabelChange = { customDateLabel = it },
            onDateChange = { selectedLocalDate = it },
            onSaveToProfileChange = { saveToProfile = it },
            onConfirm = {
                if (customDateLabel.isNotBlank()) {
                    if (saveToProfile) {
                        onAddCustomDate(customDateLabel, selectedLocalDate)
                    }
                    val millis = selectedLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                    onDateSelected(millis)
                    showAddDateDialog = false
                    customDateLabel = ""
                    selectedLocalDate = LocalDate.now()
                    saveToProfile = true
                }
            },
            onDismiss = { 
                showAddDateDialog = false 
                customDateLabel = ""
                selectedLocalDate = LocalDate.now()
                saveToProfile = true
            }
        )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddDateDialog(
    customDateLabel: String,
    selectedDate: LocalDate,
    saveToProfile: Boolean,
    onLabelChange: (String) -> Unit,
    onDateChange: (LocalDate) -> Unit,
    onSaveToProfileChange: (Boolean) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    )
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Date") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = customDateLabel,
                    onValueChange = onLabelChange,
                    label = { Text("Occasion/Label") },
                    placeholder = { Text("e.g., Anniversary, Birthday") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedCard(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Date",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = selectedDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Pick date",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = saveToProfile,
                        onCheckedChange = onSaveToProfileChange
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Save to person's profile",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = customDateLabel.isNotBlank()
            ) {
                Text("Add & Use")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
    
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val newDate = Date(millis).toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                            onDateChange(newDate)
                        }
                        showDatePicker = false
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
}
