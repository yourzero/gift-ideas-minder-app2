package com.threekidsinatrenchcoat.giftideaminder.data.repository

import android.content.ContentResolver
import android.database.Cursor
import android.provider.ContactsContract
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

data class ContactInfo(
    val id: String,
    val name: String,
    val phoneNumber: String? = null
)

@Singleton
class ContactsRepository @Inject constructor(
    private val contentResolver: ContentResolver
) {
    
    suspend fun searchContacts(query: String): List<ContactInfo> = withContext(Dispatchers.IO) {
        Log.d("ContactsRepository", "searchContacts: Starting search for '$query'")
        if (query.isBlank()) {
            Log.d("ContactsRepository", "searchContacts: Query is blank, returning empty list")
            return@withContext emptyList()
        }
        
        val contacts = mutableListOf<ContactInfo>()
        val projection = arrayOf(
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.Contacts.HAS_PHONE_NUMBER
        )
        
        val selection = "${ContactsContract.Contacts.DISPLAY_NAME} LIKE ?"
        val selectionArgs = arrayOf("%$query%")
        val sortOrder = ContactsContract.Contacts.DISPLAY_NAME
        
        Log.d("ContactsRepository", "searchContacts: Executing query with selection: '$selection', args: ${selectionArgs.contentToString()}")
        Log.d("ContactsRepository", "searchContacts: Using Contacts table instead of Phone table")
        
        try {
            val cursor: Cursor? = contentResolver.query(
                ContactsContract.Contacts.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder
            )
            
            Log.d("ContactsRepository", "searchContacts: Query executed, cursor: ${cursor?.count ?: 0} rows")
            
            cursor?.use {
                val idIndex = it.getColumnIndex(ContactsContract.Contacts._ID)
                val nameIndex = it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
                val hasPhoneIndex = it.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)
                
                val seen = mutableSetOf<String>() // Avoid duplicates
                
                while (it.moveToNext()) {
                    val id = it.getString(idIndex) ?: continue
                    val name = it.getString(nameIndex) ?: continue
                    val hasPhone = it.getInt(hasPhoneIndex) == 1
                    
                    // Get phone number if available
                    var phoneNumber: String? = null
                    if (hasPhone) {
                        phoneNumber = getPhoneNumber(id)
                    }
                    
                    // Avoid duplicate names
                    if (seen.add(name)) {
                        contacts.add(ContactInfo(id = id, name = name, phoneNumber = phoneNumber))
                        Log.d("ContactsRepository", "searchContacts: Added contact: $name (${phoneNumber ?: "no phone"})")
                    } else {
                        Log.d("ContactsRepository", "searchContacts: Skipped duplicate: $name")
                    }
                    
                    // Limit results
                    if (contacts.size >= 10) {
                        Log.d("ContactsRepository", "searchContacts: Hit limit of 10 contacts, breaking")
                        break
                    }
                }
            }
        } catch (e: Exception) {
            // Handle permission denied or other errors gracefully
            Log.e("ContactsRepository", "searchContacts: Error executing query", e)
            e.printStackTrace()
        }
        
        Log.d("ContactsRepository", "searchContacts: Returning ${contacts.size} contacts: ${contacts.map { it.name }}")
        return@withContext contacts
    }
    
    private fun getPhoneNumber(contactId: String): String? {
        return try {
            val phoneCursor = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER),
                "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?",
                arrayOf(contactId),
                null
            )
            
            phoneCursor?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                    cursor.getString(phoneIndex)
                } else null
            }
        } catch (e: Exception) {
            Log.e("ContactsRepository", "getPhoneNumber: Error getting phone for contact $contactId", e)
            null
        }
    }
    
    suspend fun getAllContactNames(limit: Int = 50): List<ContactInfo> = withContext(Dispatchers.IO) {
        val contacts = mutableListOf<ContactInfo>()
        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )
        
        val sortOrder = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
        
        try {
            val cursor: Cursor? = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection,
                null,
                null,
                sortOrder
            )
            
            cursor?.use {
                val idIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
                val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                val phoneIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                
                val seen = mutableSetOf<String>() // Avoid duplicates
                
                while (it.moveToNext() && contacts.size < limit) {
                    val id = it.getString(idIndex) ?: continue
                    val name = it.getString(nameIndex) ?: continue
                    val phoneNumber = it.getString(phoneIndex)
                    
                    // Avoid duplicate names
                    if (seen.add(name)) {
                        contacts.add(ContactInfo(id = id, name = name, phoneNumber = phoneNumber))
                    }
                }
            }
        } catch (e: Exception) {
            // Handle permission denied or other errors gracefully
            e.printStackTrace()
        }
        
        return@withContext contacts
    }
}