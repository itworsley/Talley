package nz.ac.uclive.itw21.project2.helper

import android.app.*
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.preference.PreferenceManager
import nz.ac.uclive.itw21.project2.R

object Utilities {
    fun showNotification(context: Context) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)

        val builder = NotificationCompat.Builder(context, Notification.CATEGORY_REMINDER)
            .setSmallIcon(R.drawable.app_logo_single)
            .setContentTitle(context.getString(R.string.format_device_created_notification))
            .setContentText(context.getString(R.string.format_device_number_created_notification, prefs.getInt("number_devices_created", 0).toString()))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            notify(0, builder.build())
        }
    }
}
