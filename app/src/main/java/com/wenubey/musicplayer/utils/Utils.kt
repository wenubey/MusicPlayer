package com.wenubey.musicplayer.utils

import androidx.core.net.toUri
import com.wenubey.musicplayer.data.local.Audio

object Utils {
    val fakeAudio = Audio(
        "".toUri(), "", 0L, "", "", 0, ""
    )
}