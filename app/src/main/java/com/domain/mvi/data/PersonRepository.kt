package com.domain.mvi.data

import com.domain.mvi.common.Person
import com.domain.mvi.util.DispatcherProvider
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

interface PersonRepository {
    suspend fun getPersonById(id: String): Person
    suspend fun deletePersonById(id: String)
    suspend fun addPerson(person: Person)
    suspend fun updatePerson(person: Person)
    suspend fun getPeople(): List<Person>
}

class PersonRepositoryImpl @Inject constructor(private val dispatcherProvider: DispatcherProvider) :
    PersonRepository {
    private val currentPeople = mutableListOf<Person>()

    init {
        Thread.sleep(1000)
        loadList()
    }

    override suspend fun getPersonById(id: String): Person {
        return withContext(dispatcherProvider.getIoDispatcher()) {
            Thread.sleep(1000)
            currentPeople.first {
                it.id == id
            }
        }
    }

    override suspend fun deletePersonById(id: String) {
        return withContext(dispatcherProvider.getIoDispatcher()) {
            Thread.sleep(1000)
            val person = currentPeople.first {
                it.id == id
            }
            currentPeople.remove(person)
        }
    }

    override suspend fun addPerson(person: Person) {
        return withContext(dispatcherProvider.getIoDispatcher()) {
            Thread.sleep(1000)
            currentPeople.add(person)
        }
    }

    override suspend fun updatePerson(person: Person) {
        return withContext(dispatcherProvider.getIoDispatcher()) {
            Thread.sleep(1000)
            val pos = currentPeople.indexOfFirst {
                it.id == person.id
            }
            currentPeople[pos] = person
        }
    }

    override suspend fun getPeople(): List<Person> = currentPeople

    private fun loadList() {
        currentPeople.add(
            Person(
                id = UUID.randomUUID().toString(),
                name = "Bob",
                surname = "Smith",
                age = 30
            )
        )
        currentPeople.add(
            Person(
                id = UUID.randomUUID().toString(),
                name = "Susan",
                surname = "Wells",
                age = 28
            )
        )
        currentPeople.add(
            Person(
                id = UUID.randomUUID().toString(),
                name = "Gary",
                surname = "Spencer",
                age = 34
            )
        )
    }
}