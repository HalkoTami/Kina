package com.koronnu.kina.ui.viewmodel

import androidx.compose.ui.graphics.vector.EmptyPath
import androidx.lifecycle.*
import androidx.navigation.NavController
import com.koronnu.kina.customClasses.enumClasses.Count
import com.koronnu.kina.db.MyRoomRepository
import com.koronnu.kina.db.dataclass.Card
import com.koronnu.kina.db.dataclass.File
import com.koronnu.kina.customClasses.normalClasses.MakeToastFromVM
import com.koronnu.kina.db.enumclass.FileStatus
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

class ChooseFileMoveToViewModel(val repository: MyRoomRepository) : ViewModel() {



    fun getMovableFiles(fileId: Int?):LiveData<List<File>>{
        val childFilesList = repository.getFileDataByParentFileId(fileId)
        val withoutFlashCardSelectedItemAreIn =
            childFilesList.filter { it.filter { it.fileId!=getMovingItemsParentFileId } == it }
        val withoutSelectedFiles =
            childFilesList.filter {  it.filter { returnMovingItems().filterIsInstance<File>().map { it.fileId }.contains(it.fileId).not() }==it }
        return when(getMovableFileStatus){
            FileStatus.FLASHCARD_COVER-> withoutFlashCardSelectedItemAreIn.asLiveData()
            FileStatus.FOLDER -> withoutSelectedFiles.asLiveData()
            else -> throw IllegalArgumentException()
        }


    }

    fun checkRvItemMoveBtnVisible(item: File):Boolean{
        val movingFilesNotInInRvItem = getMovingItemsParentFileId != item.fileId
        val isFlashCard = item.fileStatus==FileStatus.FLASHCARD_COVER
        val moveButtonVisible = when(getMovableFileStatus){
            FileStatus.FLASHCARD_COVER -> isFlashCard
            FileStatus.FOLDER -> movingFilesNotInInRvItem
            else -> throw IllegalArgumentException()
        }
        return moveButtonVisible
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
    private val _movingItemsParentFileId = MutableLiveData<Int?>()
    fun setMovingItemsParentFileId(fileId:Int?){
        _movingItemsParentFileId.value = fileId
    }
    val getMovingItemsParentFileId get() = _movingItemsParentFileId.value
    private val _movableFileStatus = MutableLiveData<FileStatus>()
    fun setMovableFileStatus(fileStatus: FileStatus){
        _movableFileStatus.value = fileStatus
    }
    val getMovableFileStatus get() = _movableFileStatus.value!!
    private val _movingItemSistersUpdateNeeded = MutableLiveData<List<Any>>()
    fun setMovingItemSistersUpdateNeeded(list:List<Any>){
        _movingItemSistersUpdateNeeded.value = list
    }
    private fun getMovingItemSistersUpdateNeeded():List<Any>{
        return _movingItemSistersUpdateNeeded.value ?: mutableListOf()
    }
    class FileMoveToData(
        var calledList:Int = 0,
        var file: File,
        val fileChildLastId:Int?
    )
    private val _fileMoveTo = MutableLiveData<FileMoveToData>()
    private fun setFileMoveTo(fileMoveToData: FileMoveToData){
        _fileMoveTo.value = fileMoveToData
        if(fileMoveToData.calledList==2) setCollectMovingFileData(Count.End){}
    }
    private val getFileMoveTo get() = _fileMoveTo.value!!
    private val _collectMovingFileData = MutableLiveData<Count>()
    private fun setCollectMovingFileData(count: Count,doOnCompleted:()->Unit){
        _collectMovingFileData.value = count
        if(count==Count.End) doOnCompleted()
    }
    val collectMovingFileData:LiveData<Count> = _collectMovingFileData

    private val _lastCardIdInFlashCardMoveTo = MutableLiveData<Int>()
    private fun setLastCardIdInFlashCardMoveTo(int: Int){
        _lastCardIdInFlashCardMoveTo.value = int
    }
    private fun getLastCardIdInFlashCardMoveTo():Int?{
        return _lastCardIdInFlashCardMoveTo.value
    }

    fun onClickRvBtnMove(item:File,){
        setPopUpText("選択中のアイテムを${item.title}へ移動しますか？")
        setCollectMovingFileData(Count.Start){
            setPopUpVisible(true)
        }

    }
    fun openChooseFileMoveTo(){
        TODO()
    }


    fun moveSelectedItemToFile(navController: NavController){
        val item = getFileMoveTo.file
        val change = returnMovingItems()
        val updatedSisters = getMovingItemSistersUpdateNeeded().filterIsInstance<Card>()
        val lastId = getFileMoveTo.fileChildLastId

        val changeCards = change.filterIsInstance<Card>()
        updatedSisters.onEach {
            update(it)
        }
        changeCards.onEach {
            if(it.cardBefore == null) it.cardBefore = lastId
            it.belongingFlashCardCoverId = item.fileId
            update(it)
        }


        makeToastFromVM("${item.title}へ移動しました")
        setPopUpVisible(false)
        navController.popBackStack()

    }
    private fun update(any:Any){
        viewModelScope.launch {
            repository.update(any)
        }
    }

}