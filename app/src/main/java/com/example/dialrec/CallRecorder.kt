package com.example.dialrec

import android.content.ContentValues
import android.content.Context
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import java.io.OutputStream

class CallRecorder(private val context: Context) {
    private var mediaRecorder: MediaRecorder? = null
    private var outputFileUri: Uri? = null

    fun startRecording() {
        Log.d("CallRecorder", "startRecording")

        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "recording_${System.currentTimeMillis()}.3gp")
            put(MediaStore.MediaColumns.MIME_TYPE, "audio/3gpp")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, "Music/Recordings")
            }
        }

        outputFileUri = context.contentResolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values)

        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

            outputFileUri?.let { uri ->
                val parcelFileDescriptor = context.contentResolver.openFileDescriptor(uri, "w")
                setOutputFile(parcelFileDescriptor?.fileDescriptor)
            }

            try {
                prepare()
                start()
            } catch (e: Exception) {
                Log.e("CallRecorder", "startRecording failed", e)
            }
        }
    }

    fun stopRecording() {
        Log.d("CallRecorder", "stopRecording")
        try {
            mediaRecorder?.apply {
                stop()
                release()
            }
            mediaRecorder = null

            // Write an empty byte array to the output file to trigger the media scanner
            outputFileUri?.let { uri ->
                context.contentResolver.openOutputStream(uri)?.use(OutputStream::close)
            }
        } catch (e: Exception) {
            Log.e("CallRecorder", "stopRecording failed", e)
        }
    }
}