package com.anu.animehub.data.mapper

import com.anu.animehub.data.local.entity.AnimeEntity
import com.anu.animehub.domain.model.Anime
import com.anu.animehub.domain.model.CastMember
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

@Serializable
private data class StoredCastEntry(
    val character: String,
    val role: String? = null,
    val voiceActor: String? = null
)

fun AnimeEntity.toDomain(json: Json): Anime {
    val genres = genresJson?.let { runCatching { json.decodeFromString<StringListWrapper>(it).list }.getOrNull() } ?: emptyList()
    val cast = charactersJson?.let { str ->
        runCatching {
            json.decodeFromString(ListSerializer(StoredCastEntry.serializer()), str).map { e ->
                CastMember(
                    characterName = e.character,
                    role = e.role,
                    voiceActorName = e.voiceActor
                )
            }
        }.getOrNull() ?: emptyList()
    } ?: emptyList()
    return Anime(
        malId = malId,
        title = title,
        titleEnglish = titleEnglish,
        episodes = episodes,
        score = score,
        imageUrl = imageUrl,
        synopsis = synopsis,
        genres = genres,
        trailerYoutubeId = trailerYoutubeId,
        trailerEmbedUrl = trailerEmbedUrl,
        mainCast = cast
    )
}
