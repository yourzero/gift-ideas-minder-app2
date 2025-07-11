package com.giftideaminder.viewmodel

import android.content.Context
import android.provider.Telephony
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.giftideaminder.data.model.Gift
import com.giftideaminder.data.repository.GiftRepository
import com.opencsv.CSVReader
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import java.io.File
import java.io.StringReader
import javax.inject.Inject

@HiltViewModel
class ImportViewModel @Inject constructor(
    private val giftRepository: GiftRepository,
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
                        price = parts.getOrNull(3)?.toDoubleOrNull()
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
                            price = row.getOrNull(3)?.toDoubleOrNull()
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
                        // Simple extraction: use body as description
                        val gift = Gift(title = "From SMS", description = body)
                        giftRepository.insert(gift)
                    }
                }
            }
        }
    }
} 