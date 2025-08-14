// File: app/src/main/java/com/com.threekidsinatrenchcoat.giftideaminder/ui/screens/AddEditRecipientScreen.kt
package com.threekidsinatrenchcoat.giftideaminder.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
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
import com.threekidsinatrenchcoat.giftideaminder.viewmodel.AddEditRecipientViewModel
import com.threekidsinatrenchcoat.giftideaminder.viewmodel.RecipientEvent
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.TextButton
import androidx.compose.material3.AlertDialog
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding


@Composable
fun AddEditRecipientScreen(
    viewModel: AddEditRecipientViewModel = hiltViewModel(),
    onNavigateBack: (String?) -> Unit,
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

    // Handle events like person saved
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is RecipientEvent.PersonSaved -> {
                    val message = if (event.isEdit) {
                        "${event.personName} was updated"
                    } else {
                        "${event.personName} was added"
                    }
                    // Navigate back immediately with the success message
                    onNavigateBack(message)
                }
            }
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
                title = { Text(if (uiState.isEditing) "Edit Recipient" else "Add Recipient") },
                navigationIcon = {
                    IconButton(onClick = { onNavigateBack(null) }) {
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

        var showGiftInspirationsDialog by remember { mutableStateOf(false) }
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
                        contentDescription = "Recipient photo",
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
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                OutlinedButton(onClick = { /* could navigate to a dedicated notes screen if desired */ }) { Text("Notes") }
                OutlinedButton(onClick = { showGiftInspirationsDialog = true }) { Text("Gift Inspirations") }
            }

            // Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    onClick = { onNavigateBack(null) },
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Text("Cancel")
                }
                Button(onClick = {
                    viewModel.onSave()
                    // Navigation will happen after the snackbar is shown
                }) {
                    Text("Save")
                }
            }
        }

        // Gift Inspirations Dialog
        GiftInspirationsDialog(
            visible = showGiftInspirationsDialog,
            current = uiState.preferences,
            onAdd = { viewModel.addPreference(it) },
            onRemove = { viewModel.removePreference(it) },
            onDismiss = { showGiftInspirationsDialog = false }
        )

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
private fun GiftInspirationsDialog(
    visible: Boolean,
    current: List<String>,
    onAdd: (String) -> Unit,
    onRemove: (String) -> Unit,
    onDismiss: () -> Unit
) {
    if (!visible) return
    var input by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Gift Inspirations") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = input, onValueChange = { input = it }, label = { Text("Add item") })
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = {
                        onAdd(input)
                        input = ""
                    }, enabled = input.isNotBlank()) { Text("Add") }
                }
                current.forEach { item ->
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
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