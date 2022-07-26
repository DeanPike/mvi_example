package com.domain.mvi.presentation.list

import com.domain.mvi.common.Person
import com.domain.mvi.presentation.base.UiEffect
import com.domain.mvi.presentation.base.UiEvent
import com.domain.mvi.presentation.base.UiState

class PersonListContract {
    data class PersonListState(
        val state: ScreenState = ScreenState.LOADING,
        val people: List<Person> = mutableListOf()
    ) : UiState

    sealed class PersonListEvent : UiEvent {
        data class LoadPeopleEvent(val isRefreshing: Boolean) : PersonListEvent()
        data class PersonSelectedEvent(val id: String) : PersonListEvent()
    }

    sealed class PersonListEffect : UiEffect {
        data class ShowPersonEffect(val id: String) : PersonListEffect()
    }

    enum class ScreenState {
        LOADING,
        REFRESHING,
        LOADED,
        ERROR
    }
}