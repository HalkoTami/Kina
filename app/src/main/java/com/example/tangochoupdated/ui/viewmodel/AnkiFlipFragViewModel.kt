package com.example.tangochoupdated.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.navigation.NavDirections
import com.example.tangochoupdated.db.MyRoomRepository
import com.example.tangochoupdated.db.dataclass.Card
import com.example.tangochoupdated.db.dataclass.File

class AnkiFlipFragViewModel(val repository: MyRoomRepository) : ViewModel() {


    private val _text = MutableLiveData<String>().apply {
        value = "This is Anki Fragment"
    }
    fun getCardFromDB(cardId:Int) :LiveData<Card> = repository.getCardByCardId(cardId).asLiveData()
    val _parentCard = MutableLiveData<Card>()
    fun setParentCard(card: Card){
        _parentCard.value = card
    }
    val parentCard :LiveData<Card> = _parentCard

    private val _parentPosition = MutableLiveData<Int>()
    fun setParentPosition(position: Int){
        _parentPosition.value = position
    }
    val parentPosition :LiveData<Int> = _parentPosition
    fun returnParentPosition():Int{
        return _parentPosition.value ?:0
    }


    val _ankiFlipItems = MutableLiveData<MutableList<Card>>()
    fun setAnkiFlipItems(list: List<Card>){
        val a = mutableListOf<Card>()
        a.addAll(list)
        _ankiFlipItems.value = a
    }
    fun returnFlipItems():MutableList<Card>{
        return  _ankiFlipItems.value ?: mutableListOf()
    }
    val ankiFlipItems :LiveData<MutableList<Card>> = _ankiFlipItems

}