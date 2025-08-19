package com.threekidsinatrenchcoat.giftideaminder.data.repository

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.provider.Telephony
import android.util.Log
import androidx.core.content.ContextCompat
import com.threekidsinatrenchcoat.giftideaminder.data.model.SmsMessage
import com.threekidsinatrenchcoat.giftideaminder.data.model.SmsConversation
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SmsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    companion object {
        private const val TAG = "SmsRepository"
        private const val MAX_MESSAGES_PER_CONVERSATION = 100
        private const val DEFAULT_DAYS_LOOKBACK = 30
    }

    /**
     * Check if the app has READ_SMS permission
     */
    fun hasReadSmsPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_SMS
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Read SMS messages within specified date range
     */
    suspend fun getSmsMessages(
        daysLookback: Int = DEFAULT_DAYS_LOOKBACK,
        maxMessagesPerConversation: Int = MAX_MESSAGES_PER_CONVERSATION
    ): List<SmsConversation> = withContext(Dispatchers.IO) {
        if (!hasReadSmsPermission()) {
            Log.w(TAG, "No READ_SMS permission, returning empty list")
            return@withContext emptyList()
        }

        try {
            val startDate = getStartDateMillis(daysLookback)
            Log.d(TAG, "Reading SMS messages from last $daysLookback days (since $startDate)")
            
            val messages = readAllSmsMessages(startDate)
            Log.d(TAG, "Found ${messages.size} SMS messages")
            
            val conversations = groupMessagesIntoConversations(messages, maxMessagesPerConversation)
            Log.d(TAG, "Grouped into ${conversations.size} conversations")
            
            conversations
        } catch (e: Exception) {
            Log.e(TAG, "Error reading SMS messages", e)
            emptyList()
        }
    }

    /**
     * Read SMS messages for a specific phone number
     */
    suspend fun getSmsMessagesForContact(
        phoneNumber: String,
        daysLookback: Int = DEFAULT_DAYS_LOOKBACK
    ): SmsConversation? = withContext(Dispatchers.IO) {
        if (!hasReadSmsPermission()) {
            Log.w(TAG, "No READ_SMS permission")
            return@withContext null
        }

        try {
            val startDate = getStartDateMillis(daysLookback)
            val messages = readSmsMessagesForAddress(phoneNumber, startDate)
            
            if (messages.isEmpty()) {
                return@withContext null
            }

            val contactName = ContactsRepository.getContactNameByPhoneNumber(context, phoneNumber)
            SmsConversation(
                contactName = contactName,
                phoneNumber = phoneNumber,
                messages = messages.sortedByDescending { it.date },
                lastMessageDate = messages.maxOfOrNull { it.date } ?: 0L
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error reading SMS for contact $phoneNumber", e)
            null
        }
    }

    private fun readAllSmsMessages(startDate: Long): List<SmsMessage> {
        val messages = mutableListOf<SmsMessage>()
        
        // Read both sent and received messages
        val uris = listOf(
            Telephony.Sms.Sent.CONTENT_URI,
            Telephony.Sms.Inbox.CONTENT_URI
        )

        uris.forEach { uri ->
            val cursor = context.contentResolver.query(
                uri,
                arrayOf(
                    Telephony.Sms._ID,
                    Telephony.Sms.ADDRESS,
                    Telephony.Sms.BODY,
                    Telephony.Sms.DATE,
                    Telephony.Sms.TYPE,
                    Telephony.Sms.THREAD_ID
                ),
                "${Telephony.Sms.DATE} >= ?",
                arrayOf(startDate.toString()),
                "${Telephony.Sms.DATE} DESC"
            )

            cursor?.use { c ->
                messages.addAll(parseSmsMessages(c))
            }
        }

        return messages
    }

    private fun readSmsMessagesForAddress(address: String, startDate: Long): List<SmsMessage> {
        val messages = mutableListOf<SmsMessage>()
        
        val cursor = context.contentResolver.query(
            Telephony.Sms.CONTENT_URI,
            arrayOf(
                Telephony.Sms._ID,
                Telephony.Sms.ADDRESS,
                Telephony.Sms.BODY,
                Telephony.Sms.DATE,
                Telephony.Sms.TYPE,
                Telephony.Sms.THREAD_ID
            ),
            "${Telephony.Sms.ADDRESS} = ? AND ${Telephony.Sms.DATE} >= ?",
            arrayOf(address, startDate.toString()),
            "${Telephony.Sms.DATE} DESC"
        )

        cursor?.use { c ->
            messages.addAll(parseSmsMessages(c))
        }

        return messages
    }

    private fun parseSmsMessages(cursor: Cursor): List<SmsMessage> {
        val messages = mutableListOf<SmsMessage>()
        
        while (cursor.moveToNext()) {
            try {
                val id = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms._ID))
                val address = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.ADDRESS)) ?: ""
                val body = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.BODY)) ?: ""
                val date = cursor.getLong(cursor.getColumnIndexOrThrow(Telephony.Sms.DATE))
                val type = cursor.getInt(cursor.getColumnIndexOrThrow(Telephony.Sms.TYPE))
                val threadId = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.THREAD_ID))

                // Skip empty messages or system messages
                if (body.isBlank() || body.length < 3) {
                    continue
                }

                messages.add(
                    SmsMessage(
                        id = id,
                        address = address,
                        body = body,
                        date = date,
                        type = type,
                        threadId = threadId
                    )
                )
            } catch (e: Exception) {
                Log.w(TAG, "Error parsing SMS message", e)
            }
        }
        
        return messages
    }

    private fun groupMessagesIntoConversations(
        messages: List<SmsMessage>,
        maxMessagesPerConversation: Int
    ): List<SmsConversation> {
        val conversationMap = mutableMapOf<String, MutableList<SmsMessage>>()
        
        // Group messages by phone number
        messages.forEach { message ->
            val normalizedAddress = normalizePhoneNumber(message.address)
            conversationMap.getOrPut(normalizedAddress) { mutableListOf() }.add(message)
        }

        // Convert to conversations and limit message count
        return conversationMap.map { (address, messageList) ->
            val sortedMessages = messageList
                .sortedByDescending { it.date }
                .take(maxMessagesPerConversation)
            
            val contactName = ContactsRepository.getContactNameByPhoneNumber(context, address)
            
            SmsConversation(
                contactName = contactName,
                phoneNumber = address,
                messages = sortedMessages,
                lastMessageDate = sortedMessages.maxOfOrNull { it.date } ?: 0L
            )
        }.sortedByDescending { it.lastMessageDate }
    }

    private fun normalizePhoneNumber(phoneNumber: String): String {
        // Remove common formatting and keep only digits
        return phoneNumber.replace(Regex("[^0-9]"), "").let { digits ->
            // If US number with country code, normalize
            when {
                digits.length == 11 && digits.startsWith("1") -> digits.substring(1)
                digits.length == 10 -> digits
                else -> phoneNumber // Keep original if can't normalize
            }
        }
    }

    private fun getStartDateMillis(daysLookback: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -daysLookback)
        return calendar.timeInMillis
    }
}