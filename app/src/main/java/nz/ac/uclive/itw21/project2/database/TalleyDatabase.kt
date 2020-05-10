package nz.ac.uclive.itw21.project2.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@Database(entities = [Profile::class, Device::class], version = 1, exportSchema = false)
@TypeConverters(TimeConverter::class)
abstract class TalleyDatabase : RoomDatabase() {
    abstract fun profileDao(): ProfileDao
    abstract fun deviceDao(): DeviceDao


    private class DatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(database.profileDao(), database.deviceDao())
                }
            }
        }

        suspend fun populateDatabase(profileDao: ProfileDao, deviceDao: DeviceDao) {
            // Delete all content here.
//            profileDao.deleteAll()
//            deviceDao.insert(Device(null, "hello", "1", "20/04/2020", 23, "HI", 2))
//            deviceDao.insert(Device(null, "Device", "1", "20/05/2018", "23.90", "HI", 100, "", ""))
//            deviceDao.deleteAll()
        }
    }


    companion object {
        // Singleton prevents multiple instances of database opening at the same time.
        @Volatile
        private var INSTANCE: TalleyDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): TalleyDatabase {
            val tempInstance = INSTANCE

            if (tempInstance != null) {
                return tempInstance
            }

            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TalleyDatabase::class.java,
                    "Talley_database"
                )
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }


}