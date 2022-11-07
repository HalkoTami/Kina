package com.koronnu.kina.ui.viewmodel

import androidx.lifecycle.*
import com.koronnu.kina.db.MyRoomRepository
import com.koronnu.kina.db.dataclass.Card
import com.koronnu.kina.db.dataclass.File
import com.koronnu.kina.db.enumclass.FileStatus
import com.koronnu.kina.customClasses.MakeToastFromVM
import kotlinx.coroutines.launch

class DeletePopUpViewModel(private val repository: MyRoomRepository) : ViewModel() {
    private val _toast = MutableLiveData<MakeToastFromVM>()
    private fun makeToastFromVM(string: String){
        _toast.value = MakeToastFromVM(string,true)
        _toast.value = MakeToastFromVM("",false)
    }

    val toast :LiveData<MakeToastFromVM> = _toast

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
    private fun deleteMultipleFiles(file: List<File>, deleteChildren:Boolean){
        file.onEach { deleteSingleFile(it,deleteChildren) }
        if(file.size == 1) {
            val single = file.single()
            if(deleteChildren) makeToastFromVM("${single.title}と中身を削除しました")
            else  makeToastFromVM("${single.title}を削除しました")
        } else makeToastFromVM("選択中のアイテムを削除しました")
    }
    private fun deleteCards(cards: List<Card> ){
        val updateNeeded = getDeletingItemsSistersUpdateNeeded()
        viewModelScope.launch {
            repository.deleteMultiple(cards)
            repository.updateMultiple(updateNeeded)
        }
        makeToastFromVM("${cards.size}枚のカードを削除しました")
    }
    fun getAllDescendantsByFileId(fileIdList: List<Int>): LiveData<List<File>> = repository.getAllDescendantsFilesByMultipleFileId(fileIdList).asLiveData()
    fun getCardsByMultipleFileId(fileIdList: List<Int>): LiveData<List<Card>> = repository.getAllDescendantsCardsByMultipleFileId(fileIdList).asLiveData()
    private val _deletingItems = MutableLiveData<MutableList<Any>>()
    fun setDeletingItem(list:MutableList<Any>){
        _deletingItems.value = list
    }
    private fun returnDeletingItems():List<Any>{
        return _deletingItems.value ?: mutableListOf()
    }
    val deletingItem:LiveData<MutableList<Any>> = _deletingItems

    private val _deletingItemChildrenFiles = MutableLiveData<List<File>?>()
    fun setDeletingItemChildrenFiles(list:List<File>?){
        _deletingItemChildrenFiles.value = list
    }
    private fun returnDeletingItemChildrenFiles():List<File>{
        return _deletingItemChildrenFiles.value ?: mutableListOf()
    }
//    val deletingItemChildrenFiles:LiveData<List<File>?> = _deletingItemChildrenFiles
    private val _deletingItemChildrenCards = MutableLiveData<List<Card>?>()
    fun setDeletingItemChildrenCards(list:List<Card>?){
        _deletingItemChildrenCards.value = list
    }
    private fun returnDeletingItemChildrenCards():List<Card>{
       return  _deletingItemChildrenCards.value ?: mutableListOf()
    }
    fun setContainingFilesAmount(list:List<File>){
        val a = returnConfirmDeleteWithChildrenView()
        a.containingFolder = list.filter { it.fileStatus == FileStatus.FOLDER }.size
        a.containingFlashCardCover = list.filter { it.fileStatus == FileStatus.FLASHCARD_COVER }.size
        setConfirmDeleteWithChildrenView(a)
    }
    fun setContainingCardsAmount(list:List<Card>){
        val a = returnConfirmDeleteWithChildrenView()
        a.containingCards = list.size
        setConfirmDeleteWithChildrenView(a)
    }
    fun setDeleteText(deletingItems:List<Any>){
        val a = returnConfirmDeleteView()
        val single = deletingItems.size == 1
        val singleItem = if(single)returnDeletingItems().single() else null
        a.confirmText = if(single && singleItem is File) "${singleItem.title}を削除しますか？"
        else "選択中のアイテムを削除しますか？"
        setConfirmDeleteView(a)
    }

    fun setDeleteWithChildrenText(deletingItems:List<Any>){
        val a = returnConfirmDeleteWithChildrenView()
        val single = deletingItems.size == 1
        val singleItem = if(single)returnDeletingItems().single() else null
        a.confirmText = if(single && singleItem is File) "${singleItem.title}の中身をすべて削除しますか？"
        else "中身をすべて削除しますか？"
        setConfirmDeleteWithChildrenView(a)
    }

    fun checkDeletingItemsHasChildren():Boolean{
        return returnDeletingItemChildrenCards().isEmpty().not()||returnDeletingItemChildrenFiles().isEmpty().not()
    }
    fun deleteOnlyFile(){
        if(returnDeletingItems().filterIsInstance<File>().size == 1){
            deleteSingleFile(returnDeletingItems().filterIsInstance<File>().single(),false)

        } else {
            deleteMultipleFiles(returnDeletingItems().filterIsInstance<File>() , false)
            deleteCards(returnDeletingItems().filterIsInstance<Card>())
        }
        setConfirmDeleteVisible(false)
        setConfirmDeleteWithChildrenVisible(false)
    }
    fun deleteCard(){
        val a =returnDeletingItems()
        val cards:List<Card> = a.filterIsInstance<Card>()
        deleteCards(cards)
    }
    fun deleteFileWithChildren(){
        deleteMultipleFiles(returnDeletingItems().filterIsInstance<File>(),true)
        setConfirmDeleteWithChildrenVisible(false)
    }
    private val _deletingItemsSistersUpdateNeeded = MutableLiveData<List<Any>>()
    fun setDeletingItemsSistersUpdateNeeded(list:List<Any>){
        _deletingItemsSistersUpdateNeeded.value = list
    }
    fun getDeletingItemsSistersUpdateNeeded():List<Any>{
        return _deletingItemsSistersUpdateNeeded.value ?: mutableListOf()
    }

    class ConfirmDeleteView(
        var visible:Boolean = false,
        var confirmText:String = ""
    )
    class ConfirmDeleteWithChildrenView(
        var visible:Boolean = false,
        var confirmText:String = "",
        var containingCards:Int = 0,
        var containingFlashCardCover:Int = 0,
        var containingFolder:Int = 0,

        )


    fun checkBackgroundVisible():Boolean{
        return returnConfirmDeleteView().visible||returnConfirmDeleteWithChildrenView().visible
    }
    private val _confirmDeleteView =  MutableLiveData<ConfirmDeleteView>()
    private fun setConfirmDeleteView(confirmDeleteView: ConfirmDeleteView){
        _confirmDeleteView.value = confirmDeleteView
    }
    private fun returnConfirmDeleteView():ConfirmDeleteView{
        return _confirmDeleteView.value ?:ConfirmDeleteView()
    }
    val confirmDeleteView:LiveData<ConfirmDeleteView> = _confirmDeleteView
    private val _confirmDeleteWithChildrenView =  MutableLiveData<ConfirmDeleteWithChildrenView>()
    private fun setConfirmDeleteWithChildrenView(confirmDeleteWithChildrenView: ConfirmDeleteWithChildrenView){
        _confirmDeleteWithChildrenView.value = confirmDeleteWithChildrenView
    }
    private fun returnConfirmDeleteWithChildrenView():ConfirmDeleteWithChildrenView{
        return _confirmDeleteWithChildrenView.value ?: ConfirmDeleteWithChildrenView()
    }
    val confirmDeleteWithChildrenView:LiveData<ConfirmDeleteWithChildrenView> = _confirmDeleteWithChildrenView

    fun setConfirmDeleteVisible(visible: Boolean){
        val a = returnConfirmDeleteView()
        if(a.visible!=visible){
            a.visible = visible
            setConfirmDeleteView(a)
        }
    }
    fun returnConfirmDeleteVisible():Boolean{
        return returnConfirmDeleteView().visible
    }
    fun returnConfirmDeleteWithChildrenVisible():Boolean{
        return returnConfirmDeleteWithChildrenView().visible
    }
    fun setConfirmDeleteWithChildrenVisible(visible: Boolean){
        val a = returnConfirmDeleteWithChildrenView()
        if(a.visible!=visible){
            a.visible = visible
            setConfirmDeleteWithChildrenView(a)
        }

    }


}