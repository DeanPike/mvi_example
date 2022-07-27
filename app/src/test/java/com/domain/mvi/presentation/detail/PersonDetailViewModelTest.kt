package com.domain.mvi.presentation.detail

import app.cash.turbine.test
import com.domain.mvi.common.Person
import com.domain.mvi.domain.PersonService
import com.domain.mvi.presentation.list.TestBase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

@ExperimentalCoroutinesApi
class PersonDetailViewModelTest : TestBase() {

    private val service: PersonService = mockk()
    private lateinit var viewModel: PersonDetailViewModel
    private val personId = "person_1"
    private val expectedPerson = Person(
        id = personId,
        name = "Person",
        surname = "Surname",
        age = 34
    )

    @Test
    fun `should have initial state`() = runTest {
        setupTest(testScheduler)
        with(viewModel.uiState.value) {
            assertThat(state).isEqualTo(PersonDetailContract.ScreenState.LOADING)
            assertThat(person).isNull()
            assertThat(isValidName).isTrue()
        }
    }

    @Test
    fun `should handle LoadPersonEvent`() = runTest {
        setupTest(testScheduler)
        coEvery { service.getPersonById(personId) } returns expectedPerson

        viewModel.setEvent(PersonDetailContract.PersonDetailEvent.LoadPersonEvent(personId))

        viewModel.uiState.test {
            awaitItem().apply {
                assertThat(state).isEqualTo(PersonDetailContract.ScreenState.LOADING)
            }
            awaitItem().apply {
                assertThat(state).isEqualTo(PersonDetailContract.ScreenState.LOADED)
                assertThat(person).isEqualTo(expectedPerson)
            }
        }

        coVerify { service.getPersonById(personId) }
    }

    @Test
    fun `should handle UpdatePersonEvent`() = runTest {
        setupTest(testScheduler)
        coEvery { service.updatePerson(expectedPerson) } just runs

        viewModel.setEvent(PersonDetailContract.PersonDetailEvent.UpdatePersonEvent(expectedPerson))

        viewModel.uiState.test {
            skipItems(1)
            assertThat(awaitItem().state).isEqualTo(PersonDetailContract.ScreenState.UPDATING)
            awaitItem().apply {
                assertThat(isValidName).isTrue
                assertThat(isValidSurname).isTrue
                assertThat(isValidAge).isTrue
                assertThat(state).isEqualTo(PersonDetailContract.ScreenState.UPDATED)
            }
        }

        viewModel.effect.test {
            assertThat(awaitItem()).isInstanceOf(PersonDetailContract.PersonDetailEffect.PersonUpdatedEffect::class.java)
        }

        coVerify { service.updatePerson(expectedPerson) }
        coVerify(exactly = 0) { service.addPerson(any())  }
    }

    @Test
    fun `should fail person update with invalid name`() = runTest{
        setupTest(testScheduler)

        viewModel.setEvent(PersonDetailContract.PersonDetailEvent.UpdatePersonEvent(expectedPerson.copy(
            age = 0
        )))

        viewModel.uiState.test {
            skipItems(1)
            awaitItem().apply {
                assertThat(isValidName).isTrue
                assertThat(isValidSurname).isTrue
                assertThat(isValidAge).isFalse
                assertThat(state).isEqualTo(PersonDetailContract.ScreenState.ERROR)
            }
        }

        coVerify(exactly = 0) { service.updatePerson(any()) }
        coVerify(exactly = 0) { service.addPerson(any())  }
    }

    override fun setupTest(testScheduler: TestCoroutineScheduler) {
        setupDispatcherProvider(testScheduler)
        viewModel = PersonDetailViewModel(service, dispatcherProvider)
    }
}