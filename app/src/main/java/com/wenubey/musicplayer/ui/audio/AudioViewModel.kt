package com.wenubey.musicplayer.ui.audio

import androidx.compose.runtime.State
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
import kotlin.math.absoluteValue

@HiltViewModel
@OptIn(SavedStateHandleSaveableApi::class)
class AudioViewModel @Inject constructor(
    private val musicPlayerServiceHandler: MusicPlayerServiceHandler,
    private val repository: AudioRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {


    private val _progress = mutableFloatStateOf(0f)
    val progress: State<Float> = _progress

    private val _currentDuration = mutableStateOf("00:00")
    val currentDuration: State<String> = _currentDuration

    private val _isPlaying = mutableStateOf(false)
    val isPlaying: State<Boolean> = _isPlaying

    private val _currentSelectedAudio = mutableStateOf(fakeAudio)
    val currentSelectedAudio: State<Audio> = _currentSelectedAudio

    private val _audioList = mutableStateOf<List<Audio>>(emptyList())
    val audioList: State<List<Audio>> = _audioList

    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Initial)
    val uiState: StateFlow<UiState> get() = _uiState.asStateFlow()


    init {
        loadAudioData()
        getSavedState()
    }

    private fun getSavedState() {
        _progress.floatValue = savedStateHandle.get<Float>(PROGRESS) ?: 0f
        _currentDuration.value = savedStateHandle.get<String>(CURRENT_DURATION) ?: "00:00"
        _isPlaying.value = savedStateHandle.get<Boolean>(IS_PLAYING) ?: false
        _currentSelectedAudio.value = savedStateHandle.get<Audio>(CURRENT_SELECTED_AUDIO) ?: fakeAudio
        _audioList.value = savedStateHandle.get<List<Audio>>(AUDIO_LIST) ?: emptyList()
    }

    init {
        viewModelScope.launch {
            musicPlayerServiceHandler.audioState.collectLatest { musicPlayerState ->
                when (musicPlayerState) {
                    is MusicPlayerState.Initial -> _uiState.value = UiState.Initial
                    is MusicPlayerState.Playing -> _isPlaying.value = musicPlayerState.isPlaying
                    is MusicPlayerState.Buffering -> calculateProgress(musicPlayerState.progress)
                    is MusicPlayerState.Progress -> calculateProgress(musicPlayerState.progress)
                    is MusicPlayerState.CurrentPlaying -> {
                        _currentSelectedAudio.value =
                            _audioList.value[musicPlayerState.mediaItemIndex]
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
                musicPlayerServiceHandler.onPlayerEvent(
                    PlayerEvent.SeekTo,
                    seekPosition = ((currentSelectedAudio.value.duration * uiEvent.position) / 100f).toLong()
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
                _progress.floatValue = uiEvent.newProgress
            }
        }
    }


    private fun calculateProgress(currentProgress: Long) {
        _progress.floatValue =
            if (currentProgress > 0) ((currentProgress.absoluteValue.toFloat() / currentSelectedAudio.value.duration.toFloat()) * 100f)
            else 0f
        _currentDuration.value = currentProgress.formatDuration()

    }

    private fun loadAudioData() = viewModelScope.launch {
        val audios = repository.getAudioData()
        _audioList.value = audios
        setMediaItems()
    }


    private fun setMediaItems() {
        _audioList.value.map { audio: Audio ->
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
            saveStates()
        }
        super.onCleared()
    }

    private fun saveStates() {
        savedStateHandle[AUDIO_LIST] = audioList
        savedStateHandle[PROGRESS] = progress
        savedStateHandle[IS_PLAYING] = isPlaying
        savedStateHandle[CURRENT_DURATION] = currentDuration
        savedStateHandle[CURRENT_SELECTED_AUDIO] = currentSelectedAudio
    }

    companion object {
        const val AUDIO_LIST = "audioList"
        const val PROGRESS = "progress"
        const val IS_PLAYING = "isPlaying"
        const val CURRENT_DURATION = "currentDuration"
        const val CURRENT_SELECTED_AUDIO = "currentSelectedAudio"
    }
}

