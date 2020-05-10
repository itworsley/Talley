package nz.ac.uclive.itw21.project2.helper

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.preference.PreferenceManager
import nz.ac.uclive.itw21.project2.MainActivity
import nz.ac.uclive.itw21.project2.R
import java.text.SimpleDateFormat
import java.util.*


class AlarmReceiver: BroadcastReceiver() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context?, intent: Intent?) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val intent = Intent(context, MainActivity::class.java)
        intent.putExtra("NOTIFICATION_SHOWED", true)
        intent.putExtra("DEVICE_ID", prefs.getString("next_reminder_device", "")?.toInt())

        val pendingIntent: PendingIntent = intent.run {
            PendingIntent.getActivity(context, 0, this, 0)
        }


        val notification = Notification.Builder(context, Notification.CATEGORY_REMINDER).run {
            setSmallIcon(R.drawable.app_logo_single)
            setContentTitle("Warranty warning")
            setContentText("${prefs.getString("next_reminder_device", "")}'s warranty is set to expire on ${prefs.getString("next_reminder_date", "")}")
            setContentIntent(pendingIntent)
            setOngoing(true)
            setAutoCancel(true)
            build()
        }

        val manager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(0, notification)
    }

}