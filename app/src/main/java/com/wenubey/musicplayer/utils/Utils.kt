package com.wenubey.musicplayer.utils

import androidx.core.net.toUri
import com.wenubey.musicplayer.data.local.Audio

object Utils {
    val fakeAudio = Audio(
        "".toUri(), "Music", 0L, "Artist", "", 100, "TED: Sharon Weinberger (2020 Salon)", "".toUri()
    )
}