package com.giftideaminder.ui.screens

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.giftideaminder.data.model.Person
import com.giftideaminder.ui.components.PersonItem
import com.giftideaminder.viewmodel.PersonViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun PersonListScreen(navController: NavController) {
    val viewModel: PersonViewModel = hiltViewModel()
    val persons by viewModel.allPersons.collectAsState(initial = emptyList())

    var showDeleteDialog by remember { mutableStateOf(false) }
    var personToDelete by remember { mutableStateOf<Person?>(null) }

    val sortedPersons = persons.sortedBy { calculateNextBirthday(it.birthday) ?: Long.MAX_VALUE }

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

private fun calculateNextBirthday(birthday: Long?): Long? {
    if (birthday == null) return null
    val birthCal = java.util.Calendar.getInstance().apply { timeInMillis = birthday }
    val currentCal = java.util.Calendar.getInstance()
    val nextCal = java.util.Calendar.getInstance().apply {
        set(java.util.Calendar.YEAR, currentCal.get(java.util.Calendar.YEAR))
        set(java.util.Calendar.MONTH, birthCal.get(java.util.Calendar.MONTH))
        set(java.util.Calendar.DAY_OF_MONTH, birthCal.get(java.util.Calendar.DAY_OF_MONTH))
    }
    if (nextCal.before(currentCal)) {
        nextCal.add(java.util.Calendar.YEAR, 1)
    }
    return nextCal.timeInMillis
} 