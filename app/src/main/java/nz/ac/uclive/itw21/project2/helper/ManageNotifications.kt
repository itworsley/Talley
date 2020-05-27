package nz.ac.uclive.itw21.project2.helper

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.preference.PreferenceManager

class ManageNotifications private constructor() {


    private object HOLDER {
        val INSTANCE = ManageNotifications()
    }

    companion object {
        val instance: ManageNotifications by lazy { HOLDER.INSTANCE }
    }


    fun initialiseNotifications(context: Context, application: Application) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        // If notifications enabled.

        if (prefs.getBoolean("enable_notifications", false)) {
            if (prefs.getBoolean("5th_device", false) && prefs.getInt("number_devices_created", 0) == 5) {
                Utilities.showNotification(application)
            }

            if (prefs.getBoolean("10th_device", false) && prefs.getInt("number_devices_created", 0) == 10) {
                Utilities.showNotification(application)
            }

            if (prefs.getBoolean("25th_device", false) && prefs.getInt("number_devices_created", 0) == 25) {
                Utilities.showNotification(application)
            }

            if (prefs.getBoolean("50th_device", false) && prefs.getInt("number_devices_created", 0) == 50) {
                Utilities.showNotification(application)
            }

            if (prefs.getBoolean("100th_device", false) && prefs.getInt("number_devices_created", 0) == 100) {
                Utilities.showNotification(application)
            }
        }
    }
}
