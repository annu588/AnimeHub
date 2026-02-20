package com.anu.animehub.domain.usecase

import com.anu.animehub.domain.model.Anime
import com.anu.animehub.domain.repository.AnimeRepository
import kotlinx.coroutines.flow.Flow

class GetTopAnimeUseCase(
    private val repository: AnimeRepository
) {

    fun invoke(): Flow<List<Anime>> = repository.getTopAnime()

    suspend fun refresh(): Result<Unit> = repository.refreshTopAnime()
}
