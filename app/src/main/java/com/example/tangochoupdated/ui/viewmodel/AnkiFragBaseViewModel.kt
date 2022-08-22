package com.example.tangochoupdated.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.navigation.NavDirections
import com.example.tangochoupdated.db.MyRoomRepository
import com.example.tangochoupdated.db.dataclass.Card
import com.example.tangochoupdated.db.dataclass.File

class AnkiFragBaseViewModel(val repository: MyRoomRepository) : ViewModel() {
    private val _ankiFragChangeAction = MutableLiveData<NavDirections>()
    fun setAnkiFragChangeAction (navDirections: NavDirections){
        _ankiFragChangeAction.value = navDirections
    }
    val tabChangeAction :LiveData<NavDirections> = _ankiFragChangeAction
    private val _settingVisible = MutableLiveData<Boolean>()
    fun setSettingVisible(boolean: Boolean){
        _settingVisible.value = boolean
    }
    val settingVisible:LiveData<Boolean> = _settingVisible



}