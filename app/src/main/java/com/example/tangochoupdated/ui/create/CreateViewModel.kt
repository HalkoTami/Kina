package com.example.tangochoupdated.ui.planner

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tangochoupdated.room.MyRoomRepository

class CreateViewModel(repository: MyRoomRepository) : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is planner Fragment"
    }
    val text: LiveData<String> = _text
}