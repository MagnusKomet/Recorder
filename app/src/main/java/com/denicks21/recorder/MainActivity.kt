package com.denicks21.recorder

import android.Manifest.permission
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.io.IOException

class MainActivity : AppCompatActivity() {
    lateinit var startTV: TextView
    lateinit var stopTV: TextView
    lateinit var playTV: TextView
    lateinit var stopplayTV: TextView
    lateinit var statusTV: TextView
    private var mRecorder: MediaRecorder? = null
    private var mPlayer: MediaPlayer? = null
    var mFileName: File? = null
    var escalaButton: Float = 0.8f


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        statusTV = findViewById(R.id.idTVstatus)
        startTV = findViewById(R.id.btnRecord)
        stopTV = findViewById(R.id.btnStop)
        playTV = findViewById(R.id.btnPlay)
        stopplayTV = findViewById(R.id.btnStopPlay)

        startTV.setOnClickListener {
            startRecording()
        }

        startTV.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    v.animate().scaleX(escalaButton).scaleY(escalaButton).setDuration(100).start()
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    v.animate().scaleX(1f).scaleY(1f).setDuration(100).start()
                }
            }
            false
        }

        stopTV.setOnClickListener {
            stopRecording()
        }

        stopTV.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    v.animate().scaleX(escalaButton).scaleY(escalaButton).setDuration(100).start()
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    v.animate().scaleX(1f).scaleY(1f).setDuration(100).start()
                }
            }
            false
        }

        playTV.setOnClickListener {
            playAudio()
        }

        playTV.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    v.animate().scaleX(escalaButton).scaleY(escalaButton).setDuration(100).start()
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    v.animate().scaleX(1f).scaleY(1f).setDuration(100).start()
                }
            }
            false
        }

        stopplayTV.setOnClickListener {
            stopAudio()
        }

        stopplayTV.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    v.animate().scaleX(escalaButton).scaleY(escalaButton).setDuration(100).start()
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    v.animate().scaleX(1f).scaleY(1f).setDuration(100).start()
                }
            }
            false
        }
    }

    private fun startRecording() {

        // Check permissions
        if (CheckPermissions()) {

            // Save file
            mFileName = File(getExternalFilesDir("")?.absolutePath,"Record.3gp")

            // If file exists then increment counter
            var n = 0
            while (mFileName!!.exists()) {
                n++
                mFileName = File(getExternalFilesDir("")?.absolutePath,"Record$n.3gp")
            }

            // Initialize the class MediaRecorder
            mRecorder = MediaRecorder()

            // Set source to get audio
            mRecorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)

            // Set the format of the file
            mRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)

            // Set the audio encoder
            mRecorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

            // Set the save path
            mRecorder!!.setOutputFile(mFileName)
            try {
                // Preparation of the audio file
                mRecorder!!.prepare()
            } catch (e: IOException) {
                Log.e("TAG", "prepare() failed")
            }
            // Start the audio recording
            mRecorder!!.start()
            statusTV.text = "Recording in progress"
        } else {
            // Request permissions
            RequestPermissions()
        }
    }

    fun stopRecording() {

        // Stop recording
        if (mFileName == null || mRecorder == null) {

            // Message
            Toast.makeText(getApplicationContext(), "Registration not started", Toast.LENGTH_LONG).show()

        } else {

            try {
                mRecorder!!.stop()

                // Message to confirm save file
                val savedUri = Uri.fromFile(mFileName)
                val msg = "File saved: " + savedUri!!.lastPathSegment
                Toast.makeText(applicationContext, msg, Toast.LENGTH_LONG).show()

                // Release the class mRecorder
                mRecorder!!.release()
                mRecorder = null
                statusTV.text = "Recording saved to a file"

            } catch (e: IllegalStateException) {
                Toast.makeText(applicationContext, "Error saving the recording", Toast.LENGTH_LONG).show()
            }

        }
    }

    fun playAudio() {
        if (mFileName != null) {
            if (mPlayer == null) {
                // Inicializar y preparar el MediaPlayer si es null
                mPlayer = MediaPlayer()

                mPlayer!!.setDataSource(mFileName.toString())

                // Fetch the source of the mPlayer
                mPlayer!!.prepare()
            }

            if (mPlayer!!.isPlaying) {
                // Pausar la reproducción si está reproduciendo
                mPlayer!!.pause()
                statusTV.text = "Playback paused"
            } else {
                // Reanudar la reproducción si está en pausa
                mPlayer!!.start()
                statusTV.text = "Listening recording playback"
            }

        } else {
            Toast.makeText(applicationContext, "Recording not found", Toast.LENGTH_LONG).show()
        }
    }


    fun stopAudio() {

        if (mPlayer != null) {
            mPlayer!!.stop()
            mPlayer!!.release()
            mPlayer = null
            statusTV.text = "Playback stopped"
        }
    }



    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // If permissions accepted ->
        when (requestCode) {
            REQUEST_AUDIO_PERMISSION_CODE -> if (grantResults.size > 0) {
                val permissionToRecord = grantResults[0] == PackageManager.PERMISSION_GRANTED
                val permissionToStore = grantResults[1] == PackageManager.PERMISSION_GRANTED
                if (permissionToRecord && permissionToStore) {

                    // Message
                    Toast.makeText(applicationContext, "Permission Granted", Toast.LENGTH_LONG).show()

                } else {

                    // Message
                    Toast.makeText(applicationContext, "Permission Denied", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    fun CheckPermissions(): Boolean {

        // Check permissions
        val result =
            ContextCompat.checkSelfPermission(applicationContext, permission.WRITE_EXTERNAL_STORAGE)
        val result1 = ContextCompat.checkSelfPermission(applicationContext, permission.RECORD_AUDIO)
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED
    }

    private fun RequestPermissions() {

        // Request permissions
        ActivityCompat.requestPermissions(this,
            arrayOf(permission.RECORD_AUDIO, permission.WRITE_EXTERNAL_STORAGE),
            REQUEST_AUDIO_PERMISSION_CODE)
    }

    companion object {
        const val REQUEST_AUDIO_PERMISSION_CODE = 1
    }
}