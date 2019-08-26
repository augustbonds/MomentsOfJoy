package com.augustbonds.momentsofjoy

import android.content.Context
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.util.*

class NotificationWorker(context: Context, workerParameters: WorkerParameters) : Worker(context, workerParameters) {

    override fun doWork(): Result {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)

        //Only allow notifications between 9 and 18
        if ( 9 < hour || hour >= 18){
            Logger.debug("doWork called in the night.")
            return Result.success()
        }

        val notification = FriendlyNotificationBuilder.build(applicationContext)
        with(NotificationManagerCompat.from(applicationContext)) {
            // replace previous notification if any
            notify(0, notification)
        }
        return Result.success()
    }


}