package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.udacity.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0
    private lateinit var binding: ActivityMainBinding

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        notificationManager = ContextCompat.getSystemService(
            applicationContext,
            NotificationManager::class.java
        ) as NotificationManager

        createChannel(
            getString(R.string.download_notification_channel_id),
            getString(R.string.download_notification_channel_name)
        )

        Loading_button.setOnClickListener {
            val downloadOption = getType()
            if (downloadOption == null)
                Toast.makeText(
                    this,
                    getString(R.string.not_select_item_massage),
                    Toast.LENGTH_SHORT
                ).show()
            else {
                binding.LoadingButton.start()
                try {
                    download(getUrl(downloadOption))
                } catch (e: Exception) {
                    Log.d("MAINX", "ERROR")
                }
            }
        }
        }

    private fun getType(): type? {
        return when (radioGroup.checkedRadioButtonId) {
            R.id.gildeButton -> type.Glide
            R.id.loadAppButton -> type.LoadApp
            R.id.retrofitButton -> type.Retrofit
            else -> null
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            var Message: DownloadDetails? = null
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (id == downloadID) {
                val q = DownloadManager.Query()
                q.setFilterById(id)
                val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                val c: Cursor = downloadManager.query(q)
                if (c.moveToFirst()) {
                    binding.LoadingButton.stop()
                    val status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS))
                    val title = c.getString(c.getColumnIndex(DownloadManager.COLUMN_TITLE))
                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        Message = DownloadDetails(title, DownloadStatus.SUCCESS)
                    } else if (status == DownloadManager.STATUS_FAILED) {
                        Message = DownloadDetails(title, DownloadStatus.FAILED)
                    }
                    notificationManager.sendNotification(Message!!, applicationContext)
                }
            }
        }

    }

    private fun download(url: String) {

        val request =
            DownloadManager.Request(Uri.parse(url))
                .setTitle(title)
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)


        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }
    private fun getUrl(Type: type): String {
        return when (Type) {
            type.Glide -> resources.getString(R.string.glide_url)
            type.LoadApp -> resources.getString(R.string.load_app_url)
            type.Retrofit -> resources.getString(R.string.retrofit_url)
        }
    }
    private fun getDescription(downloadOption: type): String {
        return when (downloadOption) {
            type.Glide -> resources.getString(R.string.glide)
            type.LoadApp -> resources.getString(R.string.load_app)
            type.Retrofit -> resources.getString(R.string.retrofit)
        }
    }
    companion object {
        private const val URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val CHANNEL_ID = "channelId"
    }
    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = getString(R.string.download_is_done)

            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

}
