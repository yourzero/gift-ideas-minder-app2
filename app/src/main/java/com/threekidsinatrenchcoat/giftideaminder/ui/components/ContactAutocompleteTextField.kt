package com.threekidsinatrenchcoat.giftideaminder.ui.components

import android.util.Log
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
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
    val contactSuggestions by contactsViewModel.contactSuggestions.collectAsState()
    val isSearching by contactsViewModel.isSearching.collectAsState()
    
    var isExpanded by remember { mutableStateOf(false) }
    var isFocused by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    
    // Update search query when value changes
    LaunchedEffect(value) {
        Log.d("ContactAutocompleteTextField", "LaunchedEffect: value changed to '$value'")
        contactsViewModel.updateSearchQuery(value)
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
                                    contact.phoneNumber?.let { phone ->
                                        Text(
                                            text = phone,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
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
}