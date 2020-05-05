package nz.ac.uclive.itw21.project2.database

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class DeviceViewModel(application: Application): AndroidViewModel(application) {
    private val repository: DeviceRepository
    val devices: LiveData<List<Device>>

    init {
        val deviceDao = TalleyDatabase.getDatabase(application, viewModelScope).deviceDao()
        repository = DeviceRepository(deviceDao)
        devices = repository.devices
    }

    fun insert(device: Device) = viewModelScope.launch {
        repository.insert(device)
    }


    fun deleteDevice(device: Device) = viewModelScope.launch {
        repository.deleteDevice(device)
    }
}