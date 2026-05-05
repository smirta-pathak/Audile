package com.mrsep.musicrecognizer.core.domain.concerts

interface ConcertRepository {
    suspend fun getConcerts(
        artist: String,
        latitude: Double?,
        longitude: Double?,
    ): Result<List<Concert>>
}
