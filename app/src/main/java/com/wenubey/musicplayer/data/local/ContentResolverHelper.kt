package com.wenubey.musicplayer.data.local

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.WorkerThread
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class ContentResolverHelper @Inject constructor(
    @ApplicationContext val context: Context
) {

    private var cursor: Cursor? = null

    private val projection = arrayOf(
        MediaStore.Audio.AudioColumns.DISPLAY_NAME,
        MediaStore.Audio.AudioColumns._ID,
        MediaStore.Audio.AudioColumns.ARTIST,
        MediaStore.Audio.AudioColumns.DATA,
        MediaStore.Audio.AudioColumns.DURATION,
        MediaStore.Audio.AudioColumns.TITLE,
    )

    private var selectionQuery =
        "${MediaStore.Audio.AudioColumns.IS_MUSIC} = ? AND ${MediaStore.Audio.Media.MIME_TYPE} = ?"
    private var selectionArgs = arrayOf("1", "audio/mpeg")

    private val sortOrder = "${MediaStore.Audio.AudioColumns.DISPLAY_NAME} ASC"

    @WorkerThread
    fun getAudioData(): List<Audio> {
        return getCursorData()
    }

    private fun getCursorData(): MutableList<Audio> {
        val audioList = mutableListOf<Audio>()

        cursor = context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selectionQuery,
            selectionArgs,
            sortOrder,
        )

        cursor?.use {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns._ID)
            val displayNameColumn =
                it.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DISPLAY_NAME)
            val artistColumn = it.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ARTIST)
            val dataColumn = it.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DATA)
            val durationColumn = it.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DURATION)
            val titleColumn = it.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.TITLE)

            it.apply {
                if (count == 0) {
                    Log.e(TAG, "getCursorData: Cursor is Empty")
                } else {
                    Log.w(TAG, "getCursorData:Success")
                    while (it.moveToNext()) {
                        val displayName = getString(displayNameColumn)
                        val id = getLong(idColumn)
                        val artist = getString(artistColumn)
                        val data = getString(dataColumn)
                        val duration = getInt(durationColumn)
                        val title = getString(titleColumn)
                        val uri = ContentUris.withAppendedId(
                            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                            id
                        )

                        val albumArtUri  = getAlbumArtUri(data)
                        audioList += Audio(
                            displayName = displayName,
                            id = id,
                            artist = artist,
                            data = data,
                            duration = duration,
                            title = title,
                            uri = uri,
                            albumArtUri = albumArtUri
                        )
                    }
                }
            }
        }
        return audioList
    }


    private fun getAlbumArtUri(filePath: String): Uri? {
        val retriever = MediaMetadataRetriever()
        return try {
            retriever.setDataSource(filePath)
            val artBytes = retriever.embeddedPicture
            if (artBytes != null) {
                val bitmap = BitmapFactory.decodeByteArray(artBytes, 0, artBytes.size)
                // Save bitmap to cache directory and return its Uri
                saveAlbumArtToCache(bitmap, filePath)
            } else null
        } catch (e: Exception) {
            Log.e(TAG, "getAlbumArtUri:Error", e)
            null
        } finally {
            retriever.release()
        }
    }

    private fun saveAlbumArtToCache(bitmap: Bitmap, filePath: String): Uri? {
        val cacheDir = context.cacheDir
        if (!cacheDir.exists()) {
            cacheDir.mkdirs()
        }

        val fileName = "album_art_" + filePath.hashCode() + ".png"
        val file = File(cacheDir, fileName)

        return try {
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            Uri.fromFile(file)
        } catch (e: Exception) {
            Log.e(TAG, "saveAlbumArtToCache:Error", e)
            null
        }
    }

    companion object {
        private const val TAG = "contentResolverHelper"
    }
}