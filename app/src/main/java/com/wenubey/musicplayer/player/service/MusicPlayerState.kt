package com.wenubey.musicplayer.player.service

sealed class MusicPlayerState {
    data object Initial: MusicPlayerState()
    data object Ready: MusicPlayerState()
    data class Progress(val progress: Long): MusicPlayerState()
    data class Buffering(val progress: Long): MusicPlayerState()
    data class Playing(val isPlaying: Boolean): MusicPlayerState()
    data class CurrentPlaying(val mediaItemIndex: Int): MusicPlayerState()
}