package com.koronnu.kina.ui

import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.*
import androidx.navigation.NavController
import com.koronnu.kina.data.model.enumClasses.AnkiFragments
import com.koronnu.kina.data.model.enumClasses.MainFragment
import com.koronnu.kina.databinding.ActivityMainBinding
import com.koronnu.kina.ui.editCard.CreateCardViewModel
import com.koronnu.kina.ui.tabLibrary.LibraryBaseFragDirections
import com.koronnu.kina.ui.tabLibrary.LibraryBaseViewModel
import com.koronnu.kina.ui.tabAnki.AnkiBaseFragDirections
import com.koronnu.kina.ui.editCard.EditCardBaseFragDirections
import com.koronnu.kina.util.KeyboardListener
import com.koronnu.kina.ui.tabAnki.AnkiBaseViewModel
import com.koronnu.kina.ui.tabLibrary.DeletePopUpViewModel
import kotlinx.coroutines.*


class MainViewModel(val layoutInflater: LayoutInflater):ViewModel(){
    companion object {
        fun getViewModel(mainActivity: MainActivity): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val mainModel = MainViewModel(mainActivity.layoutInflater)
                val editFileViewModel = getViewModelProviderWithFactory(EditFileViewModel.getFactory(
                    mainModel))[EditFileViewModel::class.java]
                val deletePopUpViewModel = getViewModelProviderWithFactory(DeletePopUpViewModel.Factory)[DeletePopUpViewModel::class.java]
                val ankiBaseViewModel = getViewModelProviderWithFactory(AnkiBaseViewModel.getFactory(mainModel,mainActivity))[AnkiBaseViewModel::class.java]
                val createCardViewModel = getViewModelProviderWithFactory(CreateCardViewModel.getFactory(mainModel))[CreateCardViewModel::class.java]
                mainModel.editFileViewModel = editFileViewModel
                mainModel.deletePopUpViewModel = deletePopUpViewModel
                mainModel.ankiBaseViewModel = ankiBaseViewModel
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
//    vars init onCreate
    lateinit var libraryBaseViewModel: LibraryBaseViewModel
    lateinit var editFileViewModel: EditFileViewModel
    lateinit var deletePopUpViewModel: DeletePopUpViewModel
    lateinit var ankiBaseViewModel: AnkiBaseViewModel
    lateinit var createCardViewModel: CreateCardViewModel




    fun observeLiveDataFromMainActivity(lifecycleOwner: LifecycleOwner,activity: MainActivity){
        editFileViewModel.observeLiveData(lifecycleOwner)
    }
    private var _mainActivityBinding:ActivityMainBinding? = null
    private val bnvBinding      get() =  mainActivityBinding.bindingTwgActivityMain


    fun setMainActivityBinding(mainActivityBinding: ActivityMainBinding){
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
        val topConLay = mainActivityBinding.root as ConstraintLayout
        val keyboardListener = object : KeyboardListener(topConLay){}.apply{
            onKeyBoardAppear = { setBnvVisibility(false) }
            onKeyBoardDisappear = {setBnvVisibility(checkBnvVisible())}
        }
        topConLay.viewTreeObserver.addOnGlobalLayoutListener(keyboardListener)
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

    val mainActivityChildFragment = MutableLiveData<MainFragment>()
    val _childFragmentsStatus = MutableLiveData<MainActivityChildFragmentStatus>()
    fun setChildFragmentStatus(fragment: MainFragment){
        val previous = getFragmentStatus.now
        val newStatus = MainActivityChildFragmentStatus(fragment,previous)
        _childFragmentsStatus.value = newStatus
        mainActivityChildFragment.value = newStatus.now
    }
    val getFragmentStatus get() = _childFragmentsStatus?.value?: MainActivityChildFragmentStatus(now = MainFragment.Library,null)
    fun navigateInMainActivityFragCon(to: MainFragment){
        if(to == getFragmentStatus.now) return
        getMainActivityNavCon.navigate(
            when(to){
                MainFragment.Anki -> AnkiBaseFragDirections.toAnki()
                MainFragment.EditCard -> EditCardBaseFragDirections.openCreateCard()
                MainFragment.Library  -> LibraryBaseFragDirections.toLibrary()
            }
        )
    }

    val _bnvVisibility = MutableLiveData<Boolean>()
    val bnvVisibility:LiveData<Boolean> = _bnvVisibility
    fun setBnvVisibility(boolean: Boolean){
        _bnvVisibility.value = boolean
    }


    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }

    fun doOnBackPress() {
        if( createCardViewModel.doOnBackPress()
            ||editFileViewModel.doOnBackPress()
            ||deletePopUpViewModel.doOnBackPress()
            ||libraryBaseViewModel.doOnBackPress()
            ||ankiBaseViewModel.doOnBackPress()) return
        getMainActivityNavCon.popBackStack()

    }


}

