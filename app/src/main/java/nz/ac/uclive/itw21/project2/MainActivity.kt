package nz.ac.uclive.itw21.project2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import nz.ac.uclive.itw21.project2.ui.CreateDeviceActivity
import nz.ac.uclive.itw21.project2.ui.DeviceAdapter
import nz.ac.uclive.itw21.project2.ui.DeviceFragment
import nz.ac.uclive.itw21.project2.ui.SettingsActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.tool_bar))
        supportActionBar?.setDisplayShowTitleEnabled(false)
        loadFragment(DeviceFragment())
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

    fun showCreateDeviceActivity(view: View) {
        Log.d("CLICK", "${view.id} button clicked")
        startActivity(Intent(baseContext, CreateDeviceActivity::class.java))
    }

    fun showSettingsActivity(view: View) {
        Log.d("CLICK", "${view.id} button clicked")
        startActivity(Intent(baseContext, SettingsActivity::class.java))
    }
}
