package com.koronnu.kina.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.koronnu.kina.db.MyRoomRepository
import com.koronnu.kina.customClasses.enumClasses.AnkiFragments

class AnkiBaseViewModel(val repository: MyRoomRepository) : ViewModel() {
    private val _activeFragment = MutableLiveData<AnkiFragments>()
    fun setActiveFragment (ankiFragments: AnkiFragments){
        _activeFragment.value = ankiFragments
    }
    fun returnActiveFragment(): AnkiFragments {
        return _activeFragment.value ?: AnkiFragments.AnkiBox
    }
    private val _ankiBaseNavCon = MutableLiveData<NavController>()
    fun setAnkiBaseNavCon(navController: NavController){
        _ankiBaseNavCon.value = navController
    }
    fun returnAnkiBaseNavCon(): NavController?{
        return _ankiBaseNavCon.value
    }
    private val _settingVisible = MutableLiveData<Boolean>()
    fun setSettingVisible(boolean: Boolean){
        _settingVisible.value = boolean
    }
    fun returnSettingVisible():Boolean{
        return _settingVisible.value ?:false
    }
    val settingVisible:LiveData<Boolean> = _settingVisible



}