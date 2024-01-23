package com.wenubey.musicplayer.ui.audio

sealed class UiEvent {
    data object PlayPause : UiEvent()
    data class SelectedAudioChange(val index: Int) : UiEvent()
    data class SeekTo(val position: Float) : UiEvent()
    data object SeekToNext : UiEvent()
    data object SeekToPrevious : UiEvent()
    data object Backward : UiEvent()
    data object Forward : UiEvent()
    data class UpdateProgress(val newProgress: Float) : UiEvent()
}