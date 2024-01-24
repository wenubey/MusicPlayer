package com.wenubey.musicplayer.player.service


import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.wenubey.musicplayer.di.AppModule.MainDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class MusicPlayerServiceHandler @Inject constructor(
    private val exoPlayer: ExoPlayer,
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher
) : Player.Listener {

    private val _audioState: MutableStateFlow<MusicPlayerState> =
        MutableStateFlow(MusicPlayerState.Initial)
    val audioState: StateFlow<MusicPlayerState> get() = _audioState.asStateFlow()

    private var job: Job? = null

    init {
        exoPlayer.addListener(this)
    }

    fun addMediaItem(mediaItem: MediaItem) {
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
    }

    fun setMediaItemList(mediaItems: List<MediaItem>) {
        exoPlayer.setMediaItems(mediaItems)
        exoPlayer.prepare()
    }


    suspend fun onPlayerEvent(
        playerEvent: PlayerEvent,
        selectedAudioIndex: Int = -1,
        seekPosition: Long = 0,
    ) {
        Log.w(TAG, "playerEvent: $playerEvent")
        when (playerEvent) {
            is PlayerEvent.PlayPause -> playOrPause()
            is PlayerEvent.SeekTo -> exoPlayer.seekTo(seekPosition)
            is PlayerEvent.Backward -> exoPlayer.seekBack()
            is PlayerEvent.Forward -> exoPlayer.seekForward()
            is PlayerEvent.SeekToNext -> {
                if (!exoPlayer.hasNextMediaItem()) {
                    exoPlayer.seekTo(0, 0)
                } else {
                    exoPlayer.seekToNext()
                }
            }
            is PlayerEvent.SeekToPrevious -> exoPlayer.seekToPrevious()
            is PlayerEvent.Stop -> stopProgressUpdate()
            is PlayerEvent.UpdateProgress -> {
                val xd = (exoPlayer.duration * playerEvent.newProgress).toLong()
                exoPlayer.seekTo(
                    xd
                )
            }
            is PlayerEvent.SelectedAudioChange -> {
                when (selectedAudioIndex) {
                    exoPlayer.currentMediaItemIndex -> {
                        playOrPause()
                    }

                    else -> {
                        exoPlayer.seekToDefaultPosition(selectedAudioIndex)
                        _audioState.value = MusicPlayerState.Playing(
                            isPlaying = true
                        )
                        exoPlayer.playWhenReady = true
                        startProgressUpdate()
                    }
                }
            }


        }
    }


    private suspend fun playOrPause() {
        if (exoPlayer.isPlaying) {
            exoPlayer.pause()
            stopProgressUpdate()
        } else {
            exoPlayer.play()
            _audioState.value = MusicPlayerState.Playing(
                isPlaying = true
            )
            startProgressUpdate()
        }
    }

    private suspend fun startProgressUpdate() = job.run {
        while (true) {
            delay(500)
            _audioState.value = MusicPlayerState.Progress(exoPlayer.currentPosition)
        }
    }

    private fun stopProgressUpdate() {
        job?.cancel()
        job = null
        _audioState.value = MusicPlayerState.Playing(isPlaying = false)
    }


    override fun onIsPlayingChanged(isPlaying: Boolean) {
        _audioState.value = MusicPlayerState.Playing(isPlaying = isPlaying)
    }

    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
        super.onMediaItemTransition(mediaItem, reason)
        CoroutineScope(mainDispatcher).launch {
            if (mediaItem != null) {
                _audioState.value = MusicPlayerState.CurrentPlaying(exoPlayer.currentMediaItemIndex)
                startProgressUpdate()
            } else {
                stopProgressUpdate()
            }
        }
    }


    override fun onPlaybackStateChanged(playbackState: Int) {
        when (playbackState) {
            ExoPlayer.STATE_BUFFERING -> _audioState.value =
                MusicPlayerState.Buffering(exoPlayer.currentPosition)

            ExoPlayer.STATE_READY -> {
                _audioState.value = MusicPlayerState.Ready
                _audioState.value = MusicPlayerState.CurrentPlaying(exoPlayer.currentMediaItemIndex)
            }

            Player.STATE_ENDED -> {}
            Player.STATE_IDLE -> {}
        }
    }

    companion object {
        private const val TAG = "MusicPlayerServiceHandler"
    }
}