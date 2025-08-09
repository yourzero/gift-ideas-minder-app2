package com.giftideaminder.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.giftideaminder.data.model.Person
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
@Preview
@Composable
fun PersonItem(
    person: Person,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                text = person.name,
                style = MaterialTheme.typography.titleMedium
            )
            val dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
            person.birthday?.let { birthday ->
                Text("Birthday: ${birthday.format(dateFormatter)}")
                val upcoming = calculateNextBirthday(birthday)
                Text("Upcoming: ${upcoming.format(dateFormatter)}")
            } ?: Text("No birthday set")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Button(onClick = onEdit) {
                    Text("Edit")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = onDelete) {
                    Text("Delete")
                }
            }
        }
    }
}

private fun calculateNextBirthday(birthday: LocalDate): LocalDate {
    val today = LocalDate.now()
    val thisYearBirthday = birthday.withYear(today.year)
    return if (thisYearBirthday.isBefore(today) || thisYearBirthday.isEqual(today)) {
        thisYearBirthday.plusYears(1)
    } else {
        thisYearBirthday
    }
} 