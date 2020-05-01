package nz.ac.uclive.itw21.project2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import nz.ac.uclive.itw21.project2.ui.CreateProfileActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        startActivity(Intent(baseContext, CreateProfileActivity::class.java));
    }
}
