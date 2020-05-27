package nz.ac.uclive.itw21.project2.ui

import android.Manifest
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.Activity
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.preference.PreferenceManager
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.material.textfield.TextInputEditText
import nz.ac.uclive.itw21.project2.R
import nz.ac.uclive.itw21.project2.database.Device
import nz.ac.uclive.itw21.project2.database.DeviceViewModel
import nz.ac.uclive.itw21.project2.helper.ExportImportData
import nz.ac.uclive.itw21.project2.helper.ManageNotifications
import nz.ac.uclive.itw21.project2.helper.validateStrings
import java.io.File
import java.io.IOException
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*


class CreateDeviceActivity : AppCompatActivity() {

    private lateinit var dateSetListener: OnDateSetListener
    private lateinit var deviceViewModel: DeviceViewModel
    private lateinit var typeDropdownAdapter: ArrayAdapter<String>
    private lateinit var typeDropdown: Spinner

    private lateinit var deviceName: TextInputEditText
    private lateinit var deviceType: String
    private lateinit var deviceVendor: TextInputEditText
    private lateinit var devicePrice: TextInputEditText
    private lateinit var deviceWarrantyValue: TextInputEditText
    private lateinit var deviceDateOfPurchase: TextInputEditText

    private lateinit var warrantyUnit: String
    private lateinit var deviceImageView: ImageView
    private lateinit var receiptImageView: ImageView

    private var currentlyDeviceImage: Boolean = false
    private var deviceImageUri: Uri = Uri.EMPTY
    private var receiptImageUri: Uri = Uri.EMPTY
    private lateinit var currentPhotoPath: String

    private val permissionCode = 1000
    private val locationPermissionCode = 1005
    private val imageRequestCode = 1001
    private val uploadFileRequestCode = 1002

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_device)

        if (savedInstanceState != null ) {
            deviceImageUri = savedInstanceState.getParcelable("deviceImageUri")!!
            receiptImageUri = savedInstanceState.getParcelable("receiptImageUri")!!
        }

        setSupportActionBar(findViewById(R.id.tool_bar))
        supportActionBar?.setTitle(R.string.header_add_a_device)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        deviceViewModel = DeviceViewModel(application)



        deviceName = findViewById(R.id.text_input_device_name)
        deviceVendor = findViewById(R.id.text_input_vendor)
        devicePrice = findViewById(R.id.text_input_price)
        deviceWarrantyValue = findViewById(R.id.text_input_warranty_period)
        deviceDateOfPurchase = findViewById(R.id.text_input_date_of_purchase)

        deviceImageView = findViewById(R.id.device_image)
        receiptImageView = findViewById(R.id.device_receipt)

        deviceImageView.setOnClickListener {
            currentlyDeviceImage = true
            handleAddPhotos()
        }
        receiptImageView.setOnClickListener {
            currentlyDeviceImage = false
            handleAddPhotos()
        }

        if (deviceImageUri != Uri.EMPTY) {
            deviceImageView.foreground = null
            deviceImageView.setImageURI(deviceImageUri)
        }
        if (receiptImageUri != Uri.EMPTY) {
            receiptImageView.foreground = null
            receiptImageView.setImageURI(receiptImageUri)
        }

        setUpTypeDropdown()
        setUpWarrantyUnitDropdown()
        setUpDateClicker()
        findViewById<ImageButton>(R.id.button_get_location).setOnClickListener{ handleGetPlacesDetails() }
    }

    private fun handleGetPlacesDetails() {
        if (checkSelfPermission(ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, arrayOf(ACCESS_FINE_LOCATION), locationPermissionCode)
        } else {
            getPlacesDetails()
        }
    }

    private fun getPlacesDetails() {

        // This API key is restricted to solely on this application, with its own SHA-1 certificate.
        // I will remove the API credential once the project has been marked.
        Places.initialize(applicationContext, "AIzaSyAzXMoRnyq7qbu_xRhpvLfk4gvd3_eCOps")


        val placesClient: PlacesClient = Places.createClient(this)
        val placeFields: List<Place.Field> = Collections.singletonList(Place.Field.NAME)
        val request: FindCurrentPlaceRequest = FindCurrentPlaceRequest.newInstance(placeFields)

        if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            val placeResponse: Task<FindCurrentPlaceResponse> =
                placesClient.findCurrentPlace(request)
            placeResponse.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val response: FindCurrentPlaceResponse? = task.result
                    if (response != null) {
                        this.deviceVendor.setText(response.placeLikelihoods[0].place.name)
                    }
                } else {
                    Toast.makeText(this, getString(R.string.error_no_places_found), Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            handleGetPlacesDetails()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable("deviceImageUri", deviceImageUri)
        outState.putParcelable("receiptImageUri", receiptImageUri)
    }

    private fun setUpTypeDropdown() {
        typeDropdownAdapter = ArrayAdapter(
            baseContext,
            android.R.layout.simple_spinner_item,
            listOf("Other", "Headphones", "Kitchenware", "Laptop", "Mouse", "PC", "Phone", "Tablet", "TV", "Watch")
        )

        typeDropdownAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        typeDropdown = findViewById(R.id.device_type_dropdown)
        typeDropdown.adapter = typeDropdownAdapter

        typeDropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                deviceType = typeDropdownAdapter.getItem(position).toString()
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

        deviceWarrantyValue.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable) {
                if (typeDropdown.selectedItem.toString() == "None") {
                    typeDropdown.setSelection(3)
                }

                if (deviceWarrantyValue.text.isNullOrEmpty()) {
                    typeDropdown.setSelection(0)
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
            }
        })
    }

    private fun setUpDateClicker() {
        val dateField = findViewById<TextInputEditText>(R.id.text_input_date_of_purchase)
        dateSetListener = OnDateSetListener { _, year, month, day ->
                var monthIncreased = month
                monthIncreased += 1
                val date = "$day/$monthIncreased/$year"
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
        if (checkSelfPermission(Manifest.permission.CAMERA) ==
            PackageManager.PERMISSION_DENIED || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            // Request permissions
            val permission = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
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
                    when(currentlyDeviceImage) {
                        true -> deviceImageUri = photoURI
                        false -> receiptImageUri = photoURI
                    }
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
            }
            locationPermissionCode -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getPlacesDetails()
            }
            else {
                Toast.makeText(this, "Permission denied...", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == imageRequestCode && resultCode == Activity.RESULT_OK) {
            when(currentlyDeviceImage) {
                true -> {
                    deviceImageView.foreground = null
                    deviceImageView.setImageURI(deviceImageUri)
                    deviceImageView.tag = deviceImageUri.toString()
                }
                false -> {
                    receiptImageView.foreground = null
                    receiptImageView.setImageURI(receiptImageUri)
                    receiptImageView.tag = receiptImageUri.toString()
                }
            }
        }

        if (requestCode == uploadFileRequestCode && resultCode == Activity.RESULT_OK) {
            data?.data?.also { uri ->
                val device = ExportImportData().importDeviceFile(baseContext, uri)
                if (device != null) {
                    deviceViewModel.insert(device).invokeOnCompletion {
                        finish()
                    }
                }

            }
        }
    }

    @Throws(IOException::class)
    fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    fun importFromFile(view: View) {
        Log.d("CLICK", "${view.id} button clicked")

        val fileIntent = Intent(Intent.ACTION_GET_CONTENT)
        fileIntent.type = "application/octet-stream"
        fileIntent.addCategory(Intent.CATEGORY_OPENABLE)
        fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        val finalIntent = Intent.createChooser(fileIntent, "Select a .json file to import")
        startActivityForResult(finalIntent, uploadFileRequestCode)
    }


    fun saveDevice(view: View) {
        Log.d("CLICK", "${view.id} button clicked")
        val name = deviceName.text?.toString().orEmpty()
        val type = deviceType
        val vendor = deviceVendor.text?.toString().orEmpty()
        val price = devicePrice.text?.toString().orEmpty()
        val warrantyValue = deviceWarrantyValue.text?.toString().orEmpty()
        val dateOfPurchase = deviceDateOfPurchase.text?.toString().orEmpty()
        val receiptUri = findViewById<ImageView>(R.id.device_receipt).tag?.toString().orEmpty()
        val deviceImageUri = findViewById<ImageView>(R.id.device_image).tag?.toString().orEmpty()

        if (!validateStrings(mapOf("name" to name, "type" to type, "vendor" to vendor, "price" to price, "warranty" to warrantyValue, "date of purchase" to dateOfPurchase), this)) {
            return
        }
        val priceAsDouble: Double
        try {
            priceAsDouble = price.toDouble()
            
        } catch (_: Exception) {
            Toast.makeText(this, "Price is invalid", Toast.LENGTH_SHORT).show()
            return
        }

        val warrantyAsLong: Long
        try {
            warrantyAsLong = warrantyValue.toLong()
        } catch (_: Exception) {
            Toast.makeText(this, "Warranty is invalid", Toast.LENGTH_SHORT).show()
            return
        }

        val warrantyEndDate = calculateWarrantyEndDate(warrantyAsLong, warrantyUnit, dateOfPurchase)

        val device = Device(null, name, type, dateOfPurchase, formatPrice(priceAsDouble), vendor, warrantyEndDate, receiptUri, deviceImageUri)

        deviceViewModel.insert(device).invokeOnCompletion {
            finish()
        }

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val devicesCreated = prefs.getInt("number_devices_created", 0) + 1
        with (prefs.edit()) {
            putInt("number_devices_created", devicesCreated)
            commit()
        }

        ManageNotifications.instance.initialiseNotifications(this, application)
    }

    private fun calculateWarrantyEndDate(warrantyValue: Long, warrantyUnit: String, dateOfPurchase: String): String {
        var warrantyDays = 0L
        when(warrantyUnit) {
            "None" -> warrantyDays = 0L
            "Months" -> warrantyDays = (warrantyValue * 31)
            "Years" -> warrantyDays = (warrantyValue * 365)
        }

        val c = Calendar.getInstance()
        try {
            c.time = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).parse(dateOfPurchase)!!
        } catch (_: Exception) {
            // Parse a default date so doesn't crash.
            c.time = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).parse("01/01/2000")!!
        }

        c.add(Calendar.DAY_OF_MONTH, warrantyDays.toInt())

        return SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(c.time)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun formatPrice(price: Double): String {
        val nf: NumberFormat = NumberFormat.getNumberInstance(Locale.ENGLISH)
        nf.minimumFractionDigits = 2
        nf.maximumFractionDigits = 2

        return nf.format(price)
    }
}
