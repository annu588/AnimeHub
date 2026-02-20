package com.anu.animehub.domain.repository

import com.anu.animehub.domain.model.Anime
import kotlinx.coroutines.flow.Flow

interface AnimeRepository {
    fun getTopAnime(): Flow<List<Anime>>
    fun getAnimeDetail(id: Int): Flow<Anime?>
    suspend fun refreshTopAnime(): Result<Unit>
    suspend fun ensureAnimeDetail(id: Int): Result<Anime>
    fun isOnline(): Flow<Boolean>
}
