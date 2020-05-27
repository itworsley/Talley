package nz.ac.uclive.itw21.project2.helper

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log


class AlarmReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("DEBUG", "New Notification Displayed")
    }

}
