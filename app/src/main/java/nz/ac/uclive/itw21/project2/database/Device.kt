package nz.ac.uclive.itw21.project2.database

import androidx.lifecycle.LiveData
import androidx.room.*
import org.jetbrains.annotations.NotNull

@Entity(tableName = "devices")
class Device (
    @PrimaryKey(autoGenerate = true)
    val uid: Int?,

    @NotNull
    @ColumnInfo(name = "device_name") val deviceName: String,

    @NotNull
    @ColumnInfo(name = "type") val type: String,

    @NotNull
    @ColumnInfo(name = "date_of_purchase") val dateOfPurchase: String,

    @NotNull
    @ColumnInfo(name = "price_at_purchase") val price: String,

    @NotNull
    @ColumnInfo(name = "vendor") val vendor: String,

    @NotNull
    @ColumnInfo(name = "warranty_period_days") val warrantyPeriodDays: Int,


    @ColumnInfo(name = "receipt_image_uri") val receiptImageUri: String,


    @ColumnInfo(name = "device_image_uri") val deviceImageUri: String
)

@Dao
interface DeviceDao {
    @Query("SELECT * FROM devices")
    fun getDevices(): LiveData<List<Device>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(device: Device)

    @Query("DELETE FROM devices")
    suspend fun deleteAll()
}