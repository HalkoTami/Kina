package com.koronnu.kina.ui.viewmodel

import android.content.SharedPreferences
import android.view.View
import androidx.lifecycle.*
import androidx.navigation.NavController
import com.koronnu.kina.actions.hideKeyBoard
import com.koronnu.kina.customClasses.enumClasses.AnkiFragments
import com.koronnu.kina.customClasses.enumClasses.MainFragment
import com.koronnu.kina.databinding.AnkiFragBaseBinding
import com.koronnu.kina.ui.fragment.anki_frag_con.AnkiBoxContentFragDirections
import com.koronnu.kina.ui.fragment.anki_frag_con.AnkiBoxFragDirections
import com.koronnu.kina.ui.fragment.anki_frag_con.AnkiFlipBaseFragDirections
import com.koronnu.kina.ui.fragment.anki_frag_con.AnkiFlipCompleteFragDirections

class AnkiBaseViewModel(val mainViewModel: MainViewModel ) : ViewModel() {

    private lateinit var _ankiFlipBaseViewModel: AnkiFlipBaseViewModel
    val ankiFlipBaseViewModel get() = _ankiFlipBaseViewModel
    private lateinit var ankiSettingPopUpViewModel: AnkiSettingPopUpViewModel
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
                    ViewModelProvider(viewModelStoreOwner,AnkiBoxViewModel.Factory)[AnkiBoxViewModel::class.java]
                ankiBaseViewModel._ankiBoxViewModel = ankiBoxViewModel
                ankiBaseViewModel.ankiSettingPopUpViewModel = ankiSettingPopUpViewModel
                val ankiFlipBaseViewModel =
                    ViewModelProvider(viewModelStoreOwner,AnkiFlipBaseViewModel.Factory)[AnkiFlipBaseViewModel::class.java]
                ankiBaseViewModel._ankiFlipBaseViewModel = ankiFlipBaseViewModel
                return ankiBaseViewModel as T
            }
        }
    }
    fun changeBnvBtnAddStatus(able:Boolean){
        mainViewModel.mainActivityBinding.bindingWidgetTwgActivityMain.bnvImvAdd.apply {
            visibility = if(able)View.VISIBLE else View.INVISIBLE
            isEnabled = able
        }
    }

    private var _ankiFragBaseBinding:AnkiFragBaseBinding? = null
    val ankiFragBaseBinding get() = _ankiFragBaseBinding!!
    private fun setAnkiFragBaseBinding(ankiFragBaseBinding: AnkiFragBaseBinding){
        _ankiFragBaseBinding = ankiFragBaseBinding
        setCL()
    }

    fun onFragmentCreated(ankiFragBaseBinding: AnkiFragBaseBinding,
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
        ankiFragBaseBinding.viewAnkiFragConCover.setOnClickListener { setSettingVisible(false) }
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
        arrayOf(ankiFragBaseBinding.frameLayAnkiSetting,ankiFragBaseBinding.viewAnkiFragConCover).onEach {
            it.visibility = if(getSettingVisible) View.VISIBLE else View.GONE
        }
        if(getSettingVisible.not()) hideKeyBoard(ankiFragBaseBinding.bindingSetting.bindingSettingContent.edtAutoFlipSeconds)
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