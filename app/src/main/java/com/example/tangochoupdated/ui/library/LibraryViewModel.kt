package com.example.tangochoupdated.ui.library

import android.widget.Switch
import androidx.compose.runtime.internal.illegalDecoyCallException
import androidx.lifecycle.*
import com.example.tangochoupdated.room.MyRoomRepository
import com.example.tangochoupdated.room.dataclass.Card
import com.example.tangochoupdated.room.dataclass.CardAndTags
import com.example.tangochoupdated.room.dataclass.File
import com.example.tangochoupdated.room.dataclass.FileWithChild
import com.example.tangochoupdated.room.enumclass.CardStatus
import com.example.tangochoupdated.room.enumclass.FileStatus
import com.example.tangochoupdated.room.rvclasses.LibRVViewType
import com.example.tangochoupdated.room.rvclasses.LibraryRV
import kotlinx.coroutines.cancel

class LibraryViewModel(private val repository: MyRoomRepository) : ViewModel() {

    val menuOpened =  MutableLiveData<Boolean>()
    val multipleSelectMode =  MutableLiveData<Boolean>()




     fun getCards(id:Int):LiveData<List<CardAndTags>>? =  this.repository.getCardDataByFileId(id).asLiveData()
     fun getFiles(id:Int):LiveData<FileWithChild>? = this.repository.getFileDataByParentFileId(id).asLiveData()
     fun fileWithoutParent():LiveData<List<File>> = this.repository.getFileWithoutParent.asLiveData()
     fun parentFile(id:Int):LiveData<File> = repository.getFileByFileId(id).asLiveData()
     fun parentItem(id:Int):LiveData<LibraryRV> = Transformations.switchMap(parentFile(id)){file ->
         convertParentFileToItem(file)
     }

    val finalList= MediatorLiveData<List<LibraryRV>>()

    fun convertParentFileToItem(file: File):LiveData<LibraryRV>{
        val a = MutableLiveData<LibraryRV>()
        val b = convertFileToLibraryRV(file)

        a.apply {
            value = b
        }
        return a

    }

    fun convertFileToLibraryRV(file: File): LibraryRV {

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
    fun convertCardToLibraryRV(card: CardAndTags): LibraryRV {
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


    fun convertDBItemsToLibRV(files:List<File>?,cards:List<CardAndTags>?,childFiles:FileWithChild?):MutableLiveData<List<LibraryRV>>{
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

    fun file(id:Int):LiveData<List<LibraryRV>> = Transformations.switchMap(getFiles(id)!!){
        convertDBItemsToLibRV(it.childFiles,null,null)
    }
    fun card(id:Int):LiveData<List<LibraryRV>> = Transformations.switchMap(getCards(id)!!){
        convertDBItemsToLibRV(null,it,null)
    }
    val noParents:LiveData<List<LibraryRV>> = Transformations.switchMap(fileWithoutParent()){list->
        convertDBItemsToLibRV(list,null,null)
    }

    fun getFinalList(id:Int?):MediatorLiveData<List<LibraryRV>>{





        val a = MediatorLiveData<List<LibraryRV>>()
        if (id!=null){


                a.addSource(file(id)){
                    a.value = it
                }
                a.addSource(card(id)){
                    a.value = it
                }

        } else{
            a.addSource(noParents){
                a.value = it
            }
        }

        return a

    }
    fun makeAllSelectable(id:Int?) {
        getFinalList(id).apply {
            value?.onEach { it.selectable = true }
        }

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