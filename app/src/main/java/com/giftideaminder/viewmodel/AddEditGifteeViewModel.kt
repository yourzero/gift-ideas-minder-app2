package com.giftideaminder.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.giftideaminder.data.model.Person
import com.giftideaminder.data.repository.PersonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.provider.ContactsContract
import java.text.SimpleDateFormat
import java.util.Locale

data class GifteeUiState(
    val isEditing: Boolean = false,
    val id: Int? = null,
    val photoUri: Uri? = null,
    val name: String = "",
    val eventDate: Long? = null,
    val isDatePickerOpen: Boolean = false,
    val relationships: List<String> = emptyList(),
    val isRelationshipDropdownOpen: Boolean = false,
    val notes: String = "",
    val phoneNumber: String? = null,
    val showSmsPrompt: Boolean = false
)

@HiltViewModel
class AddEditGifteeViewModel @Inject constructor(
    private val personRepo: PersonRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(GifteeUiState())
    val uiState: StateFlow<GifteeUiState> = _uiState.asStateFlow()

    // Expose relationship options publicly
    val relationshipOptions: List<String> = listOf("Family", "Friend", "Coworker")

    init {
        savedStateHandle.get<Int>("gifteeId")?.let { id ->
            viewModelScope.launch {
                personRepo.getPersonById(id).firstOrNull()?.let { person ->
                    _uiState.update { s ->
                        s.copy(
                            isEditing = true,
                            id = person.id,
                            photoUri = person.photoUri?.let(Uri::parse),
                            name = person.name,
                            eventDate = person.birthday,
                            relationships = person.relationships,
                            notes = person.notes ?: ""
                        )
                    }
                }
            }
        }
    }

    fun onNameChange(new: String) = _uiState.update { it.copy(name = new) }
    fun onEventDateChange(ts: Long) = _uiState.update { it.copy(eventDate = ts) }
    fun onNotesChange(new: String) = _uiState.update { it.copy(notes = new) }
    fun onShowDatePicker(open: Boolean) =
        _uiState.update { it.copy(isDatePickerOpen = open) }
    fun onShowRelationshipDropdown(open: Boolean) =
        _uiState.update { it.copy(isRelationshipDropdownOpen = open) }
    fun onRelationshipsChange(new: List<String>) =
        _uiState.update { it.copy(relationships = new) }

    fun findContactByName(ctx: Context, name: String) {
        viewModelScope.launch {
            val uri = ContactsContract.Contacts.CONTENT_URI
            val selection = "${ContactsContract.Contacts.DISPLAY_NAME} LIKE ?"
            val selectionArgs = arrayOf("%$name%")
            
            ctx.contentResolver.query(uri, null, selection, selectionArgs, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val contactId = cursor.getLong(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID))
                    val displayName = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME))
                    val photoUriStr = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.PHOTO_URI))
                    val photoUri = photoUriStr?.let { Uri.parse(it) }
                    val birthday = getBirthday(ctx, contactId)
                    val phone = getPhoneNumber(ctx, contactId)
                    
                    _uiState.update {
                        it.copy(
                            name = displayName,
                            photoUri = photoUri,
                            eventDate = birthday,
                            phoneNumber = phone,
                            showSmsPrompt = phone != null
                        )
                    }
                }
            }
        }
    }

    fun onDismissSmsPrompt() = _uiState.update { it.copy(showSmsPrompt = false) }

    fun scanSmsForIdeas(ctx: Context, phone: String) {
        viewModelScope.launch {
            val uri = android.net.Uri.parse("content://sms/")
            val projection = arrayOf("body")
            val selection = "address = ?"
            val selectionArgs = arrayOf(phone)
            val messages = mutableListOf<String>()
            ctx.contentResolver.query(uri, projection, selection, selectionArgs, "date DESC LIMIT 50")?.use { cursor ->
                while (cursor.moveToNext()) {
                    messages.add(cursor.getString(0))
                }
            }
            // Mock AI processing
            val ideas = "Idea 1: Book\nIdea 2: Chocolate" // Replace with actual AI call
            _uiState.update { it.copy(notes = it.notes + "\nGift Ideas from SMS:\n$ideas") }
        }
    }

    fun loadContact(ctx: Context, contactUri: Uri) {
        viewModelScope.launch {
            ctx.contentResolver.query(contactUri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME))
                    val photoUriStr = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.PHOTO_URI))
                    val photoUri = photoUriStr?.let { Uri.parse(it) }
                    val contactId = cursor.getLong(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID))
                    val birthday = getBirthday(ctx, contactId)
                    val phone = getPhoneNumber(ctx, contactId)
                    _uiState.update {
                        it.copy(
                            name = name,
                            photoUri = photoUri,
                            eventDate = birthday,
                            phoneNumber = phone,
                            showSmsPrompt = true
                        )
                    }
                }
            }
        }
    }

    private fun getBirthday(ctx: Context, contactId: Long): Long? {
        val uri = ContactsContract.Data.CONTENT_URI
        val projection = arrayOf(ContactsContract.CommonDataKinds.Event.START_DATE)
        val selection = "${ContactsContract.Data.CONTACT_ID} = ? AND ${ContactsContract.Data.MIMETYPE} = ? AND ${ContactsContract.CommonDataKinds.Event.TYPE} = ?"
        val selectionArgs = arrayOf(contactId.toString(), ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE, ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY.toString())
        ctx.contentResolver.query(uri, projection, selection, selectionArgs, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val dateStr = cursor.getString(0)
                return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dateStr)?.time
            }
        }
        return null
    }

    private fun getPhoneNumber(ctx: Context, contactId: Long): String? {
        val uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        val projection = arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER)
        val selection = "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?"
        val selectionArgs = arrayOf(contactId.toString())
        ctx.contentResolver.query(uri, projection, selection, selectionArgs, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                return cursor.getString(0)
            }
        }
        return null
    }

    fun onSave() {
        viewModelScope.launch {
            val s = _uiState.value
            val person = Person(
                id = s.id ?: 0,
                name = s.name,
                photoUri = s.photoUri?.toString(),
                birthday = s.eventDate,
                relationships = s.relationships,
                notes = s.notes,
                contactInfo = s.phoneNumber
            )
            
            if (s.isEditing) {
                personRepo.update(person)
            } else {
                personRepo.insert(person)
            }
        }
    }
}
