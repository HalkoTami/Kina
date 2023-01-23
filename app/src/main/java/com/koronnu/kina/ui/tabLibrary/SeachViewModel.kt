package com.koronnu.kina.ui.viewmodel

import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.CreationExtras
import com.koronnu.kina.RoomApplication
import com.koronnu.kina.db.MyRoomRepository
import com.koronnu.kina.data.source.local.entity.Card
import com.koronnu.kina.data.source.local.entity.File

class SearchViewModel(private val repository: MyRoomRepository):ViewModel() {

    companion object{
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]) as RoomApplication
                val repository = application.repository
                return SearchViewModel(repository) as T
            }

        }
    }
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
    fun returnSearchText():String{
        return _searchingText.value ?:""
    }
    val searchingText:LiveData<String> = _searchingText
    private val _searchModeActive = MutableLiveData<Boolean>()
    fun setSearchModeActive(boolean: Boolean){
        _searchModeActive.value = boolean
    }

    private fun returnSearchModeActive():Boolean{
        return _searchModeActive.value ?:false
    }

    fun doOnBackPress(): Boolean {
        if(!returnSearchModeActive()) return false
        setSearchModeActive(false)
        return true
    }

    val searchModeActive:LiveData<Boolean> = _searchModeActive


}