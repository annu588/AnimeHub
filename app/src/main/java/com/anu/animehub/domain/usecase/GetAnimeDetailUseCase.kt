package com.anu.animehub.domain.usecase

import com.anu.animehub.domain.model.Anime
import com.anu.animehub.domain.repository.AnimeRepository
import kotlinx.coroutines.flow.Flow

class GetAnimeDetailUseCase(
    private val repository: AnimeRepository
) {

    fun observe(id: Int): Flow<Anime?> = repository.getAnimeDetail(id)

    suspend fun ensureLoaded(id: Int): Result<Anime> = repository.ensureAnimeDetail(id)
}
