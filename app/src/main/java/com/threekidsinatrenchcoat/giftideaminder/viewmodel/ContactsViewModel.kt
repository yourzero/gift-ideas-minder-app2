package com.threekidsinatrenchcoat.giftideaminder.viewmodel

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
                    if (query.length >= 2) { // Only search if at least 2 characters
                        searchContacts(query)
                    } else {
                        _contactSuggestions.value = emptyList()
                    }
                }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    private fun searchContacts(query: String) {
        viewModelScope.launch {
            _isSearching.value = true
            try {
                val contacts = contactsRepository.searchContacts(query)
                _contactSuggestions.value = contacts
            } catch (e: Exception) {
                _contactSuggestions.value = emptyList()
            } finally {
                _isSearching.value = false
            }
        }
    }

    fun clearSuggestions() {
        _contactSuggestions.value = emptyList()
        _searchQuery.value = ""
    }
}