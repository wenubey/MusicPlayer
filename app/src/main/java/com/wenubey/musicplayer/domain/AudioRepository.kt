package com.wenubey.musicplayer.domain

import com.wenubey.musicplayer.data.local.Audio

interface AudioRepository {

    suspend fun getAudioData(): List<Audio>
}