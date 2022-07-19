package com.dvh.composetest.domain.shared

sealed class Resource<T>(
    val data: T?,
    val ex: Throwable?
) {
    data class Loading<T>(val loadingData: T? = null) : Resource<T>(loadingData, null)
    data class Success<T>(val d: T) : Resource<T>(d, null)
    data class Error<T>(val error: Throwable?) : Resource<T>(null, error)
}