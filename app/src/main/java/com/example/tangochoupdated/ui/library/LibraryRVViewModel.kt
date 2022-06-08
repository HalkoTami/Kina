package com.example.tangochoupdated.ui.library

import androidx.lifecycle.*
import com.example.tangochoupdated.room.MyRoomRepository
import com.example.tangochoupdated.room.dataclass.File
import com.example.tangochoupdated.room.rvclasses.LibraryRV
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class  LibraryViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Library Fragment"
    }
    val text: LiveData<String> = _text
}
class LibraryRVViewModel(private val repository: MyRoomRepository) : ViewModel() {
    val test:LiveData<File> = repository.allFolder.asLiveData()


    fun rvList(id:Int?) =
      viewModelScope.launch() { repository.getLibRVCover(id) }


    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */

    private val _text = MutableLiveData<String>().apply {
        value = "This is library Fragment"
    }
    val text: LiveData<String> = _text
}
class ViewModelFactory(private val repository: MyRoomRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LibraryRVViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LibraryRVViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}