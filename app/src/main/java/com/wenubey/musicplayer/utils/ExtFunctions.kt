package com.wenubey.musicplayer.utils

import java.util.concurrent.TimeUnit
import kotlin.math.floor

fun Long.formatDuration(): String {
    val minute = TimeUnit.MINUTES.convert(this, TimeUnit.MILLISECONDS)
    val seconds = (minute) - minute * TimeUnit.SECONDS.convert(1, TimeUnit.MINUTES)
    return String.format("%02d:%02d", minute, seconds)
}


 fun Long.timeStampToDuration(): String {
    val totalSecond = floor(this / 1E3).toInt()
    val minutes = totalSecond / 60
    val remainingSeconds = totalSecond - (minutes * 60)
    return if (this < 0) "--:--"
    else "%d:%02d".format(minutes, remainingSeconds)
}