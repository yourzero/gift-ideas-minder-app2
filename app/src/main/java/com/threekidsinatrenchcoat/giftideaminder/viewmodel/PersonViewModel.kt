package com.threekidsinatrenchcoat.giftideaminder.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.threekidsinatrenchcoat.giftideaminder.data.model.Interest
import com.threekidsinatrenchcoat.giftideaminder.data.model.InterestType
import com.threekidsinatrenchcoat.giftideaminder.data.model.Person
import com.threekidsinatrenchcoat.giftideaminder.data.repository.InterestRepository
import com.threekidsinatrenchcoat.giftideaminder.data.repository.PersonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PersonViewModel @Inject constructor(
    private val personRepository: PersonRepository,
    private val interestRepository: InterestRepository
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
    
    // Interest management methods
    fun getInterestsForPerson(personId: Int): Flow<List<Interest>> =
        interestRepository.getInterestsForPerson(personId)
    
    fun getInterestsByType(personId: Int, type: InterestType): Flow<List<Interest>> =
        interestRepository.getInterestsByType(personId, type)
    
    fun getAvailableInterestsForPerson(personId: Int): Flow<List<Interest>> =
        interestRepository.getAvailableInterestsForPerson(personId)
    
    fun addInterest(personId: Int, type: InterestType, value: String, alreadyOwned: Boolean = false) {
        viewModelScope.launch {
            val interest = Interest(
                personId = personId,
                type = type,
                value = value.trim(),
                alreadyOwned = alreadyOwned
            )
            interestRepository.insertInterest(interest)
        }
    }
    
    fun updateInterest(interest: Interest) {
        viewModelScope.launch {
            interestRepository.updateInterest(interest)
        }
    }
    
    fun deleteInterest(interest: Interest) {
        viewModelScope.launch {
            interestRepository.deleteInterest(interest)
        }
    }
    
    fun toggleInterestOwned(interest: Interest) {
        viewModelScope.launch {
            val updatedInterest = interest.copy(alreadyOwned = !interest.alreadyOwned)
            interestRepository.updateInterest(updatedInterest)
        }
    }
}