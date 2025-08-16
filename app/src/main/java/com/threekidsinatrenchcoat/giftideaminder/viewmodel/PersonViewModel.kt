package com.threekidsinatrenchcoat.giftideaminder.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.threekidsinatrenchcoat.giftideaminder.data.model.Person
import com.threekidsinatrenchcoat.giftideaminder.data.repository.PersonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PersonViewModel @Inject constructor(
    private val personRepository: PersonRepository
) : ViewModel() {

    val allPersons: Flow<List<Person>> = personRepository.allPersons

    fun insertPerson(person: Person) = viewModelScope.launch {
        personRepository.insert(person)
    }

    fun updatePerson(person: Person) = viewModelScope.launch {
        personRepository.update(person)
    }

    fun deletePerson(person: Person) = viewModelScope.launch {
        personRepository.delete(person)
    }

    fun getPersonById(id: Int): Flow<Person> = personRepository.getPersonById(id)

    suspend fun insertAndReturnId(person: Person): Int {
        return personRepository.insert(person)
    }
}