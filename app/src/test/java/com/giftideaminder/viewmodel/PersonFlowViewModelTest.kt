package com.threekidsinatrenchcoat.giftideaminder.viewmodel

import com.threekidsinatrenchcoat.giftideaminder.data.model.ImportantDate
import com.threekidsinatrenchcoat.giftideaminder.data.model.Person
import com.threekidsinatrenchcoat.giftideaminder.data.model.RelationshipType
import com.threekidsinatrenchcoat.giftideaminder.data.repository.ImportantDateRepository
import com.threekidsinatrenchcoat.giftideaminder.data.repository.PersonRepository
import com.threekidsinatrenchcoat.giftideaminder.data.repository.RelationshipTypeRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class PersonFlowViewModelTest {

    private val personRepo: PersonRepository = mockk(relaxed = true)
    private val dateRepo: ImportantDateRepository = mockk(relaxed = true)
    private val relRepo: RelationshipTypeRepository = mockk(relaxed = true)

    private val relationshipTypesFlow = MutableSharedFlow<List<RelationshipType>>(replay = 1)
    private val existingDatesFlow = MutableSharedFlow<List<ImportantDate>>(replay = 1)

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { relRepo.getAll() } returns relationshipTypesFlow
        coEvery { personRepo.getPersonByIdSuspend(any()) } returns null
        every { dateRepo.getForPerson(any()) } returns (existingDatesFlow as Flow<List<ImportantDate>>)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun onDatePicked_and_onRemoveDate_updatesState() = runTest(testDispatcher) {
        val vm = PersonFlowViewModel(personRepo, dateRepo, relRepo, savedStateHandle = androidx.lifecycle.SavedStateHandle())

        val date = LocalDate.of(2024, 12, 25)
        vm.onDatePicked("Birthday", date)
        assertEquals(date, vm.uiState.value.pickedDates["Birthday"])

        vm.onRemoveDate("Birthday")
        assertTrue(vm.uiState.value.pickedDates.isEmpty())
    }

    @Test
    fun promptDerivation_usesRelationshipFlags_whenAvailable() = runTest(testDispatcher) {
        relationshipTypesFlow.emit(
            listOf(
                RelationshipType(name = "Spouse", hasBirthday = true, hasAnniversary = true),
                RelationshipType(name = "Friend", hasBirthday = true, hasAnniversary = false)
            )
        )
        val vm = PersonFlowViewModel(personRepo, dateRepo, relRepo, savedStateHandle = androidx.lifecycle.SavedStateHandle())

        vm.onRelationshipSelected("Spouse")
        vm.onNextOrSave() // to Details and set prompts
        vm.onNextOrSave() // to Dates
        assertEquals(listOf("Birthday", "Anniversary"), vm.uiState.value.datePrompts)

        // Change to Friend and recompute
        vm.onBack() // back to Details
        vm.onBack() // back to Relationship
        vm.onRelationshipSelected("Friend")
        vm.onNextOrSave()
        vm.onNextOrSave()
        assertEquals(listOf("Birthday"), vm.uiState.value.datePrompts)
    }

    @Test
    fun persistPersonAndDates_insertsAndReplacesDates_onSave() = runTest(testDispatcher) {
        // Arrange repos
        coEvery { personRepo.insert(any<Person>()) } returns 123
        relationshipTypesFlow.emit(emptyList())
        existingDatesFlow.emit(emptyList())

        val vm = PersonFlowViewModel(personRepo, dateRepo, relRepo, savedStateHandle = androidx.lifecycle.SavedStateHandle())

        vm.onRelationshipSelected("Friend")
        vm.onNextOrSave() // to Details
        vm.onNameChange("Alex")
        vm.onNextOrSave() // to Dates
        val date = LocalDate.of(2025, 1, 1)
        vm.onDatePicked("Birthday", date)
        vm.onNextOrSave() // to Preferences
        vm.onNextOrSave() // to Review
        val result2 = vm.onNextOrSave() // triggers save
        assertTrue(result2.saved)

        coVerify { personRepo.insert(any()) }
        coVerify { dateRepo.replaceForPerson(123, listOf(ImportantDate(personId = 123, label = "Birthday", date = date))) }
    }
}