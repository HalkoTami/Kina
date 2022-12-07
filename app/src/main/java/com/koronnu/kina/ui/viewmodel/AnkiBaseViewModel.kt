package com.koronnu.kina.ui.viewmodel

import android.text.Spannable.Factory
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.navigation.NavController
import com.koronnu.kina.application.RoomApplication
import com.koronnu.kina.db.MyRoomRepository
import com.koronnu.kina.customClasses.enumClasses.AnkiFragments
import com.koronnu.kina.customClasses.enumClasses.MainFragment

class AnkiBaseViewModel() : ViewModel() {
    lateinit var mainViewModel: MainViewModel

    companion object{
        fun getFactory(mainViewModel: MainViewModel): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val ankiBaseViewModel = AnkiBaseViewModel()
                ankiBaseViewModel.mainViewModel = mainViewModel
                return ankiBaseViewModel as T
            }
        }
    }



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

    fun doOnBackPress(): Boolean {
        val isActive = mainViewModel.returnFragmentStatus()?.now == MainFragment.Anki
        if(!isActive) return false
        val isStartFragment = returnActiveFragment()==AnkiFragments.AnkiBox
        if(isStartFragment
            &&!returnSettingVisible()) return false
        if(returnSettingVisible()) {
            setSettingVisible(false)
            return true
        }
        if(!isStartFragment) returnAnkiBaseNavCon()?.popBackStack()
        return true
    }

    val settingVisible:LiveData<Boolean> = _settingVisible



}