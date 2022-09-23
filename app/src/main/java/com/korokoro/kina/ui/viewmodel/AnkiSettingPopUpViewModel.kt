package com.korokoro.kina.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.korokoro.kina.ui.customClasses.AnkiFilter
import com.korokoro.kina.ui.customClasses.AnkiOrder
import com.korokoro.kina.ui.customClasses.AutoFlip

class AnkiSettingPopUpViewModel : ViewModel() {


    fun start(){
        setAnkiFilter(AnkiFilter())
        setAnkiOrder(AnkiOrder.Library)
        setAutoFlip(AutoFlip())
        setTypeAnswer(false)
        setReverseCardSide(false)

    }
    private val _ankiOrder = MutableLiveData<AnkiOrder>()
    fun setAnkiOrder(ankiOrder: AnkiOrder){
        _ankiOrder.value = ankiOrder
    }

    private val _ankiFilter = MutableLiveData<AnkiFilter>()
    fun setAnkiFilter(ankiFilter: AnkiFilter){
        _ankiFilter.value = ankiFilter
    }

    fun returnAnkiFilter(): AnkiFilter {
        return _ankiFilter.value ?: AnkiFilter()
    }

    private val _autoFlip = MutableLiveData<AutoFlip>()
    fun setAutoFlip(autoFlip: AutoFlip){
        _autoFlip.value = autoFlip
    }
    val autoFlip:LiveData<AutoFlip> = _autoFlip
    fun returnAutoFlip(): AutoFlip {
        return _autoFlip.value ?: AutoFlip()
    }

    private val _typeAnswer = MutableLiveData<Boolean>()
    fun setTypeAnswer(boolean: Boolean){
        _typeAnswer.value = boolean
    }
    val typeAnswer:LiveData<Boolean> = _typeAnswer
    fun returnTypeAnswer():Boolean{
        return _typeAnswer.value ?:false
    }

    private val _reverseCardSide = MutableLiveData<Boolean>()
    fun setReverseCardSide(boolean: Boolean){
        _reverseCardSide.value = boolean
    }
    fun returnReverseCardSide():Boolean{
        return _reverseCardSide.value ?:false
    }



}