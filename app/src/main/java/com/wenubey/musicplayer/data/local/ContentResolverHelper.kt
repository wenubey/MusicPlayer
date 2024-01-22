package com.wenubey.musicplayer.data.local

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.WorkerThread
import dagger.hilt.android.qualifiers.ApplicationContext
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

    private var selectionQuery = "${MediaStore.Audio.AudioColumns.IS_MUSIC} = ? AND ${MediaStore.Audio.Media.MIME_TYPE} = ?"
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
            val displayNameColumn  = it.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DISPLAY_NAME)
            val artistColumn  = it.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ARTIST)
            val dataColumn  = it.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DATA)
            val durationColumn  = it.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DURATION)
            val titleColumn  = it.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.TITLE)

            it.apply {
                if (count == 0) {
                    Log.e(TAG, "getCursorData: Cursor is Empty")
                } else {
                    Log.w(TAG, "getCursorData:Success")
                    while (it.moveToNext()) {
                        val displayName = getString(displayNameColumn)
                        val id = getLong(idColumn)
                        val artist = getString(artistColumn)
                        val data  = getString(dataColumn)
                        val duration  = getInt(durationColumn)
                        val title  = getString(titleColumn)
                        val uri   = ContentUris.withAppendedId(
                            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                            id
                        )

                        audioList += Audio(
                            displayName = displayName,
                            id = id,
                            artist = artist,
                            data = data,
                            duration = duration,
                            title = title,
                            uri = uri
                        )
                    }
                }
            }
        }
        return audioList
    }

    companion object {
        private const val TAG = "contentResolverHelper"
    }
}