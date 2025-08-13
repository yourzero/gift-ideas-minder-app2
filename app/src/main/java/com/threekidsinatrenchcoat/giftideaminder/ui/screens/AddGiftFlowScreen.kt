package com.threekidsinatrenchcoat.giftideaminder.ui.screens

import android.content.Context
import android.net.Uri
import android.provider.ContactsContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.threekidsinatrenchcoat.giftideaminder.data.model.Person
import com.threekidsinatrenchcoat.giftideaminder.viewmodel.GiftViewModel
import com.threekidsinatrenchcoat.giftideaminder.viewmodel.PersonViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Add Gift Flow: pick person → pick date → add gift details
 * Multi-step flow for creating a new gift idea
 */
@Composable
fun AddGiftFlowScreen(
    viewModel: GiftViewModel = hiltViewModel(),
    onNavigateBack: (String?) -> Unit,
    navController: NavController,
    giftId: Int? = null,
    sharedText: String? = null
) {
    val context = LocalContext.current
    val personViewModel: PersonViewModel = hiltViewModel()
    val scope = rememberCoroutineScope()

    // Persons list
    val persons by personViewModel.allPersons.collectAsState(initial = emptyList())

    // VM state (Single Source of Truth)
    val ui by viewModel.uiState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    var currentStep by remember { mutableIntStateOf(1) }
    val totalSteps = 3

    val stepTitles = listOf(
        "Pick Person",
        "Pick Date",
        "Gift Details"
    )

    // UI-only controls
    var expanded by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    // Hoisted state used/updated inside PickPersonStep
    var showAddPersonDialog by remember { mutableStateOf(false) }
    var personName by remember { mutableStateOf("") }

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

    // Edit mode: load gift once and push into VM state. Avoid overwriting user's in-progress edits.
    LaunchedEffect(giftId) {
        if (giftId != null) {
            viewModel.getGiftById(giftId).collectLatest { gift ->
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
                    onNavigateBack("Saved")
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("${stepTitles[currentStep - 1]} (${currentStep}/${totalSteps})") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Progress indicator
            LinearProgressIndicator(
                progress = { currentStep / totalSteps.toFloat() },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(24.dp))

            // Step content
            when (currentStep) {
                1 -> PickPersonStep(
                    //onPersonSelected = { currentStep = 2 },
                    persons = persons,
                    selectedPersonId = ui.personId,
                    context = context,
                    viewModel = viewModel,
                    showAddPersonDialog = showAddPersonDialog,
                    onShowAddPersonDialogChange = { showAddPersonDialog = it },
                    personName = personName,
                    onPersonNameChange = { personName = it }
                )
                2 -> PickDateStep(
                    onDateSelected = { currentStep = 3 }
                )
                3 -> GiftDetailsStep(
                    onGiftCreated = {
                        // Navigate back to gifts screen
                        navController.navigateUp()
                    }
                )
            }

            Spacer(Modifier.height(24.dp))

            // Navigation buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (currentStep > 1) {
                    OutlinedButton(
                        onClick = { currentStep-- }
                    ) {
                        Text("Back")
                    }
                } else {
                    Spacer(Modifier.width(1.dp))
                }

                if (currentStep < totalSteps) {
                    Button(
                        onClick = { currentStep++ }
                    ) {
                        Text("Next")
                    }
                }
            }
        }
    }

    // Dialog to add a new person and auto-select them
    if (showAddPersonDialog) {
        AlertDialog(
            onDismissRequest = { showAddPersonDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            // 1) Insert new person (id = 0 so Room will auto-generate)
                            val name = personName.trim()
                            if (name.isNotEmpty()) {
                                val newId = personViewModel.insertAndReturnId(Person(id = 0, name = name))
                                viewModel.onPersonSelected(newId)
                                showAddPersonDialog = false

//                                // 2) Take one snapshot of the list and find the inserted person
//                                val latest = personViewModel.allPersons.first()
//                                val inserted = latest.maxByOrNull { it.id } // simplest heuristic if IDs are monotonic
//                                    ?: latest.find { it.name == name }        // fallback by name
//                                // 3) Select that person in the gift VM
//                                inserted?.let { viewModel.onPersonSelected(it.id) }
                            }
                            // 4) Close dialog
                            //showAddPersonDialog = false
                        }
                    }
                ) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showAddPersonDialog = false }) { Text("Cancel") }
            },
            title = { Text("Add New Person") },
            text = {
                OutlinedTextField(
                    value = personName,
                    onValueChange = { personName = it },
                    label = { Text("Name") },
                    singleLine = true
                )
            }
        )
    }
}

@Composable
private fun PickPersonStep(
    //onPersonSelected: () -> Unit,
    persons: List<Person>,
    selectedPersonId: Int?,
    context: Context,
    viewModel: GiftViewModel,
    showAddPersonDialog: Boolean,
    onShowAddPersonDialogChange: (Boolean) -> Unit,
    personName: String,
    onPersonNameChange: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

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
                        //onPersonSelected()
                    } else {
                        onShowAddPersonDialogChange(true)
                        onPersonNameChange(name)
                    }
                }
            }
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Select who this gift is for",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(Modifier.height(16.dp))


        Box {
            Button(onClick = { expanded = true }) {
                Text(persons.find { it.id == selectedPersonId }?.name ?: "Select Person")
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
      //                      onPersonSelected()
                        }
                    )
                }
                DropdownMenuItem(
                    text = { Text("Add New Person") },
                    onClick = {
                        onShowAddPersonDialogChange(true)
                        onPersonNameChange("") // clear prefill
                        expanded = false
                    }
                )
            }
        }




    }
}

@Composable
private fun PickDateStep(onDateSelected: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "When is this gift for?",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(Modifier.height(16.dp))

        // TODO: Replace with actual date picker
        Button(onClick = onDateSelected) {
            Text("Pick Date")
        }
    }
}

@Composable
private fun GiftDetailsStep(onGiftCreated: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Add gift details",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(Modifier.height(16.dp))

        // TODO: Replace with actual gift form
        OutlinedTextField(
            value = "",
            onValueChange = { },
            label = { Text("Gift idea") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = onGiftCreated,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create Gift")
        }
    }
}
