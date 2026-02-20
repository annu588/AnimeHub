package com.anu.animehub.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.anu.animehub.data.local.entity.AnimeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AnimeDao {

    @Query("SELECT * FROM anime ORDER BY score DESC, malId ASC")
    fun getAllAnime(): Flow<List<AnimeEntity>>

    @Query("SELECT * FROM anime WHERE malId = :id LIMIT 1")
    suspend fun getAnimeById(id: Int): AnimeEntity?

    @Query("SELECT * FROM anime WHERE malId = :id LIMIT 1")
    fun getAnimeByIdFlow(id: Int): Flow<AnimeEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(anime: List<AnimeEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(anime: AnimeEntity)
}
