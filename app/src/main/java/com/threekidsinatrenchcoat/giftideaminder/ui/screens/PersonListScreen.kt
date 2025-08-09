package com.threekidsinatrenchcoat.giftideaminder.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.threekidsinatrenchcoat.giftideaminder.data.model.Person
import com.threekidsinatrenchcoat.giftideaminder.ui.components.PersonItem
import com.threekidsinatrenchcoat.giftideaminder.viewmodel.PersonViewModel
import androidx.compose.ui.tooling.preview.Preview
import java.time.LocalDate
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp


@Preview
@Composable
fun PersonListScreen(navController: NavController) {
    val viewModel: PersonViewModel = hiltViewModel()
    val persons by viewModel.allPersons.collectAsState(initial = emptyList())

    var showDeleteDialog by remember { mutableStateOf(false) }
        CenterAlignedTopAppBar(
            modifier = Modifier.height(48.dp), // shorter bar
            title = {
                Box(
                    modifier = Modifier.height(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Recipients",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary // or your alternate color
            )
//            title = { Text("Recipients", style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center) },
//            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
//                containerColor = MaterialTheme.colorScheme.primaryContainer,
//                titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
//            ),
//                    modifier = Modifier.height(48.dp)
        )
    var personToDelete by remember { mutableStateOf<Person?>(null) }

    val sortedPersons = persons.sortedBy { person ->
        person.birthday?.let { calculateNextBirthday(it).toEpochDay() } ?: Long.MAX_VALUE
    }

    LazyColumn {
        item {
            Button(onClick = { navController.navigate("add_person") }) {
                Text("Add New Person")
            }
        }
        items(sortedPersons) { person ->
            PersonItem(
                person = person,
                onEdit = { navController.navigate("edit_person/${person.id}") },
                onDelete = {
                    personToDelete = person
                    showDeleteDialog = true
                }
            )
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirm Delete") },
            text = { Text("Are you sure you want to delete ${personToDelete?.name}?") },
            confirmButton = {
                Button(onClick = {
                    personToDelete?.let { viewModel.deletePerson(it) }
                    showDeleteDialog = false
                }) {
                    Text("Delete")
                }
            },
            dismissButton = {
                Button(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
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