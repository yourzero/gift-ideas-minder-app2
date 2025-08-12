package com.threekidsinatrenchcoat.giftideaminder.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.threekidsinatrenchcoat.giftideaminder.viewmodel.PersonFlowViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.Alignment
import androidx.navigation.NavController
import java.time.format.DateTimeFormatter
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete

@Composable
fun AddEditRecipientFlowScreen(
    onNavigateBack: (String?) -> Unit,
    navController: NavController,
    personId: Int? = null,
    viewModel: PersonFlowViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (state.isEditing) "Edit Recipient" else "Add Recipient") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )

        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            when (state.step) {
                PersonFlowViewModel.Step.Relationship -> {
                    Text("Select relationship", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    RelationshipChips(
                        options = state.availableRelationships,
                        selected = state.selectedRelationship,
                        onSelected = { viewModel.onRelationshipSelected(it) }
                    )
                }

                PersonFlowViewModel.Step.Details -> {
                    OutlinedTextField(
                        value = state.name,
                        onValueChange = viewModel::onNameChange,
                        label = { Text("Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                PersonFlowViewModel.Step.Dates -> {
                    Text("Important Dates", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))

                    // Compute visible rows: prompts + additional + any existing picked labels (edit mode), minus removed
                    val visibleLabels = remember(
                        state.datePrompts,
                        state.additionalDateLabels,
                        state.pickedDates,
                        state.removedDateLabels
                    ) {
                        (state.datePrompts + state.additionalDateLabels + state.pickedDates.keys)
                            .distinct()
                            .filter { it !in state.removedDateLabels }
                    }

                    visibleLabels.forEach { label ->
                        TypedDateRow(
                            label = label,
                            date = state.pickedDates[label],
                            onLabelChange = { newLabel ->
                                viewModel.onChangeDateLabel(label, newLabel)
                            },
                            onPicked = { picked ->
                                viewModel.onDatePicked(label, picked)
                            },
                            onRemove = {
                                viewModel.onRemoveDateItem(label)
                            }
                        )
                    }

                    Spacer(Modifier.height(12.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                        OutlinedButton(onClick = { viewModel.onAddDateItem("Custom") }) { Text("Add Date") }
                    }
                }

                PersonFlowViewModel.Step.Preferences -> {
                    Text("Gift Inspirations", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    GiftInspirationsInline(
                        current = state.preferences,
                        onAdd = { viewModel.onAddPreference(it) },
                        onRemove = { viewModel.onRemovePreference(it) }
                    )
                }

                PersonFlowViewModel.Step.Review -> {
                    Text("Review", style = MaterialTheme.typography.titleMedium)
                    Text("Relationship: ${state.selectedRelationship ?: "None"}")
                    Text("Name: ${state.name}")
                    val formatter = remember { DateTimeFormatter.ofPattern("MMM d, yyyy") }
                    if (state.pickedDates.isNotEmpty()) {
                        Spacer(Modifier.height(8.dp))
                        Text("Dates:")
                        state.pickedDates.entries.sortedBy { it.key }.forEach { (label, date) ->
                            Text("- $label: ${date.format(formatter)}")
                        }
                    }
                }

//                else -> {
//                    Text("Unknown step: ${state.step}") // TODO - remove this debugging code
//                }
            }

            Spacer(Modifier.height(16.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                OutlinedButton(onClick = {
                    val result = viewModel.onBack()
                    if (result.navigateBack) onNavigateBack(null)
                }) { Text("Back") }
                Button(onClick = {
                    val result = viewModel.onNextOrSave()
                    if (result.saved) onNavigateBack(result.successMessage)
                }) { Text(if (state.step == PersonFlowViewModel.Step.Review) "Save" else "Next") }
            }
        }
    }
}

@Composable
private fun GiftInspirationsEditorDialog(
    current: List<String>,
    onAdd: (String) -> Unit,
    onRemove: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var input by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Gift Inspirations") },
        text = {
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    label = { Text("Add item") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = {
                        onAdd(input)
                        input = ""
                    }, enabled = input.isNotBlank()) { Text("Add") }
                }
                Spacer(Modifier.height(8.dp))
                current.forEach { item ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(item)
                        TextButton(onClick = { onRemove(item) }) { Text("Remove") }
                    }
                }
            }
        },
        confirmButton = { Button(onClick = onDismiss) { Text("Done") } }
    )
}

@Composable
fun RelationshipChips(
    options: List<String>,
    selected: String?,
    onSelected: (String) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        options.forEach { label ->
            val isSelected = label == selected
            OutlinedButton(onClick = { onSelected(label) }) {
                Text(if (isSelected) "[$label]" else label)
            }
        }
    }
}


@Composable
private fun DatePickerRow(
    label: String,
    date: LocalDate?,
    onPicked: (LocalDate) -> Unit,
    onRemove: (() -> Unit)? = null
) {
    var open by remember { mutableStateOf(false) }
    val formatter = remember { DateTimeFormatter.ofPattern("MMM d, yyyy") }
    Card(
        colors = CardDefaults.cardColors(),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(if (date != null) "$label: ${date.format(formatter)}" else label)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (date != null && onRemove != null) {
                    OutlinedButton(onClick = { onRemove() }) { Text("Clear") }
                }
                OutlinedButton(onClick = { open = true }) { Text("Pick") }
            }
        }
    }
    if (open) {
        val initialMillis = date?.atStartOfDay(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()
        val state = if (initialMillis != null) {
            androidx.compose.material3.rememberDatePickerState(initialSelectedDateMillis = initialMillis)
        } else {
            androidx.compose.material3.rememberDatePickerState()
        }
        DatePickerDialog(
            onDismissRequest = { open = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let { millis ->
                        val picked =
                            Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate()
                        onPicked(picked)
                    }
                    open = false
                }) { Text("Done") }
            },
            dismissButton = { TextButton(onClick = { open = false }) { Text("Cancel") } }
        ) {
            DatePicker(state = state)
        }
    }
}

@Composable
private fun TypedDateRow(
    label: String,
    date: LocalDate?,
    onLabelChange: (String) -> Unit,
    onPicked: (LocalDate) -> Unit,
    onRemove: () -> Unit
) {
    var openPicker by remember { mutableStateOf(false) }
    val formatter = remember { DateTimeFormatter.ofPattern("MMM d, yyyy") }
    Card(
        colors = CardDefaults.cardColors(),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                DateTypeSelector(label = label, onLabelChange = onLabelChange)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(onClick = { onRemove() }) { Text("Delete") }
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(if (date != null) "${date.format(formatter)}" else "No date selected")
                OutlinedButton(onClick = { openPicker = true }) { Text("Pick Date") }
            }
        }
    }
    if (openPicker) {
        val initialMillis = date?.atStartOfDay(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()
        val state = if (initialMillis != null) {
            androidx.compose.material3.rememberDatePickerState(initialSelectedDateMillis = initialMillis)
        } else {
            androidx.compose.material3.rememberDatePickerState()
        }
        DatePickerDialog(
            onDismissRequest = { openPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let { millis ->
                        val picked =
                            Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate()
                        onPicked(picked)
                    }
                    openPicker = false
                }) { Text("Done") }
            },
            dismissButton = { TextButton(onClick = { openPicker = false }) { Text("Cancel") } }
        ) {
            DatePicker(state = state)
        }
    }
}

@Composable
private fun DateTypeSelector(
    label: String,
    onLabelChange: (String) -> Unit
) {
    val knownTypes = listOf(
        "Birthday",
        "Anniversary",
        "Graduation",
        "First Met",
        "Valentine's Day",
        "Mother's Day",
        "Father's Day",
        "Custom"
    )
    val isKnownNonCustom = label in knownTypes && label != "Custom"
    var expanded by remember { mutableStateOf(false) }
    var customText by remember(label) { mutableStateOf(if (isKnownNonCustom) "" else label) }

    Column(Modifier.fillMaxWidth(0.7f)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(onClick = { expanded = true }) {
                Text(if (isKnownNonCustom) label else "Custom")
            }
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            knownTypes.forEach { type ->
                DropdownMenuItem(
                    text = { Text(type) },
                    onClick = {
                        expanded = false
                        if (type == "Custom") {
                            // Switch to custom, keep current customText
                            onLabelChange(if (customText.isBlank()) "Custom" else customText)
                        } else {
                            onLabelChange(type)
                        }
                    }
                )
            }
        }
        if (!isKnownNonCustom) {
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = customText,
                onValueChange = {
                    customText = it
                    onLabelChange(it)
                },
                label = { Text("Custom label") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun GiftInspirationsInline(
    current: List<String>,
    onAdd: (String) -> Unit,
    onRemove: (String) -> Unit
) {
    var input by remember { mutableStateOf("") }
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
            value = input,
            onValueChange = { input = it },
            label = { Text("Add item") },
            modifier = Modifier.fillMaxWidth()
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = {
                onAdd(input)
                input = ""
            }, enabled = input.isNotBlank()) { Text("Add") }
        }
        Spacer(Modifier.height(8.dp))
        current.forEach { item ->
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(item)
                TextButton(onClick = { onRemove(item) }) { Text("Remove") }
            }
        }
    }
}
