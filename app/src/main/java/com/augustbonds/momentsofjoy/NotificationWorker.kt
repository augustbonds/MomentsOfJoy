package com.augustbonds.momentsofjoy

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.util.*

class NotificationWorker(context: Context, workerParameters: WorkerParameters) : Worker(context, workerParameters) {

    override fun doWork(): Result {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)

        //Only allow notifications between 9 and 18
        if (hour < 9 || hour > 18){
            return Result.SUCCESS
        }

        val notification = createFriendlyNotification()
        with(NotificationManagerCompat.from(applicationContext)) {
            // replace previous notification if any
            notify(0, notification)
        }
        return Result.SUCCESS
    }

    private fun createFriendlyNotification() : Notification {

        val mBuilder = NotificationCompat.Builder(applicationContext, MainActivity.CHANNEL_ID)
                .setSmallIcon(R.mipmap.heart)
                .setContentTitle(applicationContext.getString(R.string.notification_title))
                .setContentText(getRandomReminder())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))

        val intent = Intent(applicationContext, MainActivity::class.java)
        val activity = PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)
        mBuilder.setContentIntent(activity)

        return mBuilder.build()
    }

    private fun getRandomReminder() : String {
        val reminders = applicationContext.resources.getStringArray(R.array.reminders)
        val random = Random(System.currentTimeMillis())
        val index = random.nextInt(reminders.size)
        return reminders[index]
    }
}