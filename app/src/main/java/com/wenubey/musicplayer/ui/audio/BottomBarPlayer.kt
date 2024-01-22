package com.wenubey.musicplayer.ui.audio

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wenubey.musicplayer.data.local.Audio
import com.wenubey.musicplayer.utils.Utils.fakeAudio

@Composable
fun BottomBarPlayer(
    progress: Float,
    onProgress: (Float) -> Unit,
    audio: Audio,
    isAudioPlaying: Boolean,
    onStart: () -> Unit,
    onNext: () -> Unit,
) {
    BottomBarPlayerContent(
        progress = progress,
        onProgress = onProgress,
        audio = audio,
        isAudioPlaying = isAudioPlaying,
        onStart = onStart,
        onNext = onNext
    )
}

@Composable
private fun BottomBarPlayerContent(
    progress: Float = 0f,
    onProgress: (Float) -> Unit = {},
    audio: Audio = fakeAudio,
    isAudioPlaying: Boolean = false,
    onStart: () -> Unit = {},
    onNext: () -> Unit = {},
) {
    BottomAppBar(
        content = {
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ArtistInfo(
                        audio = audio,
                        modifier = Modifier.weight(1f)
                    )
                    MediaPlayerController(
                        isAudioPlaying = isAudioPlaying,
                        onStart = onStart,
                        onNext = onNext
                    )
                    Slider(
                        value = progress,
                        onValueChange = { onProgress(it) },
                        valueRange = 0f..100f,
                    )
                }
            }
        }
    )
}

@Composable
fun MediaPlayerController(
    isAudioPlaying: Boolean,
    onNext: () -> Unit,
    onStart: () -> Unit
) {
    Row(modifier = Modifier
        .height(56.dp)
        .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        ) {
        PlayerIconItem(icon = if (isAudioPlaying) Icons.Default.Pause else Icons.Default.PlayArrow, onClick = onStart)
        Spacer(modifier = Modifier.height(8.dp))
        Icon(
            imageVector = Icons.Default.SkipNext,
            contentDescription = null,
            modifier = Modifier.clickable { onNext() })
    }
}

@Composable
fun ArtistInfo(audio: Audio, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.padding(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PlayerIconItem(
            icon = Icons.Default.MusicNote,
            borderStroke = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onSurface
            ),
            onClick = {}
        )
        Spacer(Modifier.height(4.dp))
        Column {
            Text(
                text = audio.title,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text =
                audio.artist,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
            )
        }
    }
}

@Composable
fun PlayerIconItem(
    icon: ImageVector,
    borderStroke: BorderStroke? = null,
    onClick: () -> Unit,
    color: Color = MaterialTheme.colorScheme.onSurface,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
) {
    Surface(
        shape = CircleShape,
        border = borderStroke,
        modifier = Modifier
            .clip(CircleShape)
            .clickable {
                onClick()
            },
        color = backgroundColor,
        contentColor = color
    ) {
        Box(modifier = Modifier.padding(4.dp), contentAlignment = Alignment.Center) {
            Icon(imageVector = icon, contentDescription = null)
        }
    }
}

@Preview(name = "Dark mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(name = "Light mode", uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
@Composable
private fun BottomBarPlayerContentPreview() {
    com.wenubey.musicplayer.ui.theme.MusicPlayerTheme {
        Surface {
            BottomBarPlayerContent()
        }
    }
}