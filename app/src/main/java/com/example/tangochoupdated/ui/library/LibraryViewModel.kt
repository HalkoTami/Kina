package com.example.tangochoupdated.ui.library

import androidx.compose.runtime.internal.illegalDecoyCallException
import androidx.lifecycle.*
import androidx.navigation.navArgument
import com.example.tangochoupdated.room.MyRoomRepository
import com.example.tangochoupdated.room.dataclass.CardAndTags
import com.example.tangochoupdated.room.dataclass.File
import com.example.tangochoupdated.room.dataclass.FileWithChild
import com.example.tangochoupdated.room.enumclass.CardStatus
import com.example.tangochoupdated.room.enumclass.FileStatus
import com.example.tangochoupdated.room.rvclasses.LibRVViewType
import com.example.tangochoupdated.room.rvclasses.LibraryRV
import kotlinx.coroutines.cancel

class LibraryViewModel(private val repository: MyRoomRepository) : ViewModel() {

    private val _menuOpened =  MutableLiveData<Boolean>()
    fun setMenuStatus(boolean: Boolean){
        _menuOpened.apply {
            value = boolean
        }
    }
    val menuViewMode:LiveData<Boolean> = _menuOpened


    fun setMultipleSelectMode(boolean: Boolean){
        _multipleSelectMode.apply {
            value = boolean
        }
    }

    private val _multipleSelectMode =  MutableLiveData<Boolean>()
    val multipleSelectMode:LiveData<Boolean> = _multipleSelectMode


    private val parentItemId = MutableLiveData<Int>()
    fun setParentItemId (id:Int?){
        parentItemId.value = id
    }




     private val getCards:LiveData<List<CardAndTags>> =  if(parentItemId.value == null){
         MutableLiveData()
     } else {
         this.repository.getCardDataByFileId(parentItemId.value).asLiveData()
     }


     private val getFiles:LiveData<FileWithChild> = if(parentItemId.value == null){
         MutableLiveData()
     } else {
         this.repository.getFileDataByParentFileId(parentItemId.value).asLiveData()
     }
     private val  fileWithoutParent:LiveData<List<File>> = this.repository.getFileWithoutParent.asLiveData()

    private val  parentFile:LiveData<List<File>> = if(parentItemId.value == null){
        MutableLiveData()
    } else {
        repository.getFileByFileId(parentItemId.value).asLiveData()
    }



    val parentItem :LiveData<List<LibraryRV>> = Transformations.switchMap(parentFile){
        convertDBItemsToLibRV(it,null,null)
    }
    private val _myParentItem = MutableLiveData<List<LibraryRV>>()
    val myParentItem:LiveData<List<LibraryRV>> = _myParentItem
    fun setValueToMyParentItem(item:List<LibraryRV>?){
        _myParentItem.apply {
            value = item
        }
    }

    private val _myFinalList= MutableLiveData<List<LibraryRV>>()
    val myFinalList :LiveData<List<LibraryRV>> = _myFinalList
    fun setValueToFinalList(list:List<LibraryRV>){
        _myFinalList.apply {
            value = list
        }
    }




    val noParents:LiveData<List<LibraryRV>> = Transformations.switchMap(fileWithoutParent){
        convertDBItemsToLibRV(it,null,null)}
    private val _noParents = MutableLiveData<List<LibraryRV>>()
    fun setValueToNoParents(list:List<LibraryRV>){
        list.sortedBy { it.position }
        _noParents.value = list
    }


    val file:LiveData<List<LibraryRV>> = Transformations.switchMap(getFiles){
        convertDBItemsToLibRV(it.childFiles,null,null)
    }
    private val _files = mutableListOf<LibraryRV>()
    fun setValueToFiles(list:List<LibraryRV>){
        _files.addAll(list)
    }

    val card:LiveData<List<LibraryRV>> = Transformations.switchMap(getCards){
        convertDBItemsToLibRV(null,it,null)
    }
    private val _cards =mutableListOf<LibraryRV>()
    fun setValueToCards(list:List<LibraryRV>){
        _cards.addAll(list)
    }


    fun chooseValuesOfFinalList (){
        val mutableList = mutableListOf<LibraryRV>()
        if(parentItemId.value ==null){
            _noParents.value?.onEach { mutableList.add(it) }
            setValueToFinalList(mutableList)
        }
        else{
            mutableList.addAll(_files)
            mutableList.addAll(_cards)
            mutableList.sortBy { it.position }
            setValueToFinalList(mutableList)
        }
    }





    private fun convertFileToLibraryRV(file: File): LibraryRV {

        when (file.fileStatus) {

            FileStatus.FOLDER -> {
                return LibraryRV(
                    type = LibRVViewType.Folder,
                    position = file.libOrder!!,
                    file = file,
                    card = null,
                    tag = null,
                    id = file.fileId,
                    selectable = false,
                    selected = false
                )

            }

            FileStatus.TANGO_CHO_COVER ->
                return LibraryRV(
                    type = LibRVViewType.FlashCardCover,
                    position = file.libOrder!!,
                    file = file,
                    card = null,
                    tag = null,
                    id = file.fileId
                )


            else -> illegalDecoyCallException("unknown class")
        }
    }
    private fun convertCardToLibraryRV(card: CardAndTags): LibraryRV {
        when (card.card.cardStatus) {
            CardStatus.STRING -> return LibraryRV(
                type = LibRVViewType.StringCard,
                position = card.card.libOrder,
                file = null,
                card = card.card,
                tag = card.tags,
                id = card.card.id
            )
            CardStatus.CHOICE -> return LibraryRV(
                type = LibRVViewType.ChoiceCard,
                position = card.card.libOrder,
                file = null,
                card = card.card,
                tag = card.tags,
                id = card.card.id
            )

            CardStatus.MARKER -> return LibraryRV(
                type = LibRVViewType.MarkerCard,
                position = card.card.libOrder,
                file = null,
                card = card.card,
                tag = card.tags,
                id = card.card.id
            )

            else -> illegalDecoyCallException("unknown class")
        }
    }


    private fun convertDBItemsToLibRV(files:List<File>?,cards:List<CardAndTags>?,childFiles:FileWithChild?):MutableLiveData<List<LibraryRV>>{
        val liveD = MutableLiveData<List<LibraryRV>>()
        val finalList = mutableListOf<LibraryRV>()
        files?.onEach {  finalList.add(convertFileToLibraryRV(it))}
        cards?.onEach {  finalList.add(convertCardToLibraryRV(it))}
        childFiles?.childFiles?.onEach{finalList.add(convertFileToLibraryRV(it))}

        liveD.apply {
            value = finalList
        }
        return  liveD
    }




    fun changeAllSelectableState(boolean: Boolean) {
        val a = mutableListOf<LibraryRV>()
        _myFinalList.value?.onEach { it.selectable = boolean
        a.add(it)}
        setValueToFinalList(a)

    }
    fun changeSelectedState(boolean: Boolean,position: Int) {
        val a = mutableListOf<LibraryRV>()
        _myFinalList.value?.onEach { if(it.position == position){
            it.selected = boolean
        }
        a.add(it)}
        setValueToFinalList(a)

    }





    private val _selectedItems = MutableLiveData<List<LibraryRV>>()
    val selectedItems:LiveData<List<LibraryRV>> = _selectedItems
    fun setValueToSelectedItem(list: List<LibraryRV>){
        val a = mutableListOf<LibraryRV>()
        list.onEach { if(it.selected){
            a.add(it)
        }
        }
        _selectedItems.value = a
    }

    fun topBarText():String{
        var a = "A"

        return a
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }

}