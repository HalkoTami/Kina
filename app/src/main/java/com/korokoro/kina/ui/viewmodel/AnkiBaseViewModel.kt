package com.korokoro.kina.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import com.korokoro.kina.db.MyRoomRepository
import com.korokoro.kina.ui.viewmodel.customClasses.AnkiFragments

class AnkiBaseViewModel(val repository: MyRoomRepository) : ViewModel() {
    private val _activeFragment = MutableLiveData<AnkiFragments>()
    fun setActiveFragment (ankiFragments: AnkiFragments){
        _activeFragment.value = ankiFragments
    }
    fun returnActiveFragment(): AnkiFragments {
        return _activeFragment.value ?: AnkiFragments.AnkiBox
    }
    val activeFragment :LiveData<AnkiFragments> = _activeFragment


    private val _ankiBaseNavCon = MutableLiveData<NavController>()
    fun setAnkiBoxNavCon(navController: NavController){
        _ankiBaseNavCon.value = navController
    }
    fun returnAnkiBoxNavCon(): NavController?{
        return _ankiBaseNavCon.value
    }
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