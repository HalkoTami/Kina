package com.example.tangochoupdated

import androidx.compose.runtime.internal.illegalDecoyCallException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.tangochoupdated.room.MyRoomRepository
import com.example.tangochoupdated.ui.create.card.CreateCardViewModel
import com.example.tangochoupdated.ui.library.LibraryViewModel
import com.example.tangochoupdated.ui.create.file.CreateFileViewModel
import java.security.Provider

class ViewModelFactory(private val repository: MyRoomRepository  ) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val a = when{
            modelClass.isAssignableFrom(BaseViewModel::class.java)-> BaseViewModel(repository)
            modelClass.isAssignableFrom(LibraryViewModel::class.java)-> LibraryViewModel(repository)
            modelClass.isAssignableFrom(CreateFileViewModel::class.java)-> CreateFileViewModel(repository)
            modelClass.isAssignableFrom(CreateCardViewModel::class.java)-> CreateCardViewModel(repository)
            else ->  illegalDecoyCallException("unknown ViewModel class")
        }
        return a as T

    }
}
