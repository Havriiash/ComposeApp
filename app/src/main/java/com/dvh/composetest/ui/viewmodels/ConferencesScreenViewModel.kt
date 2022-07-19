package com.dvh.composetest.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dvh.composetest.data.di.NetworkModule
import com.dvh.composetest.data.repositories.ConferencesRepositoryImpl
import com.dvh.composetest.data.usecases.ConferencesUseCaseImpl
import com.dvh.composetest.domain.entities.ConferenceUi
import com.dvh.composetest.domain.shared.Resource
import com.dvh.composetest.domain.usecases.ConferencesUseCase
import kotlinx.coroutines.*

class ConferencesScreenViewModel : ViewModel() {

    private val useCase: ConferencesUseCase = ConferencesUseCaseImpl(
        ConferencesRepositoryImpl(NetworkModule.api)
    )


    private val _data = MutableLiveData<Resource<List<ConferenceUi>>>()
    val data: LiveData<Resource<List<ConferenceUi>>> get() = _data

    private val _favorites = MutableLiveData<List<ConferenceUi>>()
    val favorites: LiveData<List<ConferenceUi>> get() = _favorites

    private val originalSessions = arrayListOf<ConferenceUi>()
    private var searchJob: Job? = null


    fun fetchItems() {
        viewModelScope.launch {
            _data.value = Resource.Loading()
            try {
                val conferences = withContext(Dispatchers.IO) {
                    useCase.fetchConferences()
                }
                originalSessions.clear()
                originalSessions.addAll(conferences)
                _data.value = Resource.Success(conferences)
            } catch (ex: Exception) {
                _data.value = Resource.Error(ex)
            }
        }
    }

    fun toggleFavorites(item: ConferenceUi): Boolean {
        val list = _favorites.value?.toMutableList() ?: arrayListOf()
        if (list.contains(item)) {
            list.remove(item)
        } else {
            if (list.size < 3) {
                list.add(item)
            } else {
                return false
            }
        }
        _favorites.value = list
        return true
    }


    fun search(request: String) {
        if (request.isEmpty()) {
            _data.value = Resource.Success(originalSessions)
            return
        }

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            withContext(Dispatchers.IO) {
                delay(300)
            }
            try {
                _data.value = Resource.Loading()
                val filtered = withContext(Dispatchers.IO) {
                    originalSessions.filter {
                        it.description.lowercase().contains(request)
                                || it.speaker.lowercase().contains(request)
                    }
                }
                _data.value = Resource.Success(filtered)
            } catch (ex: Exception) {
                _data.value = Resource.Error(ex)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        searchJob?.cancel()
    }
}