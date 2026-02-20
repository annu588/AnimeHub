package com.anu.animehub.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anu.animehub.domain.repository.AnimeRepository
import com.anu.animehub.domain.usecase.GetAnimeDetailUseCase
import com.anu.animehub.presentation.state.DetailUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DetailViewModel constructor(
    savedStateHandle: SavedStateHandle,
    private val getAnimeDetailUseCase: GetAnimeDetailUseCase,
    private val repository: AnimeRepository
) : ViewModel() {

    private val animeId: Int = savedStateHandle.get<Int>("animeId") ?: 0

    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    init {
        if (animeId != 0) {
            observeDetail()
            ensureLoaded()
            observeNetworkAndSyncWhenOnline()
        } else {
            _uiState.value = DetailUiState.Error("Invalid anime id")
        }
    }

    private fun observeNetworkAndSyncWhenOnline() {
        viewModelScope.launch {
            var wasOffline = false
            repository.isOnline()
                .distinctUntilChanged()
                .collect { online ->
                    if (online && wasOffline) {
                        getAnimeDetailUseCase.ensureLoaded(animeId)
                    }
                    wasOffline = !online
                }
        }
    }

    private fun observeDetail() {
        getAnimeDetailUseCase.observe(animeId)
            .onEach { anime ->
                anime?.let { _uiState.value = DetailUiState.Success(it) }
            }
            .catch { e ->
                _uiState.update { current ->
                    if (current is DetailUiState.Loading) {
                        DetailUiState.Error(e.message ?: "Failed to load detail")
                    } else current
                }
            }
            .launchIn(viewModelScope)
    }

    private fun ensureLoaded() {
        viewModelScope.launch {
            _uiState.value = DetailUiState.Loading
            getAnimeDetailUseCase.ensureLoaded(animeId)
                .onSuccess { anime ->
                    _uiState.value = DetailUiState.Success(anime)
                }
                .onFailure { e ->
                    _uiState.value = DetailUiState.Error(e.message ?: "Failed to load detail")
                }
        }
    }

    fun clearError() {
        _uiState.update { current ->
            if (current is DetailUiState.Error) DetailUiState.Loading else current
        }
    }
}
