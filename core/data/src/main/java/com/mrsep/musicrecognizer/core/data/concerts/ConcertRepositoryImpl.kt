package com.mrsep.musicrecognizer.core.data.concerts

import com.mrsep.musicrecognizer.core.common.di.IoDispatcher
import com.mrsep.musicrecognizer.core.domain.concerts.Concert
import com.mrsep.musicrecognizer.core.domain.concerts.ConcertRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import javax.inject.Inject

internal class ConcertRepositoryImpl @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : ConcertRepository {

    private val httpClient = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                explicitNulls = false
            })
        }
    }

    override suspend fun getConcerts(
        artist: String,
        latitude: Double?,
        longitude: Double?,
    ): Result<List<Concert>> = withContext(ioDispatcher) {
        runCatching {
            val response: TicketmasterResponse = httpClient.get(
                "https://app.ticketmaster.com/discovery/v2/events.json"
            ) {
                parameter("apikey", "TzkRPdkvBqMxC3gVFNYchLCpWJrXNmAP")
                parameter("keyword", artist)
                parameter("classificationName", "music")
                parameter("size", 20)
                if (latitude != null && longitude != null) {
                    parameter("latlong", "$latitude,$longitude")
                    parameter("radius", 200)
                    parameter("unit", "km")
                }
            }.body()

            response.embedded?.events?.map { event ->
                val venue = event.embedded?.venues?.firstOrNull()
                Concert(
                    id = event.id,
                    name = event.name,
                    date = event.dates?.start?.localDate ?: "Date TBA",
                    venueName = venue?.name ?: "Venue TBA",
                    city = venue?.city?.name ?: "",
                    country = venue?.country?.name ?: "",
                    imageUrl = event.images
                        .filter { it.width > 500 }
                        .maxByOrNull { it.width }?.url,
                    ticketUrl = event.url,
                )
            } ?: emptyList()
        }
    }
}
