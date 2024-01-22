package com.wenubey.musicplayer.ui.audio

sealed class UiState {
    data object Initial : UiState()
    data object Ready : UiState()
}