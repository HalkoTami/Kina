package com.korokoro.kina.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.korokoro.kina.db.MyRoomRepository
import com.korokoro.kina.db.dataclass.Card
import com.korokoro.kina.db.dataclass.File

class SearchViewModel(private val repository: MyRoomRepository):ViewModel() {
    fun getCardsByWords(search:String): LiveData<List<Card>> = repository.searchCardsByWords(search).asLiveData()
    fun getFilesByWords(search:String): LiveData<List<File>> = repository.searchFilesByWords(search).asLiveData()
    private val _matchedCards = MutableLiveData<List<Card>>()
    fun setMatchedCards(list: List<Card>){
        _matchedCards.value = list
    }
    fun returnMatchedCards():List<Card>{
        return _matchedCards.value ?: mutableListOf()
    }
    private val _matchedFile = MutableLiveData<List<File>>()
    fun setMatchedFiles(list: List<File>){
        _matchedFile.value = list
    }
    fun returnMatchedFiles():List<File>{
        return _matchedFile.value ?: mutableListOf()
    }
    private val _matchedItems = MutableLiveData<List<Any>>()
    fun setMatchedItems(list: List<Any>){
        _matchedItems.value = list
    }
    val matchedItems :LiveData<List<Any>> = _matchedItems
    private val _searchingText = MutableLiveData<String>()
    fun setSearchText(string: String){
        _searchingText.value = string
    }
    val searchingText:LiveData<String> = _searchingText

}