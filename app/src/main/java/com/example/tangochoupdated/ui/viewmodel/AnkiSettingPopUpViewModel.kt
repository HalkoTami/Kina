package com.example.tangochoupdated.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AnkiSettingPopUpViewModel : ViewModel() {

    fun start(){
        setAnkiFilter(AnkiFilter())
        setAnkiOrder(AnkiOrder.Library)
        setAutoFlip(AutoFlip())
        setTypeAnswer(false)
        setReverseCardSide(false)

    }
    enum class AnkiOrder{
        Library, Random
    }
    class AnkiFilter{
        var correctAnswerTyped:Boolean? = null
        var remembered:Boolean ? = null
        var flag:Boolean? = null
    }
    class AutoFlip{
        var active:Boolean = false
        var seconds:Int = 20
    }

    private val _ankiOrder = MutableLiveData<AnkiOrder>()
    private fun setAnkiOrder(ankiOrder:AnkiOrder){
        _ankiOrder.value = ankiOrder
    }
    val ankiOrder:LiveData<AnkiOrder> = _ankiOrder

    private val _ankiFilter = MutableLiveData<AnkiFilter>()
    private fun setAnkiFilter(ankiFilter: AnkiFilter){
        _ankiFilter.value = ankiFilter
    }
    val ankiFilter:LiveData<AnkiFilter> = _ankiFilter

    private val _autoFlip = MutableLiveData<AutoFlip>()
    private fun setAutoFlip(autoFlip: AutoFlip){
        _autoFlip.value = autoFlip
    }
    val autoFlip:LiveData<AutoFlip> = _autoFlip

    private val _typeAnswer = MutableLiveData<Boolean>()
    private fun setTypeAnswer(boolean: Boolean){
        _typeAnswer.value = boolean
    }
    val typeAnswer:LiveData<Boolean> = _typeAnswer

    private val _reverseCardSide = MutableLiveData<Boolean>()
    private fun setReverseCardSide(boolean: Boolean){
        _reverseCardSide.value = boolean
    }
    val reverseCardSide:LiveData<Boolean> = _reverseCardSide

    private val _active = MutableLiveData<Boolean>()
    fun setActive(boolean: Boolean){
        _active.value = boolean
    }
    val active:LiveData<Boolean> = _active

}