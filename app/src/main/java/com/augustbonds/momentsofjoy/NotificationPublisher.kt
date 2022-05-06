package com.augustbonds.momentsofjoy

import android.app.Notification
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NotificationPublisher : BroadcastReceiver() {
    companion object {
        const val NOTIFICATION_ID = "notification_id"
        const val NOTIFICATION = "notification"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        Logger.info("inside onReceive")
        val notificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = intent?.getParcelableExtra<Notification>(NOTIFICATION)
        if (intent != null) {
            val notificationId = intent.getIntExtra(NOTIFICATION_ID, 0)
            notificationManager.notify(notificationId, notification)
        }
    }
}