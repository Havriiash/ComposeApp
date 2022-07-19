package com.dvh.composetest.domain.entities

import com.google.gson.annotations.SerializedName

data class ConferenceNetwork(
    @SerializedName("id")
    val id: String?,
    @SerializedName("date")
    val date: String?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("imageUrl")
    val imageUrl: String?,
    @SerializedName("isFavorite")
    val isFavorite: Boolean?,
    @SerializedName("speaker")
    val speaker: String?,
    @SerializedName("timeInterval")
    val timeInterval: String
)

fun ConferenceNetwork?.toUi() = ConferenceUi(
    id = this?.id.orEmpty(),
    date = this?.date.orEmpty(),
    description = this?.description.orEmpty(),
    imageUrl = this?.imageUrl.orEmpty(),
    isFavorite = this?.isFavorite ?: false,
    speaker = this?.speaker.orEmpty(),
    timeInterval = this?.timeInterval.orEmpty()
)