package com.example.tangochoupdated.ui.viewmodel

import androidx.lifecycle.*
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import com.example.tangochoupdated.db.MyRoomRepository
import com.example.tangochoupdated.db.dataclass.Card
import com.example.tangochoupdated.db.dataclass.File
import kotlinx.coroutines.launch

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
        if(position<0||returnFlipItems().size-1<position) return else
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
        val changeCard = (reverseMode&&returnFront())||(reverseMode.not()&&returnFront().not())
        val isLastCard = (returnParentPosition()==returnFlipItems().size-1)
        if(changeCard)
            setParentPosition(returnParentPosition()+1)
        if(!(isLastCard&&changeCard))
            setFront(returnFront().not())

    }
    fun flipPrevious(reverseMode:Boolean){
        val changeCard = (reverseMode&&returnFront().not())||(reverseMode.not()&&returnFront())
        val isFirstCard = (returnParentPosition()==0)
        if(changeCard)
            setParentPosition(returnParentPosition()-1)
        if(!(isFirstCard&&changeCard))
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

    fun changeRememberStatus(){
        val change = _parentCard.value ?:return
        change.remembered = change.remembered.not()
        viewModelScope.launch {
            repository.update(change)
        }
    }



}