package com.anu.animehub.presentation.state

import com.anu.animehub.domain.model.Anime

sealed class AnimeListUiState {

    data object Loading : AnimeListUiState()

    data class Success(
        val animeList: List<Anime>,
        val isOnline: Boolean = true,
        val isRefreshing: Boolean = false
    ) : AnimeListUiState()

    data class Error(
        val message: String,
        val isOnline: Boolean = true
    ) : AnimeListUiState()
}
