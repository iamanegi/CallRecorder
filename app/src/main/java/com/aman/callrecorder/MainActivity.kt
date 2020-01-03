package com.aman.callrecorder

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File


class MainActivity : AppCompatActivity() {

    companion object {
        private const val DRAW_OVER_OTHER_APP_PERMISSION = 123
    }

    lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        askForPermissions()

        checkAndCreateDirectory()

        recyclerView = findViewById(R.id.main_recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

    }

    override fun onResume() {
        super.onResume()
        val callRecordingList = getRecordingList()
        recyclerView.adapter = RecordingListAdapter(callRecordingList)
    }

    private fun getRecordingList(): Array<out File>? {
        val directory = File(FloatingWidgetService.FOLDER_PATH)
        return directory.listFiles()
    }

    private fun checkAndCreateDirectory() {
        val file = File(FloatingWidgetService.FOLDER_PATH)
        if (!file.exists()) {
            file.mkdir()
        }
    }

    private fun askForPermissions() {
        // system overlay permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            startActivityForResult(intent, DRAW_OVER_OTHER_APP_PERMISSION)
        }

        val permissionNeeded = arrayOf(
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        val permissionsNotGranted = ArrayList<String>()

        for (permission in permissionNeeded) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsNotGranted.add(permission)
            }
        }

        val array = arrayOfNulls<String>(permissionsNotGranted.size)

        if (array.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionsNotGranted.toArray(array), 0
            )
        }
    }
}
