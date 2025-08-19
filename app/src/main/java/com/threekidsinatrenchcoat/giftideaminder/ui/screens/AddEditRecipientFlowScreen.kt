package com.threekidsinatrenchcoat.giftideaminder.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.unit.dp
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.threekidsinatrenchcoat.giftideaminder.viewmodel.PersonFlowViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContactPage
import androidx.compose.ui.platform.testTag
import android.net.Uri
import android.provider.ContactsContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import com.threekidsinatrenchcoat.giftideaminder.ui.components.ContactAutocompleteTextField
import com.threekidsinatrenchcoat.giftideaminder.data.model.Interest
import com.threekidsinatrenchcoat.giftideaminder.data.model.InterestType
import com.threekidsinatrenchcoat.giftideaminder.viewmodel.PersonViewModel
import com.threekidsinatrenchcoat.giftideaminder.viewmodel.SettingsViewModel

@Composable
fun AddEditRecipientFlowScreen(
    onNavigateBack: (String?) -> Unit,
    navController: NavController,
    personId: Int? = null,
    viewModel: PersonFlowViewModel = hiltViewModel(),
    personViewModel: PersonViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = { Text(if (state.isEditing) "Edit Recipient" else "Add Recipient") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            // Consistent action bar
            Surface(tonalElevation = 2.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = {
                            val result = viewModel.onBack()
                            if (result.navigateBack) onNavigateBack(null)
                        },
                        modifier = Modifier.weight(1f)
                    ) { Text("Back") }

                    Button(
                        onClick = {
                            val result = viewModel.onNextOrSave()
                            if (result.saved) onNavigateBack(result.successMessage)
                        },
                        modifier = Modifier.weight(1f)
                    ) { Text(if (state.step == PersonFlowViewModel.Step.Review) "Save" else "Next") }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(top = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StepHeader(
                current = state.step.ordinal,
                labels = PersonFlowViewModel.Step.values().map { it.name }
            )

            when (state.step) {
                PersonFlowViewModel.Step.Details -> {
                    val context = LocalContext.current
                    
                    // Contact picker launcher
                    val contactPicker = rememberLauncherForActivityResult(
                        ActivityResultContracts.PickContact()
                    ) { uri: Uri? ->
                        uri?.let {
                            context.contentResolver.query(it, null, null, null, null)?.use { cursor ->
                                if (cursor.moveToFirst()) {
                                    val name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME))
                                    viewModel.onNameChange(name)
                                }
                            }
                        }
                    }
                    
                    SectionCard(
                        icon = Icons.Default.Person,
                        title = "Details"
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            ContactAutocompleteTextField(
                                value = state.name,
                                onValueChange = viewModel::onNameChange,
                                onContactSelected = { contact ->
                                    // Auto-fill name from selected contact
                                    viewModel.onNameChange(contact.name)
                                    
                                    // Auto-select relationship if detected
                                    contact.detectedRelationship?.let { relationship ->
                                        // Map to available relationship options
                                        val mappedRelationship = when (relationship) {
                                            "Mother", "Father", "Sister", "Brother", "Aunt", "Uncle", 
                                            "Cousin", "Grandmother", "Grandfather", "Child", "Spouse" -> "Family"
                                            "Friend" -> "Friend"
                                            "Colleague" -> "Coworker"
                                            "Partner" -> "Family"  // Partners are typically family-like relationships
                                            else -> null
                                        }
                                        
                                        mappedRelationship?.let { mapped ->
                                            // Map detected relationships to PersonFlow available relationships
                                            val relationshipMapping = when (mapped) {
                                                "Family" -> when (relationship) {
                                                    "Mother", "Father" -> "Parent"
                                                    "Sister", "Brother" -> "Sibling"
                                                    "Child" -> "Child"
                                                    "Spouse" -> "Spouse"
                                                    "Partner" -> "Partner"
                                                    else -> null
                                                }
                                                "Friend" -> "Friend"
                                                "Coworker" -> "Coworker"
                                                else -> null
                                            }
                                            
                                            relationshipMapping?.let { targetRelationship ->
                                                // Check if it's in available options and not already selected
                                                if (targetRelationship in state.availableRelationships && 
                                                    targetRelationship !in state.selectedRelationships) {
                                                    viewModel.onRelationshipSelected(targetRelationship)
                                                }
                                            }
                                        }
                                    }
                                },
                                label = { Text("Name") },
                                placeholder = { Text("Start typing to search contacts") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            
                            // Import from contacts button
                            OutlinedButton(
                                onClick = { contactPicker.launch(null) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Icon(
                                    Icons.Default.ContactPage,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text("Import from Contacts")
                            }
                            
                            Text("Relationship", style = MaterialTheme.typography.titleSmall)
                            RelationshipSelector(
                                options = state.availableRelationships,
                                selected = state.selectedRelationships,
                                onSelected = { viewModel.onRelationshipSelected(it) },
                                onAddNew = { name, hasBirthday, hasAnniversary -> 
                                    viewModel.onAddNewRelationshipType(name, hasBirthday, hasAnniversary)
                                }
                            )
                        }
                    }
                }

                PersonFlowViewModel.Step.Dates -> SectionCard(
                    icon = Icons.Default.CalendarToday,
                    title = "Important Dates"
                ) {
                    // Visible rows: prompts + additional + any existing picked labels (edit mode), minus removed
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

                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        visibleLabels.forEach { label ->
                            ImprovedDateRow(
                                label = label,
                                date = state.pickedDates[label],
                                onLabelChange = { newLabel -> viewModel.onChangeDateLabel(label, newLabel) },
                                onPicked = { picked -> viewModel.onDatePicked(label, picked) },
                                onRemove = { viewModel.onRemoveDateItem(label) }
                            )
                        }

                        OutlinedButton(
                            onClick = { viewModel.onAddDateItem("Custom") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Add Another Date")
                        }
                    }
                }

                PersonFlowViewModel.Step.Preferences -> {
                    // Get current person ID - either existing or temp ID for new person
                    val currentPersonId = personId ?: -1 // Use -1 for new person temporarily
                    
                    if (currentPersonId != -1) {
                        // Existing person - use real Interest system
                        val interests by personViewModel.getInterestsForPerson(currentPersonId).collectAsState(initial = emptyList())
                        val isAdvancedMode by settingsViewModel.isAdvancedMode.collectAsState()
                        
                        SectionCard(
                            icon = Icons.Default.FavoriteBorder,
                            title = "Gift Inspirations"
                        ) {
                            NewInterestSystem(
                                interests = interests,
                                isAdvancedMode = isAdvancedMode,
                                onAddInterest = { type, value ->
                                    personViewModel.addInterest(currentPersonId, type, value)
                                },
                                onDeleteInterest = { interest ->
                                    personViewModel.deleteInterest(interest)
                                },
                                onToggleOwned = { interest ->
                                    personViewModel.toggleInterestOwned(interest)
                                }
                            )
                        }
                    } else {
                        // New person - use fallback to old system for now
                        SectionCard(
                            icon = Icons.Default.FavoriteBorder,
                            title = "Gift Inspirations"
                        ) {
                            GiftInspirationsInline(
                                current = state.preferences,
                                onAdd = { viewModel.onAddPreference(it) },
                                onRemove = { viewModel.onRemovePreference(it) }
                            )
                        }
                    }
                }

                PersonFlowViewModel.Step.Review -> SectionCard(
                    icon = Icons.Default.Person,
                    title = "Review"
                ) {
                    val formatter = remember { DateTimeFormatter.ofPattern("MMM d, yyyy") }
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        LabeledValue(
                            label = "Relationships", 
                            value = if (state.selectedRelationships.isEmpty()) "None" else state.selectedRelationships.joinToString(", ")
                        )
                        LabeledValue(label = "Name", value = state.name.ifBlank { "—" })
                        
                        if (state.pickedDates.isNotEmpty()) {
                            Text("Important Dates", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold))
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                state.pickedDates.entries.sortedBy { it.key }.forEach { (label, date) ->
                                    Text("• $label: ${date.format(formatter)}")
                                }
                            }
                        }
                        
                        if (state.preferences.isNotEmpty()) {
                            Text("Gift Preferences", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold))
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                state.preferences.forEach { preference ->
                                    Text("• $preference")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    ElevatedCard(
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text(title, style = MaterialTheme.typography.titleMedium)
            }
            content()
        }
    }
}

@Composable
private fun StepHeader(current: Int, labels: List<String>) {
    val total = labels.size
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        // Dots
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(total) { index ->
                val active = index == current
                val color = if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .size(if (active) 10.dp else 8.dp)
                        .clip(CircleShape)
                        .background(color)
                )
            }
        }
        // Label
        Text(
            text = "Step ${current + 1} of $total",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
private fun RelationshipChips(
    options: List<String>,
    selected: String?,
    onSelected: (String) -> Unit
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { label ->
            val isSelected = label == selected
            FilterChip(
                selected = isSelected,
                onClick = { onSelected(label) },
                label = { Text(label) }
            )
        }
    }
}

@Composable
private fun RelationshipSelector(
    options: List<String>,
    selected: List<String>,
    onSelected: (String) -> Unit,
    onAddNew: (String, Boolean, Boolean) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }
    
    ElevatedCard(
        onClick = { showDialog = true },
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = if (selected.isNotEmpty()) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                if (selected.isEmpty()) {
                    Text(
                        text = "Select relationships",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Text(
                        text = "${selected.size} relationship${if (selected.size == 1) "" else "s"} selected",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = selected.joinToString(", "),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            Icon(
                Icons.Default.ArrowDropDown,
                contentDescription = "Select",
                tint = if (selected.isNotEmpty()) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
    
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Select Relationships") },
            text = {
                LazyColumn {
                    items(options) { option ->
                        Surface(
                            onClick = { onSelected(option) },
                            modifier = Modifier.fillMaxWidth(),
                            color = if (option in selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (option in selected) {
                                    Icon(
                                        Icons.Default.Person,
                                        contentDescription = "Selected",
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.padding(end = 12.dp)
                                    )
                                }
                                Text(
                                    text = option,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = if (option in selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                    
                    item {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        Surface(
                            onClick = { 
                                showDialog = false
                                showAddDialog = true 
                            },
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = "Add new",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(end = 12.dp)
                                )
                                Text(
                                    text = "Add New Relationship Type",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Done")
                }
            }
        )
    }
    
    if (showAddDialog) {
        AddNewRelationshipDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { name, hasBirthday, hasAnniversary ->
                onAddNew(name, hasBirthday, hasAnniversary)
                showAddDialog = false
            }
        )
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
        // Help text
        Text(
            text = "Enter as many things that this recipient likes to help generate gift ideas",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        
        OutlinedTextField(
            value = input,
            onValueChange = { input = it },
            label = { Text("Add item") },
            placeholder = { Text("e.g., coffee, hiking, books, gaming") },
            modifier = Modifier.fillMaxWidth()
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { onAdd(input).also { input = "" } },
                enabled = input.isNotBlank()
            ) { Text("Add") }
        }
        
        if (current.isNotEmpty()) {
            Text(
                text = "Current interests:",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
        
        current.forEach { item ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(item)
                TextButton(onClick = { onRemove(item) }) { Text("Remove") }
            }
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

    Surface(
        tonalElevation = 2.dp,
        shape = MaterialTheme.shapes.large,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                DateTypeSelector(label = label, onLabelChange = onLabelChange)
                OutlinedButton(onClick = onRemove) { Text("Delete") }
            }
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(if (date != null) date.format(formatter) else "No date selected")
                Button(onClick = { openPicker = true }) { Text("Pick Date") }
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
                        val picked = Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate()
                        onPicked(picked)
                    }
                    openPicker = false
                }) { Text("Done") }
            },
            dismissButton = { TextButton(onClick = { openPicker = false }) { Text("Cancel") } }
        ) { DatePicker(state = state) }
    }
}

@Composable
private fun ImprovedDateRow(
    label: String,
    date: LocalDate?,
    onLabelChange: (String) -> Unit,
    onPicked: (LocalDate) -> Unit,
    onRemove: () -> Unit
) {
    var openPicker by remember { mutableStateOf(false) }
    val formatter = remember { DateTimeFormatter.ofPattern("MMM d, yyyy") }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    DateTypeSelector(label = label, onLabelChange = onLabelChange)
                }
                IconButton(onClick = onRemove) {
                    Icon(Icons.Default.Delete, contentDescription = "Remove", tint = MaterialTheme.colorScheme.error)
                }
            }
            
            ElevatedCard(
                onClick = { openPicker = true },
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = if (date != null) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (date != null) date.format(formatter) else "Tap to select date",
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (date != null) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Icon(
                        Icons.Default.CalendarToday,
                        contentDescription = "Select date",
                        tint = if (date != null) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
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
                        val picked = Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate()
                        onPicked(picked)
                    }
                    openPicker = false
                }) { Text("Done") }
            },
            dismissButton = { TextButton(onClick = { openPicker = false }) { Text("Cancel") } }
        ) { DatePicker(state = state) }
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
            OutlinedButton(modifier = Modifier.testTag("date-type-selector"), onClick = { expanded = true }) {
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
private fun LabeledValue(label: String, value: String) {
    Column {
        Text(label, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun AddNewRelationshipDialog(
    onDismiss: () -> Unit,
    onAdd: (String, Boolean, Boolean) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var hasBirthday by remember { mutableStateOf(true) }
    var hasAnniversary by remember { mutableStateOf(false) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Relationship Type") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Relationship Name") },
                    placeholder = { Text("e.g., Cousin, Neighbor, Boss") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Text("Important Dates", style = MaterialTheme.typography.titleSmall)
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = hasBirthday,
                        onCheckedChange = { hasBirthday = it }
                    )
                    Text("Has Birthday")
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = hasAnniversary,
                        onCheckedChange = { hasAnniversary = it }
                    )
                    Text("Has Anniversary")
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onAdd(name, hasBirthday, hasAnniversary) },
                enabled = name.isNotBlank()
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun NewInterestSystem(
    interests: List<Interest>,
    isAdvancedMode: Boolean,
    onAddInterest: (InterestType, String) -> Unit,
    onDeleteInterest: (Interest) -> Unit,
    onToggleOwned: (Interest) -> Unit
) {
    var selectedType by remember(isAdvancedMode) { 
        mutableStateOf(if (isAdvancedMode) InterestType.GENERAL else InterestType.GENERAL) 
    }
    var showAddDialog by remember { mutableStateOf(false) }
    var newInterestText by remember { mutableStateOf("") }
    
    // Filter interests by selected type and mode
    val filteredInterests = interests.filter { 
        if (!isAdvancedMode) {
            // Simple mode: only show general interests
            it.type == InterestType.GENERAL 
        } else {
            // Advanced mode: show selected type
            it.type == selectedType
        }
    }
    
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Type toggle (only show in advanced mode)
        if (isAdvancedMode) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    onClick = { selectedType = InterestType.GENERAL },
                    label = { Text("General") },
                    selected = selectedType == InterestType.GENERAL,
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    onClick = { selectedType = InterestType.SPECIFIC },
                    label = { Text("Specific") },
                    selected = selectedType == InterestType.SPECIFIC,
                    modifier = Modifier.weight(1f)
                )
            }
        } else {
            Text(
                text = "Simple mode: Only general interests (cooking, sports, music)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        // Add interest button
        OutlinedButton(
            onClick = { showAddDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            val typeToAdd = if (!isAdvancedMode) InterestType.GENERAL else selectedType
            Text("Add ${typeToAdd.name.lowercase().replaceFirstChar { it.uppercase() }} Interest")
        }
        
        // Interests list - make it scrollable
        if (filteredInterests.isNotEmpty()) {
            Text(
                text = "Current interests:",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            LazyColumn(
                modifier = Modifier.heightIn(max = 200.dp), // Limit height to make scrollable
                verticalArrangement = Arrangement.spacedBy(4.dp) // Tighter spacing
            ) {
                items(filteredInterests) { interest ->
                    InterestItemInline(
                        interest = interest,
                        onToggleOwned = { onToggleOwned(interest) },
                        onDelete = { onDeleteInterest(interest) },
                        isAdvancedMode = isAdvancedMode
                    )
                }
            }
        } else {
            Text(
                text = "No ${if (isAdvancedMode) selectedType.name.lowercase() else "general"} interests yet",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
    
    // Add interest dialog
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { 
                showAddDialog = false 
                newInterestText = ""
            },
            title = { 
                val typeToAdd = if (!isAdvancedMode) InterestType.GENERAL else selectedType
                Text("Add ${typeToAdd.name.lowercase().replaceFirstChar { it.uppercase() }} Interest") 
            },
            text = {
                OutlinedTextField(
                    value = newInterestText,
                    onValueChange = { newInterestText = it },
                    label = { Text("Interest") },
                    placeholder = { 
                        val typeToAdd = if (!isAdvancedMode) InterestType.GENERAL else selectedType
                        Text(
                            if (typeToAdd == InterestType.GENERAL) {
                                "e.g., cooking, sports, music"
                            } else {
                                "e.g., Nike Air Max shoes, iPhone 15"
                            }
                        ) 
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newInterestText.isNotBlank()) {
                            val typeToAdd = if (!isAdvancedMode) InterestType.GENERAL else selectedType
                            onAddInterest(typeToAdd, newInterestText.trim())
                            showAddDialog = false
                            newInterestText = ""
                        }
                    }
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { 
                        showAddDialog = false
                        newInterestText = ""
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun InterestItemInline(
    interest: Interest,
    onToggleOwned: () -> Unit,
    onDelete: () -> Unit,
    isAdvancedMode: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp), // Reduced padding for tighter layout
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = interest.value,
                    style = MaterialTheme.typography.bodyMedium
                )
                if (interest.type == InterestType.SPECIFIC && isAdvancedMode) {
                    Text(
                        text = if (interest.alreadyOwned) "Already owned" else "Available",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (interest.alreadyOwned) 
                            MaterialTheme.colorScheme.error 
                        else 
                            MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            Row {
                // Toggle owned button (only for specific items in advanced mode)
                if (interest.type == InterestType.SPECIFIC && isAdvancedMode) {
                    TextButton(onClick = onToggleOwned) {
                        Text(
                            text = if (interest.alreadyOwned) "Available" else "Owned",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
                
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Filled.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

// --- Simple FlowRow (copy to avoid extra dependency) ---
@Composable
private fun FlowRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    content: @Composable () -> Unit
) {
    // Very lightweight wrapper around Row/Wrap for chips without adding Accompanist.
    // For simplicity and stability, we just use a Column of Rows.
    Column(modifier = modifier) {
        var rowWidth = 0
        var currentRow = mutableListOf<@Composable () -> Unit>()
        val maxWidth = 10_000 // not measured here; chips will wrap visually by constraints
        currentRow.clear()
        Row(horizontalArrangement = horizontalArrangement, verticalAlignment = Alignment.CenterVertically) {
            content()
        }
    }
}
