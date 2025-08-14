package com.threekidsinatrenchcoat.giftideaminder.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.threekidsinatrenchcoat.giftideaminder.data.model.Person
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton

@Preview
@Composable
fun PersonItem(
    person: Person,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onIdeas: () -> Unit = {}
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
            
            if (person.birthday != null) {
                Text("Birthday: ${person.birthday.format(dateFormatter)}")
                val upcoming = calculateNextBirthday(person.birthday)
                Text("Upcoming: ${upcoming.format(dateFormatter)}")
            } else {
                Text("No birthday set", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onIdeas) {
                    Icon(
                        Icons.Filled.Lightbulb,
                        contentDescription = "Gift Ideas",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = onEdit) {
                    Icon(
                        Icons.Filled.Edit,
                        contentDescription = "Edit",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Filled.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error
                    )
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