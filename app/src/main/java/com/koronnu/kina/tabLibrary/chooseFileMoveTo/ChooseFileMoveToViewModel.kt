package com.koronnu.kina.tabLibrary.chooseFileMoveTo

import android.content.res.Resources
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.CreationExtras
import com.koronnu.kina.R
import com.koronnu.kina.application.RoomApplication
import com.koronnu.kina.customClasses.enumClasses.Count
import com.koronnu.kina.db.MyRoomRepository
import com.koronnu.kina.db.dataclass.Card
import com.koronnu.kina.db.dataclass.File
import com.koronnu.kina.customClasses.normalClasses.MakeToastFromVM
import com.koronnu.kina.db.enumclass.FileStatus
import com.koronnu.kina.tabLibrary.LibraryBaseViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ChooseFileMoveToViewModel(val repository: MyRoomRepository,
                                val resources: Resources) : ViewModel() {

    companion object{
        fun getFactory(libraryBaseViewModel: LibraryBaseViewModel): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]) as RoomApplication
                val repository = application.repository
                val resources = application.resources
                val chooseFileMoveToViewModel = ChooseFileMoveToViewModel(repository,resources)
                chooseFileMoveToViewModel.libraryViewModel = libraryBaseViewModel
                return chooseFileMoveToViewModel as T
            }

        }
    }
    private lateinit var libraryViewModel: LibraryBaseViewModel




    fun getFilteredFiles(fileId: Int?):LiveData<List<File>> =
        repository.getFileDataByParentFileId(fileId).map { listFromDB->
            when(getMovableFileStatus) {
                FileStatus.FLASHCARD_COVER -> listFromDB.filter { it.fileId!=getMovingItemsParentFileId && it.fileStatus == FileStatus.FLASHCARD_COVER }
                FileStatus.FOLDER -> listFromDB.filter { (returnMovingItems().filterIsInstance<File>().map { file-> file.fileId }.contains(it.fileId).not()) &&
                it.fileStatus == FileStatus.FOLDER}
                else -> throw IllegalArgumentException()
            } }.asLiveData()

    private val _movableFiles = MutableLiveData<List<File>>()
    fun setMovableFiles(filteredFiles:List<File>){
        _movableFiles.value = filteredFiles.filter { checkRvItemMoveBtnVisible(it) }
    }
    val getMovableFiles get() = _movableFiles.value

    private var _doAfterRvAttached:()->Unit ={}
    fun setDoAfterRvAttached(unit:()->Unit){
        _doAfterRvAttached = unit
    }
    fun clearDoAfterRvAttached(){
        _doAfterRvAttached = {}
    }
    val doAfterRVAttached get() =  _doAfterRvAttached




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
        _toast.value = MakeToastFromVM(String(),false)
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
    private val getMovingItemsParentFileId get() = _movingItemsParentFileId.value
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
        var file: File,
        val fileChildLastId:Int?
    )
    private val fileMoveToChildrenObserver = Observer<List<File>> {
        setFileMoveToData(FileMoveToData(getFileMoveTo,it.lastOrNull()?.fileId))
        setCollectMovingFileData(Count.End)
    }
    private val _fileMoveToData = MutableLiveData<FileMoveToData>()
    private fun setFileMoveToData(fileMoveToData: FileMoveToData){
        _fileMoveToData.value = fileMoveToData
    }
    private val getFileMoveToData get() = _fileMoveToData.value!!
    private val _fileMoveTo = MutableLiveData<File>()
    private fun setFileMoveTo(file: File){
        _fileMoveTo.value = file
    }
    private val getFileMoveTo get() = _fileMoveTo.value!!
    private var doAfterDataCollected :()-> Unit = {}
    private val _collectMovingFileData = MutableLiveData<Count>()
    private fun setCollectMovingFileData(count: Count){
        _collectMovingFileData.value = count
        when(count){
          Count.Start -> collectChildrenData()
          Count.End -> doAfterDataCollected()
        }
    }

    private var fileMoveToChildrenFiles:LiveData<List<File>>? = null
    private fun collectChildrenData(){
        fileMoveToChildrenFiles = getFilteredFiles(getFileMoveTo.fileId)
        fileMoveToChildrenFiles!!.observeForever(fileMoveToChildrenObserver)
    }

    private fun doAfterFileMoveToDataCollected(){
        setPopUpVisible(true)
    }
    private fun doAfterItemsMovedToFile(){
        setPopUpVisible(false)
        makeToastFromVM( resources.getString(R.string.toast_AfteritemMovedtoFile,getFileMoveTo.title))
        libraryViewModel.returnLibraryNavCon()?.popBackStack()
        fileMoveToChildrenFiles!!.removeObserver(fileMoveToChildrenObserver)
        doAfterDataCollected = { }
    }
    private fun setUpActionsBeforeCollectData(){
        doAfterDataCollected = {
            doAfterFileMoveToDataCollected()
            doAfterDataCollected = { doAfterItemsMovedToFile() }
        }
    }

    fun onClickRvBtnMove(item: File){
        setFileMoveTo(item)
        setPopUpText(resources.getString(R.string.popUpConfirmMoveBin_confirmMove,item.title))
        setUpActionsBeforeCollectData()
        setCollectMovingFileData(Count.Start)

    }
    fun onClickChooseFileMoveToRvBaseContainer(file: File){
        libraryViewModel.openChooseFileMoveTo(file)
    }


    fun moveSelectedItemToFile(){
        setCollectMovingFileData(Count.Start)
        val item = getFileMoveTo
        val change = returnMovingItems()
        val updatedSisters = getMovingItemSistersUpdateNeeded().filterIsInstance<Card>()
        val lastId = getFileMoveToData.fileChildLastId

        val changeCards = change.filterIsInstance<Card>()
        updatedSisters.onEach {
            update(it)
        }
        changeCards.onEach {
            if(it.cardBefore == null) it.cardBefore = lastId
            it.belongingFlashCardCoverId = item.fileId
            update(it)
        }




    }
    private fun update(any:Any){
        viewModelScope.launch {
            repository.update(any)
        }
    }

    override fun onCleared() {
        super.onCleared()
        fileMoveToChildrenFiles?.removeObserver(fileMoveToChildrenObserver)
    }



}