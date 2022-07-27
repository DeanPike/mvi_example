package com.domain.mvi.presentation.detail

import androidx.lifecycle.viewModelScope
import com.domain.mvi.common.Person
import com.domain.mvi.domain.PersonService
import com.domain.mvi.presentation.base.BaseViewModel
import com.domain.mvi.util.DispatcherProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class PersonDetailViewModel @Inject constructor(
    private val service: PersonService,
    private val dispatcherProvider: DispatcherProvider
) :
    BaseViewModel<PersonDetailContract.PersonDetailEvent, PersonDetailContract.PersonDetailState, PersonDetailContract.PersonDetailEffect>() {

    var job: Job? = null

    override fun createInitialState() = PersonDetailContract.PersonDetailState()

    override fun handleEvent(event: PersonDetailContract.PersonDetailEvent) {
        when (event) {
            is PersonDetailContract.PersonDetailEvent.LoadPersonEvent -> {
                loadPerson(event.id)
            }
            is PersonDetailContract.PersonDetailEvent.UpdatePersonEvent -> {
                savePerson(event.person)
            }
            is PersonDetailContract.PersonDetailEvent.DeletePersonEvent -> {
                deletePerson(uiState.value.person?.id)
            }
            is PersonDetailContract.PersonDetailEvent.CreateNewPerson -> {
                createPerson()
            }
        }
    }

    private fun loadPerson(id: String) {
        cancelJob()
        job = viewModelScope.launch(dispatcherProvider.getMainDispatcher()) {
            setState {
                copy(state = PersonDetailContract.ScreenState.LOADING)
            }
            val person = service.getPersonById(id)
            setState {
                copy(
                    state = PersonDetailContract.ScreenState.LOADED,
                    person = person
                )
            }
        }
    }

    private fun savePerson(person: Person) {
        cancelJob()
        job = viewModelScope.launch(dispatcherProvider.getMainDispatcher()) {
            if (person.name.isBlank() || person.surname.isBlank() || person.age == 0) {
                setState {
                    copy(
                        isValidName = person.name.isNotBlank(),
                        isValidSurname = person.surname.isNotBlank(),
                        isValidAge = person.age != 0,
                        state = PersonDetailContract.ScreenState.ERROR
                    )
                }
            } else {
                setState {
                    copy(
                        state = PersonDetailContract.ScreenState.UPDATING
                    )
                }
                if (person.id == null) {
                    service.addPerson(person)
                } else {
                    service.updatePerson(person)
                }
                setState {
                    copy(
                        isValidName = true,
                        isValidSurname = true,
                        isValidAge = true,
                        state = PersonDetailContract.ScreenState.UPDATED
                    )
                }
                setEffect { PersonDetailContract.PersonDetailEffect.PersonUpdatedEffect }
            }
        }
    }

    private fun deletePerson(id: String?) {
        cancelJob()
        job = viewModelScope.launch(dispatcherProvider.getMainDispatcher()) {
            if (id != null) {
                setState {
                    copy(
                        state = PersonDetailContract.ScreenState.UPDATING
                    )
                }
                service.deletePersonById(id)
                setState {
                    copy(
                        state = PersonDetailContract.ScreenState.NEW,
                        person = null
                    )
                }
                setEffect { PersonDetailContract.PersonDetailEffect.PersonDeletedEffect }
            }
        }
    }

    private fun createPerson() {
        cancelJob()

        job = viewModelScope.launch(dispatcherProvider.getMainDispatcher()) {
            setState {
                copy(
                    state = PersonDetailContract.ScreenState.NEW
                )
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        cancelJob()
    }

    private fun cancelJob() {
        job?.cancelChildren()
    }
}