package com.dvh.composetest.domain.repositories

import com.dvh.composetest.domain.entities.ConferenceNetwork

interface ConferenceRepository {
    suspend fun fetchConferences(): List<ConferenceNetwork>
}