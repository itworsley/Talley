package nz.ac.uclive.itw21.project2.database

import androidx.lifecycle.LiveData

class DeviceRepository(private val deviceDao: DeviceDao) {
    val devices: LiveData<List<Device>> = deviceDao.getDevices()

    suspend fun insert(device: Device) {
        deviceDao.insert(device)
    }
}