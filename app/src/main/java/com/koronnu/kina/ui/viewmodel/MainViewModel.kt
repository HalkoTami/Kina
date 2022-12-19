package com.koronnu.kina.ui.viewmodel

import android.view.LayoutInflater
import android.widget.ImageView
import androidx.lifecycle.*
import androidx.navigation.NavController
import com.koronnu.kina.activity.MainActivity
import com.koronnu.kina.customClasses.enumClasses.AnkiFragments
import com.koronnu.kina.customClasses.enumClasses.MainFragment
import com.koronnu.kina.databinding.MainActivityBinding
import com.koronnu.kina.tabLibrary.LibraryBaseFragDirections
import com.koronnu.kina.tabLibrary.LibraryBaseViewModel
import com.koronnu.kina.ui.fragment.base_frag_con.AnkiBaseFragDirections
import com.koronnu.kina.ui.fragment.base_frag_con.EditCardBaseFragDirections
import com.koronnu.kina.ui.listener.KeyboardListener
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
                val editFileViewModel = getViewModelProviderWithFactory(EditFileViewModel.getFactory(mainModel))[EditFileViewModel::class.java]
                val deletePopUpViewModel = getViewModelProviderWithFactory(DeletePopUpViewModel.Factory)[DeletePopUpViewModel::class.java]
                val ankiBaseViewModel = getViewModelProviderWithFactory(AnkiBaseViewModel.getFactory(mainModel,mainActivity))[AnkiBaseViewModel::class.java]
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
        editFileViewModel.observeLiveData(lifecycleOwner)
    }
    private var _mainActivityBinding:MainActivityBinding? = null
    private val bnvBinding      get() =  mainActivityBinding.bnvBinding


    fun setMainActivityBinding(mainActivityBinding: MainActivityBinding){
       _mainActivityBinding =  mainActivityBinding
        doAfterSetMainActivityBin()
    }
    private fun checkBnvVisible():Boolean{
        val isEditCardFragment = getFragmentStatus.now== MainFragment.EditCard
        val isFlipFragment = ankiBaseViewModel.returnActiveFragment()== AnkiFragments.Flip
        return !(isFlipFragment||isEditCardFragment)
    }
    private fun doAfterSetMainActivityBin(){
        setListeners()
    }
    private fun setListeners(){
        val topConLay = mainActivityBinding.mainTopConstrainLayout
        val keyboardListener = object : KeyboardListener(topConLay){}.apply{
            onKeyBoardAppear = { setBnvVisibility(false) }
            onKeyBoardDisappear = {setBnvVisibility(checkBnvVisible())}
        }
        topConLay.viewTreeObserver.addOnGlobalLayoutListener(keyboardListener)
        bnvBinding.bnvImvAdd        .setOnClickListener { editFileViewModel.setBottomMenuVisible(true)}
        bnvBinding.bnvTxvTabAnki    .setOnClickListener { navigateInMainActivityFragCon(MainFragment.Anki)}
        bnvBinding.bnvTxvTabLibrary .setOnClickListener { navigateInMainActivityFragCon(MainFragment.Library)}
    }
    val mainActivityBinding get() = _mainActivityBinding!!


    class MainActivityChildFragmentStatus(
        var now: MainFragment,
        var before: MainFragment?
    )
    private var _mainActivityNavCon:NavController? = null
    fun setMainActivityNavCon(navController: NavController){
        _mainActivityNavCon = navController
    }
    val getMainActivityNavCon get() = _mainActivityNavCon!!

    private var _childFragmentsStatus :MainActivityChildFragmentStatus? = null
    fun setChildFragmentStatus(fragment: MainFragment){
        val previous = getFragmentStatus.now
        val newStatus = MainActivityChildFragmentStatus(fragment,previous)
        _childFragmentsStatus = newStatus
        setUpBnv()
    }
    private fun setUpBnv(){
        if(getFragmentStatus.now==MainFragment.EditCard
            ||getFragmentStatus.before==MainFragment.EditCard) return
        fun getImv(mainFragment: MainFragment): ImageView{
            return when(mainFragment){
                MainFragment.Anki -> bnvBinding.bnvImvTabAnki
                MainFragment.Library -> bnvBinding.bnvImvTabLibrary
                else                -> throw IllegalArgumentException()
            }
        }
        getImv(getFragmentStatus.now).isSelected = true
        getImv(getFragmentStatus.before ?:MainFragment.Anki).isSelected = false
    }
    val getFragmentStatus get() = _childFragmentsStatus?:MainActivityChildFragmentStatus(now = MainFragment.Library,null)
    private fun navigateInMainActivityFragCon(to: MainFragment){
        if(to == getFragmentStatus.now) return
        getMainActivityNavCon.navigate(
            when(to){
                MainFragment.Anki -> AnkiBaseFragDirections.toAnki()
                MainFragment.EditCard -> EditCardBaseFragDirections.openCreateCard()
                MainFragment.Library  -> LibraryBaseFragDirections.toLibrary()
            }
        )
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
        if(guideOptionMenuViewModel.doOnBackPress()
            ||guideViewModel.doOnBackPress()
            ||createCardViewModel.doOnBackPress()
            ||editFileViewModel.doOnBackPress()
            ||deletePopUpViewModel.doOnBackPress()
            ||libraryBaseViewModel.doOnBackPress()
            ||ankiBaseViewModel.doOnBackPress()) return
        getMainActivityNavCon.popBackStack()

    }


}

