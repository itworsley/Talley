package nz.ac.uclive.itw21.project2.helper

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import nz.ac.uclive.itw21.project2.database.Device
import java.util.*

object Utilities {
    fun scheduleReminder(context: Context, dateOfReminder: Date, hour: Int, minute: Int) {
        val date = Calendar.getInstance()
        date.time = dateOfReminder
        date.set(Calendar.HOUR_OF_DAY, hour)
        date.set(Calendar.MINUTE, minute)

        // Only set a reminder if it is in the future.
        if (date.time.after(Date())) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.set(AlarmManager.RTC,  date.timeInMillis, reminderIntent(context))

            Log.d("ITW_DEBUG", "${date.time.toLocaleString()}")
        }
    }

    private fun reminderIntent(context: Context): PendingIntent {
        return Intent(context, AlarmReceiver::class.java).let {
            PendingIntent.getBroadcast(context, 0, it, PendingIntent.FLAG_UPDATE_CURRENT)
        }
    }
}