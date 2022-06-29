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
    fun setMenuView(boolean: Boolean){
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


    private val parentItemId = MutableLiveData<Int>().apply {
        value = null
    }
    fun setParentItemId (id:Int?){
        parentItemId.value = null
    }




     private val getCards:LiveData<List<CardAndTags>> =  this.repository.getCardDataByFileId(parentItemId.value).asLiveData()
     private val getFiles:LiveData<FileWithChild> = this.repository.getFileDataByParentFileId(parentItemId.value).asLiveData()
     private val  fileWithoutParent:LiveData<List<File>> = this.repository.getFileWithoutParent.asLiveData()
     private val  parentFile:LiveData<File> = if(parentItemId.value == null){
        MutableLiveData()
     } else {
         repository.getFileByFileId(parentItemId.value!!.toInt()).asLiveData()
     }
//     fun parentItem(id:Int?):LiveData<LibraryRV?> {
//         return if(id==null){
//             val a = MutableLiveData<LibraryRV?>()
//             a.apply {
//                 value = null
//             }
//             a
//         } else{
//
//             val a = Transformations.switchMap(parentFile(id)){file ->
//                 convertParentFileToItem(file) }
//             a
//         }
//     }


    private val _parentItem :LiveData<LibraryRV> = Transformations.switchMap(parentFile){
        convertParentFileToItem(it)
    }
    val myParentItem : LiveData<LibraryRV> = _parentItem
    val noParents:LiveData<List<LibraryRV>> = Transformations.switchMap(fileWithoutParent){
         convertDBItemsToLibRV(it,null,null)}

    private val _myFinalList= MutableLiveData<List<LibraryRV>>()
    val myFinalList :LiveData<List<LibraryRV>> = _myFinalList
    fun setValueToFinalList(list:List<LibraryRV>){
        _myFinalList.apply {
            value = list
        }
    }
    val file:LiveData<List<LibraryRV>> = Transformations.switchMap(getFiles){
        convertDBItemsToLibRV(it.childFiles,null,null)
    }
    val card:LiveData<List<LibraryRV>> = Transformations.switchMap(getCards){
        convertDBItemsToLibRV(null,it,null)
    }
    private val mutableList = mutableListOf<LibraryRV>()
    fun addValue (list: List<LibraryRV>){

        list.onEach { mutableList.add(it) }
        setValueToFinalList(mutableList)

    }
    private fun convertParentFileToItem(file: File):LiveData<LibraryRV>{
        val a = MutableLiveData<LibraryRV>()
        val b = convertFileToLibraryRV(file)

        a.apply {
            value = b
        }
        return a

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




    fun makeAllSelectable() {
        val a = mutableListOf<LibraryRV>()
        _myFinalList.value?.onEach { it.selectable =true
        a.add(it)}
        setValueToFinalList(a)

    }
    fun makeSelected(position:Int){
        _myFinalList.value?.get(position)?.selected = true
    }
    fun makeUnselected(position: Int){
        _myFinalList.value?.get(position)?.selected = false
    }



    val selectedItems = MutableLiveData<List<LibraryRV>>()

    fun topBarText():String{
        var a = "A"

        return a
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }

}