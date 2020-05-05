package nz.ac.uclive.itw21.project2.helper

import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import nz.ac.uclive.itw21.project2.R


class FullscreenImageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fullscreen_image)
        val fullScreenImageView: ImageView = findViewById(R.id.fullScreenImageView)
        val callingActivityIntent = intent
        if (callingActivityIntent != null) {
            val imageUri: Uri? = Uri.parse(callingActivityIntent.getStringExtra("URI"))
            if (imageUri != null) {
                Glide.with(this)
                    .load(imageUri)
                    .into(fullScreenImageView)
            }
        }
    }

    override fun onBackPressed() {
        finish()
    }

}
