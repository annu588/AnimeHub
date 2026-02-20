package com.anu.animehub.data.mapper

import com.anu.animehub.data.remote.dto.AnimeItemDto
import com.anu.animehub.domain.model.Anime

fun AnimeItemDto.toDomain(): Anime = Anime(
    malId = malId,
    title = title,
    titleEnglish = titleEnglish,
    episodes = episodes,
    score = score,
    imageUrl = images?.jpg?.imageUrl,
    synopsis = synopsis,
    genres = genres?.map { it.name } ?: emptyList(),
    trailerYoutubeId = trailer?.youtubeId,
    trailerEmbedUrl = trailer?.embedUrl,
    mainCast = emptyList()
)
