package com.augustbonds.momentsofjoy

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import java.util.*

class FriendlyNotificationBuilder {
    companion object {
        fun build(context: Context): Notification {
            return FriendlyNotificationBuilder().createFriendlyNotification(context)
        }
    }

    private fun createFriendlyNotification(context: Context): Notification {

        val mBuilder = NotificationCompat.Builder(context, MainActivity.CHANNEL_ID)
                .setSmallIcon(R.mipmap.heart)
                .setContentTitle(context.getString(R.string.notification_title))
                .setContentText(getRandomReminder(context))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))

        val intent = Intent(context, MainActivity::class.java)
        val activity = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)
        mBuilder.setContentIntent(activity)

        return mBuilder.build()
    }

    private fun getRandomReminder(context: Context) : String {
        val reminders = context.resources.getStringArray(R.array.reminders)
        val random = Random(System.currentTimeMillis())
        val index = random.nextInt(reminders.size)
        return reminders[index]
    }
}
