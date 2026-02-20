package com.anu.animehub.data.mapper

import com.anu.animehub.data.local.entity.AnimeEntity
import com.anu.animehub.data.remote.dto.AnimeItemDto
import kotlinx.serialization.json.Json

fun AnimeItemDto.toEntity(
    charactersJson: String?,
    updatedAt: Long,
    json: Json
): AnimeEntity {
    val imageUrl = images?.jpg?.imageUrl
    val genresList = genres?.map { it.name } ?: emptyList()
    val genresJson = if (genresList.isEmpty()) null else json.encodeToString(
        StringListWrapper.serializer(),
        StringListWrapper(genresList)
    )
    return AnimeEntity(
        malId = malId,
        title = title,
        titleEnglish = titleEnglish,
        episodes = episodes,
        score = score,
        imageUrl = imageUrl,
        synopsis = synopsis,
        genresJson = genresJson,
        trailerYoutubeId = trailer?.youtubeId,
        trailerEmbedUrl = trailer?.embedUrl,
        charactersJson = charactersJson,
        updatedAt = updatedAt
    )
}
