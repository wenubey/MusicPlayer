package com.wenubey.musicplayer.ui.audio

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wenubey.musicplayer.data.local.Audio
import com.wenubey.musicplayer.utils.Utils.fakeAudio
import com.wenubey.musicplayer.utils.formatDuration

@Composable
fun MediaDisplay(
    modifier: Modifier = Modifier,
    audio: Audio,
    progress: Float,
    onProgress: (Float) -> Unit,
    progressString: String,
) {
    MediaDisplayContent(
        modifier = modifier,
        audio = audio,
        progress = progress,
        onProgress = onProgress,
        progressString = progressString,
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MediaDisplayContent(
    modifier: Modifier = Modifier,
    audio: Audio = fakeAudio,
    progress: Float = 0f,
    onProgress: (Float) -> Unit = {},
    progressString: String = "00:00",
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(4.dp),
    ) {
        Text(
            modifier = Modifier.fillMaxWidth().basicMarquee(delayMillis = 0) ,
            text = audio.title,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = audio.artist,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Slider(
            value = progress,
            onValueChange = { onProgress(it) },
            valueRange = 0f..100f,
        )
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(text = progressString)
            Text(text = audio.duration.toLong().formatDuration())
        }
    }
}

@Preview(name = "Dark mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(name = "Light mode", uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
@Composable
private fun MediaDisplayContentPreview() {
    com.wenubey.musicplayer.ui.theme.MusicPlayerTheme {
        Surface {
            MediaDisplayContent()
        }
    }
}