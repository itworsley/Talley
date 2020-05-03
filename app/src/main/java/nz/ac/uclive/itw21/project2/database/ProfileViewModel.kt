package nz.ac.uclive.itw21.project2.database

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application): AndroidViewModel(application) {
    private val repository: ProfileRepository
    val profile: LiveData<Profile>

    init {
        val profileDao = TalleyDatabase.getDatabase(application, viewModelScope).profileDao()
        repository = ProfileRepository(profileDao)
        profile = repository.profile
    }

    fun insert(profile: Profile) = viewModelScope.launch {
        repository.insert(profile)
    }
}