package com.giftideaminder.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.giftideaminder.data.model.Person
import java.text.SimpleDateFormat
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
            val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            person.birthday?.let { birthdayMillis ->
                Text("Birthday: ${dateFormat.format(Date(birthdayMillis))}")
                val upcoming = calculateNextBirthday(birthdayMillis)
                Text("Upcoming: ${dateFormat.format(Date(upcoming))}")
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

private fun calculateNextBirthday(birthdayMillis: Long): Long {
    val birthCal = Calendar.getInstance().apply { timeInMillis = birthdayMillis }
    val currentCal = Calendar.getInstance()
    val nextCal = Calendar.getInstance().apply {
        set(Calendar.YEAR, currentCal.get(Calendar.YEAR))
        set(Calendar.MONTH, birthCal.get(Calendar.MONTH))
        set(Calendar.DAY_OF_MONTH, birthCal.get(Calendar.DAY_OF_MONTH))
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    if (nextCal.timeInMillis < currentCal.timeInMillis) {
        nextCal.add(Calendar.YEAR, 1)
    }
    return nextCal.timeInMillis
} 