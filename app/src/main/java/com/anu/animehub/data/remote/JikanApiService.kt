package com.anu.animehub.data.remote

import com.anu.animehub.data.remote.dto.AnimeCharactersResponse
import com.anu.animehub.data.remote.dto.AnimeDetailResponse
import com.anu.animehub.data.remote.dto.TopAnimeResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface JikanApiService {

    @GET("v4/top/anime")
    suspend fun getTopAnime(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 25
    ): TopAnimeResponse

    @GET("v4/anime/{id}")
    suspend fun getAnimeById(@Path("id") animeId: Int): AnimeDetailResponse

    @GET("v4/anime/{id}/characters")
    suspend fun getAnimeCharacters(@Path("id") animeId: Int): AnimeCharactersResponse
}
