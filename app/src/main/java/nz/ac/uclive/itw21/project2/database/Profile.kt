package nz.ac.uclive.itw21.project2.database

import androidx.lifecycle.LiveData
import androidx.room.*
import org.jetbrains.annotations.NotNull

@Entity(tableName = "profile")
class Profile (
    @PrimaryKey(autoGenerate = true)
    val uid: Int?,

    @NotNull
    @ColumnInfo(name = "full_name") val fullName: String,

    @NotNull
    @ColumnInfo(name = "email_address") val emailAddress: String,

    @NotNull
    @ColumnInfo(name = "contact_number") val contactNumber: String
)

@Dao
interface ProfileDao {
    @Query("SELECT * FROM profile")
    fun getProfile(): LiveData<Profile>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(profile: Profile)

    @Query("DELETE FROM profile")
    suspend fun deleteAll()
}