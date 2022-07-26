package com.domain.mvi.presentation.list

import app.cash.turbine.test
import com.domain.mvi.common.Person
import com.domain.mvi.domain.PersonService
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

@ExperimentalCoroutinesApi
class PersonListViewModelTest : TestBase() {

    private val service: PersonService = mockk()

    private lateinit var viewModel: PersonListViewModel

    @Test
    fun `should have initial state`() = runTest {
        setupTest(testScheduler)
        assertThat(viewModel.uiState.value.state).isEqualTo(PersonListContract.ScreenState.LOADING)

    }

    @Test
    fun `should handle LoadPersonEvent`() = runTest {
        setupTest(testScheduler)
        coEvery { service.getPeople() } returns getPeople()

        viewModel.setEvent(PersonListContract.PersonListEvent.LoadPeopleEvent(false))

        viewModel.uiState.test {
            assertThat(awaitItem().state).isEqualTo(PersonListContract.ScreenState.LOADING)
            awaitItem().apply {
                assertThat(state).isEqualTo(PersonListContract.ScreenState.LOADED)
                assertThat(people.size).isEqualTo(2)
                assertThat(people[0].id).isEqualTo("person_1")
                assertThat(people[1].id).isEqualTo("person_2")
            }
        }
    }

    @Test
    fun `should handle PersonSelectedEvent`() = runTest {
        setupTest(testScheduler)

        viewModel.setEvent(PersonListContract.PersonListEvent.PersonSelectedEvent("person_1"))

        viewModel.effect.test {
            assertThat(awaitItem()).isInstanceOf(PersonListContract.PersonListEffect.ShowPersonEffect::class.java)
        }
    }

    override fun setupTest(testScheduler: TestCoroutineScheduler) {
        setupDispatcherProvider(testScheduler)
        viewModel = PersonListViewModel(service, dispatcherProvider)
    }

    private fun getPeople(): List<Person> {
        val person1 = Person(
            id = "person_1",
            name = "person1",
            surname = "surname1",
            age = 11
        )
        val person2 = Person(
            id = "person_2",
            name = "person2",
            surname = "surname2",
            age = 22
        )
        return listOf(person1, person2)
    }
}