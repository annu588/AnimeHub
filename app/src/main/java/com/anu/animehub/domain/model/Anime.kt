package com.anu.animehub.domain.model

data class Anime(
    val malId: Int,
    val title: String,
    val titleEnglish: String?,
    val episodes: Int?,
    val score: Double?,
    val imageUrl: String?,
    val synopsis: String?,
    val genres: List<String>,
    val trailerYoutubeId: String?,
    val trailerEmbedUrl: String?,
    val mainCast: List<CastMember>
)

data class CastMember(
    val characterName: String,
    val role: String?,
    val voiceActorName: String?
)
