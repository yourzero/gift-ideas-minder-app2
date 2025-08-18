package com.threekidsinatrenchcoat.giftideaminder.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.threekidsinatrenchcoat.giftideaminder.data.model.Person
import com.threekidsinatrenchcoat.giftideaminder.ui.components.PersonItem
import com.threekidsinatrenchcoat.giftideaminder.viewmodel.PersonViewModel
import java.time.LocalDate
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import com.threekidsinatrenchcoat.giftideaminder.ui.components.AppTopBar

@Composable
fun PersonListScreen(
    navController: NavController,
    viewModel: PersonViewModel = hiltViewModel()
) {
    val persons by viewModel.allPersons.collectAsState(initial = emptyList())

    var showDeleteDialog by remember { mutableStateOf(false) }
    var personToDelete by remember { mutableStateOf<Person?>(null) }

    val sortedPersons = persons.sortedBy { person ->
        person.birthday?.let { calculateNextBirthday(it).toEpochDay() } ?: Long.MAX_VALUE
    }

    Scaffold(
        topBar = { AppTopBar("Recipients") }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            LazyColumn {
                items(sortedPersons) { person ->
                    PersonItem(
                        person = person,
                        onEdit = { navController.navigate("edit_person/${person.id}") },
                        onDelete = {
                            personToDelete = person
                            showDeleteDialog = true
                        },
                        onIdeas = { navController.navigate("person_ideas/${person.id}") },
                        onInterests = { navController.navigate("person_detail/${person.id}") }
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
