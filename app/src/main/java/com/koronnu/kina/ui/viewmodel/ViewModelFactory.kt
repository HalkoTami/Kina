package com.koronnu.kina.ui.viewmodel

import androidx.compose.runtime.internal.illegalDecoyCallException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.koronnu.kina.db.MyRoomRepository

class ViewModelFactory(private val repository: MyRoomRepository  ) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val finalViewModel = when{
//            modelClass.isAssignableFrom(LibraryBaseViewModel::class.java)-> LibraryBaseViewModel(repository)
            modelClass.isAssignableFrom(EditFileViewModel::class.java)-> EditFileViewModel(repository)
            modelClass.isAssignableFrom(CreateCardViewModel::class.java)-> CreateCardViewModel(repository)
            modelClass.isAssignableFrom(SearchViewModel::class.java) -> SearchViewModel(repository)
            modelClass.isAssignableFrom(CardTypeStringViewModel::class.java) -> CardTypeStringViewModel()
            modelClass.isAssignableFrom(AnkiBoxViewModel::class.java) -> AnkiBoxViewModel(repository)
            modelClass.isAssignableFrom(AnkiBaseViewModel::class.java) -> AnkiBaseViewModel(repository)
            modelClass.isAssignableFrom(AnkiFlipBaseViewModel::class.java) -> AnkiFlipBaseViewModel(repository)
            modelClass.isAssignableFrom(FlipTypeAndCheckViewModel::class.java) -> FlipTypeAndCheckViewModel(repository)
            modelClass.isAssignableFrom(DeletePopUpViewModel::class.java) -> DeletePopUpViewModel(repository)
            modelClass.isAssignableFrom(ChooseFileMoveToViewModel::class.java) -> ChooseFileMoveToViewModel(repository)
            else ->  illegalDecoyCallException("unknown ViewModel class")
        }
        return finalViewModel as T

    }



}
