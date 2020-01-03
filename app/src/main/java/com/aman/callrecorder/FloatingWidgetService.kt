package com.aman.callrecorder

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.media.MediaRecorder
import android.os.Build
import android.os.Environment
import android.os.IBinder
import android.view.*
import android.view.View.OnTouchListener
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt


class FloatingWidgetService : Service() {

    companion object {
        val FOLDER_PATH = Environment.getExternalStorageDirectory().toString() + "/Call Recordings"
    }

    lateinit var mWindowManager: WindowManager
    lateinit var mOverlayView: View
    private val recorder = MediaRecorder()
    private lateinit var rootLayout: LinearLayout
    private lateinit var textView: TextView
    private lateinit var recordButton: ImageButton
    private lateinit var closeButton: ImageButton
    private lateinit var recordingLogo: ImageButton
    private lateinit var recordingMessage: TextView
    private var isRecording = false

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        setTheme(R.style.AppTheme)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        // difference in layout params devices greater than O
        val layoutFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }

        mOverlayView = LayoutInflater.from(this).inflate(R.layout.overlay_layout, null)
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutFlag,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        //Specify the view position
        params.gravity = Gravity.TOP or Gravity.START
        params.x = 50
        params.y = 700

        mWindowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        mWindowManager.addView(mOverlayView, params)

        rootLayout = mOverlayView.findViewById(R.id.overlay_root_layout)
        textView = mOverlayView.findViewById(R.id.overlay_text_view)
        recordButton = mOverlayView.findViewById(R.id.overlay_record_button)
        closeButton = mOverlayView.findViewById(R.id.overlay_close_button)
        recordingLogo = mOverlayView.findViewById(R.id.overlay_recording_icon)
        recordingMessage = mOverlayView.findViewById(R.id.overlay_text_view_recording)

        recordButton.setOnClickListener {
            isRecording = true
            changeFloatingWidgetView()
            startRecording()
        }

        closeButton.setOnClickListener { stopSelf() }

        rootLayout.setOnTouchListener(object : OnTouchListener {
            private var initialX = 0
            private var initialY = 0
            private var initialTouchX = 0f
            private var initialTouchY = 0f
            override fun onTouch(v: View?, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        //remember the initial position.
                        initialX = params.x
                        initialY = params.y
                        //get the touch location
                        initialTouchX = event.rawX
                        initialTouchY = event.rawY
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        val xDiff = (event.rawX - initialTouchX).roundToInt()
                        val yDiff = (event.rawY - initialTouchY).roundToInt()

                        //Calculate the X and Y coordinates of the view.
                        params.x = initialX + xDiff
                        params.y = initialY + yDiff

                        //Update the layout with new X & Y coordinates
                        mWindowManager.updateViewLayout(mOverlayView, params)
                        return true
                    }
                }
                return false
            }
        })

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        mWindowManager.removeView(mOverlayView)
        stopRecorder()
    }

    private fun changeFloatingWidgetView() {
        textView.visibility = View.GONE
        recordButton.visibility = View.GONE
        closeButton.visibility = View.GONE
        recordingLogo.visibility = View.VISIBLE
        recordingMessage.visibility = View.VISIBLE
    }

    private fun startRecording() {
        val fileName = "REC-${getCurrentDateTime()}.3gp"
        val path = "$FOLDER_PATH/$fileName"
        recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION)
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
        recorder.setOutputFile(path)
        recorder.prepare()
        recorder.start()
    }

    private fun stopRecorder() {
        if (isRecording) {
            recorder.stop()
            recorder.reset()
        }
        recorder.release()
    }

    private fun getCurrentDateTime() =
        SimpleDateFormat("dd-MMM-yyyy-hh-mm-ss-Sa", Locale.ENGLISH).format(Date())


}