package com.example.tangochoupdated.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.navigation.NavDirections
import com.example.tangochoupdated.db.MyRoomRepository
import com.example.tangochoupdated.db.dataclass.Card
import com.example.tangochoupdated.db.dataclass.File

class AnkiFlipTypeAndCheckViewModel() : ViewModel() {
   private val _keyBoardVisible = MutableLiveData<Boolean>()
    fun setKeyBoardVisible(boolean: Boolean){
        val before = _keyBoardVisible.value
        if(before != boolean){
            _keyBoardVisible.value = boolean
        } else return

    }
    fun returnKeyBoardVisible():Boolean{
        return _keyBoardVisible.value ?:false
    }
    val keyBoardVisible :LiveData<Boolean> = _keyBoardVisible
}