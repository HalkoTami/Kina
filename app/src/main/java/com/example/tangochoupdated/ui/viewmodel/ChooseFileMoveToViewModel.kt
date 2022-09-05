package com.example.tangochoupdated.ui.viewmodel

import androidx.lifecycle.*
import androidx.navigation.NavController
import com.example.tangochoupdated.db.MyRoomRepository
import com.example.tangochoupdated.db.dataclass.Card
import com.example.tangochoupdated.db.dataclass.File
import kotlinx.coroutines.launch

class ChooseFileMoveToViewModel(val repository: MyRoomRepository) : ViewModel() {
    private val _toastText= MutableLiveData<String>()
    val toastText :LiveData<String> = _toastText
    fun setToastText(string: String){
        _toastText.value = string
    }
    fun returnToastText():String{
        return  _toastText.value ?:""
    }

    private val _showToast= MutableLiveData<Boolean>()
    val showToast :LiveData<Boolean> = _showToast
    fun setShowToast(boolean: Boolean){
        _showToast.value = boolean
    }
    fun makeToastVisible(){
        setShowToast(true)
        setShowToast(false)
    }
    private val _popUpText= MutableLiveData<String>()
    val popUpText :LiveData<String> = _popUpText
    fun setPopUpText(string: String){
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
    val movingItems:LiveData<List<Any>> = _movingItems
    private val _fileMoveTo = MutableLiveData<File>()
    fun setFileMoveTo(file: File){
        _fileMoveTo.value = file
    }
    fun returnFileMoveTo():File?{
        return _fileMoveTo.value
    }
    val fileMoveTo:LiveData<File> = _fileMoveTo

    fun onClickRvBtnMove(item:File){
        setToastText("${item.title}へ移動しました")
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
        makeToastVisible()
        setPopUpVisible(false)
        navController.popBackStack()

    }
    private fun update(any:Any){
        viewModelScope.launch {
            repository.update(any)
        }
    }
    private fun updateAncestorsAndChild(beforeChange:Any){
        viewModelScope.launch {
            repository.upDateParentFile(beforeChange,returnFileMoveTo()?.fileId)
        }
    }
}