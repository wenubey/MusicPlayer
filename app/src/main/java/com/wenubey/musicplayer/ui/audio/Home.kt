package com.wenubey.musicplayer.ui.audio

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.airbnb.lottie.compose.rememberLottieDynamicProperties
import com.airbnb.lottie.compose.rememberLottieDynamicProperty
import com.wenubey.musicplayer.R
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
    onPlayPause: () -> Unit,
    onItemClick: (Int) -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    progressString: String,
) {
    HomeContent(
        progress = progress,
        onProgress = onProgress,
        isAudioPlaying = isAudioPlaying,
        audioList = audioList,
        onNext = onNext,
        onItemClick = onItemClick,
        onPlayPause = onPlayPause,
        onPrevious = onPrevious,
        currentPlayingAudio = currentPlayingAudio,
        progressString = progressString,

        )
}

@Composable
private fun HomeContent(
    progress: Float = 0f,
    onProgress: (Float) -> Unit = {},
    isAudioPlaying: Boolean = false,
    currentPlayingAudio: Audio = fakeAudio,
    audioList: List<Audio> = listOf(fakeAudio),
    onPlayPause: () -> Unit = {},
    onItemClick: (Int) -> Unit = {},
    onNext: () -> Unit = {},
    onPrevious: () -> Unit = {},
    progressString: String = "00:00",
) {
    var selectedItemIndex by remember { mutableIntStateOf(-1) }

    Scaffold { paddingValues ->
        Column(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                contentPadding = paddingValues,
                modifier = Modifier
                    .fillMaxHeight(0.85f)
                    .fillMaxWidth()
            ) {
                itemsIndexed(audioList) { index, audio ->
                    AudioItem(
                        itemIndex = index + 1,
                        audio = audio,
                        isSelected = index == selectedItemIndex,
                        onItemClick = {
                            selectedItemIndex = if (selectedItemIndex == index) {
                                -1
                            } else {
                                index
                            }
                            onItemClick(index)
                        }
                    )
                }
            }
            PlayerBar(
                progress = progress,
                onProgress = onProgress,
                audio = currentPlayingAudio,
                isAudioPlaying = isAudioPlaying,
                onPlayPause = {
                    selectedItemIndex = if (selectedItemIndex != -1) {
                        -1
                    } else {
                        audioList.indexOf(currentPlayingAudio)
                    }
                    onPlayPause()
                },
                onNext = onNext,
                onPrevious = onPrevious,
                progressString = progressString,
            )
        }

    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioItem(audio: Audio, onItemClick: () -> Unit, isSelected: Boolean, itemIndex: Int) {
    val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.audio_vawe))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
    )
    val dynamicProperties = rememberLottieDynamicProperties(
        rememberLottieDynamicProperty(
            property = LottieProperty.COLOR_FILTER,
            value = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                MaterialTheme.colorScheme.primary.hashCode(),
                BlendModeCompat.SRC_ATOP
            ),
            keyPath = arrayOf(
                "**"
            )
        )
    )

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
            if (isSelected) {
                LottieAnimation(
                    modifier = Modifier.size(35.dp),
                    composition = composition,
                    progress = { progress },
                    dynamicProperties = dynamicProperties
                )
            } else {
                Text(
                    text = itemIndex.toString(), modifier = Modifier
                        .size(30.dp)
                        .padding(horizontal = 8.dp), style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = audio.title,
                    overflow = TextOverflow.Clip,
                    maxLines = 1,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                )
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