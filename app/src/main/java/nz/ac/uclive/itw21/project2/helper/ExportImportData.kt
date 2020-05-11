package nz.ac.uclive.itw21.project2.helper

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import com.google.gson.Gson
import nz.ac.uclive.itw21.project2.MainActivity
import nz.ac.uclive.itw21.project2.R
import nz.ac.uclive.itw21.project2.database.Device
import java.io.*

class ExportImportData {

    fun exportDeviceFile(context: Context, device: Device) {
        val gson = Gson()
        device.uid = null

        val jsonOutput = gson.toJson(device)
        val successful: Boolean

        val fileName = "${device.deviceName}.json"
        successful = try {
            val file = File(context.getExternalFilesDir(""), fileName)
            val writer = BufferedWriter(FileWriter(file))
            writer.write(jsonOutput)
            writer.close()
            true
        } catch (e: Exception) {
            Toast.makeText(context, context.getString(R.string.error_cannot_read_file), Toast.LENGTH_SHORT).show()
            false
        }

        if (successful) {
            val file = File(context.getExternalFilesDir(""), fileName)

            val fileProvider = FileProvider.getUriForFile(
                context as MainActivity,
                "nz.ac.uclive.itw21.project2",
                file)

            if (!file.exists() || !file.canRead()) {
                Toast.makeText(context, context.getString(R.string.error_cannot_read_file), Toast.LENGTH_SHORT).show()
                return
            }

            val emailIntent = Intent(Intent.ACTION_SEND_MULTIPLE)
            emailIntent.type = "message/rfc822"
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.description_email_subject, device.deviceName))

            val uris = ArrayList<Uri>()
            uris.add(fileProvider)

            // Add device receipt image.
            if (device.receiptImageUri.isNotEmpty()) uris.add(Uri.parse(device.receiptImageUri))

            // Add device image.
            if (device.deviceImageUri.isNotEmpty()) uris.add(Uri.parse(device.deviceImageUri))


            emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris)
            context.startActivity(Intent.createChooser(emailIntent, context.getString(R.string.description_send_email)))
        }
    }


    fun importDeviceFile(context: Context, fileUri: Uri): Device? {
        val gson = Gson()
        var device: Device? = null
        try {
            val stringBuilder = StringBuilder()
            context.contentResolver.openInputStream(fileUri)?.use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).use { reader ->
                    var line: String? = reader.readLine()
                    while (line != null) {
                        stringBuilder.append(line)
                        line = reader.readLine()
                    }
                }
            }
            Log.d("ITW_DEBUG", stringBuilder.toString())
            device = gson.fromJson(stringBuilder.toString(), Device::class.java)
        } catch (fileNotFound: FileNotFoundException) {
            Log.d("ITW_DEBUG", "FILE NOT FOUND")
        } catch (ioException: IOException) {
            Log.d("ITW_DEBUG", "IO Exception")
        }
        return device
    }
}