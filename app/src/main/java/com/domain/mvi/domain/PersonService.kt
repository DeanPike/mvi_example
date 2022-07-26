package com.domain.mvi.domain

import com.domain.mvi.common.Person
import com.domain.mvi.data.PersonRepository
import java.util.*
import javax.inject.Inject

interface PersonService {
    suspend fun getPersonById(id: String): Person
    suspend fun deletePersonById(id: String)
    suspend fun addPerson(person: Person)
    suspend fun updatePerson(person: Person)
    suspend fun getPeople(): List<Person>
}

class PersonServiceImpl @Inject constructor(private val repo: PersonRepository) : PersonService {
    override suspend fun getPersonById(id: String): Person {
        return repo.getPersonById(id)
    }

    override suspend fun deletePersonById(id: String) {
        repo.deletePersonById(id)
    }

    override suspend fun addPerson(person: Person) {
        repo.addPerson(
            person.copy(
                id = UUID.randomUUID().toString()
            )
        )
    }

    override suspend fun updatePerson(person: Person) {
        repo.updatePerson(person)
    }

    override suspend fun getPeople(): List<Person> {
        return repo.getPeople()
    }
}