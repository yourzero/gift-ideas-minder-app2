@file:OptIn(ExperimentalMaterial3Api::class)

package com.giftideaminder.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.text.DateFormatSymbols
import java.util.*

@Preview
@Composable
fun HomeDashboard() {
    val calendar = Calendar.getInstance()
    val todayDay = calendar.get(Calendar.DAY_OF_MONTH)
    val currentMonth = calendar.get(Calendar.MONTH)
    val currentYear = calendar.get(Calendar.YEAR)

    // Set to first day of month
    calendar.set(Calendar.DAY_OF_MONTH, 1)
    val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1 // Sunday=0, Monday=1, ..., Saturday=6
    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

    // Month name
    val monthName = DateFormatSymbols.getInstance().months[currentMonth]

    Scaffold(
        topBar = { TopAppBar(title = { Text("Gift Idea Minder - Calendar") }) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            Text(
                text = "$monthName $currentYear",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(16.dp)
            )

            // Days of the week
            val weekdays = DateFormatSymbols.getInstance().shortWeekdays
            LazyVerticalGrid(columns = GridCells.Fixed(7), modifier = Modifier.fillMaxWidth()) {
                items(7) { day ->
                    // weekdays[1]=Sun, [2]=Mon, ..., [7]=Sat; map to Sun=0, Mon=1,...
                    val weekdayIndex = (day + 1) % 7 + 1 // Start from Sun
                    Text(
                        text = weekdays[weekdayIndex],
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }

            // Calendar days
            LazyVerticalGrid(columns = GridCells.Fixed(7), modifier = Modifier.fillMaxWidth()) {
                // Blank cells for offset
                items(firstDayOfWeek) {
                    Box(modifier = Modifier.padding(8.dp))
                }

                // Actual days
                items(daysInMonth) { day ->
                    val isToday = (day + 1 == todayDay && calendar.get(Calendar.MONTH) == currentMonth)
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .padding(8.dp)
                            .background(if (isToday) MaterialTheme.colorScheme.primary.copy(0.2f) else MaterialTheme.colorScheme.surface)
                    ) {
                        Text(text = (day + 1).toString(), textAlign = TextAlign.Center)
                    }
                }
            }
        }
    }
}