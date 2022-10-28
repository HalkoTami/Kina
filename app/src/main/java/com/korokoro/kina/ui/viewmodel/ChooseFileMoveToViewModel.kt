package com.korokoro.kina.ui.viewmodel

import androidx.lifecycle.*
import androidx.navigation.NavController
import com.korokoro.kina.actions.SortActions
import com.korokoro.kina.db.MyRoomRepository
import com.korokoro.kina.db.dataclass.Card
import com.korokoro.kina.db.dataclass.File
import com.korokoro.kina.ui.customClasses.MakeToastFromVM
import kotlinx.coroutines.launch

class ChooseFileMoveToViewModel(val repository: MyRoomRepository) : ViewModel() {

    fun getFilesMovableFolders(movingFileIds:List<Int>,parentFileId:Int?):LiveData<Map<File,List<File>>> = repository.getFoldersMovableTo(movingFileIds,parentFileId).asLiveData()
    fun getFilesMovableFlashCards(movingCardsIds:List<Int>):LiveData<Map<File,List<Card>>> = repository.getMovableFlashCards(movingCardsIds).asLiveData()


    private val _flashcardAndChildListMap = MutableLiveData<Map<File,List<Card>>>()
    fun setFlashcardAndChildListMap(map:Map<File,List<Card>>){
        _flashcardAndChildListMap.value = map
    }
    fun returnFlashcardAndChildListMap():Map<File,List<Card>>{
        return _flashcardAndChildListMap.value ?: mutableMapOf()
    }
    private val _folderAndChildFilesMap = MutableLiveData<Map<File,List<File>>>()
    fun setFolderAndChildFilesMap (map:Map<File,List<File>>){
        _folderAndChildFilesMap.value = map
    }
    fun getFolderAndChildFilesMap():Map<File,List<File>>{
        return _folderAndChildFilesMap.value ?: mutableMapOf()
    }

    private val _toast = MutableLiveData<MakeToastFromVM>()
    private fun makeToastFromVM(string: String){
        _toast.value = MakeToastFromVM(string,true)
        _toast.value = MakeToastFromVM("",false)
    }

    val toast :LiveData<MakeToastFromVM> = _toast

    private val _popUpText= MutableLiveData<String>()
    val popUpText :LiveData<String> = _popUpText
    private fun setPopUpText(string: String){
        _popUpText.value = string
    }

    private val _popUpVisible = MutableLiveData<Boolean>()
    fun setPopUpVisible(boolean: Boolean){
        _popUpVisible.value = boolean
    }
    val popUpVisible:LiveData<Boolean> = _popUpVisible
    private val _movingItems = MutableLiveData<List<Any>>()
    fun setMovingItems(list:List<Any>){
        _movingItems.value = list
    }
    fun returnMovingItems():List<Any>{
        return _movingItems.value ?: mutableListOf()
    }
    private val _movingItemsSisters = MutableLiveData<List<Any>>()
    fun setMovingItemsSisters(list:List<Any>){
        _movingItemsSisters.value = list
    }
    fun returnMovingItemsSisters():List<Any>{
        return _movingItemsSisters.value ?: mutableListOf()
    }
    private val _fileMoveTo = MutableLiveData<File>()
    private fun setFileMoveTo(file: File){
        _fileMoveTo.value = file
    }
    private fun returnFileMoveTo():File?{
        return _fileMoveTo.value
    }
    private val _lastCardIdInFlashCardMoveTo = MutableLiveData<Int>()
    private fun setLastCardIdInFlashCardMoveTo(int: Int){
        _lastCardIdInFlashCardMoveTo.value = int
    }
    private fun getLastCardIdInFlashCardMoveTo():Int?{
        return _lastCardIdInFlashCardMoveTo.value
    }

    fun onClickRvBtnMove(item:File,){
        setPopUpText("選択中のアイテムを${item.title}へ移動しますか？")
        setFileMoveTo(item)
        setPopUpVisible(true)
    }


    fun moveSelectedItemToFile(navController: NavController){
        val item = returnFileMoveTo() ?:return
        val change = returnMovingItems()
        val before = returnMovingItemsSisters()
        val lastId = SortActions().sortCards(returnFlashcardAndChildListMap()[item]).last().id

        val changeCards = change.filterIsInstance<Card>()
        changeCards.onEach {
            it.belongingFlashCardCoverId = item.fileId
            update(it)
        }
        val beforeCards = before.filterIsInstance<Card>()
        val cardsBeforeMustChanged = changeCards.filter {item-> changeCards.find { it.cardBefore == item.id}==null }
        val sorted = SortActions().sortCards(cardsBeforeMustChanged)
        val a = sorted.first()
        a.cardBefore = lastId
        update(a)

        makeToastFromVM("${item.title}へ移動しました")
        setPopUpVisible(false)
        navController.popBackStack()

    }
    private fun update(any:Any){
        viewModelScope.launch {
            repository.update(any)
        }
    }
    private fun updateAncestorsAndChild(beforeChange:Card){
        viewModelScope.launch {
            repository.upDateAncestorsAndChild(beforeChange,returnFileMoveTo()?.fileId)
        }
    }
}