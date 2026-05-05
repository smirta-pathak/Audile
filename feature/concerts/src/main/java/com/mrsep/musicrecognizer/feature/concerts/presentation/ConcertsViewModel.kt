package com.mrsep.musicrecognizer.feature.concerts.presentation

import androidx.lifecycle.SavedStateHandle
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrsep.musicrecognizer.core.domain.concerts.Concert
import com.mrsep.musicrecognizer.core.domain.concerts.ConcertRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ConcertsUiState {
    data object Loading : ConcertsUiState()
    data class Success(val concerts: List<Concert>) : ConcertsUiState()
    data class Empty(val artist: String) : ConcertsUiState()
    data class Error(val message: String) : ConcertsUiState()
}

@HiltViewModel
class ConcertsViewModel @Inject constructor(
    private val concertRepository: ConcertRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    val artist: String = checkNotNull(savedStateHandle["artist"])

    private val _uiState = MutableStateFlow<ConcertsUiState>(ConcertsUiState.Loading)
    val uiState: StateFlow<ConcertsUiState> = _uiState

    init {
        loadConcerts()
    }

    fun loadConcerts(latitude: Double? = null, longitude: Double? = null) {
        viewModelScope.launch {
            _uiState.value = ConcertsUiState.Loading
            concertRepository.getConcerts(artist, latitude, longitude)
                .onSuccess { concerts ->
                    _uiState.value = if (concerts.isEmpty()) {
                        ConcertsUiState.Empty(artist)
                    } else {
                        ConcertsUiState.Success(concerts)
                    }
                }
                .onFailure { error ->
                    Log.e("ConcertFinder", "Failed to load concerts", error)
                    _uiState.value = ConcertsUiState.Error(
                        error.message ?: "Unknown error"
                    )
                }
        }
    }
}
