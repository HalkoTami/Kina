package com.example.tangochoupdated.ui.viewmodel

import androidx.compose.runtime.MutableState
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tangochoupdated.db.enumclass.AnkiFilter
import com.example.tangochoupdated.db.enumclass.AnkiOrder
import com.example.tangochoupdated.db.enumclass.AutoFlip

class AnkiSettingPopUpViewModel : ViewModel() {


    fun start(){
        setAnkiFilter(AnkiFilter())
        setAnkiOrder(AnkiOrder.Library)
        setAutoFlip(AutoFlip())
        setTypeAnswer(false)
        setReverseCardSide(false)

    }
    private val _ankiOrder = MutableLiveData<AnkiOrder>()
    fun setAnkiOrder(ankiOrder:AnkiOrder){
        _ankiOrder.value = ankiOrder
    }
    val ankiOrder:LiveData<AnkiOrder> = _ankiOrder

    private val _ankiFilter = MutableLiveData<AnkiFilter>()
    fun setAnkiFilter(ankiFilter: AnkiFilter){
        _ankiFilter.value = ankiFilter
    }
    val ankiFilter:LiveData<AnkiFilter> = _ankiFilter
    class CheckDiff(var a:Boolean, val b:Boolean){
        fun makeChange(){
            if(a!= b) a = b
        }
    }

//    fun changeAnkiFilter(change: AnkiFilter){
//        val before = _ankiFilter.value ?: AnkiFilter()
//        arrayOf(
//            CheckDiff(before.remembered, change.remembered),
//            CheckDiff(before.rememberedFilterActive, change.rememberedFilterActive),
//            CheckDiff(before.flag, change.flag),
//            CheckDiff(before.flagFilterActive, change.flagFilterActive),
//            CheckDiff(before.correctAnswerTyped , change.correctAnswerTyped),
//            CheckDiff(before.answerTypedFilterActive , change.answerTypedFilterActive)
//            ).onEach { it.makeChange() }
////        if(before.flagFilterActive != change.flagFilterActive) before.flagFilterActive = change.flagFilterActive
////        if(before.flag != change.flag) before.flag = change.flag
////        if(before.rememberedFilterActive != change.flagFilterActive) before.flagFilterActive = change.flagFilterActive
//
//
//
//        setAnkiFilter(before)
//    }
    fun returnAnkiFilter():AnkiFilter?{
        return _ankiFilter.value
    }

    private val _autoFlip = MutableLiveData<AutoFlip>()
    fun setAutoFlip(autoFlip: AutoFlip){
        _autoFlip.value = autoFlip
    }
    val autoFlip:LiveData<AutoFlip> = _autoFlip
    fun returnAutoFlip():AutoFlip?{
        return _autoFlip.value
    }


    fun changeAutoFlip(change: AutoFlip){
        val before = _autoFlip.value ?: AutoFlip()
        if(before.active!= change.active ) before.active = change.active
        if(before.seconds!= change.seconds ) before.seconds = change.seconds
        setAutoFlip(before)
    }

    private val _typeAnswer = MutableLiveData<Boolean>()
    fun setTypeAnswer(boolean: Boolean){
        _typeAnswer.value = boolean
    }
    val typeAnswer:LiveData<Boolean> = _typeAnswer

    private val _reverseCardSide = MutableLiveData<Boolean>()
    fun setReverseCardSide(boolean: Boolean){
        _reverseCardSide.value = boolean
    }
    val reverseCardSide:LiveData<Boolean> = _reverseCardSide

    private val _active = MutableLiveData<Boolean>()
    fun setActive(boolean: Boolean){
        _active.value = boolean
    }
    val active:LiveData<Boolean> = _active

}