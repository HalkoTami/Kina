package com.koronnu.kina.tabLibrary.inBox

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.CreationExtras
import com.koronnu.kina.R
import com.koronnu.kina.application.RoomApplication
import com.koronnu.kina.db.MyRoomRepository
import com.koronnu.kina.db.dataclass.Card
import com.koronnu.kina.tabLibrary.LibraryBaseViewModel

class LibraryInBoxFragViewModel(repository: MyRoomRepository):ViewModel() {
    private lateinit var libraryBaseViewModel: LibraryBaseViewModel


    companion object {
        fun getFactory(libraryBaseViewModel: LibraryBaseViewModel,
                       context: Context): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]) as RoomApplication
                val repository = application.repository
                val viewModel = LibraryInBoxFragViewModel(repository)
                viewModel.libraryBaseViewModel = libraryBaseViewModel

                return viewModel as T
            }

        }
    }
    private val inBoxItem:LiveData<List<Card>> = repository.getCardDataByFileId(null).asLiveData()

    private val inBoxItemObserver = Observer<List<Card>>{
        libraryBaseViewModel.setParentRVItems(it)
    }
    fun observeLiveDataInInboxFrag(lifecycleOwner: LifecycleOwner){
        inBoxItem.observe(lifecycleOwner,inBoxItemObserver)
    }






}