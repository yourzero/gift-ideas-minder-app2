package com.giftideaminder.ui.screens

import android.app.DatePickerDialog
import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.giftideaminder.data.model.Person
import com.giftideaminder.viewmodel.PersonViewModel
import kotlinx.coroutines.flow.collectLatest
import java.util.Calendar
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import java.util.Locale

@Preview(showBackground = true)
@Composable
fun AddEditPersonScreenPreview() {
    // Use a dummy NavController and default/null values
    val fakeNavController = rememberNavController()
    AddEditPersonScreen(
        navController = fakeNavController,
        personId = null // or use a sample value like 1
    )
}


//@Preview
@Composable
fun AddEditPersonScreen(
    navController: NavController,
    personId: Int?
) {
    val viewModel: PersonViewModel = hiltViewModel()
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var birthday by remember { mutableLongStateOf(0L) }
    var showDatePicker by remember { mutableStateOf(false) }

    if (personId != null) {
        LaunchedEffect(personId) {
            viewModel.getPersonById(personId).collectLatest { person ->
                name = person.name
                birthday = person.birthday ?: 0L
            }
        }
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") }
        )
        Button(onClick = { showDatePicker = true }) {
            Text("Pick Birthday")
        }
        Text("Selected Birthday: ${if (birthday > 0) java.text.SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(java.util.Date(birthday)) else "Not set"}")
        Button(onClick = {
            val newPerson = Person(
                id = personId ?: 0,
                name = name,
                birthday = if (birthday > 0) birthday else null
            )
            if (personId != null) {
                viewModel.updatePerson(newPerson)
            } else {
                viewModel.insertPerson(newPerson)
            }
            navController.popBackStack()
        }) {
            Text(if (personId != null) "Update Person" else "Add Person")
        }
    }

    if (showDatePicker) {
        val calendar = Calendar.getInstance()
        if (birthday > 0) calendar.timeInMillis = birthday
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                birthday = calendar.timeInMillis
                showDatePicker = false
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
        showDatePicker = false
    }
} 