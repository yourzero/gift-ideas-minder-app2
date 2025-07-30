// File: app/src/main/java/com/giftideaminder/ui/screens/AddEditGifteeScreen.kt
package com.giftideaminder.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.giftideaminder.viewmodel.AddEditGifteeViewModel
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.TextButton
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditGifteeScreen(
    viewModel: AddEditGifteeViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Contact picker launcher
    val contactPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickContact()
    ) { uri: Uri? ->
        uri?.let { viewModel.loadContact(context, it) }
    }

    // Date picker state
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = uiState.eventDate)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (uiState.isEditing) "Edit Giftee" else "Add Giftee") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
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
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Photo
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                uiState.photoUri?.let { uri ->
                    AsyncImage(
                        model = uri,
                        contentDescription = "Giftee photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } ?: Icon(
                    Icons.Filled.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                    modifier = Modifier.size(48.dp)
                )
            }

            // Import from Contacts
            Button(
                onClick = {
                    if (uiState.name.isNotBlank()) viewModel.findContactByName(context, uiState.name)
                    else contactPicker.launch(null)
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Text("Import from Contacts")
            }

            // Name Field
            OutlinedTextField(
                value = uiState.name,
                onValueChange = viewModel::onNameChange,
                label = { Text("Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Birthday / Anniversary
            val sdf = remember { SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()) }
            OutlinedTextField(
                value = uiState.eventDate?.let { sdf.format(Date(it)) } ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Birthday / Anniversary") },
                trailingIcon = {
                    IconButton(onClick = { viewModel.onShowDatePicker(true) }) {
                        Icon(Icons.Filled.CalendarToday, contentDescription = "Pick date")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            // Relationship Picker
            RelationshipPicker(
                label = "Relationship",
                selected = uiState.relationships,
                options = viewModel.relationshipOptions,
                expanded = uiState.isRelationshipDropdownOpen,
                onExpand = { viewModel.onShowRelationshipDropdown(true) },
                onDismiss = { viewModel.onShowRelationshipDropdown(false) },
                onSelectionChanged = viewModel::onRelationshipsChange
            )

            // Notes
            OutlinedTextField(
                value = uiState.notes,
                onValueChange = viewModel::onNotesChange,
                label = { Text("Notes") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 5
            )

            // Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    onClick = onNavigateBack,
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Text("Cancel")
                }
                Button(onClick = {
                    viewModel.onSave()
                    onNavigateBack()
                }) {
                    Text("Save")
                }
            }
        }

        // Date Picker Dialog
        if (uiState.isDatePickerOpen) {
            DatePickerDialog(
                onDismissRequest = { viewModel.onShowDatePicker(false) },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.onEventDateChange(datePickerState.selectedDateMillis ?: 0L)
                        viewModel.onShowDatePicker(false)
                    }) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.onShowDatePicker(false) }) {
                        Text("Cancel")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
    }
}

@Composable
fun RelationshipPicker(
    label: String,
    selected: List<String>,
    options: List<String>,
    expanded: Boolean,
    onExpand: () -> Unit,
    onDismiss: () -> Unit,
    onSelectionChanged: (List<String>) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onExpand)
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            selected.forEach { rel ->
                AssistChip(
                    onClick = { onSelectionChanged(selected - rel) },
                    label = { Text(rel) }
                )
            }
            Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = onDismiss) {
            options.forEach { rel ->
                val checked = rel in selected
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = checked, onCheckedChange = null)
                            Spacer(Modifier.width(8.dp))
                            Text(rel)
                        }
                    },
                    onClick = {
                        val newList = if (checked) selected - rel else selected + rel
                        onSelectionChanged(newList)
                    }
                )
            }
        }
    }
}
