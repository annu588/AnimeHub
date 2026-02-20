package com.anu.animehub.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TopAnimeResponse(
    @SerialName("data") val data: List<AnimeItemDto>
)

@Serializable
data class AnimeItemDto(
    @SerialName("mal_id") val malId: Int,
    @SerialName("title") val title: String,
    @SerialName("title_english") val titleEnglish: String? = null,
    @SerialName("episodes") val episodes: Int? = null,
    @SerialName("score") val score: Double? = null,
    @SerialName("synopsis") val synopsis: String? = null,
    @SerialName("images") val images: AnimeImagesDto? = null,
    @SerialName("genres") val genres: List<GenreDto>? = null,
    @SerialName("trailer") val trailer: TrailerDto? = null
)

@Serializable
data class AnimeDetailResponse(
    @SerialName("data") val data: AnimeItemDto
)

@Serializable
data class AnimeImagesDto(
    @SerialName("jpg") val jpg: ImageSizeDto? = null
)

@Serializable
data class ImageSizeDto(
    @SerialName("image_url") val imageUrl: String? = null,
    @SerialName("small_image_url") val smallImageUrl: String? = null,
    @SerialName("large_image_url") val largeImageUrl: String? = null
)

@Serializable
data class GenreDto(
    @SerialName("mal_id") val malId: Int,
    @SerialName("name") val name: String
)

@Serializable
data class TrailerDto(
    @SerialName("youtube_id") val youtubeId: String? = null,
    @SerialName("url") val url: String? = null,
    @SerialName("embed_url") val embedUrl: String? = null
)

@Serializable
data class AnimeCharactersResponse(
    @SerialName("data") val data: List<CharacterEntryDto>
)

@Serializable
data class CharacterEntryDto(
    @SerialName("character") val character: CharacterDto,
    @SerialName("role") val role: String? = null,
    @SerialName("voice_actors") val voiceActors: List<VoiceActorDto>? = null
)

@Serializable
data class CharacterDto(
    @SerialName("mal_id") val malId: Int,
    @SerialName("name") val name: String,
    @SerialName("images") val images: CharacterImagesDto? = null
)

@Serializable
data class CharacterImagesDto(
    @SerialName("jpg") val jpg: ImageSizeDto? = null
)

@Serializable
data class VoiceActorDto(
    @SerialName("person") val person: PersonDto? = null,
    @SerialName("language") val language: String? = null
)

@Serializable
data class PersonDto(
    @SerialName("name") val name: String
)
