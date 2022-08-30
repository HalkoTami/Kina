package com.example.tangochoupdated.ui.viewmodel

import androidx.compose.runtime.MutableState
import androidx.lifecycle.*
import com.example.tangochoupdated.db.MyRoomRepository
import com.example.tangochoupdated.db.dataclass.Card
import com.example.tangochoupdated.db.dataclass.File
import com.example.tangochoupdated.db.enumclass.AnkiFilter
import com.example.tangochoupdated.db.enumclass.AnkiOrder
import com.example.tangochoupdated.db.enumclass.AutoFlip
import com.example.tangochoupdated.db.enumclass.FileStatus
import com.example.tangochoupdated.ui.view_set_up.ConfirmMode
import kotlinx.coroutines.launch

class DeletePopUpViewModel(private val repository: MyRoomRepository) : ViewModel() {

    private fun deleteSingleFile(file: File, deleteChildren:Boolean){
        viewModelScope.launch {
            if(!deleteChildren){
                repository.upDateChildFilesOfDeletedFile(file.fileId,file.parentFileId)
                repository.delete(file)
            } else {
                repository.deleteFileAndAllDescendants(file.fileId)
            }
        }
    }
    fun getAllDescendantsByFileId(fileIdList: List<Int>): LiveData<List<File>> = repository.getAllDescendantsFilesByMultipleFileId(fileIdList).asLiveData()
    fun getCardsByMultipleFileId(fileIdList: List<Int>): LiveData<List<Card>> = repository.getAllDescendantsCardsByMultipleFileId(fileIdList).asLiveData()
    private val _deletingItems = MutableLiveData<MutableList<Any>>()
    fun setDeletingItem(list:MutableList<Any>){
        _deletingItems.value = list
    }
    fun returnDeletingItems():List<Any>{
        return _deletingItems.value ?: mutableListOf()
    }
    val deletingItem:LiveData<MutableList<Any>> = _deletingItems

    private val _deletingItemChildrenFiles = MutableLiveData<List<File>?>()
    fun setDeletingItemChildrenFiles(list:List<File>?){
        _deletingItemChildrenFiles.value = list
    }
    fun returnDeletingItemChildrenFiles():List<File>{
        return _deletingItemChildrenFiles.value ?: mutableListOf()
    }
    val deletingItemChildrenFiles:LiveData<List<File>?> = _deletingItemChildrenFiles
    private val _deletingItemChildrenCards = MutableLiveData<List<Card>?>()
    fun setDeletingItemChildrenCards(list:List<Card>?){
        _deletingItemChildrenCards.value = list
    }
    fun returnDeletingItemChildrenCards():List<Card>{
       return  _deletingItemChildrenCards.value ?: mutableListOf()
    }


    val deletingItemChildrenCards:LiveData<List<Card>?> = _deletingItemChildrenCards

    fun checkDeletingItemsHasChildren():Boolean{
        return returnDeletingItemChildrenCards().isEmpty().not()||returnDeletingItemChildrenFiles().isEmpty().not()
    }
    fun deleteOnlyFile(){
        returnDeletingItems().onEach {
            deleteSingleFile(it as File,false)
        }
        setConfirmDeleteVisible(false)
        setConfirmDeleteWithChildrenVisible(false)
    }
    fun deleteWithChildren(){
        returnDeletingItems().onEach {
            deleteSingleFile(it as File,true)
        }
        setConfirmDeleteWithChildrenVisible(false)
    }


    class ConfirmDeleteView(
        var visible:Boolean,
        var confirmText:String
    )
    class ConfirmDeleteWithChildrenView(
        var visible:Boolean,
        var confirmText:String,
        var containingCards:Int,
        var containingFlashCardCover:Int,
        var containingFolder:Int,

    )


    private val _confirmDeleteView =  MutableLiveData<ConfirmDeleteView>()
    private fun setConfirmDeleteView(confirmDeleteView: ConfirmDeleteView){
        _confirmDeleteView.value = confirmDeleteView
    }
    val confirmDeleteView:LiveData<ConfirmDeleteView> = _confirmDeleteView
    private val _confirmDeleteWithChildrenView =  MutableLiveData<ConfirmDeleteWithChildrenView>()
    private fun setConfirmDeleteWithChildrenView(confirmDeleteWithChildrenView: ConfirmDeleteWithChildrenView){
        _confirmDeleteWithChildrenView.value = confirmDeleteWithChildrenView
    }
    val confirmDeleteWithChildrenView:LiveData<ConfirmDeleteWithChildrenView> = _confirmDeleteWithChildrenView

    fun setConfirmDeleteVisible(visible: Boolean,){
        val single = returnDeletingItems().size == 1
        val singleItem = returnDeletingItems().single()
        val txvConfirmText:String = if(single && singleItem is File) "${singleItem.title}を削除しますか？"
        else "選択中のアイテムを削除しますか？"
        setConfirmDeleteView(ConfirmDeleteView(visible,txvConfirmText))
    }
    fun setConfirmDeleteWithChildrenVisible(visible: Boolean,){
        val single = returnDeletingItems().size == 1
        val singleItem = returnDeletingItems().single()
        val txvConfirmText:String = if(single && singleItem is File) "${singleItem.title}の中身をすべて削除しますか？"
        else "中身をすべて削除しますか？"
        setConfirmDeleteWithChildrenView(ConfirmDeleteWithChildrenView(visible,txvConfirmText,
        containingCards = returnDeletingItemChildrenCards().size,
            containingFlashCardCover = returnDeletingItemChildrenFiles().filter { it.fileStatus == FileStatus.TANGO_CHO_COVER }.size,
            containingFolder = returnDeletingItemChildrenFiles().filter { it.fileStatus == FileStatus.FOLDER }.size
            ))
    }

}