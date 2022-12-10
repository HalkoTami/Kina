package com.koronnu.kina.ui.viewmodel

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.*
import com.koronnu.kina.R
import com.koronnu.kina.actions.GuideActions
import com.koronnu.kina.actions.changeViewVisibility
import com.koronnu.kina.actions.setClickListeners
import com.koronnu.kina.customClasses.enumClasses.Guides
import com.koronnu.kina.databinding.CallOnInstallBinding
import com.koronnu.kina.ui.listener.GuideBindingCL

class GuideViewModel : ViewModel(){
    companion object{
        fun getViewModelFactory(mainVM: MainViewModel,resources: Resources): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val viewModel = GuideViewModel()
                viewModel.mainViewModel = mainVM
                viewModel.layoutInflater = mainVM.layoutInflater
                viewModel.resources = resources
                viewModel.setPopUpContentEndGuide()
                return viewModel as T
            }

        }
    }

    val libraryBaseViewModel get() = mainViewModel.libraryBaseViewModel
    lateinit var mainViewModel: MainViewModel
    lateinit var layoutInflater: LayoutInflater
    lateinit var resources: Resources




    private val _guideVisibility = MutableLiveData<Boolean>().apply {
        value = false
    }
    private fun setGuideVisibility(visible:Boolean){
        _guideVisibility.value = visible
    }
    private val getGuideVisibility get() = _guideVisibility.value!!
    private val guideVisibility :LiveData<Boolean> = _guideVisibility

    private val _popUpConfirmEndGuideVisibility = MutableLiveData<Boolean>().apply {
        value = false
    }
    fun setPopUpConfirmEndGuideVisibility(visible:Boolean){
        _popUpConfirmEndGuideVisibility.value = visible
    }
    private val getPopUpConfirmEndGuideVisibility get() =_popUpConfirmEndGuideVisibility.value!!
    private val popUpConfirmEndGuideVisibility:LiveData<Boolean> = _popUpConfirmEndGuideVisibility
    private val popUpConfirmEndGuideVisibilityObserver = Observer<Boolean>{
        getGuideBinding.confirmEndGuideBinding.apply {
            txvConfirm.text = resources.getString(popUpGuideContent.popUpTextId)
            btnCancelEnd.text = resources.getString(popUpGuideContent.popUpLeftBtnTextId)
            btnCommit.text = resources.getString(popUpGuideContent.popUpRightBtnTextId)
        }
        changeViewVisibility(getGuideBinding.frameLayConfirmEndGuidePopUp,it)
    }
    class GuidePopUpContent(
        val popUpTextId:Int,
        val popUpLeftBtnTextId:Int,
        val popUpRightBtnTextId:Int,
        val onClickLeftBtn:()->Unit,
        val onClickRightBtn: () -> Unit
    )
    private var _popUpContent:GuidePopUpContent? = null
    private fun setPopUpContent(popUpContent: GuidePopUpContent){
        _popUpContent = popUpContent
    }
    private val popUpGuideContent get () = _popUpContent!!
    fun setPopUpContentEndGuide(){
        val popUpContent = GuidePopUpContent(
             popUpTextId =  R.string.guide_popUp_confirm_end,
             popUpLeftBtnTextId = R.string.cancel,
            popUpRightBtnTextId = R.string.commit_end,
            onClickLeftBtn = {setPopUpConfirmEndGuideVisibility(false) },
            onClickRightBtn = {endGuide()}
        )
        setPopUpContent(popUpContent)
    }
    fun setPopUpContentCreateMovableFile(goNext:()->Unit){
        val popUpContent = GuidePopUpContent(
            popUpTextId =  R.string.guide_popUp_confirm_create_movable_File,
            popUpLeftBtnTextId = R.string.commit_end,
            popUpRightBtnTextId =R.string.commit_create,
            onClickLeftBtn = { endGuide()
                             setPopUpContentEndGuide()},
            onClickRightBtn = {
                setPopUpConfirmEndGuideVisibility(false)
                goNext()}
        )
        setPopUpContent(popUpContent)
    }
    fun endGuide(){
        actionsBeforeEndGuideList.onEach {
            it()
        }
        actionsBeforeEndGuideList = mutableListOf()
        setPopUpConfirmEndGuideVisibility(false)
        setGuideVisibility(false)
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
            confirmEndBinding.btnCommit)
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
        setGuideVisibility(true)
        guideActions.callOnFirst(guides)

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
        popUpGuideContent.onClickLeftBtn()
    }

    fun onClickBtnCommitEnd() {
        popUpGuideContent.onClickRightBtn()
    }


}