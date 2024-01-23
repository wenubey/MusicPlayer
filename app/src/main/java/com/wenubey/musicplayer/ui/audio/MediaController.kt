package com.wenubey.musicplayer.ui.audio

import android.content.res.Configuration
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.wenubey.musicplayer.R

@Composable
fun MediaController(
    modifier: Modifier,
    isAudioPlaying: Boolean,
    onStart: () -> Unit,
    onNext: () -> Unit ,
    onPrevious: () -> Unit,
    albumArtUri: Uri?,
) {
    MediaControllerContent(
        modifier = modifier,
        isAudioPlaying = isAudioPlaying,
        onStart = onStart,
        onNext = onNext,
        onPrevious = onPrevious,
        albumArtUri = albumArtUri
    )
}

@Composable
private fun MediaControllerContent(
    modifier: Modifier = Modifier,
    isAudioPlaying: Boolean = false,
    onStart: () -> Unit = {},
    onNext: () -> Unit = {},
    onPrevious: () -> Unit = {},
    albumArtUri: Uri? = Uri.EMPTY,
) {
    val painter = rememberAsyncImagePainter(
        model = albumArtUri,
        placeholder = painterResource(id = R.drawable.baseline_music_note_24)
    )
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Image(
            painter = painter,
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(4.dp)),
            alpha = 0.3f,
        )
        ControllerButtons(
            isAudioPlaying = isAudioPlaying,
            onStart = onStart,
            onNext = onNext,
            onPrevious = onPrevious
        )
    }
}


@Composable
private fun ControllerButtons(
    isAudioPlaying: Boolean = false,
    onNext: () -> Unit = {},
    onStart: () -> Unit = {},
    onPrevious: () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .height(56.dp)
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PlayerIconItem(
            icon = Icons.Default.SkipPrevious,
            onClick = onPrevious,
            buttonType = ButtonType.NEXT_PREV
        )
        Spacer(modifier = Modifier.width(2.dp))
        PlayerIconItem(
            icon = if (isAudioPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
            onClick = onStart,
            buttonType = ButtonType.PLAY
        )
        Spacer(modifier = Modifier.width(2.dp))
        PlayerIconItem(
            icon = Icons.Default.SkipNext,
            onClick = onNext,
            buttonType = ButtonType.NEXT_PREV
        )
    }
}

@Composable
fun PlayerIconItem(
    icon: ImageVector = Icons.Default.PlayArrow,
    borderStroke: BorderStroke? = null,
    onClick: () -> Unit = {},
    color: Color = MaterialTheme.colorScheme.onSurface,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    buttonType: ButtonType = ButtonType.NEXT_PREV,
) {
    Surface(
        shape = CircleShape,
        border = borderStroke,
        modifier = Modifier
            .size(
                when (buttonType) {
                    ButtonType.NEXT_PREV -> 32.dp
                    ButtonType.PLAY -> 40.dp
                }
            )
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

enum class ButtonType {
    PLAY,
    NEXT_PREV,
}

@Preview(name = "Dark mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(name = "Light mode", uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
@Composable
private fun PlayerIconItemPreview() {
     com.wenubey.musicplayer.ui.theme.MusicPlayerTheme {
        Surface {
            PlayerIconItem()
        }
    }
}

@Preview(name = "Dark mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(name = "Light mode", uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
@Composable
private fun ControllerButtonsPreview() {
     com.wenubey.musicplayer.ui.theme.MusicPlayerTheme {
        Surface {
             ControllerButtons()
        }
    }
}


@Preview(name = "Dark mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(name = "Light mode", uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
@Composable
private fun MediaControllerContentPreview() {
     com.wenubey.musicplayer.ui.theme.MusicPlayerTheme {
        Surface {
             MediaControllerContent()
        }
    }
}