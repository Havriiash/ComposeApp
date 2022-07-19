package com.dvh.composetest.ui.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.dvh.composetest.domain.entities.ConferenceUi

class SharedViewModel : ViewModel() {

    var conference = mutableStateOf<ConferenceUi?>(null)
        private set

    fun setConference(conference: ConferenceUi) {
        this.conference.value = conference
    }

}