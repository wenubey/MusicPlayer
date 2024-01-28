package com.wenubey.musicplayer.ui.audio

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wenubey.musicplayer.data.local.Audio
import com.wenubey.musicplayer.utils.Utils.fakeAudio

@Composable
fun PlayerBar(
    modifier: Modifier = Modifier,
    progress: Float,
    onProgress: (Float) -> Unit,
    audio: Audio,
    isAudioPlaying: Boolean,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    progressString: String,
) {
    PlayerBarContent(
        modifier =  modifier,
        progress = progress,
        onProgress = onProgress,
        audio = audio,
        isAudioPlaying = isAudioPlaying,
        onStart = onPlayPause,
        onNext = onNext,
        onPrevious = onPrevious,
        progressString = progressString,
    )
}


@Composable
private fun PlayerBarContent(
    modifier: Modifier = Modifier,
    progress: Float = 0f,
    onProgress: (Float) -> Unit = {},
    progressString: String = "00:00",
    audio: Audio = fakeAudio,
    isAudioPlaying: Boolean = false,
    onStart: () -> Unit = {},
    onNext: () -> Unit = {},
    onPrevious: () -> Unit = {},
) {
    ElevatedCard(
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .padding(4.dp)
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                MediaController(
                    modifier = Modifier.weight(0.3f),
                    isAudioPlaying = isAudioPlaying,
                    onStart = onStart,
                    onNext = onNext,
                    onPrevious = onPrevious,
                    albumArtUri = audio.albumArtUri
                )
                Spacer(modifier = Modifier.width(4.dp))
                MediaDisplay(
                    modifier = Modifier.fillMaxWidth(0.7f),
                    audio = audio,
                    progress = progress,
                    onProgress = onProgress,
                    progressString = progressString,
                )

            }
        }
    }
}

@Preview(name = "Dark mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(name = "Light mode", uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
@Composable
private fun PlayerBarContentPreview() {
    com.wenubey.musicplayer.ui.theme.MusicPlayerTheme {
        Surface {
            PlayerBarContent()
        }
    }
}