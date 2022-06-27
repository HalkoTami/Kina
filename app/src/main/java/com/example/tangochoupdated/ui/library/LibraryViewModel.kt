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

class LibraryViewModel(private val repository: MyRoomRepository) : ViewModel() {
    val parentItem = MutableLiveData<LibraryRV>()
    val menuOpened =  MutableLiveData<Boolean>()
    val multipleSelectMode =  MutableLiveData<Boolean>()




    private fun getCards(parentFileId: Int) =  repository.getCardDataByFileId(parentFileId).asLiveData()
    private fun getFiles(parentFileId: Int?) = repository.getFileDataByParentFileId(parentFileId).asLiveData()
    val fileWithoutParent = repository.getFileWithoutParent().asLiveData()


    fun convertFileToLibraryRV(file: File): LibraryRV {

        when (file.fileStatus) {

            FileStatus.FOLDER -> {
                return LibraryRV(
                    type = LibRVViewType.Folder,
                    position = file.libOrder!!,
                    file = file,
                    card = null,
                    tag = null,
                    id = file.fileId
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


    private fun convertDBItemsToLibRV(item:List<Any>?):LiveData<List<LibraryRV>>{
        val finalList = mutableListOf<LibraryRV>()
        item?.onEach { when (it ){
            is File -> finalList.add(convertFileToLibraryRV(it))
            is FileWithChild -> it.childFiles.onEach { finalList.add(convertFileToLibraryRV(it)) }
            is CardAndTags -> finalList.add(convertCardToLibraryRV(it))
        } }
        val liveD = MutableLiveData<List<LibraryRV>>()
        liveD.apply {
            value = finalList
        }
        return  liveD
    }

    fun getFinalList(id:Int?):LiveData<List<LibraryRV>?>{
        val a = MediatorLiveData<List<LibraryRV>>()
        if (id!=null){
            val file = Transformations.switchMap(getFiles(id)){
                convertDBItemsToLibRV(it)
            }
            val card = Transformations.switchMap(getCards(id)){
                convertDBItemsToLibRV(it)
            }
            a.apply {
                addSource(file){
                    a.value = it
                }
                addSource(card){
                    a.value = it
                }
            }
        } else{
            val noParents = Transformations.switchMap(fileWithoutParent){
                convertDBItemsToLibRV(it)
            }
            a.addSource(noParents){
                a.value = it
            }
        }

        return a

    }



    val selectedItems = MutableLiveData<List<LibraryRV>>()

    fun topBarText():String{
        var a = "A"

        return a
    }


}