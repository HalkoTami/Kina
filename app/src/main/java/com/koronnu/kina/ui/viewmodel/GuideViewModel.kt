package com.koronnu.kina.ui.viewmodel

import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.koronnu.kina.actions.changeViewVisibility
import com.koronnu.kina.actions.setClickListeners
import com.koronnu.kina.databinding.CallOnInstallBinding
import com.koronnu.kina.ui.listener.GuideBindingCL

class GuideViewModel : ViewModel(){
    lateinit var libraryBaseViewModel: LibraryBaseViewModel
    lateinit var mainViewModel: MainViewModel
    val layoutInflater: LayoutInflater get() = mainViewModel.layoutInflater

    fun setLateInitVars(mainVM: MainViewModel){
        mainViewModel = mainVM
        libraryBaseViewModel = mainVM.libraryBaseViewModel
    }

    private val _guideVisibility = MutableLiveData<Boolean>()
    private fun setGuideVisibility(visible:Boolean){
        _guideVisibility.value = visible
    }
    private val guideVisibility :LiveData<Boolean> = _guideVisibility

    private val _popUpConfirmEndGuideVisibility = MutableLiveData<Boolean>()
    private fun setPopUpConfirmEndGuideVisibility(visible:Boolean){
        _popUpConfirmEndGuideVisibility.value = visible
    }
    private val popUpConfirmEndGuideVisibility:LiveData<Boolean> = _popUpConfirmEndGuideVisibility
    private val popUpConfirmEndGuideVisibilityObserver = Observer<Boolean>{
        changeViewVisibility(getGuideBinding.frameLayConfirmEndGuidePopUp,it)
    }

    private val _guideBinding =MutableLiveData<CallOnInstallBinding>()
    private fun setGuideBinding(guideBinding:CallOnInstallBinding){
        _guideBinding.value = guideBinding
    }
    private val guideBinding:LiveData<CallOnInstallBinding> = _guideBinding


    val getGuideBinding = _guideBinding.value!!
    private val guideBindingObserver = Observer<CallOnInstallBinding>{
        setGuideBindingCL()
    }
    private val guideBindingClickableViews:Array<View> get() {
        val confirmEndBinding = getGuideBinding.confirmEndGuideBinding
        return arrayOf(confirmEndBinding.btnCloseConfirmEnd,
            confirmEndBinding.btnCancelEnd,
            confirmEndBinding.btnCommitEnd)
    }
    private fun setGuideBindingCL(){
        setClickListeners(guideBindingClickableViews, GuideBindingCL(this))
    }

    private val guideVisibilityObserver = Observer<Boolean>{
        val frameLay = mainViewModel.mainActivityBinding.frameLayCallOnInstall
        frameLay.removeAllViews()
        if(!it) return@Observer
        val newBinding = CallOnInstallBinding.inflate(mainViewModel.layoutInflater)
        setGuideBinding(newBinding)
        frameLay.addView(getGuideBinding.root)
    }
    fun observeGuideViewModelLiveData(lifecycleOwner: LifecycleOwner){
        guideVisibility.observe(lifecycleOwner,guideVisibilityObserver)
        guideBinding.observe(lifecycleOwner,guideBindingObserver)
        popUpConfirmEndGuideVisibility.observe(lifecycleOwner,popUpConfirmEndGuideVisibilityObserver)
    }

    fun startCreateGuide(){

    }

    fun onClickBtnCloseConfirmEnd() {
        setPopUpConfirmEndGuideVisibility(false)
    }

    fun onClickBtnCancelEnd() {
        setPopUpConfirmEndGuideVisibility(false)
    }

    fun onClickBtnCommitEnd() {
        setGuideVisibility(false)
    }


}