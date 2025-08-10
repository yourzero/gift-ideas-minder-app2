package com.threekidsinatrenchcoat.giftideaminder.viewmodel

import android.content.Context
import android.provider.Telephony
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.threekidsinatrenchcoat.giftideaminder.data.model.Gift
import com.threekidsinatrenchcoat.giftideaminder.data.repository.GiftRepository
import com.threekidsinatrenchcoat.giftideaminder.data.api.AIService
import com.threekidsinatrenchcoat.giftideaminder.data.api.MessagePayload
import com.threekidsinatrenchcoat.giftideaminder.data.api.PersonHint
import com.threekidsinatrenchcoat.giftideaminder.data.api.SummarizeMessagesRequest
import com.threekidsinatrenchcoat.giftideaminder.data.repository.PersonRepository
import com.opencsv.CSVReader
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import java.io.File
import java.io.StringReader
import javax.inject.Inject

@HiltViewModel
class ImportViewModel @Inject constructor(
    private val giftRepository: GiftRepository,
    private val personRepository: PersonRepository,
    private val aiService: AIService,
    @ApplicationContext private val context: Context
) : ViewModel() {

    fun parseOcrTextToGifts(text: String) {
        viewModelScope.launch {
            // Simple parsing: assume lines are title;description;url;price
            text.lines().forEach { line ->
                val parts = line.split(";")
                if (parts.size >= 1) {
                    val gift = Gift(
                        title = parts[0],
                        description = parts.getOrNull(1),
                        url = parts.getOrNull(2),
                        currentPrice = parts.getOrNull(3)?.toDoubleOrNull()
                    )
                    giftRepository.insert(gift)
                }
            }
        }
    }

    fun importFromCsv(csvText: String) {
        viewModelScope.launch {
            CSVReader(StringReader(csvText)).use { reader ->
                reader.readAll().forEach { row ->
                    if (row.size >= 1) {
                        val gift = Gift(
                            title = row[0],
                            description = row.getOrNull(1),
                            url = row.getOrNull(2),
                            currentPrice = row.getOrNull(3)?.toDoubleOrNull()
                        )
                        giftRepository.insert(gift)
                    }
                }
            }
        }
    }

    fun extractFromSms() {
        viewModelScope.launch {
            val cursor = context.contentResolver.query(
                Telephony.Sms.CONTENT_URI,
                null, null, null, null
            )
            cursor?.use {
                while (it.moveToNext()) {
                    val body = it.getString(it.getColumnIndexOrThrow(Telephony.Sms.BODY))
                    if (body.contains("gift", ignoreCase = true)) {
                        val gift = Gift(title = "From SMS", description = body)
                        giftRepository.insert(gift)
                    }
                }
            }
        }
    }

    fun summarizeSmsToInsights(personNames: List<String>) {
        viewModelScope.launch {
            val messages = mutableListOf<MessagePayload>()
            val cursor = context.contentResolver.query(
                Telephony.Sms.CONTENT_URI,
                arrayOf(Telephony.Sms.BODY, Telephony.Sms.DATE), null, null, null
            )
            cursor?.use {
                while (it.moveToNext()) {
                    val body = it.getString(it.getColumnIndexOrThrow(Telephony.Sms.BODY))
                    val ts = it.getLong(it.getColumnIndexOrThrow(Telephony.Sms.DATE))
                    messages.add(MessagePayload(text = body, timestamp = ts))
                }
            }
            val hints = personNames.map { PersonHint(name = it) }
            try {
                val resp = aiService.summarizeMessages(
                    SummarizeMessagesRequest(messages = messages, persons = hints)
                )
                // Simple persistence: append insights to matching person's notes
                val people = personRepository.allPersons.first()
                people.forEach { person ->
                    val match = resp.insights.firstOrNull { it.name.equals(person.name, ignoreCase = true) }
                    if (match != null) {
                        val sb = StringBuilder(person.notes.orEmpty())
                        if (sb.isNotEmpty()) sb.append('\n')
                        if (match.interests.isNotEmpty()) sb.append("Interests: ").append(match.interests.joinToString()).append('\n')
                        if (match.avoid.isNotEmpty()) sb.append("Avoid: ").append(match.avoid.joinToString()).append('\n')
                        match.sizes?.let { sb.append("Sizes: ").append(it).append('\n') }
                        match.notes?.let { sb.append(it).append('\n') }
                        if (match.specialDates.isNotEmpty()) sb.append("Dates: ").append(match.specialDates.joinToString()).append('\n')
                        personRepository.update(person.copy(notes = sb.toString().trim()))
                    }
                }
            } catch (_: Throwable) {
                // Swallow for now; surface errors to UI if needed
            }
        }
    }

    fun summarizeSmsToInsightsFromPersons() {
        viewModelScope.launch {
            // Load person names from repo and call summarize
            val names = personRepository.allPersons.first().map { it.name }
            summarizeSmsToInsights(names)
        }
    }
}