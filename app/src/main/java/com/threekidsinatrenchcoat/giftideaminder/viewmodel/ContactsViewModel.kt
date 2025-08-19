package com.threekidsinatrenchcoat.giftideaminder.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.threekidsinatrenchcoat.giftideaminder.data.repository.ContactInfo
import com.threekidsinatrenchcoat.giftideaminder.data.repository.ContactsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class ContactsViewModel @Inject constructor(
    private val contactsRepository: ContactsRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _contactSuggestions = MutableStateFlow<List<ContactInfo>>(emptyList())
    val contactSuggestions: StateFlow<List<ContactInfo>> = _contactSuggestions
    
    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching

    init {
        // Set up debounced search
        viewModelScope.launch {
            _searchQuery
                .debounce(300) // Wait 300ms after user stops typing
                .distinctUntilChanged()
                .collect { query ->
                    Log.d("ContactsViewModel", "Debounced query received: '$query' (length: ${query.length})")
                    if (query.length >= 2) { // Only search if at least 2 characters
                        Log.d("ContactsViewModel", "Query meets minimum length, triggering search")
                        searchContacts(query)
                    } else {
                        Log.d("ContactsViewModel", "Query too short, clearing suggestions")
                        _contactSuggestions.value = emptyList()
                    }
                }
        }
    }

    fun updateSearchQuery(query: String) {
        Log.d("ContactsViewModel", "updateSearchQuery: '$query' (length: ${query.length})")
        _searchQuery.value = query
    }

    private fun searchContacts(query: String) {
        Log.d("ContactsViewModel", "searchContacts: Starting search for '$query'")
        viewModelScope.launch {
            _isSearching.value = true
            try {
                Log.d("ContactsViewModel", "searchContacts: Calling repository.searchContacts")
                val contacts = contactsRepository.searchContacts(query)
                Log.d("ContactsViewModel", "searchContacts: Found ${contacts.size} contacts: ${contacts.map { it.name }}")
                _contactSuggestions.value = contacts
            } catch (e: Exception) {
                Log.e("ContactsViewModel", "searchContacts: Error searching contacts", e)
                _contactSuggestions.value = emptyList()
            } finally {
                _isSearching.value = false
                Log.d("ContactsViewModel", "searchContacts: Search completed")
            }
        }
    }

    fun clearSuggestions() {
        Log.d("ContactsViewModel", "clearSuggestions: Clearing all suggestions and search query")
        _contactSuggestions.value = emptyList()
        _searchQuery.value = ""
    }
}