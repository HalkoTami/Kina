package com.koronnu.kina.ui.viewmodel

import android.content.SharedPreferences
import android.view.View
import androidx.lifecycle.*
import androidx.navigation.NavController
import com.koronnu.kina.actions.hideKeyBoard
import com.koronnu.kina.customClasses.enumClasses.AnkiFragments
import com.koronnu.kina.customClasses.enumClasses.MainFragment
import com.koronnu.kina.databinding.FragmentAnkiBaseBinding
import com.koronnu.kina.ui.tabAnki.ankiItemContent.AnkiBoxContentFragDirections
import com.koronnu.kina.ui.tabAnki.ankiBox.AnkiBoxFragDirections
import com.koronnu.kina.ui.tabAnki.flip.AnkiFlipBaseFragDirections
import com.koronnu.kina.ui.tabAnki.flipCompleted.AnkiFlipCompleteFragDirections

class AnkiBaseViewModel(val mainViewModel: MainViewModel ) : ViewModel() {

    private lateinit var _ankiFlipBaseViewModel: AnkiFlipBaseViewModel
    val ankiFlipBaseViewModel get() = _ankiFlipBaseViewModel
    private lateinit var _ankiSettingPopUpViewModel: AnkiSettingPopUpViewModel
    val ankiSettingPopUpViewModel get() =_ankiSettingPopUpViewModel
    private lateinit var _ankiBoxViewModel: AnkiBoxViewModel
    val ankiBoxViewModel get() = _ankiBoxViewModel

    companion object{
        fun getFactory(mainViewModel: MainViewModel,
                       viewModelStoreOwner: ViewModelStoreOwner): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val ankiBaseViewModel = AnkiBaseViewModel(mainViewModel)
                val ankiSettingPopUpViewModel =
                    ViewModelProvider(viewModelStoreOwner,
                        AnkiSettingPopUpViewModel.getFactory(ankiBaseViewModel)
                    )[AnkiSettingPopUpViewModel::class.java]
                val ankiBoxViewModel =
                    ViewModelProvider(viewModelStoreOwner,AnkiBoxViewModel.getFactory(ankiBaseViewModel))[AnkiBoxViewModel::class.java]
                ankiBaseViewModel._ankiBoxViewModel = ankiBoxViewModel

                ankiBaseViewModel._ankiSettingPopUpViewModel = ankiSettingPopUpViewModel
                val ankiFlipBaseViewModel =
                    ViewModelProvider(viewModelStoreOwner,AnkiFlipBaseViewModel.getFactory(mainViewModel))[AnkiFlipBaseViewModel::class.java]
                ankiBaseViewModel._ankiFlipBaseViewModel = ankiFlipBaseViewModel
                return ankiBaseViewModel as T
            }
        }
    }
    fun changeBnvBtnAddStatus(able:Boolean){
        mainViewModel.mainActivityBinding.bindingTwgActivityMain.imvCreateNewItems.apply {
            visibility = if(able)View.VISIBLE else View.INVISIBLE
            isEnabled = able
        }
    }

    private var _ankiFragBaseBinding:FragmentAnkiBaseBinding? = null
    val ankiFragBaseBinding get() = _ankiFragBaseBinding!!
    private fun setAnkiFragBaseBinding(ankiFragBaseBinding: FragmentAnkiBaseBinding){
        _ankiFragBaseBinding = ankiFragBaseBinding
        setCL()
    }

    fun onFragmentCreated(ankiFragBaseBinding: FragmentAnkiBaseBinding,
                          lifecycleOwner: LifecycleOwner,
                          getSharedPref:(int:Int)->SharedPreferences,
                          navController: NavController, ){
        setAnkiFragBaseBinding(ankiFragBaseBinding)
        setAnkiBaseNavCon(navController)
        ankiSettingPopUpViewModel.afterBindingSet(lifecycleOwner,getSharedPref)
        mainViewModel.setChildFragmentStatus(MainFragment.Anki)
        changeBnvBtnAddStatus(false)

    }


    private fun setCL(){
        ankiSettingPopUpViewModel.setListener()
        ankiFragBaseBinding.vAnkiFragConCover.setOnClickListener { setSettingVisible(false) }
    }

    private val _activeFragment = MutableLiveData<AnkiFragments>()
    fun setActiveFragment (ankiFragments: AnkiFragments){
        _activeFragment.value = ankiFragments
    }
    fun returnActiveFragment(): AnkiFragments {
        return _activeFragment.value ?: AnkiFragments.AnkiBox
    }
    private lateinit var _ankiBaseNavCon :NavController
    private fun setAnkiBaseNavCon(navController: NavController){
        _ankiBaseNavCon = navController
    }
    val getAnkiBaseNavCon get() = _ankiBaseNavCon
    private val _settingVisible = MutableLiveData<Boolean>()
    fun setSettingVisible(boolean: Boolean){
        _settingVisible.value = boolean
        doAfterSetSettingVisible()
    }
    private fun doAfterSetSettingVisible(){
        arrayOf(ankiFragBaseBinding.flPwAnkiSetting,ankiFragBaseBinding.vAnkiFragConCover).onEach {
            it.visibility = if(getSettingVisible) View.VISIBLE else View.GONE
        }
        if(getSettingVisible.not()) hideKeyBoard(ankiFragBaseBinding.bindingPwAnkiSetting.edtAutoFlipSeconds)
    }
    private val getSettingVisible get() = _settingVisible.value!!

    fun doOnBackPress(): Boolean {
        val isActive = mainViewModel.getFragmentStatus.now == MainFragment.Anki
        if(!isActive) return false
        val isStartFragment = returnActiveFragment()==AnkiFragments.AnkiBox
        if(isStartFragment
            &&!getSettingVisible) return false
        if(getSettingVisible) {
            setSettingVisible(false)
            return true
        }
        if(ankiFlipBaseViewModel.onBackPressed()) return true
        if(!isStartFragment) getAnkiBaseNavCon.popBackStack()
        return true
    }
    fun navigateInAnkiFragments(ankiFragments: AnkiFragments){
        getAnkiBaseNavCon.navigate(
            when(ankiFragments){
                AnkiFragments.Flip ->AnkiFlipBaseFragDirections.toFlipFrag()
                AnkiFragments.AnkiBox -> AnkiBoxFragDirections.toAnkiBoxFrag()
                AnkiFragments.FlipCompleted -> AnkiFlipCompleteFragDirections.toFlipCompletedFrag()
                AnkiFragments.FlipItems -> AnkiBoxContentFragDirections.toFlipItemRvFrag()
            }
        )
    }




}