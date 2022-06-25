package com.example.tangochoupdated.ui.library

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tangochoupdated.BaseViewModel
import com.example.tangochoupdated.room.MyRoomRepository

class LibraryViewModel() : ViewModel() {
    val selectedAmount = MutableLiveData<Int>()

    var multipleSelectMode :Boolean = false
    fun topBarText():String{
        var a = "A"
        if(multipleSelectMode){
            a = "$selectedAmount 個　選択中"
        } else{
            a ="最初"
        }

        return a
    }


}