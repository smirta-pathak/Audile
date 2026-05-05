package com.mrsep.musicrecognizer.core.data.concerts

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TicketmasterResponse(
    @SerialName("_embedded") val embedded: EmbeddedDto? = null,
)

@Serializable
data class EmbeddedDto(
    @SerialName("events") val events: List<EventDto> = emptyList(),
)

@Serializable
data class EventDto(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("dates") val dates: DatesDto? = null,
    @SerialName("_embedded") val embedded: EventEmbeddedDto? = null,
    @SerialName("images") val images: List<ImageDto> = emptyList(),
    @SerialName("url") val url: String = "",
)

@Serializable
data class DatesDto(
    @SerialName("start") val start: StartDateDto? = null,
)

@Serializable
data class StartDateDto(
    @SerialName("localDate") val localDate: String? = null,
    @SerialName("localTime") val localTime: String? = null,
)

@Serializable
data class EventEmbeddedDto(
    @SerialName("venues") val venues: List<VenueDto> = emptyList(),
)

@Serializable
data class VenueDto(
    @SerialName("name") val name: String = "",
    @SerialName("city") val city: CityDto? = null,
    @SerialName("country") val country: CountryDto? = null,
)

@Serializable
data class CityDto(
    @SerialName("name") val name: String = "",
)

@Serializable
data class CountryDto(
    @SerialName("name") val name: String = "",
)

@Serializable
data class ImageDto(
    @SerialName("url") val url: String = "",
    @SerialName("width") val width: Int = 0,
    @SerialName("height") val height: Int = 0,
)
