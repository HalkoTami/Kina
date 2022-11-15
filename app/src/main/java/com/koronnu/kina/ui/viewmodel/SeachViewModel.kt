package com.koronnu.kina.ui.viewmodel

import android.content.Context
import android.widget.EditText
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.koronnu.kina.actions.hideKeyBoard
import com.koronnu.kina.actions.showKeyBoard
import com.koronnu.kina.db.MyRoomRepository
import com.koronnu.kina.db.dataclass.Card
import com.koronnu.kina.db.dataclass.File

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
    fun returnSearchText():String{
        return _searchingText.value ?:""
    }
    val searchingText:LiveData<String> = _searchingText
    private val _searchModeActive = MutableLiveData<Boolean>()
    fun setSearchModeActive(boolean: Boolean){
        _searchModeActive.value = boolean
    }
    fun onClickSearchLoup(edtLibrarySearch:EditText,context: Context){
        edtLibrarySearch.requestFocus()
        showKeyBoard(edtLibrarySearch,context)
        setSearchModeActive(true)
    }
    fun onClickCancel(edtLibrarySearch:EditText,context: Context){
        hideKeyBoard(edtLibrarySearch,context)
        setSearchModeActive(false)
    }
    fun returnSearchModeActive():Boolean{
        return _searchModeActive.value ?:false
    }
    val searchModeActive:LiveData<Boolean> = _searchModeActive


}