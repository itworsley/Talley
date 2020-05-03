package nz.ac.uclive.itw21.project2.ui

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import nz.ac.uclive.itw21.project2.R
import nz.ac.uclive.itw21.project2.database.Device
import nz.ac.uclive.itw21.project2.database.DeviceViewModel
import java.util.*


class CreateDeviceActivity : AppCompatActivity() {

    private lateinit var dateSetListener: OnDateSetListener
    private lateinit var deviceViewModel: DeviceViewModel

    private lateinit var deviceType: String
    private lateinit var warrantyUnit: String

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
            Log.d("ITW_DEBUG", "CLICKED")
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val dialog = DatePickerDialog(this, dateSetListener, year, month, day)

            dialog.show()
        }
    }

    fun saveDevice(view: View) {
        val name = findViewById<TextInputEditText>(R.id.text_input_device_name).text.toString()
        val type = deviceType
        val vendor = findViewById<TextInputEditText>(R.id.text_input_vendor).text.toString()
        val price = findViewById<TextInputEditText>(R.id.text_input_price).text.toString().toInt()
        var warrantyValue = findViewById<TextInputEditText>(R.id.text_input_warranty_period).text.toString().toLong()
        val dateOfPurchase = findViewById<TextInputEditText>(R.id.text_input_date_of_purchase).text.toString()


        when(warrantyUnit) {
            "None" -> warrantyValue = 0L
            "Months" -> warrantyValue = (warrantyValue * 31)
            "Years" -> warrantyValue = (warrantyValue * 365)
        }

        val device = Device(null, name, type, dateOfPurchase, price, vendor, warrantyValue.toInt())
        Log.d("ITW_DEBUG", device.toString())

        deviceViewModel.insert(device).invokeOnCompletion {
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
