package com.wenubey.musicplayer.player.notification

import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.toBitmap
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerNotificationManager
import coil.ImageLoader
import coil.request.ImageRequest
import coil.target.Target

@UnstableApi
class MusicPlayerNotificationAdapter(
    private val context: Context,
    private val pendingIntent: PendingIntent?
) : PlayerNotificationManager.MediaDescriptionAdapter {
    override fun getCurrentContentTitle(player: Player): CharSequence =
        player.mediaMetadata.albumTitle ?: UNKNOWN

    override fun createCurrentContentIntent(player: Player): PendingIntent? = pendingIntent


    override fun getCurrentContentText(player: Player): CharSequence =
        player.mediaMetadata.displayTitle ?: UNKNOWN

    override fun getCurrentLargeIcon(
        player: Player,
        callback: PlayerNotificationManager.BitmapCallback
    ): Bitmap? {
        val imageLoader = ImageLoader(context)
        val request = ImageRequest.Builder(context)
            .data(player.mediaMetadata.artworkUri)
            .allowHardware(false)
            .target(object : Target {
                override fun onSuccess(result: Drawable) {
                    callback.onBitmap(result.toBitmap())
                }
            })
            .build()

        imageLoader.enqueue(request)
        return null
    }

    companion object {
        private const val UNKNOWN = "Unknown"
    }
}