package com.augustbonds.momentsofjoy

import android.app.*
import android.content.Context
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.SeekBar
import android.widget.TextView
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    companion object {
        const val CHANNEL_ID = "reminders"
        const val FREQUENCY_PREF_KEY = "notificationFrequency"
    }

    var notificationFrequency = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createNotificationChannel()

        loadSharedPrefs()

        setupSeekBar()
    }

    override fun onResume() {
        super.onResume()
        Logger.info("Statuses: ")
        WorkManager.getInstance().getStatusesByTag("notification").value?.forEach { Logger.info("Status: $it") }
    }

    override fun onPause() {
        super.onPause()
        Logger.info("pause called")
        saveSharedPrefs()
    }

    private fun scheduleWork() {
        //Remove previous scheduling
        cancelWork()

        if (notificationFrequency == 0){
            //If the user has requested to remove notifications, we are done.
            return
        }


        val hoursBetween = when (notificationFrequency) {
            1 -> 8
            2 -> 5
            3 -> 3
            else -> Long.MAX_VALUE
        }

        val workRequestBuilder = PeriodicWorkRequestBuilder<NotificationWorker>(hoursBetween, TimeUnit.HOURS)
                .addTag("notification")

        WorkManager.getInstance().enqueue(workRequestBuilder.build())
        val workQueueSize = WorkManager.getInstance().getStatusesByTag("notification").value?.size
        if (workQueueSize != null){
            Logger.info("queue size after enqueue: $workQueueSize")
        } else {
            Logger.info("Failed to add work to queue")
        }
    }

    private fun cancelWork(){
        WorkManager.getInstance().cancelAllWork()
    }

    private fun setupSeekBar(){
        val notificationFrequencyDescriptions = resources.getTextArray(R.array.reminder_frequency_descriptions)

        val notificationFrequencyDescriptionTextView = findViewById<TextView>(R.id.notificationFrequencyDescription)
        notificationFrequencyDescriptionTextView.text = notificationFrequencyDescriptions[notificationFrequency]

        val seekBar = findViewById<SeekBar>(R.id.notificationFrequencySeekBar)
        seekBar.progress = notificationFrequency

        seekBar.setOnSeekBarChangeListener( object : SeekBar.OnSeekBarChangeListener {
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
            val notificationManager: NotificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
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