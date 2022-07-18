package com.dvh.composetest.data.usecases

import com.dvh.composetest.domain.entities.ConferenceUi
import com.dvh.composetest.domain.entities.toUi
import com.dvh.composetest.domain.repositories.ConferenceRepository
import com.dvh.composetest.domain.usecases.ConferencesUseCase

class ConferencesUseCaseImpl(
    private val repository: ConferenceRepository
) : ConferencesUseCase {

    override suspend fun fetchConferences(): List<ConferenceUi> {
        return repository.fetchConferences().map { it.toUi() }.sortedBy { it.date }
    }

}