package com.anu.animehub.presentation.state

import com.anu.animehub.domain.model.Anime

sealed class DetailUiState {

    data object Loading : DetailUiState()

    data class Success(val anime: Anime) : DetailUiState()

    data class Error(val message: String) : DetailUiState()
}
