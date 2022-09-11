package com.example.tangochoupdated.ui.viewmodel

import androidx.compose.runtime.internal.illegalDecoyCallException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.tangochoupdated.db.MyRoomRepository

class ViewModelFactory(private val repository: MyRoomRepository  ) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val a = when{
            modelClass.isAssignableFrom(LibraryViewModel::class.java)-> LibraryViewModel(repository)
            modelClass.isAssignableFrom(CreateFileViewModel::class.java)-> CreateFileViewModel(repository)
            modelClass.isAssignableFrom(CreateCardViewModel::class.java)-> CreateCardViewModel(repository)
            modelClass.isAssignableFrom(SearchViewModel::class.java) -> SearchViewModel(repository)
            modelClass.isAssignableFrom(StringCardViewModel::class.java) -> StringCardViewModel()
            modelClass.isAssignableFrom(AnkiBoxFragViewModel::class.java) -> AnkiBoxFragViewModel(repository)
            modelClass.isAssignableFrom(AnkiFragBaseViewModel::class.java) -> AnkiFragBaseViewModel(repository)
            modelClass.isAssignableFrom(AnkiFlipFragViewModel::class.java) -> AnkiFlipFragViewModel(repository)
            modelClass.isAssignableFrom(AnkiFlipTypeAndCheckViewModel::class.java) -> AnkiFlipTypeAndCheckViewModel(repository)
            modelClass.isAssignableFrom(DeletePopUpViewModel::class.java) -> DeletePopUpViewModel(repository)
            modelClass.isAssignableFrom(ChooseFileMoveToViewModel::class.java) -> ChooseFileMoveToViewModel(repository)
            else ->  illegalDecoyCallException("unknown ViewModel class")
        }
        return a as T

    }
}
