package com.wenubey.musicplayer.utils

import androidx.core.net.toUri
import com.wenubey.musicplayer.data.local.Audio

object Utils {
    val fakeAudio = Audio(
        "".toUri(), "FAKE", 0L, "", "", 0, "", "".toUri()
    )
}