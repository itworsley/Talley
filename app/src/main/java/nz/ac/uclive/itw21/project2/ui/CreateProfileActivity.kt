package nz.ac.uclive.itw21.project2.ui

import android.content.Intent
import android.graphics.drawable.Animatable
import android.graphics.drawable.AnimatedVectorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.Observer
import com.google.android.material.textfield.TextInputEditText
import nz.ac.uclive.itw21.project2.MainActivity
import nz.ac.uclive.itw21.project2.R
import nz.ac.uclive.itw21.project2.database.Profile
import nz.ac.uclive.itw21.project2.database.ProfileViewModel
import nz.ac.uclive.itw21.project2.helper.validateStrings

class CreateProfileActivity : AppCompatActivity() {

    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var animation: AnimatedVectorDrawable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_profile)
        profileViewModel = ProfileViewModel(application)

        profileViewModel.profile.observe(this, Observer { profile ->
            // If a profile exists, then skip the profile creation screen.
            if (profile?.uid != null) {
                startActivity(Intent(baseContext, MainActivity::class.java))
                finish()
            }
        })
    }

    override fun onStart() {
        super.onStart()
        val logoAnimation = findViewById<ImageView>(R.id.app_logo_createProfile).drawable as Animatable
        logoAnimation.start()
    }


    fun saveProfile(view: View) {
        Log.d("CLICK", "${view.id} button clicked")
        val name = findViewById<TextInputEditText>(R.id.text_input_name).text?.toString().orEmpty()
        val email = findViewById<TextInputEditText>(R.id.text_input_email).text?.toString().orEmpty()
        val contactNumber = findViewById<TextInputEditText>(R.id.text_input_contact_number).text?.toString().orEmpty()

        if (!validateStrings(mapOf("name" to name, "email" to email, "contact number" to contactNumber), this)) {
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Email is invalid", Toast.LENGTH_SHORT).show()
            return
        }

        profileViewModel.insert(Profile(null, name, email, contactNumber)).invokeOnCompletion {
            startActivity(Intent(baseContext, MainActivity::class.java))
            finish()
        }
    }
}
