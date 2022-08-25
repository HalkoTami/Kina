package com.example.tangochoupdated.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavDirections
import com.example.tangochoupdated.db.MyRoomRepository
import com.example.tangochoupdated.db.enumclass.AnkiFragments

class AnkiFragBaseViewModel(val repository: MyRoomRepository) : ViewModel() {
    private val _activeFragment = MutableLiveData<AnkiFragments>()
    fun setActiveFragment (ankiFragments: AnkiFragments){
        _activeFragment.value = ankiFragments
    }
    fun returnActiveFragment(): AnkiFragments {
        return _activeFragment.value ?: AnkiFragments.AnkiBox
    }
    val activeFragment :LiveData<AnkiFragments> = _activeFragment


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