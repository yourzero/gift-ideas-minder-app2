package com.threekidsinatrenchcoat.giftideaminder.ui.screens

import android.app.DatePickerDialog
import android.net.Uri
import android.provider.ContactsContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.threekidsinatrenchcoat.giftideaminder.data.model.Person
import com.threekidsinatrenchcoat.giftideaminder.viewmodel.GiftViewModel
import com.threekidsinatrenchcoat.giftideaminder.viewmodel.PersonViewModel
import kotlinx.coroutines.flow.collectLatest
import java.util.Calendar
import java.util.Locale
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Arrangement


@Composable
fun AddEditGiftScreen(
    viewModel: GiftViewModel = hiltViewModel(),
    onNavigateBack: (String?) -> Unit,
    navController: NavController,
    giftId: Int? = null,
    sharedText: String? = null
) {
val context = LocalContext.current
    val personViewModel: PersonViewModel = hiltViewModel()

    // VM state (Single Source of Truth)
    val ui by viewModel.uiState.collectAsState()

    // Persons list
    val persons by personViewModel.allPersons.collectAsState(initial = emptyList())

    // UI-only controls
    var expanded by remember { mutableStateOf(false) }
    var showAddPersonDialog by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var personName by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }

    // Contact picker launcher
    val contactPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickContact()
    ) { uri: Uri? ->
        uri?.let {
            context.contentResolver.query(it, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val name =
                        cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME))
                    // If person exists, select; else prompt to add with prefill
                    val existing = persons.find { p -> p.name == name }
                    if (existing != null) {
                        viewModel.onPersonSelected(existing.id)
                    } else {
                        showAddPersonDialog = true
                        personName = name
                    }
                }
            }
        }
    }

    // Seed from share
    if (sharedText != null && giftId == null) {
        LaunchedEffect(sharedText) {
            if (sharedText.startsWith("http")) {
                viewModel.onUrlChanged(sharedText)
            } else {
                viewModel.onDescriptionChanged(sharedText)
            }
        }
    }

    // Reset state when adding new gift or editing a different gift
    LaunchedEffect(giftId) {
        if (giftId == null) {
            // Adding new gift - clear all previous state
            viewModel.resetState()
        } else {
            // Edit mode: load gift once and push into VM state
            viewModel.getGiftById(giftId).collectLatest { gift ->
                // Only load if state is still blank or matching id
                val uiState = viewModel.uiState.value
                if (uiState.id == null || uiState.id == giftId) {
                    viewModel.loadForEdit(gift)
                }
            }
        }
    }

    // Listen for one-off events (Saved => snackbar + back)
    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { ev ->
            when (ev) {
                is GiftViewModel.GiftEvent.Saved -> {
                    //snackbarHostState.showSnackbar("Saved")
                    // Navigate back after snackbar
                    //navController.popBackStack()
                    onNavigateBack("Saved")
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (ui.id != null) "Edit Gift" else "Add Gift") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Title
            OutlinedTextField(
                value = ui.title,
                onValueChange = viewModel::onTitleChanged,
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            // Gift Idea / Description
            OutlinedTextField(
                value = ui.description,
                onValueChange = viewModel::onDescriptionChanged,
                label = { Text("Gift Idea") },
                placeholder = { Text("Describe the gift idea...") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 4
            )
            Spacer(Modifier.height(8.dp))

            // URL
            OutlinedTextField(
                value = ui.url,
                onValueChange = viewModel::onUrlChanged,
                label = { Text("URL") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            // Price
            OutlinedTextField(
                value = ui.priceText,
                onValueChange = viewModel::onPriceChanged,
                label = { Text("Price") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            // Date picker
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Event Date", style = MaterialTheme.typography.labelMedium)
                    val dateText = if (ui.eventDateMillis > 0)
                        java.text.SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                            .format(java.util.Date(ui.eventDateMillis))
                    else "Not set"
                    Text(dateText, style = MaterialTheme.typography.bodyMedium)
                }
                Button(onClick = { showDatePicker = true }) {
                    Text("Pick Date")
                }
            }
            Spacer(Modifier.height(16.dp))

            // Person select
            Text("Assigned Person")
            Box {
                Button(onClick = { expanded = true }) {
                    Text(persons.find { it.id == ui.personId }?.name ?: "Select Person")
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Select from Contacts") },
                        onClick = {
                            contactPicker.launch(null)
                            expanded = false
                        }
                    )
                    HorizontalDivider()
                    persons.forEach { person ->
                        DropdownMenuItem(
                            text = { Text(person.name) },
                            onClick = {
                                viewModel.onPersonSelected(person.id)
                                expanded = false
                            }
                        )
                    }

                }
            }
            Spacer(Modifier.height(16.dp))

            // Bottom button row matching add/edit recipient layout
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.weight(1f)
                ) { 
                    Text("Back") 
                }

                Button(
                    onClick = { viewModel.onSave() },
                    enabled = !ui.isSaving && ui.personId != null,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Save")
                }
            }

            // Show inline error if any
            ui.error?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }
        }
    }

    // Add person dialog
    if (showAddPersonDialog) {
        AlertDialog(
            onDismissRequest = { showAddPersonDialog = false },
            title = { Text("Add New Person") },
            text = {
                OutlinedTextField(
                    value = personName,
                    onValueChange = { personName = it },
                    label = { Text("Name") }
                )
            },
            confirmButton = {
                Button(onClick = {
                    if (personName.isNotEmpty()) {
                        personViewModel.insertPerson(Person(name = personName))
                        // The list updates via Flow; user can pick it right away or we could auto-select once present
                        showAddPersonDialog = false
                    }
                }) {
                    Text("Add")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showAddPersonDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Material DatePickerDialog
    if (showDatePicker) {
        LaunchedEffect(showDatePicker) {
            val calendar = Calendar.getInstance().apply {
                if (ui.eventDateMillis > 0) timeInMillis = ui.eventDateMillis
            }
            DatePickerDialog(
                context,
                { _, year, month, dayOfMonth ->
                    calendar.set(year, month, dayOfMonth)
                    viewModel.onEventDateChanged(calendar.timeInMillis)
                    showDatePicker = false
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }
}