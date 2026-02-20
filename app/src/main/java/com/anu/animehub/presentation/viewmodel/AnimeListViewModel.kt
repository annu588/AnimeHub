package com.anu.animehub.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anu.animehub.domain.repository.AnimeRepository
import com.anu.animehub.domain.usecase.GetTopAnimeUseCase
import com.anu.animehub.presentation.state.AnimeListUiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AnimeListViewModel constructor(
    private val getTopAnimeUseCase: GetTopAnimeUseCase,
    private val repository: AnimeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AnimeListUiState>(AnimeListUiState.Loading)
    val uiState: StateFlow<AnimeListUiState> = _uiState.asStateFlow()

    private val _snackbarMessage = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val snackbarMessage: SharedFlow<String> = _snackbarMessage.asSharedFlow()

    init {
        observeTopAnime()
        observeNetworkAndSyncWhenOnline()
    }

    private fun observeTopAnime() {
        getTopAnimeUseCase.invoke()
            .onEach { list ->
                _uiState.update { current ->
                    val isOnline = (current as? AnimeListUiState.Success)?.isOnline ?: true
                    val isRefreshing = (current as? AnimeListUiState.Success)?.isRefreshing == true
                    AnimeListUiState.Success(
                        animeList = list,
                        isOnline = isOnline,
                        isRefreshing = isRefreshing
                    )
                }
            }
            .catch { e ->
                _uiState.value = AnimeListUiState.Error(
                    message = e.message ?: "Failed to load anime",
                    isOnline = (_uiState.value as? AnimeListUiState.Success)?.isOnline ?: true
                )
            }
            .launchIn(viewModelScope)
    }

    private fun observeNetworkAndSyncWhenOnline() {
        viewModelScope.launch {
            repository.isOnline()
                .distinctUntilChanged()
                .collect { online ->
                    _uiState.update { current ->
                        when (current) {
                            is AnimeListUiState.Success -> current.copy(isOnline = online)
                            is AnimeListUiState.Error -> current.copy(isOnline = online)
                            else -> current
                        }
                    }
                    if (online) syncFromApi()
                }
        }
    }

    private fun clearRefreshing() {
        _uiState.update { if (it is AnimeListUiState.Success) it.copy(isRefreshing = false) else it }
    }

    private suspend fun syncFromApi() {
        getTopAnimeUseCase.refresh().onFailure { clearRefreshing() }
    }

    fun refresh() {
        viewModelScope.launch {
            if (!repository.isOnline().first()) {
                clearRefreshing()
                _snackbarMessage.tryEmit("You're offline")
                return@launch
            }
            _uiState.update { if (it is AnimeListUiState.Success) it.copy(isRefreshing = true) else it }
            getTopAnimeUseCase.refresh()
                .onSuccess { clearRefreshing() }
                .onFailure { clearRefreshing() }
        }
    }

    fun clearError() {
        _uiState.update { current ->
            if (current is AnimeListUiState.Error) {
                AnimeListUiState.Success(animeList = emptyList(), isOnline = current.isOnline)
            } else current
        }
    }
}
