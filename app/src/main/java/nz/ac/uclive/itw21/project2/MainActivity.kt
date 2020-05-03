package nz.ac.uclive.itw21.project2

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import nz.ac.uclive.itw21.project2.ui.CreateDeviceActivity
import nz.ac.uclive.itw21.project2.ui.DeviceFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.tool_bar))
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
        startActivity(Intent(baseContext, CreateDeviceActivity::class.java))
    }
}
