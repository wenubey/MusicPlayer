package com.wenubey.musicplayer.utils

import java.util.concurrent.TimeUnit

fun Long.formatDuration(): String {
    val minute = TimeUnit.MINUTES.convert(this, TimeUnit.MILLISECONDS)
    val seconds = (minute) - minute * TimeUnit.SECONDS.convert(1, TimeUnit.MINUTES)
    return String.format("%02d:%02d", minute, seconds)
}