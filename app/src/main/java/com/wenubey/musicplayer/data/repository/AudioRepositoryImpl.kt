package com.wenubey.musicplayer.data.repository

import com.wenubey.musicplayer.data.local.Audio
import com.wenubey.musicplayer.data.local.ContentResolverHelper
import com.wenubey.musicplayer.di.AppModule.IoDispatcher
import com.wenubey.musicplayer.domain.AudioRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AudioRepositoryImpl @Inject constructor(
    private val contentResolverHelper: ContentResolverHelper,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
): AudioRepository {
    override suspend fun getAudioData(): List<Audio> = withContext(ioDispatcher) {
        contentResolverHelper.getAudioData()
    }
}