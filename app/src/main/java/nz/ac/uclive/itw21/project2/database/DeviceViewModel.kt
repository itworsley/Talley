package nz.ac.uclive.itw21.project2.database

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.util.*

class DeviceViewModel(application: Application): AndroidViewModel(application) {
    private val repository: DeviceRepository
    val devices: LiveData<List<Device>>
    val devicesOrdered: LiveData<List<Device>>

    init {
        val deviceDao = TalleyDatabase.getDatabase(application, viewModelScope).deviceDao()
        repository = DeviceRepository(deviceDao)
        devices = repository.devices
        devicesOrdered = repository.devicesOrdered
    }

    fun insert(device: Device) = viewModelScope.launch {
        repository.insert(device)
    }


    fun deleteDevice(device: Device) = viewModelScope.launch {
        repository.deleteDevice(device)
    }

    fun updateDeviceReminderDate(reminderDate: Date, deviceId: Int) = viewModelScope.launch {
        repository.updateDeviceReminderDate(reminderDate, deviceId)
    }

    fun updateDeviceReminderShowed(reminderShowed: Boolean, deviceId: Int) = viewModelScope.launch {
        repository.updateDeviceReminderShowed(reminderShowed, deviceId)
    }
}