package com.anu.animehub.presentation.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import com.anu.animehub.domain.model.Anime
import com.anu.animehub.presentation.state.DetailUiState
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    uiState: DetailUiState,
    onBack: () -> Unit,
    onClearError: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState) {
        if (uiState is DetailUiState.Error) {
            snackbarHostState.showSnackbar(uiState.message)
            onClearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        when (uiState) {
                            is DetailUiState.Success -> uiState.anime.title
                            else -> "Details"
                        }
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (uiState) {
                is DetailUiState.Loading -> Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
                is DetailUiState.Error -> Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        uiState.message,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                is DetailUiState.Success -> DetailContent(
                    anime = uiState.anime,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
private fun DetailContent(
    anime: Anime,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        TrailerOrPoster(anime = anime)
        Spacer(Modifier.height(16.dp))
        Text(
            text = anime.title,
            style = MaterialTheme.typography.headlineSmall
        )
        anime.titleEnglish?.takeIf { it.isNotBlank() }?.let { en ->
            Text(
                text = en,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            anime.episodes?.let { Text("Episodes: $it", style = MaterialTheme.typography.bodyMedium) }
            anime.score?.let { Text("Score: ★ $it", style = MaterialTheme.typography.bodyMedium) }
        }
        if (anime.genres.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Genres: ${anime.genres.joinToString(", ")}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
        anime.synopsis?.takeIf { it.isNotBlank() }?.let { synopsis ->
            Spacer(Modifier.height(12.dp))
            Text(
                text = "Synopsis",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = synopsis,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        if (anime.mainCast.isNotEmpty()) {
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Main Cast",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(8.dp))
            anime.mainCast.take(10).forEach { cast ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(cast.characterName, style = MaterialTheme.typography.bodyMedium)
                    cast.voiceActorName?.let {
                        Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

private fun Anime.youtubeVideoId(): String? {
    if (!trailerYoutubeId.isNullOrBlank()) return trailerYoutubeId
    if (!trailerEmbedUrl.isNullOrBlank()) {
        return trailerEmbedUrl!!.substringAfter("/embed/").substringBefore("?").takeIf { it.isNotBlank() }
    }
    return null
}

@Composable
private fun TrailerOrPoster(anime: Anime) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var isTrailerPlaying by remember { mutableStateOf(false) }
    val youtubeVideoId = anime.youtubeVideoId()
    val hasTrailer = youtubeVideoId != null

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        if (hasTrailer && isTrailerPlaying) {
            AndroidView(
                factory = {
                    YouTubePlayerView(context).apply {
                        lifecycleOwner.lifecycle.addObserver(this)
                        addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                            override fun onReady(youTubePlayer: YouTubePlayer) {
                                youTubePlayer.loadVideo(youtubeVideoId!!, 0f)
                            }
                        })
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp))
                    .then(
                        if (hasTrailer) Modifier.clickable { isTrailerPlaying = true }
                        else Modifier
                    )
            ) {
                PosterContent(anime = anime)
                if (hasTrailer) {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play trailer",
                            modifier = Modifier
                                .size(72.dp)
                                .background(
                                    MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                                    CircleShape
                                )
                                .padding(12.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PosterContent(anime: Anime) {
    if (!anime.imageUrl.isNullOrBlank()) {
        AsyncImage(
            model = anime.imageUrl,
            contentDescription = anime.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    } else {
        Box(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Text(
                anime.title.take(2).uppercase(),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
