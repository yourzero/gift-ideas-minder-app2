package com.threekidsinatrenchcoat.giftideaminder.data.repository

import android.content.ContentResolver
import android.database.Cursor
import android.provider.ContactsContract
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
        if (query.isBlank()) return@withContext emptyList()
        
        val contacts = mutableListOf<ContactInfo>()
        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )
        
        val selection = "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} LIKE ?"
        val selectionArgs = arrayOf("%$query%")
        val sortOrder = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
        
        try {
            val cursor: Cursor? = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder
            )
            
            cursor?.use {
                val idIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
                val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                val phoneIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                
                val seen = mutableSetOf<String>() // Avoid duplicates
                
                while (it.moveToNext()) {
                    val id = it.getString(idIndex) ?: continue
                    val name = it.getString(nameIndex) ?: continue
                    val phoneNumber = it.getString(phoneIndex)
                    
                    // Avoid duplicate names
                    if (seen.add(name)) {
                        contacts.add(ContactInfo(id = id, name = name, phoneNumber = phoneNumber))
                    }
                    
                    // Limit results
                    if (contacts.size >= 10) break
                }
            }
        } catch (e: Exception) {
            // Handle permission denied or other errors gracefully
            e.printStackTrace()
        }
        
        return@withContext contacts
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