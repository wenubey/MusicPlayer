package com.wenubey.musicplayer.utils

import android.util.Log
import java.util.concurrent.TimeUnit
import kotlin.math.floor

fun Long.formatDuration(): String {
    val minutes = TimeUnit.MINUTES.convert(this, TimeUnit.MILLISECONDS)
    val seconds = TimeUnit.SECONDS.convert(this, TimeUnit.MILLISECONDS) - minutes * 60
    return String.format("%02d:%02d", minutes, seconds)
}


 fun Long.timeStampToDuration(): String {
    val totalSecond = floor(this / 1E3).toInt()
    val minutes = totalSecond / 60
    val remainingSeconds = totalSecond - (minutes * 60)
    return if (this < 0) "--:--"
    else "%d:%02d".format(minutes, remainingSeconds)
}