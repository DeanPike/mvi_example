package com.domain.mvi.presentation.list

import androidx.lifecycle.viewModelScope
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
class PersonListViewModel @Inject constructor(
    private val service: PersonService,
    private val dispatcherProvider: DispatcherProvider
) :
    BaseViewModel<PersonListContract.PersonListEvent, PersonListContract.PersonListState, PersonListContract.PersonListEffect>() {

    var job: Job? = null

    override fun createInitialState() = PersonListContract.PersonListState()

    override fun handleEvent(event: PersonListContract.PersonListEvent) {
        when (event) {
            is PersonListContract.PersonListEvent.LoadPeopleEvent -> {
                loadPeople(event.isRefreshing)
            }
            is PersonListContract.PersonListEvent.PersonSelectedEvent -> {
                showPerson(event.id)
            }
        }
    }

    private fun loadPeople(isRefreshing: Boolean) {
        cancelJob()

        job = viewModelScope.launch(dispatcherProvider.getMainDispatcher()) {
            setState {
                copy(
                    state = if (isRefreshing) PersonListContract.ScreenState.REFRESHING else PersonListContract.ScreenState.LOADING,
                )
            }

            val response = service.getPeople()

            setState {
                copy(
                    state = PersonListContract.ScreenState.LOADED,
                    people = response,
                )
            }
        }
    }

    private fun showPerson(id: String) {
        // record tracking
        setEffect {
            PersonListContract.PersonListEffect.ShowPersonEffect(id)
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