package com.anu.animehub.di

import androidx.lifecycle.SavedStateHandle
import com.anu.animehub.BuildConfig
import com.anu.animehub.data.local.AppDatabase
import com.anu.animehub.data.remote.JikanApiService
import com.anu.animehub.data.repository.AnimeRepositoryImpl
import com.anu.animehub.data.util.DefaultNetworkMonitor
import com.anu.animehub.domain.repository.AnimeRepository
import com.anu.animehub.domain.usecase.GetAnimeDetailUseCase
import com.anu.animehub.domain.usecase.GetTopAnimeUseCase
import com.anu.animehub.presentation.viewmodel.AnimeListViewModel
import com.anu.animehub.presentation.viewmodel.DetailViewModel
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

/**
 * One place that defines how to create every dependency.
 * Koin calls these when something needs them (e.g. a ViewModel needs a UseCase).
 */
val appModule = module {

    // JSON for API and DB
    single {
        Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
        }
    }

    single {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .apply {
                if (BuildConfig.DEBUG) {
                    addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                }
            }
            .build()
    }

    single<JikanApiService> {
        Retrofit.Builder()
            .baseUrl("https://api.jikan.moe/")
            .client(get())
            .addConverterFactory(get<Json>().asConverterFactory("application/json".toMediaType()))
            .build()
            .create(JikanApiService::class.java)
    }

    single { AppDatabase.getDatabase(androidContext()).animeDao() }
    single { DefaultNetworkMonitor(androidContext()) }

    single<AnimeRepository> {
        AnimeRepositoryImpl(get(), get(), get(), get())
    }

    single { GetTopAnimeUseCase(get()) }
    single { GetAnimeDetailUseCase(get()) }

    viewModel { AnimeListViewModel(get(), get()) }
    viewModel { (handle: SavedStateHandle) ->
        DetailViewModel(handle, get(), get())
    }
}
