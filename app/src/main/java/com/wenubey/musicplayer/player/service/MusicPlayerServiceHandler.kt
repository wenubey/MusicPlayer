package com.wenubey.musicplayer.player.service


import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.wenubey.musicplayer.di.AppModule.MainDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
        when (playerEvent) {
            is PlayerEvent.PlayPause -> playOrPause()
            is PlayerEvent.SeekTo -> exoPlayer.seekTo(seekPosition)
            is PlayerEvent.Backward -> exoPlayer.seekBack()
            is PlayerEvent.Forward -> exoPlayer.seekForward()
            is PlayerEvent.SeekToNext -> exoPlayer.seekToNext()
            is PlayerEvent.Stop -> stopProgressUpdate()
            is PlayerEvent.UpdateProgress -> {
                exoPlayer.seekTo(
                    (exoPlayer.duration * playerEvent.newProgress).toLong()
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

    private suspend fun startProgressUpdate()  = job.run {
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

    @OptIn(DelicateCoroutinesApi::class)
    override fun onIsPlayingChanged(isPlaying: Boolean) {
        _audioState.value = MusicPlayerState.Playing(isPlaying = isPlaying)
        _audioState.value = MusicPlayerState.CurrentPlaying(exoPlayer.currentMediaItemIndex)
        if (isPlaying) {
            GlobalScope.launch(mainDispatcher) {
                startProgressUpdate()
            }
        } else {
            stopProgressUpdate()
        }
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        when (playbackState) {
            ExoPlayer.STATE_BUFFERING -> _audioState.value =
                MusicPlayerState.Buffering(exoPlayer.currentPosition)

            ExoPlayer.STATE_READY -> _audioState.value =
                MusicPlayerState.Ready(exoPlayer.duration)
        }
    }
}