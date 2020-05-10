package nz.ac.uclive.itw21.project2.database

import androidx.lifecycle.LiveData
import java.util.*

class DeviceRepository(private val deviceDao: DeviceDao) {
    val devices: LiveData<List<Device>> = deviceDao.getDevices()
    val devicesOrdered: LiveData<List<Device>> = deviceDao.getDevicesOrdered()

    suspend fun insert(device: Device) {
        deviceDao.insert(device)
    }

    suspend fun deleteDevice(device: Device) {
        deviceDao.deleteDevice(device)
    }

    suspend fun updateDeviceReminderDate(reminderDate: Date, deviceId: Int) {
        deviceDao.updateDeviceReminderDate(reminderDate, deviceId)
    }

    suspend fun updateDeviceReminderShowed(reminderShowed: Boolean, deviceId: Int) {
        deviceDao.updateDeviceReminderShowed(reminderShowed, deviceId)
    }
}