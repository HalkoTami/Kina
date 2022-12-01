package com.koronnu.kina.ui.viewmodel

import android.view.LayoutInflater
import android.view.View
import androidx.core.view.size
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.koronnu.kina.actions.*
import com.koronnu.kina.customClasses.enumClasses.Guides
import com.koronnu.kina.customClasses.enumClasses.MainFragment
import com.koronnu.kina.databinding.HelpOptionsBinding
import com.koronnu.kina.ui.listener.GuideOptionBindingCL

class GuideOptionMenuViewModel:ViewModel(){
    private lateinit var mainViewModel: MainViewModel
    private lateinit var libraryBaseViewModel: LibraryBaseViewModel
    private lateinit var guideViewModel: GuideViewModel
    fun setLateInitVars(mainVM:MainViewModel){
        mainViewModel = mainVM
        libraryBaseViewModel = mainVM.libraryBaseViewModel
    }
    private val _guideOptionMenuVisible = MutableLiveData<Boolean>()
    private fun setGuideOptionMenuVisible(visible:Boolean){
        _guideOptionMenuVisible.value = visible
    }
    private val guideOptionMenuVisible :LiveData<Boolean> = _guideOptionMenuVisible
    private val guideMenuVisibilityObserver = Observer<Boolean>{
        val frameLay = mainViewModel.mainActivityBinding.frameLayCallOnInstall
        frameLay.removeAllViews()
        changeViewVisibility(frameLay,it)
        if(it){
            val newBinding = HelpOptionsBinding.inflate(mainViewModel.layoutInflater)
            setGuideOptionMenuBinding(newBinding)
            frameLay.addView(newBinding.root)
        }
    }
    fun observeLiveDataInGuideOptionViewModel(lifecycleOwner: LifecycleOwner){
        guideOptionMenuVisible.observe(lifecycleOwner,guideMenuVisibilityObserver)
        guideOptionMenuBinding.observe(lifecycleOwner,guideOptionMenuBindingObserver)
    }
    private val _guideOptionMenuBinding = MutableLiveData<HelpOptionsBinding>()
    private fun setGuideOptionMenuBinding(binding:HelpOptionsBinding){
        _guideOptionMenuBinding.value = binding
    }
    val getGuideOptionMenuBinding get() = _guideOptionMenuBinding.value!!
    private val guideOptionMenuBinding :LiveData<HelpOptionsBinding> = _guideOptionMenuBinding
    private val guideOptionMenuBindingObserver = Observer<HelpOptionsBinding> {
        addGuideOptionMenuBindingCL()
        filterEachMenuVisibility()
    }
    private val guideOptionMenuBindingClickableViews:Array<View> get() {
        val binding = getGuideOptionMenuBinding
        return arrayOf(
            binding.root,
            binding.menuHowToDeleteItems,
            binding.menuHowToCreateItems,
            binding.menuHowToEditItems,
            binding.menuHowToMoveItems
        )
    }
    private fun addGuideOptionMenuBindingCL(){
        setClickListeners(guideOptionMenuBindingClickableViews, GuideOptionBindingCL(this))
    }
    private fun filterEachMenuVisibility(){
        getGuideOptionMenuBinding.apply {
            changeViewVisibility(menuHowToCreateItems,isInLibraryFragment())
            arrayOf(menuHowToEditItems,
                menuHowToDeleteItems,
                menuHowToMoveItems,).onEach { changeViewVisibility(it,isLibraryFragmentWithRVItem()) }
        }
    }
    private fun isInLibraryFragment():Boolean{
        return  (mainViewModel.returnFragmentStatus()?.now == MainFragment.Library)
    }
    private fun isLibraryFragmentWithRVItem():Boolean{
        val hasItemInFirstRow = (libraryBaseViewModel.getChildFragBinding.vocabCardRV.size>0)
        return isInLibraryFragment()&&hasItemInFirstRow
    }
    fun onClickGuideMenu(guide:Guides){
        setGuideOptionMenuVisible(false)
        guideViewModel.startGuide(guide)

    }

    fun onclickBtnGuide() {
        setGuideOptionMenuVisible(true)
    }


}
