package com.mrsep.musicrecognizer.feature.library.presentation.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrsep.musicrecognizer.core.domain.preferences.FavoritesMode
import com.mrsep.musicrecognizer.core.domain.track.TrackRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class TrackStats(
    val totalTracks: Int = 0,
    val favorites: Int = 0,
    val topArtist: String? = null,
    val topAlbum: String? = null,
    val uniqueArtists: Int = 0,
)

@HiltViewModel
internal class StatsViewModel @Inject constructor(
    private val trackRepository: TrackRepository,
) : ViewModel() {

    val stats: StateFlow<TrackStats> = trackRepository
        .getTracksFlow(FavoritesMode.All)
        .map { tracks ->
            TrackStats(
                totalTracks = tracks.size,
                favorites = tracks.count { it.properties.isFavorite },
                topArtist = tracks.groupingBy { it.artist }
                    .eachCount().maxByOrNull { it.value }?.key,
                topAlbum = tracks.mapNotNull { it.album }
                    .groupingBy { it }.eachCount().maxByOrNull { it.value }?.key,
                uniqueArtists = tracks.map { it.artist }.toSet().size,
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), TrackStats())
}