package com.threekidsinatrenchcoat.giftideaminder.ui.components

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.threekidsinatrenchcoat.giftideaminder.data.repository.ContactInfo
import com.threekidsinatrenchcoat.giftideaminder.viewmodel.ContactsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactAutocompleteTextField(
    value: String,
    onValueChange: (String) -> Unit,
    onContactSelected: (ContactInfo) -> Unit,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    contactsViewModel: ContactsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val contactSuggestions by contactsViewModel.contactSuggestions.collectAsState()
    val isSearching by contactsViewModel.isSearching.collectAsState()
    
    var isExpanded by remember { mutableStateOf(false) }
    var isFocused by remember { mutableStateOf(false) }
    var hasContactsPermission by remember { 
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED
        )
    }
    var showPermissionDialog by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    
    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasContactsPermission = isGranted
            Log.d("ContactAutocompleteTextField", "Contacts permission granted: $isGranted")
            if (!isGranted) {
                showPermissionDialog = true
            }
        }
    )
    
    // Update search query when value changes, but only if we have permission
    LaunchedEffect(value, hasContactsPermission) {
        Log.d("ContactAutocompleteTextField", "LaunchedEffect: value changed to '$value', hasPermission: $hasContactsPermission")
        if (hasContactsPermission && value.length >= 2) {
            contactsViewModel.updateSearchQuery(value)
        } else if (!hasContactsPermission && value.length >= 2) {
            Log.d("ContactAutocompleteTextField", "LaunchedEffect: Requesting contacts permission")
            permissionLauncher.launch(Manifest.permission.READ_CONTACTS)
        } else {
            contactsViewModel.updateSearchQuery("")
        }
    }
    
    // Show dropdown when focused and has suggestions
    LaunchedEffect(isFocused, contactSuggestions) {
        Log.d("ContactAutocompleteTextField", "LaunchedEffect: focus=$isFocused, suggestions=${contactSuggestions.size}")
        isExpanded = isFocused && contactSuggestions.isNotEmpty()
        Log.d("ContactAutocompleteTextField", "LaunchedEffect: isExpanded=$isExpanded")
    }
    
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = label,
            placeholder = placeholder,
            trailingIcon = {
                if (isSearching) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { focusState ->
                    Log.d("ContactAutocompleteTextField", "onFocusChanged: isFocused=${focusState.isFocused}")
                    isFocused = focusState.isFocused
                    if (!focusState.isFocused) {
                        Log.d("ContactAutocompleteTextField", "onFocusChanged: Lost focus, collapsing dropdown")
                        isExpanded = false
                    }
                }
        )
        
        // Dropdown menu for suggestions
        DropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { 
                Log.d("ContactAutocompleteTextField", "DropdownMenu onDismissRequest: Collapsing dropdown")
                isExpanded = false
                focusManager.clearFocus()
            },
            properties = PopupProperties(focusable = false),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (contactSuggestions.isEmpty() && !isSearching) {
                DropdownMenuItem(
                    text = { Text("No contacts found") },
                    onClick = { },
                    enabled = false
                )
            } else {
                contactSuggestions.take(5).forEach { contact -> // Limit to 5 results
                    DropdownMenuItem(
                        text = { 
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    Icons.Filled.Person, 
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Column {
                                    Text(
                                        text = contact.name,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        contact.phoneNumber?.let { phone ->
                                            Text(
                                                text = phone,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        contact.detectedRelationship?.let { relationship ->
                                            Text(
                                                text = "• $relationship",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                }
                            }
                        },
                        onClick = {
                            Log.d("ContactAutocompleteTextField", "Contact selected: ${contact.name}")
                            onContactSelected(contact)
                            onValueChange(contact.name)
                            isExpanded = false
                            focusManager.clearFocus()
                            contactsViewModel.clearSuggestions()
                        }
                    )
                }
            }
        }
    }
    
    // Permission explanation dialog
    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDialog = false },
            title = { Text("Contacts Permission Needed") },
            text = {
                Text("To search and suggest contacts while typing, this app needs access to your contacts. You can grant this permission in your device settings if you'd like to use this feature.")
            },
            confirmButton = {
                TextButton(
                    onClick = { showPermissionDialog = false }
                ) {
                    Text("OK")
                }
            }
        )
    }
}