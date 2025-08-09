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
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditGifteeScreen(
    viewModel: AddEditGifteeViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    personId: Int? = null
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // 🔗 Load existing person when editing
    LaunchedEffect(personId) {
        if (personId != null) {
            viewModel.loadPerson(personId)   // ← update to your actual VM method name
        } else {
            // Optional: ensure a clean slate when adding
            viewModel.startNew()             // ← if you have a reset method
        }
    }

    // Contact picker launcher
    val contactPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickContact()
    ) { uri: Uri? ->
        uri?.let { viewModel.loadContact(context, it) }
    }

    // Date picker state (keep it synced with uiState after load)
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = uiState.eventDate?.let { date ->
            date.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
        }
    )
    LaunchedEffect(uiState.eventDate) {
        datePickerState.selectedDateMillis = uiState.eventDate?.let { date ->
            date.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
        }
    }

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
        // SMS Scanning Dialog
        if (uiState.showSmsPrompt && uiState.phoneNumber != null) {
            AlertDialog(
                onDismissRequest = { viewModel.onDismissSmsPrompt() },
                title = { Text("Scan SMS Messages") },
                text = {
                    Text("Would you like to scan SMS messages with ${uiState.name} for gift ideas?")
                },
                confirmButton = {
                    Button(onClick = {
                        uiState.phoneNumber?.let { phone ->
                            viewModel.scanSmsForIdeas(context, phone)
                        }
                        viewModel.onDismissSmsPrompt()
                    }) {
                        Text("Yes, Scan SMS")
                    }
                },
                dismissButton = {
                    Button(onClick = { viewModel.onDismissSmsPrompt() }) {
                        Text("No, Skip")
                    }
                }
            )
        }

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
            val dateFormatter = remember { DateTimeFormatter.ofPattern("MM/dd/yyyy") }
            OutlinedTextField(
                value = uiState.eventDate?.format(dateFormatter) ?: "",
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
                        datePickerState.selectedDateMillis?.let { millis ->
                            val localDate = Instant.ofEpochMilli(millis)
                                .atZone(ZoneOffset.UTC)
                                .toLocalDate()
                            viewModel.onEventDateChange(localDate)
                        }
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
