package nz.ac.uclive.itw21.project2.database

import androidx.lifecycle.LiveData
import androidx.room.*
import org.jetbrains.annotations.NotNull
import java.util.*

@Entity(tableName = "devices")
class Device (
    @PrimaryKey(autoGenerate = true)
    val uid: Int?,

    @NotNull
    @ColumnInfo(name = "device_name") val deviceName: String,

    @NotNull
    @ColumnInfo(name = "type") val type: String,

    @NotNull
    @ColumnInfo(name = "date_of_purchase") val dateOfPurchase: Date,

    @NotNull
    @ColumnInfo(name = "price_at_purchase") val price: String,

    @NotNull
    @ColumnInfo(name = "vendor") val vendor: String,

    @NotNull
    @ColumnInfo(name = "warranty_end_date") val warrantyEndDate: Date,


    @ColumnInfo(name = "receipt_image_uri") val receiptImageUri: String,


    @ColumnInfo(name = "device_image_uri") val deviceImageUri: String,


    @ColumnInfo(name = "next_reminder_date") val nextReminderDate: Date,

    @ColumnInfo(name = "reminder_showed") val reminderShowed: Boolean
)

@Dao
interface DeviceDao {
    @Query("SELECT * FROM devices")
    fun getDevices(): LiveData<List<Device>>

    @Query("SELECT * FROM devices WHERE reminder_showed != 1 AND next_reminder_date >= date('now') ORDER BY next_reminder_date ASC")
    fun getDevicesOrdered(): LiveData<List<Device>>

    @Query("UPDATE devices SET next_reminder_date = :nextReminderDate WHERE uid = :deviceId")
    suspend fun updateDeviceReminderDate(nextReminderDate: Date, deviceId: Int)

    @Query("UPDATE devices SET reminder_showed = :reminderShowed WHERE uid = :deviceId")
    suspend fun updateDeviceReminderShowed(reminderShowed: Boolean, deviceId: Int)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(device: Device)

    @Query("DELETE FROM devices")
    suspend fun deleteAll()

    @Delete
    suspend fun deleteDevice(device: Device)
}