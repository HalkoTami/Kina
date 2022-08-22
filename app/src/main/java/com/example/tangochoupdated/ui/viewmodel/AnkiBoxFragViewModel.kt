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
    fun onCreate(){
        setAnkiBoxItems(mutableListOf())
    }

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
    fun getCardsFromDBByMultipleCardIds(cardIds:List<Int>) :LiveData<List<Card>> = repository.getCardsByMultipleCardId(cardIds).asLiveData()


    fun getDescendantsCardIds(fileIdList:List<Int>) :LiveData<List<Int>> = repository.getDescendantsCardsIsByMultipleFileId(fileIdList).asLiveData()
    private val _ankiBoxItems = MutableLiveData<MutableList<Card>>()
    fun setAnkiBoxItems(list: List<Card>){
        val a = mutableListOf<Card>()
        a.addAll(list)
        _ankiBoxItems.value = a
    }
    fun returnAnkiBoxItems():List<Card>?{
        return  _ankiBoxItems.value
    }
    val ankiBoxItems :LiveData<MutableList<Card>> = _ankiBoxItems

    private val _tabChangeAction = MutableLiveData<NavDirections>()
    fun setTabChangeAction (navDirections: NavDirections){
        _tabChangeAction.value = navDirections
    }
    val tabChangeAction :LiveData<NavDirections> = _tabChangeAction

    val text: LiveData<String> = _text

    private val _ankiBoxFileIds = MutableLiveData<MutableList<Int>>()
    fun addToAnkiBoxFileIds(list: List<Int>){
        val a = mutableListOf<Int>()
        a.addAll(_ankiBoxFileIds.value ?: mutableListOf())
        a.addAll(list)
        setAnkiBoxFileIds(a)

    }
    fun removeFromAnkiBoxFileIds(id:Int){
        val a = mutableListOf<Int>()
        a.addAll(_ankiBoxFileIds.value ?: mutableListOf())
        a.remove(id)
        setAnkiBoxFileIds(a)

    }
    fun setAnkiBoxFileIds (list:MutableList<Int>){
        _ankiBoxFileIds.value = list
    }
    val ankiBoxFileIds:LiveData<MutableList<Int>> = _ankiBoxFileIds
    private val _ankiBoxCardIds = MutableLiveData<MutableList<Int>>()
    fun addToAnkiBoxCardIds(list: List<Int>){
        val a = mutableListOf<Int>()
        a.addAll(_ankiBoxCardIds.value ?: mutableListOf())
        a.addAll(list)
        setAnkiBoxCardIds(a)

    }
    fun setAnkiBoxCardIds (list:List<Int>){
        val a = mutableListOf<Int>()
        a.addAll(list)
        _ankiBoxCardIds.value = a
    }

    fun addToAnkiBoxItems(list: List<Card>){
        val a = mutableListOf<Card>()
        a.addAll(_ankiBoxItems.value ?: mutableListOf())
        a.addAll(list)
        setAnkiBoxItems(a)

    }
    val ankiBoxCardIds:LiveData<MutableList<Int>> = _ankiBoxCardIds
    fun onClickCheckBox(item:Any){


    }

    private val _modeCardsNotSelected = MutableLiveData<Boolean>()
    fun setModeCardsNotSelected(boolean: Boolean){
        _modeCardsNotSelected.value = boolean
    }
    fun returnModeCardsNotSelected():Boolean?{
        return _modeCardsNotSelected.value
    }
    val modeCardsNotSelected:LiveData<Boolean> = _modeCardsNotSelected
}