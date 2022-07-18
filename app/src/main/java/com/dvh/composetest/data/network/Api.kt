package com.dvh.composetest.data.network

import com.dvh.composetest.domain.entities.ConferenceNetwork
import retrofit2.Response
import retrofit2.http.GET

interface Api {

    @GET("/AJIEKCX/901e7ae9593e4afd136abe10ca7d510f/raw/61e7c1f037345370cf28b5ae6fdaffdd9e7e18d5/Sessions.json")
    suspend fun fetchConferences(): Response<List<ConferenceNetwork>>

}