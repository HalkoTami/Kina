package com.koronnu.kina.ui.viewmodel

import android.view.LayoutInflater
import androidx.lifecycle.*
import androidx.navigation.NavController
import com.koronnu.kina.activity.MainActivity
import com.koronnu.kina.customClasses.enumClasses.MainFragment
import com.koronnu.kina.databinding.MainActivityBinding
import com.koronnu.kina.ui.fragment.base_frag_con.AnkiBaseFragDirections
import com.koronnu.kina.ui.fragment.base_frag_con.LibraryBaseFragDirections
import kotlinx.coroutines.*


class MainViewModel(val layoutInflater: LayoutInflater):ViewModel(){

//    vars init onCreate
    lateinit var guideViewModel: GuideViewModel
    lateinit var libraryBaseViewModel: LibraryBaseViewModel
    lateinit var guideOptionMenuViewModel: GuideOptionMenuViewModel
    lateinit var editFileViewModel: EditFileViewModel
    lateinit var deletePopUpViewModel: DeletePopUpViewModel
    lateinit var ankiBaseViewModel: AnkiBaseViewModel
    lateinit var popUpJumpToGuideViewModel: PopUpJumpToGuideViewModel
    lateinit var createCardViewModel: CreateCardViewModel
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
                val createCardViewModel = getViewModelProviderWithFactory(CreateCardViewModel.getFactory(mainModel))[CreateCardViewModel::class.java]
                mainModel.guideViewModel = guideViewModel
                mainModel.guideOptionMenuViewModel = guideOptionMenuViewModel
                mainModel.editFileViewModel = editFileViewModel
                mainModel.deletePopUpViewModel = deletePopUpViewModel
                mainModel.ankiBaseViewModel = ankiBaseViewModel
                mainModel.popUpJumpToGuideViewModel = popUpJumpToGuideViewModel
                mainModel.createCardViewModel   = createCardViewModel
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


    private val _bnvCoverVisible = MutableLiveData<Boolean>()
    val bnvCoverVisible:LiveData<Boolean> = _bnvCoverVisible

    fun setBnvCoverVisible (boolean: Boolean){
        _bnvCoverVisible.value = boolean
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }

    fun doOnBackPress() {
        if((!guideOptionMenuViewModel.doOnBackPress()
            &&!guideViewModel.doOnBackPress())
            ||!createCardViewModel.doOnBackPress()
            ||!editFileViewModel.doOnBackPress()
            ||!deletePopUpViewModel.doOnBackPress()
            ||!libraryBaseViewModel.doOnBackPress()
            ||!ankiBaseViewModel.doOnBackPress()) return
        returnMainActivityNavCon()?.popBackStack()

    }


}

