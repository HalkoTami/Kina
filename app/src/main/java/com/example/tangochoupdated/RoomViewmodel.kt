package com.example.tangochoupdated

import androidx.lifecycle.*
import kotlinx.coroutines.launch

class roomViewModel(private val repository: RoomRepository) : ViewModel() {

    // Using LiveData and caching what allWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    val allWords: LiveData<List<Card>> = repository.allWords.asLiveData()

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert(word: Card) = viewModelScope.launch {
        repository.insert(word)
    }
}

class RoomViewModelFactory(private val repository: RoomRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(roomViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return roomViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}