package nz.ac.uclive.itw21.project2.ui

import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import nz.ac.uclive.itw21.project2.R
import nz.ac.uclive.itw21.project2.database.DeviceViewModel
import nz.ac.uclive.itw21.project2.helper.ManageNotifications


class SettingsActivity : AppCompatActivity() {
    private lateinit var enableNotifications: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        setSupportActionBar(findViewById(R.id.tool_bar))
        supportActionBar?.setTitle(R.string.header_settings)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, SettingsFragment())
            .commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


    }

    class SettingsFragment : PreferenceFragmentCompat(), OnSharedPreferenceChangeListener {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }

        override fun onResume() {
            super.onResume()
            preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
        }

        override fun onPause() {
            super.onPause()
            preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
        }

        override fun onSharedPreferenceChanged(
            sharedPreferences: SharedPreferences?,
            key: String?
        ) {
            ManageNotifications.instance.updateDevices()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}