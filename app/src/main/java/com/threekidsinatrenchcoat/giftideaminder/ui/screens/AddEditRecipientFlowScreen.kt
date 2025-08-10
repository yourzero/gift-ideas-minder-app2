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
                    Text("Important dates", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    // Prompted dates (e.g., Birthday, Anniversary)
                    state.datePrompts.forEach { label ->
                        DatePickerRow(
                            label = label,
                            date = state.pickedDates[label],
                            onPicked = { picked -> viewModel.onDatePicked(label, picked) },
                            onRemove = { viewModel.onRemoveDate(label) }
                        )
                    }

                    // Custom dates list (non-prompt labels)
                    val nonPromptDates = state.pickedDates.filterKeys { it !in state.datePrompts }
                    if (nonPromptDates.isNotEmpty()) {
                        Spacer(Modifier.height(12.dp))
                        Text("Other dates", style = MaterialTheme.typography.titleSmall)
                        Spacer(Modifier.height(4.dp))
                        nonPromptDates.forEach { (label, date) ->
                            DatePickerRow(
                                label = label,
                                date = date,
                                onPicked = { picked -> viewModel.onDatePicked(label, picked) },
                                onRemove = { viewModel.onRemoveDate(label) }
                            )
                        }
                    }

                    // Add custom labeled date
                    Spacer(Modifier.height(12.dp))
                    var customLabel by remember { mutableStateOf("") }
                    var openAddPicker by remember { mutableStateOf(false) }
                    OutlinedTextField(
                        value = customLabel,
                        onValueChange = { customLabel = it },
                        label = { Text("Custom date label") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(onClick = { openAddPicker = true }, enabled = customLabel.isNotBlank()) {
                            Text("Pick date & add")
                        }
                    }

                    if (openAddPicker) {
                        val dpState = androidx.compose.material3.rememberDatePickerState(
                            initialSelectedDateMillis = Instant.now().toEpochMilli()
                        )
                        DatePickerDialog(
                            onDismissRequest = { openAddPicker = false },
                            confirmButton = {
                                TextButton(onClick = {
                                    dpState.selectedDateMillis?.let { millis ->
                                        val date = Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate()
                                        val trimmed = customLabel.trim()
                                        if (trimmed.isNotEmpty()) {
                                            viewModel.onDatePicked(trimmed, date)
                                            customLabel = ""
                                        }
                                    }
                                    openAddPicker = false
                                }) { Text("Done") }
                            },
                            dismissButton = { TextButton(onClick = { openAddPicker = false }) { Text("Cancel") } }
                        ) {
                            DatePicker(state = dpState)
                        }
                    }
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
                        val picked = Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate()
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
