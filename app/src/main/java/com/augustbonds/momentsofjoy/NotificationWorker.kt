package com.augustbonds.momentsofjoy

import android.content.Context
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.util.*

class NotificationWorker(context: Context, workerParameters: WorkerParameters) : Worker(context, workerParameters) {

    override fun doWork(): Result {

        if (!(isDay() && isWeekday())) {
            //Only allow notifications during the workday
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

    private fun isDay(): Boolean {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

        return hour in 9..18
    }

    private fun isWeekday(): Boolean {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        return dayOfWeek in Calendar.MONDAY..Calendar.FRIDAY
    }


}