package com.koronnu.kina.ui.viewmodel

import android.content.Context
import android.view.LayoutInflater
import androidx.lifecycle.*
import androidx.navigation.NavController
import com.koronnu.kina.actions.GuideActions
import com.koronnu.kina.activity.MainActivity
import com.koronnu.kina.ui.fragment.base_frag_con.AnkiBaseFragDirections
import com.koronnu.kina.ui.fragment.base_frag_con.LibraryBaseFragDirections
import com.koronnu.kina.customClasses.enumClasses.MainFragment
import com.koronnu.kina.databinding.MainActivityBinding
import com.koronnu.kina.ui.listener.GuideOptionBindingCL
import kotlinx.coroutines.*



class MainViewModel(val layoutInflater: LayoutInflater):ViewModel(){
    lateinit var guideViewModel: GuideViewModel
    lateinit var libraryBaseViewModel: LibraryBaseViewModel
    lateinit var guideOptionMenuViewModel: GuideOptionMenuViewModel
    lateinit var editFileViewModel: EditFileViewModel
    lateinit var deletePopUpViewModel: DeletePopUpViewModel
    lateinit var ankiBaseViewModel: AnkiBaseViewModel
    lateinit var popUpJumpToGuideViewModel: PopUpJumpToGuideViewModel
    companion object {
        fun getViewModel(mainActivity: MainActivity): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val mainModel = MainViewModel(mainActivity.layoutInflater)
                val guideViewModel = getViewModelProviderWithFactory(GuideViewModel.getViewModelFactory(mainModel,mainActivity.resources))[GuideViewModel::class.java]
                val guideOptionMenuViewModel = getViewModelProviderWithFactory(GuideOptionMenuViewModel.getViewModelFactory(mainModel))[GuideOptionMenuViewModel::class.java]
                val editFileViewModel = getViewModelProviderWithFactory(EditFileViewModel.Factory)[EditFileViewModel::class.java]
                val deletePopUpViewModel = getViewModelProviderWithFactory(DeletePopUpViewModel.Factory)[DeletePopUpViewModel::class.java]
                val ankiBaseViewModel = getViewModelProviderWithFactory(AnkiBaseViewModel.getFactory(mainModel))[AnkiBaseViewModel::class.java]
                val popUpJumpToGuideViewModel = getViewModelProviderWithFactory(PopUpJumpToGuideViewModel.getViewModelFactory(mainModel))[PopUpJumpToGuideViewModel::class.java]
                mainModel.guideViewModel = guideViewModel
                mainModel.guideOptionMenuViewModel = guideOptionMenuViewModel
                mainModel.editFileViewModel = editFileViewModel
                mainModel.deletePopUpViewModel = deletePopUpViewModel
                mainModel.ankiBaseViewModel = ankiBaseViewModel
                mainModel.popUpJumpToGuideViewModel = popUpJumpToGuideViewModel
                val libraryBaseViewModel = getViewModelProviderWithFactory(LibraryBaseViewModel.getFactory(mainModel,mainActivity,mainActivity.baseContext))[LibraryBaseViewModel::class.java]
                mainModel.libraryBaseViewModel = libraryBaseViewModel
                return mainModel as T
            }
            fun getViewModelProviderWithFactory(factory: ViewModelProvider.Factory):ViewModelProvider{
                return ViewModelProvider(mainActivity,factory)
            }

        }


    }


    fun observeLiveDataFromMainActivity(lifecycleOwner: LifecycleOwner,activity: MainActivity){
        guideOptionMenuViewModel.observeLiveDataInGuideOptionViewModel(lifecycleOwner,activity)
        guideViewModel.observeGuideViewModelLiveData(lifecycleOwner)
    }
    private var _mainActivityBinding:MainActivityBinding? = null
    fun setMainActivityBinding(mainActivityBinding: MainActivityBinding){
       _mainActivityBinding =  mainActivityBinding
    }
    val mainActivityBinding get() = _mainActivityBinding!!


    class MainActivityChildFragmentStatus(
        var now: MainFragment,
        var before: MainFragment?
    )
    private val _mainActivityNavCon = MutableLiveData<NavController>()
    fun setMainActivityNavCon(navController: NavController){
        _mainActivityNavCon.value = navController
    }
    fun returnMainActivityNavCon(): NavController?{
        return _mainActivityNavCon.value
    }

    private val _childFragmentsStatus = MutableLiveData<MainActivityChildFragmentStatus>()
    val childFragmentStatus:LiveData<MainActivityChildFragmentStatus> = _childFragmentsStatus
    fun setChildFragmentStatus(fragment: MainFragment){
        val previous = _childFragmentsStatus.value?.now
        if (fragment == previous) return else {
            val newStatus = MainActivityChildFragmentStatus(fragment,previous)
            _childFragmentsStatus.value = newStatus
        }
    }
    private fun returnActiveFragment(): MainFragment?{
        return _childFragmentsStatus.value?.now
    }
    fun returnFragmentStatus():MainActivityChildFragmentStatus?{
        return _childFragmentsStatus.value
    }
    fun changeFragment(to: MainFragment){
        val navCOn = returnMainActivityNavCon()
        if(returnActiveFragment() == to||
                navCOn==null) return
        else{
            val action =
            when(to){
                MainFragment.Library -> LibraryBaseFragDirections.toLibrary()
                MainFragment.EditCard ->return
                MainFragment.Anki -> AnkiBaseFragDirections.toAnki()
            }
            navCOn.navigate(action)
        }
    }
    private val _bnvVisibility = MutableLiveData<Boolean>()
    val bnvVisibility:LiveData<Boolean> = _bnvVisibility
    fun setBnvVisibility(boolean: Boolean){
        _bnvVisibility.value = boolean
    }
    fun returnBnvVisibility():Boolean?{
        return _bnvVisibility.value
    }
    private val _helpOptionVisibility = MutableLiveData<Boolean>()
    val helpOptionVisibility:LiveData<Boolean> = _helpOptionVisibility
    fun setHelpOptionVisibility(boolean: Boolean){
        _helpOptionVisibility.value = boolean
    }
    fun returnHelpOptionVisibility():Boolean{
        return _helpOptionVisibility.value ?:false
    }
    private val _guideVisibility = MutableLiveData<Boolean>()
    val guideVisibility:LiveData<Boolean> = _guideVisibility
    fun setGuideVisibility(boolean: Boolean){
        _guideVisibility.value = boolean
    }
    fun returnGuideVisibility():Boolean{
        return _guideVisibility.value ?:false
    }
    fun checkIfFrameLayHelpIsVisible():Boolean{
        return returnGuideVisibility()||returnHelpOptionVisibility()
    }
    private val _bnvCoverVisible = MutableLiveData<Boolean>()
    val bnvCoverVisible:LiveData<Boolean> = _bnvCoverVisible

    fun setBnvCoverVisible (boolean: Boolean){
        _bnvCoverVisible.value = boolean
    }
    private val _confirmEndGuidePopUpVisible = MutableLiveData<Boolean>()
    val confirmEndGuidePopUpVisible:LiveData<Boolean> = _confirmEndGuidePopUpVisible

    fun setConfirmEndGuidePopUpVisible (boolean: Boolean){
        _confirmEndGuidePopUpVisible.value = boolean
    }
    fun returnConfirmEndGuidePopUpVisible ():Boolean{
        return _confirmEndGuidePopUpVisible.value ?:false
    }
    fun onClickEndGuide(activity:MainActivity){
        setConfirmEndGuidePopUpVisible(false)
        setGuideVisibility(false)
        setHelpOptionVisibility(false)
        val sharedPref =  activity.getSharedPreferences(
            "firstTimeGuide", Context.MODE_PRIVATE) ?: return
        val editor = sharedPref.edit()
        editor.putBoolean("firstTimeGuide", true)
        editor.apply()
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }

    fun doOnBackPress():Boolean {
        if(!guideViewModel.doOnBackPress()
            &&!guideOptionMenuViewModel.doOnBackPress()
            &&!editFileViewModel.doOnBackPress()
            &&!deletePopUpViewModel.doOnBackPress()
            &&!libraryBaseViewModel.doOnBackPress()
            &&!ankiBaseViewModel.doOnBackPress()) return false
        return true
    }


}

