package com.example.tangochoupdated.ui.create.card.string

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tangochoupdated.room.dataclass.Card
import com.example.tangochoupdated.room.dataclass.StringData

class StringCardViewModel:ViewModel() {
    private val _parentCard = MutableLiveData<Card?>()
    val parentCard:LiveData<Card?> = _parentCard
    fun setParentCard (card: Card?){
        _parentCard.value = card
    }

    private val _sendStringData = MutableLiveData<Boolean>()
    val sendStringData :LiveData<Boolean> = _sendStringData
    fun setSendStringData(boolean: Boolean){
        _sendStringData.value = boolean
    }

    private val _stringData = MutableLiveData<StringData>()
    val stringData:LiveData<StringData> = _stringData

    fun setStringData(stringData: StringData){
        _stringData.value = stringData


    }
    val a = "hinyanay"



}