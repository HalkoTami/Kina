package com.korokoro.kina.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.korokoro.kina.db.MyRoomRepository
import com.korokoro.kina.ui.customClasses.AnkiFragments

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
    val settingVisible:LiveData<Boolean> = _settingVisible



}