package com.anu.animehub.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "anime")
data class AnimeEntity(
    @PrimaryKey val malId: Int,
    val title: String,
    val titleEnglish: String?,
    val episodes: Int?,
    val score: Double?,
    val imageUrl: String?,
    val synopsis: String?,
    val genresJson: String?,
    val trailerYoutubeId: String?,
    val trailerEmbedUrl: String?,
    val charactersJson: String?,
    val updatedAt: Long
)
