package com.example.tangochoupdated.ui

import androidx.compose.runtime.internal.illegalDecoyCallException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.tangochoupdated.room.MyRoomRepository
import com.example.tangochoupdated.ui.create.card.CreateCardViewModel
import com.example.tangochoupdated.ui.create.card.string.StringCardViewModel
import com.example.tangochoupdated.ui.library.LibraryViewModel
import com.example.tangochoupdated.ui.create.file.CreateFileViewModel
import com.example.tangochoupdated.ui.mainactivity.BaseViewModel

class ViewModelFactory(private val repository: MyRoomRepository  ) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val a = when{
            modelClass.isAssignableFrom(BaseViewModel::class.java)-> BaseViewModel(repository)
            modelClass.isAssignableFrom(LibraryViewModel::class.java)-> LibraryViewModel(repository)
            modelClass.isAssignableFrom(CreateFileViewModel::class.java)-> CreateFileViewModel(repository)
            modelClass.isAssignableFrom(CreateCardViewModel::class.java)-> CreateCardViewModel(repository)
            modelClass.isAssignableFrom(StringCardViewModel::class.java) -> StringCardViewModel()
            else ->  illegalDecoyCallException("unknown ViewModel class")
        }
        return a as T

    }
}
