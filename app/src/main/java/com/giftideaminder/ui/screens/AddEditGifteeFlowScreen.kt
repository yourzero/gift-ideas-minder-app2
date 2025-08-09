package com.giftideaminder.ui.screens

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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.giftideaminder.viewmodel.PersonFlowViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

@Composable
fun AddEditGifteeFlowScreen(
    onNavigateBack: (String?) -> Unit,
    personId: Int? = null,
    viewModel: PersonFlowViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    @OptIn(ExperimentalMaterial3Api::class)
    Scaffold(
        topBar = { TopAppBar(title = { Text(if (state.isEditing) "Edit Giftee" else "Add Giftee") }) }
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
                    state.datePrompts.forEach { label ->
                        DatePickerRow(label = label) { picked ->
                            viewModel.onDatePicked(label, picked)
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    // Simple ad-hoc date add placeholder could be added later
                }
                PersonFlowViewModel.Step.Review -> {
                    Text("Review", style = MaterialTheme.typography.titleMedium)
                    Text("Relationship: ${state.selectedRelationship ?: "None"}")
                    Text("Name: ${state.name}")
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerRow(label: String, onPicked: (LocalDate) -> Unit) {
    var open by remember { mutableStateOf(false) }
    var display by remember { mutableStateOf<String?>(null) }
    Card(
        colors = CardDefaults.cardColors(),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(if (display != null) "$label: $display" else label)
            OutlinedButton(onClick = { open = true }) { Text("Pick") }
        }
    }
    if (open) {
        val state = androidx.compose.material3.rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { open = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let { millis ->
                        val date = Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate()
                        display = date.toString()
                        onPicked(date)
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

