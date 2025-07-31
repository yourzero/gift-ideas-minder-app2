package com.giftideaminder.ui.screens

import android.app.DatePickerDialog
import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import android.net.Uri
import android.provider.ContactsContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.NavController
import com.giftideaminder.data.model.Gift
import com.giftideaminder.data.model.Person
import com.giftideaminder.viewmodel.GiftViewModel
import com.giftideaminder.viewmodel.PersonViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.flow.collectLatest
import java.util.Calendar
import java.util.Locale

@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditGiftScreen(
    viewModel: GiftViewModel = hiltViewModel(),
    navController: NavController,
    giftId: Int? = null,
    sharedText: String? = null
) {
    val context = LocalContext.current
    val personViewModel: PersonViewModel = hiltViewModel()

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var eventDate by remember { mutableLongStateOf(0L) }
    var selectedPersonId by remember { mutableStateOf<Int?>(null) }
    var showContactPicker by remember { mutableStateOf(false) }

    val persons by personViewModel.allPersons.collectAsState(initial = emptyList())

    var expanded by remember { mutableStateOf(false) }
    var showAddPersonDialog by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    
    var personName by remember { mutableStateOf("") }

    // Contact picker launcher
    val contactPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickContact()
    ) { uri: Uri? ->
        uri?.let { 
            // Get contact name
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val contactId = cursor.getLong(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID))
                    val name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME))
                    
                    // Find or create person with this name
                    val existingPerson = persons.find { it.name == name }
                    if (existingPerson != null) {
                        selectedPersonId = existingPerson.id
                    } else {
                        // Show dialog to add this person
                        showAddPersonDialog = true
                        // Pre-fill the name
                        personName = name
                    }
                }
            }
        }
    }

    if (sharedText != null && giftId == null) {
        LaunchedEffect(sharedText) {
            if (sharedText.startsWith("http")) {
                url = sharedText
            } else {
                description = sharedText
            }
        }
    }

    if (giftId != null) {
        LaunchedEffect(giftId) {
            viewModel.getGiftById(giftId).collectLatest { gift ->
                title = gift.title
                description = gift.description ?: ""
                url = gift.url ?: ""
                price = gift.currentPrice?.toString() ?: ""
                eventDate = gift.eventDate ?: 0L
                selectedPersonId = gift.personId
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (giftId != null) "Edit Gift" else "Add Gift") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(16.dp)) {
            TextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxSize().weight(1f)
            )
            TextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxSize().weight(1f)
            )
            TextField(
                value = url,
                onValueChange = { url = it },
                label = { Text("URL") },
                modifier = Modifier.fillMaxSize().weight(1f)
            )
            TextField(
                value = price,
                onValueChange = { price = it },
                label = { Text("Price") },
                modifier = Modifier.fillMaxSize().weight(1f)
            )
            Button(onClick = { showDatePicker = true }) {
                Text("Pick Event Date")
            }
            Text("Selected Date: ${if (eventDate > 0) java.text.SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(java.util.Date(eventDate)) else "Not set"}")

            Text("Assigned Person")
            Box {
                Button(onClick = { expanded = true }) {
                    Text(persons.find { it.id == selectedPersonId }?.name ?: "Select Person")
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    // Option to pick from contacts
                    DropdownMenuItem(
                        text = { Text("Select from Contacts") },
                        onClick = {
                            contactPicker.launch(null)
                            expanded = false
                        }
                    )
                    
                    // Divider
                    HorizontalDivider()
                    
                    // Existing persons
                    persons.forEach { person ->
                        DropdownMenuItem(
                            text = { Text(person.name) },
                            onClick = {
                                selectedPersonId = person.id
                                expanded = false
                            }
                        )
                    }
                    DropdownMenuItem(
                        text = { Text("Add New Person") },
                        onClick = {
                            showAddPersonDialog = true
                            expanded = false
                        }
                    )
                }
            }

            Button(onClick = {
                val newPrice = price.toDoubleOrNull()
                val newGift = Gift(
                    id = giftId ?: 0,
                    title = title,
                    description = description,
                    url = url,
                    currentPrice = newPrice, // TODO - double check that this is the price we want
                    eventDate = if (eventDate > 0) eventDate else null,
                    personId = selectedPersonId
                )
                if (giftId != null) {
                    viewModel.updateGift(newGift)
                } else {
                    viewModel.insertGift(newGift)
                }
                navController.popBackStack()
            }) {
                Text(if (giftId != null) "Update Gift" else "Add Gift")
            }
        }
    }

    if (showAddPersonDialog) {
        AlertDialog(
            onDismissRequest = { showAddPersonDialog = false },
            title = { Text("Add New Person") },
            text = {
                TextField(
                    value = personName,
                    onValueChange = { personName = it },
                    label = { Text("Name") }
                )
            },
            confirmButton = {
                Button(onClick = {
                    if (personName.isNotEmpty()) {
                        personViewModel.insertPerson(Person(name = personName))
                        showAddPersonDialog = false
                    }
                }) {
                    Text("Add")
                }
            },
            dismissButton = {
                Button(onClick = { showAddPersonDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showDatePicker) {
        val calendar = Calendar.getInstance()
        if (eventDate > 0) calendar.timeInMillis = eventDate
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                eventDate = calendar.timeInMillis
                showDatePicker = false
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
        showDatePicker = false // Prevent multiple dialogs
    }
}