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
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.giftideaminder.data.model.Gift
import com.giftideaminder.data.model.Person
import com.giftideaminder.viewmodel.GiftViewModel
import kotlinx.coroutines.flow.collectLatest
import java.util.Calendar

@Composable
fun AddEditGiftScreen(
    viewModel: GiftViewModel,
    navController: NavController,
    giftId: Int?
) {
    val context = LocalContext.current

    // State variables with remember for performance
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var eventDate by remember { mutableLongStateOf(0L) }
    var selectedPersonId by remember { mutableStateOf<Int?>(null) }

    val persons by viewModel.allPersons.collectAsState(initial = emptyList())

    var expanded by remember { mutableStateOf(false) }
    var showAddPersonDialog by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    // Load existing gift if editing
    if (giftId != null) {
        LaunchedEffect(giftId) {
            viewModel.getGiftById(giftId).collectLatest { gift ->
                if (gift != null) {
                    title = gift.title
                    description = gift.description
                    url = gift.url
                    price = gift.price.toString()
                    eventDate = gift.eventDate
                    selectedPersonId = gift.personId
                }
            }
        }
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        // Title field with validation
        TextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            isError = title.isBlank()
        )
        TextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") }
        )
        TextField(
            value = url,
            onValueChange = { url = it },
            label = { Text("URL") }
        )
        TextField(
            value = price,
            onValueChange = { price = it },
            label = { Text("Price") }
        )
        Button(onClick = { showDatePicker = true }) {
            Text("Pick Event Date")
        }
        Text("Selected Date: ${if (eventDate > 0) java.text.SimpleDateFormat("dd/MM/yyyy").format(java.util.Date(eventDate)) else "Not set"}")

        Text("Assigned Person")
        Box {
            Button(onClick = { expanded = true }) {
                Text(persons.find { it.id == selectedPersonId }?.name ?: "Select Person")
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
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

        // Save button with validation
        Button(onClick = {
            if (title.isNotBlank()) {
                val newPrice = price.toDoubleOrNull() ?: 0.0
                val newGift = Gift(
                    id = giftId ?: 0,
                    title = title,
                    description = description,
                    url = url,
                    price = newPrice,
                    eventDate = if (eventDate > 0) eventDate else 0L,
                    personId = selectedPersonId
                )
                if (giftId != null) {
                    viewModel.updateGift(newGift)
                } else {
                    viewModel.insertGift(newGift)
                }
                navController.popBackStack()
            }
        }) {
            Text(if (giftId != null) "Update Gift" else "Add Gift")
        }
    }

    if (showAddPersonDialog) {
        var personName by remember { mutableStateOf("") }
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
                        viewModel.insertPerson(Person(name = personName))
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