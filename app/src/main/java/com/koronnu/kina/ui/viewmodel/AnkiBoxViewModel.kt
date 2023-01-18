package com.koronnu.kina.ui.viewmodel

import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import com.koronnu.kina.application.RoomApplication
import com.koronnu.kina.db.MyRoomRepository
import com.koronnu.kina.db.dataclass.ActivityData
import com.koronnu.kina.db.dataclass.Card
import com.koronnu.kina.db.dataclass.File
import com.koronnu.kina.customClasses.enumClasses.AnkiBoxFragments
import com.koronnu.kina.customClasses.enumClasses.AnkiFragments
import com.koronnu.kina.customClasses.normalClasses.AnkiBoxTabData
import com.koronnu.kina.db.enumclass.FileStatus
import com.koronnu.kina.ui.tabAnki.ankiBox.favourites.BoxFavouriteFragDirections
import com.koronnu.kina.ui.tabAnki.ankiBox.allFlashCards.BoxFlashCardCoversFragDirections
import com.koronnu.kina.ui.tabAnki.ankiBox.library.BoxLibraryItemsFragDirections
import com.koronnu.kina.customClasses.normalClasses.MakeToastFromVM
import kotlinx.coroutines.launch

class AnkiBoxViewModel(val repository: MyRoomRepository) : ViewModel() {

    private lateinit var ankiBaseViewModel: AnkiBaseViewModel
    companion object{
        fun getFactory(ankiBaseViewModel:AnkiBaseViewModel): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]) as RoomApplication
                val repository = application.repository
                val boxViewModel = AnkiBoxViewModel(repository)
                boxViewModel.ankiBaseViewModel = ankiBaseViewModel
                return boxViewModel as T
            }
        }
    }
    fun onCreate(){
        setAnkiBoxItems(mutableListOf())
    }
    fun getAnkiBoxRVCards(fileId:Int): LiveData<List<Card>> = repository.getAnkiBoxRVCards(fileId).asLiveData()
    fun getAnkiBoxRVDescendantsFolders(fileId:Int): LiveData<List<File>> = repository.getAnkiBoxRVDescendantsFolders(fileId).asLiveData()
    fun getAnkiBoxRVDescendantsFlashCards(fileId:Int): LiveData<List<File>> = repository.getAnkiBoxRVDescendantsFlashCards(fileId).asLiveData()
    fun ankiBoxFileAncestorsFromDB(int: Int?):LiveData<List<File>> = repository.getAllAncestorsByFileId(int).asLiveData()
    val allFlashCardCoverFromDB: LiveData<List<File>> = repository.allFlashCardCoverContainsCard.asLiveData()
    val allFavouriteAnkiBoxFromDB: LiveData<List<File>> = repository.allFavouriteAnkiBox.asLiveData()
    val cardsExistsFromDB:LiveData<Boolean> = repository.cardExists.asLiveData()

    fun getLibraryFilesFromDB(parentFileId:Int?) :LiveData<List<File>> = repository.getLibraryItemsWithDescendantCards(parentFileId).asLiveData()
    fun getCardsFromDB(parentFileId:Int?) :LiveData<List<Card>> = repository.getCardDataByFileId(parentFileId).asLiveData()
    fun getCardsFromDBByMultipleCardIds(cardIds:List<Int>) :LiveData<List<Card>> = repository.getCardsByMultipleCardId(cardIds).asLiveData()
    fun updateCardFlagStatus(card:Card){
        val new = card
        new.flag = new.flag.not()
        viewModelScope.launch {
            repository.update(new)
        }

    }
    private val _ankiBoxNavCon = MutableLiveData<NavController>()
    fun setAnkiBoxNavCon(navController: NavController){
        _ankiBoxNavCon.value = navController
    }
    private fun returnAnkiBoxNavCon():NavController?{
        val navCon = _ankiBoxNavCon.value
        if(navCon==null) makeToastFromVM("ankiBox navCon not Set")
        return _ankiBoxNavCon.value
    }
    private fun navigateWithoutBackstack(direction:NavDirections){
        val navCon = returnAnkiBoxNavCon()
        navCon?.popBackStack()
        navCon?.navigate(direction)
    }
    private fun navigateWithBackstack(direction:NavDirections){
        val navCon = returnAnkiBoxNavCon()
        navCon?.navigate(direction)
    }

    private val _toast = MutableLiveData<MakeToastFromVM>()
    private fun makeToastFromVM(string: String){
        _toast.value = MakeToastFromVM(string,true)
        _toast.value = MakeToastFromVM(String(),false)
    }

    val toast :LiveData<MakeToastFromVM> = _toast

    fun getCardActivityFromDB(cardId: Int) :LiveData<List<ActivityData>> = repository.getCardActivity(cardId).asLiveData()
    fun getDescendantsCardIds(fileIdList:List<Int>) :LiveData<List<Int>> = repository.getDescendantsCardsIsByMultipleFileId(fileIdList).asLiveData()
    private val _ankiBoxItems = MutableLiveData<MutableList<Card>>().apply {
        this.value = mutableListOf()
    }
    fun setAnkiBoxItems(list: List<Card>){
        val a = mutableListOf<Card>()
        a.addAll(list)
        _ankiBoxItems.value = a
    }
    fun returnAnkiBoxItems():List<Card>{
        return  _ankiBoxItems.value ?: mutableListOf()
    }
    val ankiBoxItems :LiveData<MutableList<Card>> = _ankiBoxItems


    private val _currentChildFragment = MutableLiveData<AnkiBoxTabData>()
    fun setCurrentChildFragment (fragment: AnkiBoxFragments){
        val before = _currentChildFragment.value
        val data = AnkiBoxTabData(fragment,before?.currentTab)
        _currentChildFragment.value = data
    }
    private fun returnCurrentChildFragment (): AnkiBoxFragments?{
        return _currentChildFragment.value?.currentTab
    }
    val currentChildFragment :LiveData<AnkiBoxTabData> = _currentChildFragment

    fun changeTab(tab: AnkiBoxFragments){
        if(returnCurrentChildFragment()==tab) return else {
            val action:NavDirections =
                when(tab){
                    AnkiBoxFragments.AllFlashCardCovers -> BoxFlashCardCoversFragDirections.toAllFlashCardCoverFrag()
                    AnkiBoxFragments.Favourites -> BoxFavouriteFragDirections.toAnkiBoxFavouriteFrag()
                    AnkiBoxFragments.Library -> BoxLibraryItemsFragDirections.toLibraryItemsFrag()
                }
            navigateWithoutBackstack(action)
        }

    }
    fun openFile(item:File){
        val action =
        when(returnCurrentChildFragment()){
            AnkiBoxFragments.AllFlashCardCovers ->{
                val a = BoxFlashCardCoversFragDirections.toAllFlashCardCoverFrag()
                a.fileId = intArrayOf(item.fileId)
                a
            }
            AnkiBoxFragments.Favourites -> {
               val a = BoxFavouriteFragDirections.toAnkiBoxFavouriteFrag()
                a.fileId = intArrayOf(item.fileId)
                a
            }
            AnkiBoxFragments.Library -> {
                val a = BoxLibraryItemsFragDirections.toLibraryItemsFrag()
                a.fileId = intArrayOf(item.fileId)
                a.flashCard = item.fileStatus == FileStatus.FLASHCARD_COVER
                a
            }
            null -> return
        }
        navigateWithBackstack(action)
    }

    private val _ankiBoxFileIds = MutableLiveData<MutableList<Int>>()
    fun addToAnkiBoxFileIds(list: List<Int>){
        val a = mutableListOf<Int>()
        a.addAll(_ankiBoxFileIds.value ?: mutableListOf())
        a.addAll(list)
        setAnkiBoxFileIds(a)

    }
    fun removeFromAnkiBoxFileIds(id:Int){
        val a = mutableListOf<Int>()
        a.addAll(_ankiBoxFileIds.value ?: mutableListOf())
        a.remove(id)
        setAnkiBoxFileIds(a)

    }
    private fun setAnkiBoxFileIds (list:MutableList<Int>){
        _ankiBoxFileIds.value = list
    }
    val ankiBoxFileIds:LiveData<MutableList<Int>> = _ankiBoxFileIds
    private val _ankiBoxCardIds = MutableLiveData<MutableList<Int>>()
    fun addToAnkiBoxCardIds(list: List<Int>){
        val a = mutableListOf<Int>()
        a.addAll(_ankiBoxCardIds.value ?: mutableListOf())
        a.addAll(list)
        setAnkiBoxCardIds(a)

    }
    fun removeFromAnkiBoxCardIds(cardId: Int){
        val a = mutableListOf<Int>()
        a.addAll(_ankiBoxCardIds.value ?: mutableListOf())
        a.remove(cardId)
        setAnkiBoxCardIds(a)

    }
    fun returnAnkiBoxCardIds():List<Int>{
        return _ankiBoxCardIds.value ?: mutableListOf()

    }
    fun setAnkiBoxCardIds (list:List<Int>){
        val a = mutableListOf<Int>()
        a.addAll(list)
        _ankiBoxCardIds.value = a
    }

    val ankiBoxCardIds:LiveData<MutableList<Int>> = _ankiBoxCardIds


    fun openFlip(){
        ankiBaseViewModel.setSettingVisible(false)
        ankiBaseViewModel.navigateInAnkiFragments(AnkiFragments.Flip)
    }
    fun onClickBtnStartFlip(){
        if(returnAnkiBoxCardIds().isEmpty()) ObserveOnce(cardsExistsFromDB){ cardExist->
            if(cardExist) openFlip() else return@ObserveOnce
        }.commit() else openFlip()

    }
}