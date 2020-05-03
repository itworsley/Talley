package nz.ac.uclive.itw21.project2.ui

import android.app.Activity
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.google.android.material.textfield.TextInputEditText
import nz.ac.uclive.itw21.project2.R
import nz.ac.uclive.itw21.project2.database.Device
import nz.ac.uclive.itw21.project2.database.DeviceViewModel
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class CreateDeviceActivity : AppCompatActivity() {

    private lateinit var dateSetListener: OnDateSetListener
    private lateinit var deviceViewModel: DeviceViewModel

    private lateinit var deviceType: String
    private lateinit var warrantyUnit: String
    private lateinit var imageView: ImageView
    private lateinit var deviceImageView: ImageView
    private lateinit var imageUri: Uri
    lateinit var currentPhotoPath: String

    private val permissionCode = 1000
    private val imageRequestCode = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_device)

        setSupportActionBar(findViewById(R.id.tool_bar))
        supportActionBar?.setTitle(R.string.header_add_a_device)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        deviceViewModel = DeviceViewModel(application)

        setUpTypeDropdown()
        setUpWarrantyUnitDropdown()
        setUpDateClicker()

        deviceImageView = findViewById<ImageView>(R.id.device_image)
        deviceImageView.setOnClickListener {
            imageView = it as ImageView
            handleAddPhotos()
        }
        val receiptImageView = findViewById<ImageView>(R.id.device_receipt).setOnClickListener {
            imageView = it as ImageView
            handleAddPhotos()
        }
    }

    private fun setUpTypeDropdown() {
        val dropdownAdapter = ArrayAdapter(
            baseContext,
            android.R.layout.simple_spinner_item,
            listOf("Other", "Headphones", "Kitchenware", "Laptop", "Mouse", "PC", "Phone", "Tablet", "TV", "Watch")
        )

        dropdownAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        val typeDropdown = findViewById<Spinner>(R.id.device_type_dropdown)
        typeDropdown.adapter = dropdownAdapter

        typeDropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                deviceType = dropdownAdapter.getItem(position).toString()
            }
        }
    }

    private fun setUpWarrantyUnitDropdown() {
        val dropdownAdapter = ArrayAdapter(
            baseContext,
            android.R.layout.simple_spinner_item,
            listOf("None", "Days", "Months", "Years")
        )

        dropdownAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        val typeDropdown = findViewById<Spinner>(R.id.device_warranty_period_dropdown)
        typeDropdown.adapter = dropdownAdapter

        typeDropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                warrantyUnit = dropdownAdapter.getItem(position).toString()
            }
        }
    }

    private fun setUpDateClicker() {
        val dateField = findViewById<TextInputEditText>(R.id.text_input_date_of_purchase)
        dateSetListener = OnDateSetListener { _, year, month, day ->
                var month = month
                month += 1
                val date = "$day/$month/$year"
                dateField.setText(date)
        }

        dateField.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val dialog = DatePickerDialog(this, dateSetListener, year, month, day)

            dialog.show()
        }
    }

    private fun handleAddPhotos() {
        if (checkSelfPermission(android.Manifest.permission.CAMERA) ==
            PackageManager.PERMISSION_DENIED || checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
            PackageManager.PERMISSION_DENIED) {
            // Request permissions
            val permission = arrayOf(
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            requestPermissions(permission, permissionCode)
        } else {
            // Permission already granted
            openCamera()
        }
    }


    private fun openCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    Toast.makeText(this, "Failed to save image...", Toast.LENGTH_SHORT).show()
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "nz.ac.uclive.itw21.project2",
                        it
                    )
                    imageUri = photoURI
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, imageRequestCode)
                }
            }
        }

    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            permissionCode -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                Toast.makeText(this, "Permission denied...", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == imageRequestCode && resultCode == Activity.RESULT_OK) {

            imageView.foreground = null
            imageView.setImageURI(imageUri)
            imageView.tag = imageUri.toString()
        }
    }

    @Throws(IOException::class)
    fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    fun saveDevice(view: View) {
        val name = findViewById<TextInputEditText>(R.id.text_input_device_name).text.toString()
        val type = deviceType
        val vendor = findViewById<TextInputEditText>(R.id.text_input_vendor).text.toString()
        val price = findViewById<TextInputEditText>(R.id.text_input_price).text.toString().toInt()
        var warrantyValue = findViewById<TextInputEditText>(R.id.text_input_warranty_period).text.toString().toLong()
        val dateOfPurchase = findViewById<TextInputEditText>(R.id.text_input_date_of_purchase).text.toString()
        val receiptUri = findViewById<ImageView>(R.id.device_receipt).tag.toString()
        val deviceImageUri = findViewById<ImageView>(R.id.device_image).tag.toString()


        when(warrantyUnit) {
            "None" -> warrantyValue = 0L
            "Months" -> warrantyValue = (warrantyValue * 31)
            "Years" -> warrantyValue = (warrantyValue * 365)
        }

        val device = Device(null, name, type, dateOfPurchase, price, vendor, warrantyValue.toInt(), receiptUri, deviceImageUri)

        deviceViewModel.insert(device).invokeOnCompletion {
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
