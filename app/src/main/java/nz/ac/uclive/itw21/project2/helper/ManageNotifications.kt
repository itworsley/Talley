package nz.ac.uclive.itw21.project2.helper

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.preference.PreferenceManager
import nz.ac.uclive.itw21.project2.database.Device
import nz.ac.uclive.itw21.project2.database.DeviceViewModel
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.util.*

class ManageNotifications private constructor() {
    private var deviceListOrdered: List<Device> = emptyList()
    var app: Application = Application()


    private object HOLDER {
        val INSTANCE = ManageNotifications()
    }

    companion object {
        val instance: ManageNotifications by lazy { HOLDER.INSTANCE }
    }


    fun setupNotifications(context: Context, application: Application) {
        app = application
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)

        // If notifications enabled.
        if (prefs.getBoolean("enable_notifications", false)) {
            DeviceViewModel(app).devicesOrdered.observeForever { devices ->
                devices?.let{ setDeviceList(it) }
                if (this.deviceListOrdered.isNotEmpty() && checkDates(this.deviceListOrdered[0].nextReminderDate)) {
                    prefs.edit().putString("next_reminder_device_id", this.deviceListOrdered[0].uid.toString()).apply()
                    prefs.edit().putString("next_reminder_device", this.deviceListOrdered[0].deviceName).apply()
                    prefs.edit().putString("next_reminder_date", SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.ENGLISH).format(this.deviceListOrdered[0].nextReminderDate)).apply()
                    Utilities.scheduleReminder(application, this.deviceListOrdered[0].nextReminderDate, 16, 22)
                }
            }
        }
    }

    private fun checkDates(reminderDate: Date): Boolean {
        val reminderDateLocal = reminderDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
        val today = Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()

        return reminderDateLocal.isAfter(today) || reminderDateLocal.isEqual(today)
    }


    private fun setDeviceList(deviceList: List<Device>) {
        this.deviceListOrdered = deviceList
    }


    fun updateDevices() {
        deviceListOrdered.forEach {device ->
            device.uid?.let {deviceId ->
                DeviceViewModel(app).updateDeviceReminderDate(
                    calculateNextReminderDate(
                        device.warrantyEndDate,
                        app.baseContext),
                    deviceId).invokeOnCompletion {
                    setupNotifications(app.baseContext, app)
                }
            }
        }
    }

    fun calculateNextReminderDate(warrantyEndDate: Date, context: Context): Date {
        val calendar: Calendar = Calendar.getInstance()
        calendar.time = warrantyEndDate

        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        if (prefs.getBoolean("enable_notifications", false)) {
            if (prefs.getBoolean("notification_frequency_one_year", false)) {
                calendar.add(Calendar.YEAR, -1)
                Log.d("ITW_DEBUG", "One Year")
                return calendar.time
            }

            if (prefs.getBoolean("notification_frequency_six_months", false)) {
                calendar.add(Calendar.MONTH, -6)
                Log.d("ITW_DEBUG", "Six Months")
                return calendar.time
            }

            if (prefs.getBoolean("notification_frequency_one_month", false)) {
                calendar.add(Calendar.MONTH, -1)
                Log.d("ITW_DEBUG", "One Month")
                return calendar.time
            }

            if (prefs.getBoolean("notification_frequency_one_week", false)) {
                calendar.add(Calendar.DAY_OF_WEEK, -7)
                Log.d("ITW_DEBUG", "One Week")
                return calendar.time
            }

            if (prefs.getBoolean("notification_frequency_on_the_day", false)) {
                Log.d("ITW_DEBUG", "On the Day")
                return calendar.time
            }
        }
        Log.d("ITW_DEBUG", "No time")

        // Return date of 1/01/1970
        return Date(0)
    }
}