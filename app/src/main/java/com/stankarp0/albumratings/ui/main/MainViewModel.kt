package com.stankarp0.albumratings.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.stankarp0.albumratings.services.*
import kotlinx.coroutines.*


class MainViewModel : ViewModel() {

    // The internal MutableLiveData String that stores the most recent response
    private val _response = MutableLiveData<String>()
    private val _albumObject = MutableLiveData<AlbumObject>()

    // The external immutable LiveData for the response String
    val response: LiveData<String>
        get() = _response

    val albumObject: LiveData<AlbumObject>
        get() = _albumObject

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    /**
     * Call updateRandomAlbums() on init so we can display status immediately.
     */
    init {
        Log.i("MainViewModel", "init")
        updateRandomAlbums()
    }

    /**
     * Sets the value of the status LiveData to the Mars API status.
     */
    // ------------- Albums ---------------
    private fun updateRandomAlbums() {
        coroutineScope.launch {
            val randomDeferred = AlbumApi.retrofitService.query("Lennon")

            try {
                val result = randomDeferred.await()
                _response.value ="Success: ${result.albums.size} Albums retrieved"
                _albumObject.value = result
            } catch (e: Exception) {
                _response.value = "Failure: ${e.message}"
            }
            Log.i("MainViewModel", _response.value.toString())
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}
