package com.korokoro.kina.ui.viewmodel

import androidx.lifecycle.*
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import com.korokoro.kina.db.MyRoomRepository
import com.korokoro.kina.db.dataclass.ActivityData
import com.korokoro.kina.db.dataclass.Card
import com.korokoro.kina.db.dataclass.File
import com.korokoro.kina.customClasses.enumClasses.AnkiBoxFragments
import com.korokoro.kina.customClasses.AnkiBoxTabData
import com.korokoro.kina.db.enumclass.FileStatus
import com.korokoro.kina.ui.fragment.ankibox_frag_con.BoxFavouriteFragDirections
import com.korokoro.kina.ui.fragment.ankibox_frag_con.BoxFlashCardCoversFragDirections
import com.korokoro.kina.ui.fragment.ankibox_frag_con.BoxLibraryItemsFragDirections
import com.korokoro.kina.customClasses.MakeToastFromVM
import kotlinx.coroutines.launch

class AnkiBoxViewModel(val repository: MyRoomRepository) : ViewModel() {
    fun onCreate(){
        setAnkiBoxItems(mutableListOf())
    }
    fun getAnkiBoxRVCards(fileId:Int): LiveData<List<Card>> = repository.getAnkiBoxRVCards(fileId).asLiveData()
    fun getAnkiBoxRVDescendantsFolders(fileId:Int): LiveData<List<File>> = repository.getAnkiBoxRVDescendantsFolders(fileId).asLiveData()
    fun getAnkiBoxRVDescendantsFlashCards(fileId:Int): LiveData<List<File>> = repository.getAnkiBoxRVDescendantsFlashCards(fileId).asLiveData()
    fun ankiBoxFileAncestorsFromDB(int: Int?):LiveData<List<File>> = repository.getAllAncestorsByFileId(int).asLiveData()
    private val _text = MutableLiveData<String>().apply {
        value = "This is Anki Fragment"
    }
    val allFlashCardCoverFromDB: LiveData<List<File>> = repository.allFlashCardCoverContainsCard.asLiveData()
    val allFavouriteAnkiBoxFromDB: LiveData<List<File>> = repository.allFavouriteAnkiBox.asLiveData()

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
        _toast.value = MakeToastFromVM("",false)
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

    val text: LiveData<String> = _text

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

    private val _modeCardsNotSelected = MutableLiveData<Boolean>()
    fun setModeCardsNotSelected(boolean: Boolean){
        _modeCardsNotSelected.value = boolean
    }
}