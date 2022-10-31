package com.korokoro.kina.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.korokoro.kina.db.dataclass.Card
import com.korokoro.kina.db.dataclass.StringData
import com.korokoro.kina.customClasses.StringFragFocusedOn

class CardTypeStringViewModel:ViewModel() {
    private val _parentCard = MutableLiveData<Card?>()
    val parentCard:LiveData<Card?> = _parentCard
    fun setParentCard (card: Card?){
        _parentCard.value = card
    }
    fun returnParentCard ():Card?{
        return _parentCard.value
    }

    fun onCreate(){
        setFocusedOn(StringFragFocusedOn.None)
    }


    private val _focusedOn = MutableLiveData<StringFragFocusedOn?>()
    val focusedOn :LiveData<StringFragFocusedOn?> = _focusedOn
    fun setFocusedOn(stringFragFocusedOn: StringFragFocusedOn?){
        _focusedOn.value = stringFragFocusedOn
    }

    private val _stringData = MutableLiveData<StringData>()
    val stringData:LiveData<StringData> = _stringData



}