package com.anu.animehub.data.repository

import com.anu.animehub.data.local.dao.AnimeDao
import com.anu.animehub.data.mapper.toDomain
import com.anu.animehub.data.mapper.toEntity
import com.anu.animehub.data.remote.JikanApiService
import com.anu.animehub.data.util.NetworkMonitor
import com.anu.animehub.domain.model.Anime
import com.anu.animehub.domain.repository.AnimeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

@Serializable
private data class StoredCastEntry(
    val character: String,
    val role: String? = null,
    val voiceActor: String? = null
)

class AnimeRepositoryImpl constructor(
    private val api: JikanApiService,
    private val dao: AnimeDao,
    private val networkMonitor: NetworkMonitor,
    private val json: Json
) : AnimeRepository {

    override fun getTopAnime(): Flow<List<Anime>> = dao.getAllAnime().map { entities ->
        entities.map { it.toDomain(json) }
    }

    override fun getAnimeDetail(id: Int): Flow<Anime?> = dao.getAnimeByIdFlow(id).map { entity ->
        entity?.toDomain(json)
    }

    override suspend fun refreshTopAnime(): Result<Unit> = runCatching {
        val response = api.getTopAnime(page = 1, limit = 25)
        val entities = response.data.map { dto ->
            dto.toEntity(charactersJson = null, updatedAt = System.currentTimeMillis(), json = json)
        }
        dao.insertAll(entities)
    }

    override suspend fun ensureAnimeDetail(id: Int): Result<Anime> {
        val existing = dao.getAnimeById(id)
        return if (existing != null) {
            Result.success(existing.toDomain(json))
        } else {
            fetchAndSaveDetail(id)
        }
    }

    override fun isOnline(): Flow<Boolean> = networkMonitor.isOnline

    private suspend fun fetchAndSaveDetail(id: Int): Result<Anime> = runCatching {
        val detail = api.getAnimeById(id)
        val characters = runCatching { api.getAnimeCharacters(id) }.getOrNull()
        val charactersJson = characters?.data?.let { list ->
            val entries = list.map { entry ->
                StoredCastEntry(
                    character = entry.character.name,
                    role = entry.role,
                    voiceActor = entry.voiceActors?.firstOrNull()?.person?.name
                )
            }
            json.encodeToString(ListSerializer(StoredCastEntry.serializer()), entries)
        }
        val entity = detail.data.toEntity(
            charactersJson = charactersJson,
            updatedAt = System.currentTimeMillis(),
            json = json
        )
        dao.insert(entity)
        entity.toDomain(json)
    }
}
