package com.example.tangochoupdated.ui.viewmodel

import android.text.Editable
import android.view.View
import androidx.lifecycle.*
import com.example.tangochoupdated.R
import com.example.tangochoupdated.db.MyRoomRepository
import com.example.tangochoupdated.db.dataclass.Card
import com.example.tangochoupdated.db.dataclass.File
import com.example.tangochoupdated.db.dataclass.FileXRef
import com.example.tangochoupdated.db.enumclass.ColorStatus
import com.example.tangochoupdated.db.enumclass.FileStatus
import com.example.tangochoupdated.db.enumclass.Mode
import kotlinx.coroutines.launch

class ChooseFileMoveToViewModel(val repository: MyRoomRepository) : ViewModel() {
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

    fun moveSelectedItemToFile(item:File){
        val change = returnMovingItems()

        change.onEach {
            when(it ){
                is Card -> {
                    it.belongingFlashCardCoverId = item.fileId
                    it.libOrder = item.childData.childCardsAmount + 1

                }
                is File -> {
                    it.parentFileId = item.fileId

                }
            }
            update(it)
        }
        setPopUpVisible(false)
    }
    private fun update(any:Any){
        viewModelScope.launch {
            repository.update(any)
        }
    }
}