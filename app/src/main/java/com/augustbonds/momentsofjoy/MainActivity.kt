package com.augustbonds.momentsofjoy

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.SeekBar
import android.widget.TextView
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class MainActivity : Activity() {
    companion object {
        const val CHANNEL_ID = "reminders"
        const val FREQUENCY_PREF_KEY = "notification frequency"
        const val UNIQUE_WORK_NAME = "friendly reminder"
    }

    var notificationFrequency = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Logger.debug("onCreate called")
        setContentView(R.layout.activity_main)
        createNotificationChannel()
        loadSharedPrefs()
        setupSeekBar()
    }

    override fun onResume() {
        super.onResume()
        Logger.debug("onResume called")
    }

    override fun onPause() {
        super.onPause()
        Logger.debug("onPause called")
        saveSharedPrefs()
    }

    private fun scheduleWork() {
        Logger.debug("scheduleWork called")

        if (notificationFrequency == 0) {
            //If the user has requested to remove notifications, we are done.
            cancelWork()
            return
        }


        val hoursBetween = when (notificationFrequency) {
            1 -> 8
            2 -> 5
            3 -> 3
            else -> Long.MAX_VALUE
        }

        val createWorkRequest = createWorkRequest(hoursBetween)
        WorkManager.getInstance(this)
                .enqueueUniquePeriodicWork(UNIQUE_WORK_NAME, ExistingPeriodicWorkPolicy.REPLACE, createWorkRequest)
    }

    private fun cancelWork() {
        Logger.debug("cancelWork called")
        WorkManager.getInstance(this).cancelUniqueWork(UNIQUE_WORK_NAME)
    }

    private fun createWorkRequest(intervalHours: Long): PeriodicWorkRequest {
        return PeriodicWorkRequestBuilder<NotificationWorker>(intervalHours, TimeUnit.HOURS).build()
    }

    private fun setupSeekBar() {
        val notificationFrequencyDescriptions = resources.getTextArray(R.array.reminder_frequency_descriptions)

        val notificationFrequencyDescriptionTextView = findViewById<TextView>(R.id.notificationFrequencyDescription)
        notificationFrequencyDescriptionTextView.text = notificationFrequencyDescriptions[notificationFrequency]

        val seekBar = findViewById<SeekBar>(R.id.notificationFrequencySeekBar)
        seekBar.progress = notificationFrequency

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                //do nothing
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                //do nothing
            }

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                notificationFrequency = progress
                notificationFrequencyDescriptionTextView.text = notificationFrequencyDescriptions[notificationFrequency]
                scheduleWork()
            }
        })
    }

    private fun createNotificationChannel() {
        Logger.debug("createNotificationChannel called")
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager = getNotificationManager()
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun getNotificationManager(): NotificationManager {
        return getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    private fun loadSharedPrefs() {
        val preferenceManager = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        notificationFrequency = preferenceManager.getInt(FREQUENCY_PREF_KEY, 0)
    }

    private fun saveSharedPrefs() {
        val preferenceManager = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val editPrefs = preferenceManager.edit()
        editPrefs.putInt(FREQUENCY_PREF_KEY, notificationFrequency)
        editPrefs.apply()
    }
}