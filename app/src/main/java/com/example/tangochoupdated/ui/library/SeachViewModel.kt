package com.example.tangochoupdated.ui.library

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.tangochoupdated.db.MyRoomRepository
import com.example.tangochoupdated.db.dataclass.Card
import com.example.tangochoupdated.db.dataclass.File
import kotlinx.coroutines.flow.Flow

class SearchViewModel(private val repository: MyRoomRepository):ViewModel() {
    fun getCardsByWords(search:String): LiveData<List<Card>> = repository.searchCardsByWords(search).asLiveData()
    fun getFilesByWords(search:String): LiveData<List<File>> = repository.searchFilesByWords(search).asLiveData()
    private val _searchingText = MutableLiveData<String>()
    fun setSearchText(string: String){
        _searchingText.value = string
    }
    val searchingText:LiveData<String> = _searchingText

}