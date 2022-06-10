package com.example.tangochoupdated.ui.library

import androidx.lifecycle.*
import com.example.tangochoupdated.RoomApplication
import com.example.tangochoupdated.room.MyRoomRepository
import com.example.tangochoupdated.room.dataclass.File
import com.example.tangochoupdated.room.rvclasses.LibraryRV
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext


class LibraryRVViewModel(private val repository: MyRoomRepository) : ViewModel(){
    private var _listData = MutableLiveData<List<LibraryRV>>()
    fun fetchListData(id:Int?){
        var a :List<LibraryRV>
        viewModelScope.launch {
            withContext(Dispatchers.Default){
                val list = repository.getLibRVCover(id)
                return@withContext  fetchData(list)
            }
        }

    }

    private fun fetchData(a:List<LibraryRV>){
        _listData.value = a
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
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