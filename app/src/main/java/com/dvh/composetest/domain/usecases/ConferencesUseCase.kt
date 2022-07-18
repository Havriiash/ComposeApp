package com.dvh.composetest.domain.usecases

import com.dvh.composetest.domain.entities.ConferenceUi

interface ConferencesUseCase {
    suspend fun fetchConferences(): List<ConferenceUi>
}