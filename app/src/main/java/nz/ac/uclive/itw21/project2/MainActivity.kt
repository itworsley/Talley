package nz.ac.uclive.itw21.project2

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
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
        createNotificationChannel()
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

    private fun createNotificationChannel() {
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(Notification.CATEGORY_REMINDER, "Device Notifications", importance).apply {
            description = "Show notifications when you create a certain number of devices"
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
