package nz.ac.uclive.itw21.project2.helper

import android.content.Context
import android.widget.Toast
import java.util.*


fun validateStrings(values: Map<String, String>, context: Context): Boolean {
    for ((key, value) in values) {
        if (value.isEmpty()) {
            Toast.makeText(
                context,
                "${key[0].toUpperCase()}${key.substring(1, key.length)} is required",
                Toast.LENGTH_SHORT).show()
            return false
        }
    }
    return true
}