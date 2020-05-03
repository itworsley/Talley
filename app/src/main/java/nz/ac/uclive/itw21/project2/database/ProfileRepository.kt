package nz.ac.uclive.itw21.project2.database

import androidx.lifecycle.LiveData

class ProfileRepository(private val profileDao: ProfileDao) {
    val profile: LiveData<Profile> = profileDao.getProfile()

    suspend fun insert(profile: Profile) {
        profileDao.insert(profile)
    }
}