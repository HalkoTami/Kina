package com.example.tangochoupdated.ui.library

import androidx.lifecycle.*
import com.example.tangochoupdated.room.MyRoomRepository
import com.example.tangochoupdated.room.dataclass.Card
import com.example.tangochoupdated.room.rvclasses.LibraryRV
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.launch

class LibraryViewModel(private val repository: MyRoomRepository) : ViewModel() {
    lateinit var cardsWithoutParent: LiveData<List<LibraryRV>>
    fun getRV(fileId: Int?) = viewModelScope.launch {
        cardsWithoutParent =  repository.getLibRVCover(fileId).asLiveData()
    }
    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */

    private val _text = MutableLiveData<String>().apply {
        value = "This is library Fragment"
    }
    val text: LiveData<String> = _text
}
class LibraryViewModelFactory(private val repository: MyRoomRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LibraryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LibraryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}