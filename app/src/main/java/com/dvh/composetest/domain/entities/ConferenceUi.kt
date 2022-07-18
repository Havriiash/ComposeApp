package com.dvh.composetest.domain.entities

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ConferenceUi(
    val id: String,
    val date: String,
    val description: String,
    val imageUrl: String,
    val isFavorite: Boolean,
    val speaker: String,
    val timeInterval: String
): Parcelable