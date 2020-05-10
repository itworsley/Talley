package nz.ac.uclive.itw21.project2

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import nz.ac.uclive.itw21.project2.database.DeviceViewModel
import nz.ac.uclive.itw21.project2.helper.ManageNotifications
import nz.ac.uclive.itw21.project2.ui.CreateDeviceActivity
import nz.ac.uclive.itw21.project2.ui.DeviceFragment
import nz.ac.uclive.itw21.project2.ui.SettingsActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.tool_bar))
        supportActionBar?.setDisplayShowTitleEnabled(false)
        loadFragment(DeviceFragment())

        if (intent.getBooleanExtra("NOTIFICATION_SHOWED", false)) {
            DeviceViewModel(application).updateDeviceReminderShowed(true, intent.getIntExtra("DEVICE_ID", 0))
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
            ManageNotifications.instance.setupNotifications(applicationContext, application)
        }
    }

    private fun loadFragment(fragment: Fragment?): Boolean {
        //switching fragment
        if (fragment != null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
            return true
        }
        return false
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(Notification.CATEGORY_REMINDER, "Warranty Reminders", importance).apply {
            description = "Send warranty reminders"
        }
        val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }



    fun showCreateDeviceActivity(view: View) {
        Log.d("CLICK", "${view.id} button clicked")
        startActivity(Intent(baseContext, CreateDeviceActivity::class.java))
    }

    fun showSettingsActivity(view: View) {
        Log.d("CLICK", "${view.id} button clicked")
        startActivity(Intent(baseContext, SettingsActivity::class.java))
    }
}
