package com.example.tangochoupdated.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.navigation.NavDirections
import com.example.tangochoupdated.db.MyRoomRepository
import com.example.tangochoupdated.db.dataclass.Card
import com.example.tangochoupdated.db.dataclass.File

class AnkiBoxFragViewModel(val repository: MyRoomRepository) : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Anki Fragment"
    }
    val allFlashCardCoverFromDB: LiveData<List<File>> = repository.allFlashCardCover.asLiveData()
    fun getLibraryFilesFromDB(parentFileId:Int?) :LiveData<List<File>> = repository.mygetFileDataByParentFileId(parentFileId).asLiveData()
    private val _libraryFilesAsAnkiBox = MutableLiveData<List<File>>()
    fun setLibraryFilesAsAnkiBox(list: List<File>){
        _libraryFilesAsAnkiBox.value = list
    }
    val libraryFilesAsAnkiBox:LiveData<List<File>> = _libraryFilesAsAnkiBox

    fun getLibraryCardsFromDB(parentFileId:Int?) :LiveData<List<Card>> = repository.getCardDataByFileId(parentFileId).asLiveData()
    private val _libraryCardsAsAnkiBox = MutableLiveData<List<Card>>()
    fun setLibraryCardsAsAnkiBox(list: List<Card>){
        _libraryCardsAsAnkiBox.value = list
    }

    private val _tabChangeAction = MutableLiveData<NavDirections>()
    fun setTabChangeAction (navDirections: NavDirections){
        _tabChangeAction.value = navDirections
    }
    val tabChangeAction :LiveData<NavDirections> = _tabChangeAction

    val text: LiveData<String> = _text

    fun onClickCheckBox(item:Any){

    }
}