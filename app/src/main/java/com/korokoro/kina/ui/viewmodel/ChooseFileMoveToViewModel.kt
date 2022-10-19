package com.korokoro.kina.ui.viewmodel

import androidx.lifecycle.*
import androidx.navigation.NavController
import com.korokoro.kina.db.MyRoomRepository
import com.korokoro.kina.db.dataclass.Card
import com.korokoro.kina.db.dataclass.File
import com.korokoro.kina.ui.customClasses.MakeToastFromVM
import kotlinx.coroutines.launch

class ChooseFileMoveToViewModel(val repository: MyRoomRepository) : ViewModel() {

    fun getFilesMovableFolders(movingFileIds:List<Int>,parentFileId:Int?):LiveData<List<File>> = repository.getFoldersMovableTo(movingFileIds,parentFileId).asLiveData()
    fun getFilesMovableFlashCards(movingCardsIds:List<Int>):LiveData<List<File>> = repository.getMovableFlashCards(movingCardsIds).asLiveData()

    private val _movableItemExists = MutableLiveData<Boolean>()
    val movableItemExists:LiveData<Boolean> = _movableItemExists
    fun setMovableItemExists(boolean: Boolean){
        _movableItemExists.value = boolean
    }
    fun returnMovableItemExists():Boolean{
        return _movableItemExists.value ?:false
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
    private val _fileMoveTo = MutableLiveData<File>()
    private fun setFileMoveTo(file: File){
        _fileMoveTo.value = file
    }
    private fun returnFileMoveTo():File?{
        return _fileMoveTo.value
    }

    fun onClickRvBtnMove(item:File){
        setPopUpText("選択中のアイテムを${item.title}へ移動しますか？")
        setFileMoveTo(item)
        setPopUpVisible(true)
    }

    fun moveSelectedItemToFile(navController: NavController){
        val item = returnFileMoveTo() ?:return
        val change = returnMovingItems()

        change.onEach {
            when(it ){
                is Card -> {
                    updateAncestorsAndChild(it)
                    it.belongingFlashCardCoverId = item.fileId
                    it.libOrder = item.childData.childCardsAmount + 1

                }
                is File -> {
                    it.parentFileId = item.fileId

                }
            }
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
    private fun updateAncestorsAndChild(beforeChange:Card){
        viewModelScope.launch {
            repository.upDateAncestorsAndChild(beforeChange,returnFileMoveTo()?.fileId)
        }
    }
}