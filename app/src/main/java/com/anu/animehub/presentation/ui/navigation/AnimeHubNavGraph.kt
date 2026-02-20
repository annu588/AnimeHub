package com.anu.animehub.presentation.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.anu.animehub.presentation.ui.animelist.AnimeListScreen
import com.anu.animehub.presentation.ui.detail.DetailScreen
import com.anu.animehub.presentation.viewmodel.AnimeListViewModel
import com.anu.animehub.presentation.viewmodel.DetailViewModel
import org.koin.androidx.compose.koinViewModel

const val ANIME_LIST = "animelist"
const val DETAIL = "detail/{animeId}"
const val DETAIL_ID_ARG = "animeId"

fun detailRoute(animeId: Int) = "detail/$animeId"

@Composable
fun AnimeHubNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = ANIME_LIST
    ) {
        composable(ANIME_LIST) {
            val animeListViewModel: AnimeListViewModel = koinViewModel()
            val animeListUiState by animeListViewModel.uiState.collectAsState()
            AnimeListScreen(
                viewModel = animeListViewModel,
                uiState = animeListUiState,
                onAnimeClick = { id -> navController.navigate(detailRoute(id)) }
            )
        }
        composable(
            route = DETAIL,
            arguments = listOf(navArgument(DETAIL_ID_ARG) { type = NavType.IntType })
        ) { backStackEntry ->
            val detailViewModel: DetailViewModel = koinViewModel(
                viewModelStoreOwner = backStackEntry
            )
            val detailUiState by detailViewModel.uiState.collectAsState()
            DetailScreen(
                uiState = detailUiState,
                onBack = { navController.popBackStack() },
                onClearError = detailViewModel::clearError
            )
        }
    }
}
