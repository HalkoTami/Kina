package com.koronnu.kina.tabLibrary.inBox

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.CreationExtras
import com.koronnu.kina.R
import com.koronnu.kina.application.RoomApplication
import com.koronnu.kina.db.MyRoomRepository
import com.koronnu.kina.db.dataclass.Card
import com.koronnu.kina.tabLibrary.LibraryBaseViewModel
import com.koronnu.kina.ui.viewmodel.PopUpJumpToGuideViewModel

class LibraryInBoxFragViewModel(repository: MyRoomRepository):ViewModel() {
    private lateinit var sharedPrefCheckForFirstTime: SharedPreferences
    private lateinit var sharedPrefKeyInboxWithContentFirstTimeOpened:String
    private lateinit var libraryBaseViewModel: LibraryBaseViewModel
    private lateinit var popUpJumpToGuideViewModel: PopUpJumpToGuideViewModel


    companion object {
        fun getFactory(libraryBaseViewModel: LibraryBaseViewModel,
                       popUpJumpToGuideViewModel: PopUpJumpToGuideViewModel,
                       context: Context): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]) as RoomApplication
                val repository = application.repository
                val viewModel = LibraryInBoxFragViewModel(repository)
                viewModel.libraryBaseViewModel = libraryBaseViewModel
                viewModel.popUpJumpToGuideViewModel = popUpJumpToGuideViewModel
                viewModel.setLateInitVarsAfterGetContext(context)

                return viewModel as T
            }

        }
    }
    private val inBoxItem:LiveData<List<Card>> = repository.getCardDataByFileId(null).asLiveData()

    private val inBoxItemObserver = Observer<List<Card>>{
        libraryBaseViewModel.setParentRVItems(it)
//        if(!jumpToGuideHowToMoveItemsPopUpShouldShown(it)) return@Observer
//        popUpJumpToGuideViewModel.apply {
//            setPopUpTextId(R.string.guide_option_how_to_move_items)
//            setPopUpVisible(true)
//        }
//        saveInBoxWIthContentFirstTimeOpened()
    }
    fun observeLiveDataInInboxFrag(lifecycleOwner: LifecycleOwner,context: Context){
        setLateInitVarsAfterGetContext(context)
        inBoxItem.observe(lifecycleOwner,inBoxItemObserver)
    }
    private fun jumpToGuideHowToMoveItemsPopUpShouldShown(list: List<Card>):Boolean{
        val inBoxContentNotEmpty = list.isNotEmpty()
        if(!inBoxContentNotEmpty) return false
        if(!getInBoxWIthContentFirstTimeOpened()) return false

        return true
    }
    private fun saveInBoxWIthContentFirstTimeOpened(){
        sharedPrefCheckForFirstTime.edit() {
            putBoolean(sharedPrefKeyInboxWithContentFirstTimeOpened,false)
            commit()
        }
    }
    private fun setLateInitVarsAfterGetContext(context: Context){
        val spTitleString = context.getString(R.string.sp_checkFirstTimeOpened)
        sharedPrefCheckForFirstTime = context.getSharedPreferences(spTitleString, Context.MODE_PRIVATE)
        sharedPrefKeyInboxWithContentFirstTimeOpened = context.getString(R.string.sp_checkFirstTimeOpened_inBoxWithContentFirstTimeOpened)
    }

    private fun getInBoxWIthContentFirstTimeOpened():Boolean{
        return  sharedPrefCheckForFirstTime.getBoolean(sharedPrefKeyInboxWithContentFirstTimeOpened, true)
    }

    fun doOnDestroy(){
        popUpJumpToGuideViewModel.setPopUpVisible(false)
    }

}