package com.example.apparchitecturepersistence.sleeptracker

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.apparchitecturepersistence.database.SleepDatabaseDao
import com.example.apparchitecturepersistence.database.SleepNight
import com.example.apparchitecturepersistence.formatNights
import kotlinx.coroutines.*

class SleepTrackerViewModel(val database: SleepDatabaseDao,
                            application: Application) : AndroidViewModel(application) {

    private var viewModelJob = Job()

    override fun onCleared() {
        super.onCleared()
        _showSnackbarEvent.value = true
        viewModelJob.cancel()
    }



    private val _navigateToSleepQuality = MutableLiveData<SleepNight?>()
    val navigateToSleepQuality: LiveData<SleepNight?>
        get() = _navigateToSleepQuality
    fun doneNavigating() {
        _navigateToSleepQuality.value = null
    }
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val tonight = MutableLiveData<SleepNight?>()
    val nights = database.getAllNights()
    val nightString = Transformations.map(nights) { night ->
        formatNights(night, application.resources)
    }

    private var _showSnackbarEvent = MutableLiveData<Boolean>()

    val showSnackBarEvent: LiveData<Boolean>
        get() = _showSnackbarEvent
    fun doneShowingSnackbar() {
        _showSnackbarEvent.value = false
    }
    private val _navigateToSleepDataQuality = MutableLiveData<Long?>()
    val navigateToSleepDataQuality
        get() = _navigateToSleepDataQuality

    fun onSleepNightClicked(id: Long){
        _navigateToSleepDataQuality.value = id
    }
    fun onSleepDataQualityNavigated() {
        _navigateToSleepDataQuality.value = null
    }




    init {
        uiScope.launch {
            tonight.value = getTonightFromDatabase();
        }
    }
    private suspend fun getTonightFromDatabase(): SleepNight? {
            return withContext(Dispatchers.IO){
                var night = database.getTonight()
                if(night?.endTimeMilli != night?.startTimeMilli) {
                    night = null
                }
                night
            }
    }
    fun onStartTracking() {
        uiScope.launch {
            val newNight = SleepNight()
            insert(newNight)
            tonight.value = getTonightFromDatabase()
        }
    }
    private suspend fun insert(night: SleepNight) {
        withContext(Dispatchers.IO) {
            database.insert(night)
        }

    }
    fun onStopTracking() {
        uiScope.launch {
            val oldNight = tonight.value ?: return@launch
            oldNight.endTimeMilli = System.currentTimeMillis()
            update(oldNight)
            _navigateToSleepQuality.value = oldNight
        }
    }

    private suspend fun update(oldNight: SleepNight) {
        withContext(Dispatchers.IO) {
            database.update(oldNight)
        }
    }

    fun onClear() {
        uiScope.launch {
            clear()
            tonight.value = null
        }
    }
    suspend fun clear() {
        withContext(Dispatchers.IO) {
            database.clear()
        }
    }
}