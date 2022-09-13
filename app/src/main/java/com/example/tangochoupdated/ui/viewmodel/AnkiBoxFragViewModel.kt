package com.example.tangochoupdated.ui.viewmodel

import androidx.lifecycle.*
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import com.example.tangochoupdated.db.MyRoomRepository
import com.example.tangochoupdated.db.dataclass.Card
import com.example.tangochoupdated.db.dataclass.File
import com.example.tangochoupdated.ui.viewmodel.customClasses.AnkiBoxFragments
import com.example.tangochoupdated.ui.viewmodel.customClasses.AnkiBoxTabData
import com.example.tangochoupdated.db.enumclass.ColorStatus
import com.example.tangochoupdated.db.enumclass.FileStatus
import com.example.tangochoupdated.ui.fragment.ankibox_frag_con.AllFlashCardCoversFragmentDirections
import com.example.tangochoupdated.ui.fragment.ankibox_frag_con.FavouriteAnkiBoxFragmentDirections
import com.example.tangochoupdated.ui.fragment.ankibox_frag_con.LibraryItemsFragmentDirections
import kotlinx.coroutines.launch

class AnkiBoxFragViewModel(val repository: MyRoomRepository) : ViewModel() {
    fun onCreate(){
        setAnkiBoxItems(mutableListOf())
    }
    fun getAnkiBoxRVCards(fileId:Int): LiveData<List<Card>> = repository.getAnkiBoxRVCards(fileId).asLiveData()
    fun getAnkiBoxFavouriteRVCards(fileId:Int): LiveData<List<Card>> = repository.getAnkiBoxFavouriteRVCards(fileId).asLiveData()

    private val _text = MutableLiveData<String>().apply {
        value = "This is Anki Fragment"
    }
    val allFlashCardCoverFromDB: LiveData<List<File>> = repository.allFlashCardCover.asLiveData()
    val allFavouriteAnkiBoxFromDB: LiveData<List<File>> = repository.allFavouriteAnkiBox.asLiveData()
    val lastInsertedFileId:LiveData<Int> = repository.lastInsertedFile.asLiveData()
    fun getLibraryFilesFromDB(parentFileId:Int?) :LiveData<List<File>> = repository.getFileDataByParentFileId(parentFileId).asLiveData()
    private val _libraryFilesAsAnkiBox = MutableLiveData<List<File>>()
    fun setLibraryFilesAsAnkiBox(list: List<File>){
        _libraryFilesAsAnkiBox.value = list
    }
    val libraryFilesAsAnkiBox:LiveData<List<File>> = _libraryFilesAsAnkiBox

    fun getCardsFromDB(parentFileId:Int?) :LiveData<List<Card>> = repository.getCardDataByFileId(parentFileId).asLiveData()
    private val _libraryCardsAsAnkiBox = MutableLiveData<List<Card>>()
    fun setLibraryCardsAsAnkiBox(list: List<Card>){
        _libraryCardsAsAnkiBox.value = list
    }
    fun getDescendantsCards(fileIds:List<Int>) :LiveData<List<Card>> = repository.getDescendantsCardsByMultipleFileId(fileIds).asLiveData()
    fun getCardsFromDBByMultipleCardIds(cardIds:List<Int>) :LiveData<List<Card>> = repository.getCardsByMultipleCardId(cardIds).asLiveData()
    private fun insertFile(file: File){
        viewModelScope.launch {
            repository.insert(file)
        }
    }

    fun insertEmptyFavourite(){
       insertFile(File(fileId = 0,
           title = "okini",
           fileStatus = FileStatus.ANKI_BOX_FAVOURITE ,
           colorStatus = ColorStatus.GRAY,
           deleted = false,
           hasParent = false,
           libOrder = 0,
           parentFileId = null
       ))
        setAddCardsToFavourite(true)

    }


    private val _addCardsToFavourite = MutableLiveData<Boolean>()
    fun setAddCardsToFavourite(boolean: Boolean){
        _addCardsToFavourite.value = boolean
    }
    fun returnAddCardsToFavourite():Boolean{
        return _addCardsToFavourite.value ?:false
    }
//    fun addCardsToFavourite(fileId:Int){
//        val a = returnAnkiBoxItems()
//        val xRef = mutableListOf<CardAndTagXRef>()
//        a?.onEach { xRef.add(CardAndTagXRef(it.id,fileId)) }
//        insertXref(xRef)
//        setAddCardsToFavourite(false)
//
//    }


    private val _ankiBoxNavCon = MutableLiveData<NavController>()
    fun setAnkiBoxNavCon(navController: NavController){
        _ankiBoxNavCon.value = navController
    }
    fun returnAnkiBoxNavCon():NavController?{
        val navCon = _ankiBoxNavCon.value
         if(navCon==null) {
             getAnkiBoxNavConIfNull()
         }
        return _ankiBoxNavCon.value
    }

    private val _getAnkiBoxNavCon = MutableLiveData<Boolean>()
    private fun setGetAnkiBoxNavCon(boolean: Boolean){
        _getAnkiBoxNavCon.value = boolean
    }
    private fun getAnkiBoxNavConIfNull(){
        setGetAnkiBoxNavCon(true)
        setGetAnkiBoxNavCon(false)
    }
    val getAnkiBoxNavCon :LiveData<Boolean> = _getAnkiBoxNavCon

    fun getDescendantsCardIds(fileIdList:List<Int>) :LiveData<List<Int>> = repository.getDescendantsCardsIsByMultipleFileId(fileIdList).asLiveData()
    private val _ankiBoxItems = MutableLiveData<MutableList<Card>>()
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
    fun returnCurrentChildFragment (): AnkiBoxFragments?{
        return _currentChildFragment.value?.currentTab
    }
    val currentChildFragment :LiveData<AnkiBoxTabData> = _currentChildFragment

    fun changeTab(tab: AnkiBoxFragments){
        if(returnCurrentChildFragment()==tab) return else {
            val action:NavDirections =
                when(tab){
                    AnkiBoxFragments.AllFlashCardCovers -> AllFlashCardCoversFragmentDirections.toAllFlashCardCoverFrag()
                    AnkiBoxFragments.Favourites -> FavouriteAnkiBoxFragmentDirections.toAnkiBoxFavouriteFrag()
                    AnkiBoxFragments.Library -> LibraryItemsFragmentDirections.toLibraryItemsFrag()
                }
            returnAnkiBoxNavCon()?.navigate(action)
        }

    }
    fun openFile(item:File){
        val action =
        when(returnCurrentChildFragment()){
            AnkiBoxFragments.AllFlashCardCovers ->{
                val a = AllFlashCardCoversFragmentDirections.toAllFlashCardCoverFrag()
                a.fileId = intArrayOf(item.fileId)
                a
            }
            AnkiBoxFragments.Favourites -> {
                return
            }
            AnkiBoxFragments.Library -> {
                val a = LibraryItemsFragmentDirections.toLibraryItemsFrag()
                a.fileId = intArrayOf(item.fileId)
                a.flashCard = item.fileStatus == FileStatus.TANGO_CHO_COVER
                a
            }
            null -> return
        }
        returnAnkiBoxNavCon()?.navigate(action)
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
    fun setAnkiBoxFileIds (list:MutableList<Int>){
        _ankiBoxFileIds.value = list
    }
    fun returnAnkiBoxFileIds ():MutableList<Int>{
        return _ankiBoxFileIds.value ?: mutableListOf()
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

    fun addToAnkiBoxItems(list: List<Card>){
        val a = mutableListOf<Card>()
        a.addAll(_ankiBoxItems.value ?: mutableListOf())
        a.addAll(list)
        setAnkiBoxItems(a)

    }
    fun removeFromAnkiBoxItems(list: List<Card>){
        val a = mutableListOf<Card>()
        a.addAll(_ankiBoxItems.value ?: mutableListOf())
        a.removeAll(list)
        setAnkiBoxItems(a)

    }
    val ankiBoxCardIds:LiveData<MutableList<Int>> = _ankiBoxCardIds
    fun onClickCheckBox(item:Any){


    }

    private val _modeCardsNotSelected = MutableLiveData<Boolean>()
    fun setModeCardsNotSelected(boolean: Boolean){
        _modeCardsNotSelected.value = boolean
    }
    fun returnModeCardsNotSelected():Boolean?{
        return _modeCardsNotSelected.value
    }
    val modeCardsNotSelected:LiveData<Boolean> = _modeCardsNotSelected
}