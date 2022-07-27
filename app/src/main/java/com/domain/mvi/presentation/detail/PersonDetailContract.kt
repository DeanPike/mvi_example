package com.domain.mvi.presentation.detail

import com.domain.mvi.common.Person
import com.domain.mvi.presentation.base.UiEffect
import com.domain.mvi.presentation.base.UiEvent
import com.domain.mvi.presentation.base.UiState

class PersonDetailContract {
    data class PersonDetailState(
        val state: ScreenState = ScreenState.LOADING,
        val person: Person? = null,
        val isValidName: Boolean = true,
        val isValidSurname: Boolean = true,
        val isValidAge: Boolean = true
    ) : UiState

    sealed class PersonDetailEvent : UiEvent {
        data class LoadPersonEvent(val id: String) : PersonDetailEvent()
        data class UpdatePersonEvent(val person: Person) : PersonDetailEvent()
        object DeletePersonEvent : PersonDetailEvent()
        object CreateNewPerson : PersonDetailEvent()
    }

    sealed class PersonDetailEffect : UiEffect {
        object PersonDeletedEffect : PersonDetailEffect()
        object PersonUpdatedEffect : PersonDetailEffect()
    }

    enum class ScreenState {
        LOADING,
        LOADED,
        ERROR,
        NEW,
        UPDATING,
        UPDATED
    }
}