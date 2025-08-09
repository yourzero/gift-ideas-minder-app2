package com.giftideaminder.data.repository

import com.giftideaminder.data.model.Person
import com.giftideaminder.data.model.PersonRole

/**
 * Sample queries demonstrating role-based filtering
 */
class PersonRoleQueries {
    
    companion object {
        /**
         * Sample usage of role-based queries in your repository or ViewModel
         */
        suspend fun getSampleQueries(personRepository: PersonRepository): Map<String, List<Person>> {
            return mapOf(
                "All Recipients" to personRepository.getPeopleWithRole(PersonRole.GIFTEE.bit),
                "All Gifters" to personRepository.getPeopleWithRole(PersonRole.GIFTER.bit),
                "Collaborators" to personRepository.getPeopleWithRole(PersonRole.COLLABORATOR.bit),
                "Contacts Only" to personRepository.getPeopleWithRole(PersonRole.CONTACT_ONLY.bit),
                "Self" to personRepository.getPeopleWithRole(PersonRole.SELF.bit),
                // Multiple roles using bitwise OR
                "Recipients and Gifters" to personRepository.getPeopleWithRole(
                    PersonRole.GIFTEE.bit or PersonRole.GIFTER.bit
                ),
                "Active participants (Recipients, Gifters, Collaborators)" to personRepository.getPeopleWithRole(
                    PersonRole.GIFTEE.bit or PersonRole.GIFTER.bit or PersonRole.COLLABORATOR.bit
                )
            )
        }
        
        /**
         * Helper functions for working with roles in business logic
         */
        fun isRecipient(person: Person): Boolean {
            return PersonRole.Companion.run { person.roles.hasRole(PersonRole.GIFTEE) }
        }
        
        fun isGifter(person: Person): Boolean {
            return PersonRole.Companion.run { person.roles.hasRole(PersonRole.GIFTER) }
        }
        
        fun canCollaborate(person: Person): Boolean {
            return PersonRole.Companion.run { 
                person.roles.hasRole(PersonRole.COLLABORATOR) || 
                person.roles.hasRole(PersonRole.GIFTER)
            }
        }
        
        fun addRecipientRole(person: Person): Person {
            return PersonRole.Companion.run {
                person.copy(roles = person.roles.plusRole(PersonRole.GIFTEE))
            }
        }
        
        fun removeRecipientRole(person: Person): Person {
            return PersonRole.Companion.run {
                person.copy(roles = person.roles.minusRole(PersonRole.GIFTEE))
            }
        }
        
        /**
         * Get people who can receive gifts (have GIFTEE role)
         */
        suspend fun getPotentialGiftRecipients(personRepository: PersonRepository): List<Person> {
            return personRepository.getPeopleWithRole(PersonRole.GIFTEE.bit)
        }
        
        /**
         * Get people who can give gifts (have GIFTER role)
         */
        suspend fun getPotentialGiftGivers(personRepository: PersonRepository): List<Person> {
            return personRepository.getPeopleWithRole(PersonRole.GIFTER.bit)
        }
        
        /**
         * Get people who can help plan gifts (have COLLABORATOR or GIFTER role)
         */
        suspend fun getGiftPlanners(personRepository: PersonRepository): List<Person> {
            return personRepository.getPeopleWithRole(
                PersonRole.COLLABORATOR.bit or PersonRole.GIFTER.bit
            )
        }
    }
}