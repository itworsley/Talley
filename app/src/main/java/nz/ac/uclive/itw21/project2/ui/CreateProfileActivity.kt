package nz.ac.uclive.itw21.project2.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import com.google.android.material.textfield.TextInputEditText
import nz.ac.uclive.itw21.project2.MainActivity
import nz.ac.uclive.itw21.project2.R
import nz.ac.uclive.itw21.project2.database.Profile
import nz.ac.uclive.itw21.project2.database.ProfileViewModel

class CreateProfileActivity : AppCompatActivity() {

    private lateinit var profileViewModel: ProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_profile)
        profileViewModel = ProfileViewModel(application)


        profileViewModel.profile.observe(this, Observer { profile ->
            // If a profile exists, then skip the profile creation screen.
            if (profile?.uid != null) {
                    startActivity(Intent(baseContext, MainActivity::class.java))
            }
        })
    }


    fun saveProfile(view: View) {
        Log.d("CLICK", "${view.id} button clicked")
        val name = findViewById<TextInputEditText>(R.id.text_input_name).text.toString()
        val email = findViewById<TextInputEditText>(R.id.text_input_email).text.toString()
        val contactNumber = findViewById<TextInputEditText>(R.id.text_input_contact_number).text.toString()

        profileViewModel.insert(Profile(null, name, email, contactNumber)).invokeOnCompletion {
            startActivity(Intent(baseContext, MainActivity::class.java))
        }
    }
}
