package com.example.tangochoupdated.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.navigation.NavController
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

    val getAllCardsFromDB:LiveData<List<Card>> = repository.allCards.asLiveData()

    private val _parentPosition = MutableLiveData<Int>()
    fun setParentPosition(position: Int){
        if(position<0) return
        _parentPosition.value = position
    }
    val parentPosition :LiveData<Int> = _parentPosition
    fun returnParentPosition():Int{
        return _parentPosition.value ?:0
    }
    private val _setting = MutableLiveData<AnkiSettingPopUpViewModel>()
    fun setSetting(viewModel:AnkiSettingPopUpViewModel){
        _setting.value = viewModel
    }

    fun flipNext(reverseMode:Boolean){
        if((reverseMode&&returnFront())||(reverseMode.not()&&returnFront().not()))
            setParentPosition(returnParentPosition()+1)
        setFront(returnFront().not())
    }
    fun flipPrevious(reverseMode:Boolean){
        if((reverseMode&&returnFront().not())||(reverseMode.not()&&returnFront()))
            setParentPosition(returnParentPosition()-1)
        setFront(returnFront().not())
    }
    private val _front = MutableLiveData<Boolean>()
    fun setFront(boolean: Boolean){
        _front.value = boolean
    }
    val front :LiveData<Boolean> = _front
    fun returnFront():Boolean{
        return _front.value ?:true
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

    private val _flipAction = MutableLiveData<NavDirections>()
    fun setFlipAction (navDirections: NavDirections){
        _flipAction.value = navDirections
    }
    val flipAction :LiveData<NavDirections> = _flipAction

}