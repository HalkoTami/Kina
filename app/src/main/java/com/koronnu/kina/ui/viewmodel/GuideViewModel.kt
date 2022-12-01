package com.koronnu.kina.ui.viewmodel

import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.*
import com.koronnu.kina.actions.GuideActions
import com.koronnu.kina.actions.changeViewVisibility
import com.koronnu.kina.actions.setClickListeners
import com.koronnu.kina.activity.MainActivity
import com.koronnu.kina.customClasses.enumClasses.Guides
import com.koronnu.kina.databinding.CallOnInstallBinding
import com.koronnu.kina.ui.listener.GuideBindingCL

class GuideViewModel : ViewModel(){
    companion object{
        fun getViewModelFactory(mainVM: MainViewModel): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val viewModel = GuideViewModel()
                viewModel.mainViewModel = mainVM
                viewModel.layoutInflater = mainVM.layoutInflater
                return viewModel as T
            }

        }
    }

    val libraryBaseViewModel get() = mainViewModel.libraryBaseViewModel
    lateinit var mainViewModel: MainViewModel
    lateinit var layoutInflater: LayoutInflater



    private val _guideVisibility = MutableLiveData<Boolean>().apply {
        value = false
    }
    fun setGuideVisibility(visible:Boolean){
        _guideVisibility.value = visible
    }
    val getGuideVisibility get() = _guideVisibility.value!!
    private val guideVisibility :LiveData<Boolean> = _guideVisibility

    private val _popUpConfirmEndGuideVisibility = MutableLiveData<Boolean>().apply {
        value = false
    }
    private fun setPopUpConfirmEndGuideVisibility(visible:Boolean){
        _popUpConfirmEndGuideVisibility.value = visible
    }
    private val getPopUpConfirmEndGuideVisibility get() =_popUpConfirmEndGuideVisibility.value!!
    private val popUpConfirmEndGuideVisibility:LiveData<Boolean> = _popUpConfirmEndGuideVisibility
    private val popUpConfirmEndGuideVisibilityObserver = Observer<Boolean>{
//        if(!getGuideVisibility) setGuideVisibility(true)
        changeViewVisibility(getGuideBinding.frameLayConfirmEndGuidePopUp,it)
    }

    private val _guideBinding =MutableLiveData<CallOnInstallBinding>()
    private fun setGuideBinding(guideBinding:CallOnInstallBinding){
        _guideBinding.value = guideBinding
    }
    private val guideBinding:LiveData<CallOnInstallBinding> = _guideBinding


    val getGuideBinding get() = _guideBinding.value ?: CallOnInstallBinding.inflate(layoutInflater)
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
        frameLay.addView(newBinding.root)
    }
    fun observeGuideViewModelLiveData(lifecycleOwner: LifecycleOwner){
        guideBinding.observe(lifecycleOwner,guideBindingObserver)
        popUpConfirmEndGuideVisibility.observe(lifecycleOwner,popUpConfirmEndGuideVisibilityObserver)
        guideVisibility.observe(lifecycleOwner,guideVisibilityObserver)
    }

    fun startGuide(guides: Guides,guideActions: GuideActions){
        var remove:()->Unit ={}
        val observer = object:Observer<CallOnInstallBinding> {
            override fun onChanged(t: CallOnInstallBinding?) {
                when(guides){
                    Guides.MoveItems -> guideActions.moveGuide()
                    Guides.EditItems -> guideActions.editGuide()
                    Guides.CreateItems -> guideActions.createGuide()
                    Guides.DeleteItems -> guideActions.deleteGuide()
                }
                remove = {guideBinding.removeObserver(this)}

            }
        }
        guideBinding.observeForever(observer)
        setGuideVisibility(true)
        remove()


    }
    var actionsBeforeEndGuideList: MutableList<()->Unit> = mutableListOf()
    fun doOnBackPress():Boolean{
        if(!getGuideVisibility) return false
        setPopUpConfirmEndGuideVisibility(getPopUpConfirmEndGuideVisibility.not())
        return true
    }

    fun onClickBtnCloseConfirmEnd() {
        setPopUpConfirmEndGuideVisibility(false)
    }

    fun onClickBtnCancelEnd() {
        setPopUpConfirmEndGuideVisibility(false)
    }

    fun onClickBtnCommitEnd() {
        actionsBeforeEndGuideList.onEach {
            it()
        }
        actionsBeforeEndGuideList = mutableListOf()
        setGuideVisibility(false)
    }


}