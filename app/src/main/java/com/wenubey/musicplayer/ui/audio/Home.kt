package com.wenubey.musicplayer.ui.audio

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wenubey.musicplayer.data.local.Audio
import com.wenubey.musicplayer.utils.Utils.fakeAudio
import com.wenubey.musicplayer.utils.timeStampToDuration

@Composable
fun Home(
    progress: Float,
    onProgress: (Float) -> Unit,
    isAudioPlaying: Boolean,
    currentPlayingAudio: Audio,
    audioList: List<Audio>,
    onStart: () -> Unit,
    onItemClick: (Int) -> Unit,
    onNext: () -> Unit,
    progressString: String,
    audioDuration: String,
) {
    HomeContent(
        progress = progress,
        onProgress = onProgress,
        isAudioPlaying = isAudioPlaying,
        audioList = audioList,
        onNext = onNext,
        onItemClick = onItemClick,
        onStart = onStart,
        currentPlayingAudio = currentPlayingAudio,
        progressString = progressString,
        audioDuration = audioDuration

    )
}

@Composable
private fun HomeContent(
    progress: Float = 0f,
    onProgress: (Float) -> Unit = {},
    isAudioPlaying: Boolean = false,
    currentPlayingAudio: Audio = fakeAudio,
    audioList: List<Audio> = listOf(fakeAudio),
    onStart: () -> Unit = {},
    onItemClick: (Int) -> Unit = {},
    onNext: () -> Unit = {},
    progressString: String = "00:00",
    audioDuration: String = "02:00"
) {
    Scaffold(
        bottomBar = {
            BottomBarPlayer(
                progress = progress,
                onProgress = onProgress,
                audio = currentPlayingAudio,
                isAudioPlaying = isAudioPlaying,
                onStart = onStart,
                onNext = onNext,
                progressString = progressString,
                audioDuration = audioDuration
            )
        }
    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues
        ) {
            itemsIndexed(audioList) { index,  audio ->
                AudioItem(
                    audio = audio,
                    onItemClick = { onItemClick(index) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioItem(audio: Audio, onItemClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        onClick = onItemClick,
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = audio.displayName, overflow = TextOverflow.Clip, maxLines = 1)
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = audio.artist, maxLines = 1, overflow = TextOverflow.Clip)
            }
            Text(text = audio.duration.toLong().timeStampToDuration())
            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}

@Preview(name = "Dark mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(name = "Light mode", uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
@Composable
private fun HomeContentPreview() {
    com.wenubey.musicplayer.ui.theme.MusicPlayerTheme {
        Surface {
            HomeContent()
        }
    }
}