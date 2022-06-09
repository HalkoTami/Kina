package com.example.tangochoupdated.ui.library

import androidx.lifecycle.*
import com.example.tangochoupdated.RoomApplication
import com.example.tangochoupdated.room.MyRoomRepository
import com.example.tangochoupdated.room.dataclass.File
import com.example.tangochoupdated.room.rvclasses.LibraryRV
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class  LibraryViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Library Fragment"
    }
    val text: LiveData<String> = _text
}
class LibraryRVViewModel(private val repository: MyRoomRepository) : ViewModel(){
    var _listData = MutableLiveData<List<LibraryRV>>()
    suspend  fun get ():List<LibraryRV>{
        return repository.getLibRVCover(null)
    }



    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    val list: LiveData<List<LibraryRV>> = _listData
}
class ViewModelFactory(private val repository: MyRoomRepository) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LibraryRVViewModel(repository) as T
    }
}