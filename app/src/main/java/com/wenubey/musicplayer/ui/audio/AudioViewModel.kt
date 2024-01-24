package com.wenubey.musicplayer.ui.audio

import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.wenubey.musicplayer.data.local.Audio
import com.wenubey.musicplayer.domain.AudioRepository
import com.wenubey.musicplayer.player.service.MusicPlayerServiceHandler
import com.wenubey.musicplayer.player.service.MusicPlayerState
import com.wenubey.musicplayer.player.service.PlayerEvent
import com.wenubey.musicplayer.utils.Utils.fakeAudio
import com.wenubey.musicplayer.utils.formatDuration
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@OptIn(SavedStateHandleSaveableApi::class)
class AudioViewModel @Inject constructor(
    private val musicPlayerServiceHandler: MusicPlayerServiceHandler,
    private val repository: AudioRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {


    var progress by savedStateHandle.saveable { mutableFloatStateOf(0f) }
    var progressString by savedStateHandle.saveable { mutableStateOf("00:00") }
    var isPlaying by savedStateHandle.saveable { mutableStateOf(false) }
    var currentSelectedAudio by savedStateHandle.saveable { mutableStateOf(fakeAudio) }
    var audioList by savedStateHandle.saveable { mutableStateOf(listOf<Audio>()) }


    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Initial)
    val uiState: StateFlow<UiState> get() = _uiState.asStateFlow()


    init {
        loadAudioData()
    }

    init {
        viewModelScope.launch {
            musicPlayerServiceHandler.audioState.collectLatest { musicPlayerState ->
                when (musicPlayerState) {
                    is MusicPlayerState.Initial -> _uiState.value = UiState.Initial
                    is MusicPlayerState.Playing -> isPlaying = musicPlayerState.isPlaying
                    is MusicPlayerState.Buffering -> calculateProgress(musicPlayerState.progress)
                    is MusicPlayerState.Progress -> calculateProgress(musicPlayerState.progress)
                    is MusicPlayerState.CurrentPlaying -> {
                        currentSelectedAudio = audioList[musicPlayerState.mediaItemIndex]
                    }
                    is MusicPlayerState.Ready -> {
                        _uiState.value = UiState.Ready
                    }
                }
            }
        }
    }

    fun onUiEvent(uiEvent: UiEvent) = viewModelScope.launch {


        when (uiEvent) {

            is UiEvent.Backward -> musicPlayerServiceHandler.onPlayerEvent(PlayerEvent.Backward)
            is UiEvent.Forward -> musicPlayerServiceHandler.onPlayerEvent(PlayerEvent.Forward)
            is UiEvent.SeekToNext -> musicPlayerServiceHandler.onPlayerEvent(PlayerEvent.SeekToNext)
            is UiEvent.SeekToPrevious -> musicPlayerServiceHandler.onPlayerEvent(PlayerEvent.SeekToPrevious)
            is UiEvent.PlayPause -> musicPlayerServiceHandler.onPlayerEvent(PlayerEvent.PlayPause)
            is UiEvent.SeekTo -> {
                val za = ((currentSelectedAudio.duration * uiEvent.position) / 100f).toLong()
                musicPlayerServiceHandler.onPlayerEvent(
                    PlayerEvent.SeekTo,
                    seekPosition = za
                )
            }

            is UiEvent.SelectedAudioChange -> {
                musicPlayerServiceHandler.onPlayerEvent(
                    PlayerEvent.SelectedAudioChange,
                    selectedAudioIndex = uiEvent.index
                )
            }

            is UiEvent.UpdateProgress -> {
                musicPlayerServiceHandler.onPlayerEvent(
                    PlayerEvent.UpdateProgress(
                        uiEvent.newProgress,
                    )
                )
                progress = uiEvent.newProgress
            }
        }
    }


    private fun calculateProgress(currentProgress: Long) {
        progress =
            if (currentProgress > 0) ((currentProgress.toFloat() / currentSelectedAudio.duration.toFloat()) * 100f)
            else 0f
        progressString = currentProgress.formatDuration()

    }

    private fun loadAudioData() = viewModelScope.launch {
        val audios = repository.getAudioData()
        audioList = audios
        setMediaItems()
    }


    private fun setMediaItems() {
        audioList.map { audio: Audio ->
            MediaItem.Builder()
                .setUri(audio.uri)
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setAlbumArtist(audio.artist)
                        .setDisplayTitle(audio.title)
                        .setSubtitle(audio.displayName)
                        .build()
                )
                .build()
        }.also {
            viewModelScope.launch {
                musicPlayerServiceHandler.setMediaItemList(it)
            }
        }
    }

    override fun onCleared() {
        viewModelScope.launch {
            musicPlayerServiceHandler.onPlayerEvent(PlayerEvent.Stop)
        }
        super.onCleared()
    }

}

