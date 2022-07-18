package com.dvh.composetest.data.repositories

import com.dvh.composetest.data.network.Api
import com.dvh.composetest.domain.entities.ConferenceNetwork
import com.dvh.composetest.domain.repositories.ConferenceRepository

class ConferencesRepositoryImpl(
    private val api: Api
) : ConferenceRepository {

    override suspend fun fetchConferences(): List<ConferenceNetwork> {
        val response = api.fetchConferences()
        if (response.isSuccessful) {
            return response.body() ?: throw RuntimeException("Response body is null")
        }

        throw RuntimeException(response.errorBody()?.string())
    }

}