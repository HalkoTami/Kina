package com.korokoro.kina.ui.viewmodel

import androidx.lifecycle.*
import com.korokoro.kina.db.MyRoomRepository
import com.korokoro.kina.db.dataclass.Card
import com.korokoro.kina.db.dataclass.File
import com.korokoro.kina.db.enumclass.FileStatus
import kotlinx.coroutines.launch

class DeletePopUpViewModel(private val repository: MyRoomRepository) : ViewModel() {
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
    private fun deleteSingleFile(file: File, deleteChildren:Boolean){
        viewModelScope.launch {
            if(!deleteChildren){
                repository.upDateChildFilesOfDeletedFile(file.fileId,file.parentFileId)
                repository.delete(file)
                setToastText("${file.title}を削除しました")
                makeToastVisible()
            } else {
                repository.deleteFileAndAllDescendants(file.fileId)
                setToastText("${file.title}と中身を削除しました")
                makeToastVisible()
            }
        }

    }
    private fun deleteMultipleFiles(file: List<File>, deleteChildren:Boolean){
        file.onEach { deleteSingleFile(it,deleteChildren) }
        setToastText("選択中のアイテムを削除しました")
        makeToastVisible()
    }
    private fun deleteCards(cards: List<Card>, ){
        viewModelScope.launch {
            repository.deleteMultiple(cards)
        }
        setToastText("${cards.size}枚のカードを削除しました")
        makeToastVisible()
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



    val deletingItemChildrenCards:LiveData<List<Card>?> = _deletingItemChildrenCards

    fun checkDeletingItemsHasChildren():Boolean{
        return returnDeletingItemChildrenCards().isEmpty().not()||returnDeletingItemChildrenFiles().isEmpty().not()
    }
    fun deleteOnlyFile(){
        if(returnDeletingItems().size == 1){
            deleteSingleFile(returnDeletingItems().single() as File,false)
        } else {
            deleteMultipleFiles(returnDeletingItems() as List<File>, false)
        }
//        returnDeletingItems().onEach {
//            deleteSingleFile(it as File,false)
//        }
        setConfirmDeleteVisible(false)
        setConfirmDeleteWithChildrenVisible(false)
    }
    fun deleteCard(){
        val a =returnDeletingItems()
        val cards:List<Card> = a.filterIsInstance<Card>()
        deleteCards(cards)
    }
    fun deleteFileWithChildren(){
        returnDeletingItems().onEach {
            deleteSingleFile(it as File,true)
        }
        setConfirmDeleteWithChildrenVisible(false)
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

    fun setConfirmDeleteVisible(visible: Boolean,){
        val a = returnConfirmDeleteView()
        if(a.visible!=visible){
            a.visible = visible
            setConfirmDeleteView(a)
        }

    }
    fun setConfirmDeleteWithChildrenVisible(visible: Boolean,){
        val a = returnConfirmDeleteWithChildrenView()
        if(a.visible!=visible){
            a.visible = visible
            setConfirmDeleteWithChildrenView(a)
        }

    }


}