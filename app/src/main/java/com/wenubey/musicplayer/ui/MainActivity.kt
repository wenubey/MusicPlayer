package com.wenubey.musicplayer.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.wenubey.musicplayer.player.service.MusicPlayerService
import com.wenubey.musicplayer.ui.audio.AudioViewModel
import com.wenubey.musicplayer.ui.audio.Home
import com.wenubey.musicplayer.ui.audio.UiEvent
import com.wenubey.musicplayer.ui.theme.MusicPlayerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: AudioViewModel by viewModels()
    private var isServiceRunning = false

    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MusicPlayerTheme {
                val rememberExternalStoragePermission =
                    rememberPermissionState(permission = Manifest.permission.READ_EXTERNAL_STORAGE)
                val lifecycleOwner = LocalLifecycleOwner.current
                DisposableEffect(key1 = lifecycleOwner) {
                    val observer = LifecycleEventObserver { _, event ->
                        if (event == Lifecycle.Event.ON_RESUME) {
                            rememberExternalStoragePermission.launchPermissionRequest()
                        }
                    }
                    lifecycleOwner.lifecycle.addObserver(observer)
                    onDispose {
                        lifecycleOwner.lifecycle.removeObserver(observer)
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Home(
                        progress = viewModel.progress.value,
                        onProgress = { viewModel.onUiEvent(UiEvent.SeekTo(it)) },
                        isAudioPlaying = viewModel.isPlaying.value,
                        currentPlayingAudio = viewModel.currentSelectedAudio.value,
                        audioList = viewModel.audioList.value,
                        onPlayPause = { viewModel.onUiEvent(UiEvent.PlayPause) },
                        onItemClick = {
                            viewModel.onUiEvent(UiEvent.SelectedAudioChange(it))
                            startPlayerService()
                        },
                        onNext = { viewModel.onUiEvent(UiEvent.SeekToNext) },
                        onPrevious = {viewModel.onUiEvent(UiEvent.SeekToPrevious)},
                        progressString = viewModel.currentDuration.value,
                    )
                }
            }
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun startPlayerService() {
        if (!isServiceRunning) {
            val intent = Intent(this, MusicPlayerService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            } else {
                startService(intent)
            }
            isServiceRunning = true
        }
    }
}
