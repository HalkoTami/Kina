package com.example.tangochoupdated.ui.library

import androidx.lifecycle.*
import com.example.tangochoupdated.room.MyRoomRepository
import com.example.tangochoupdated.room.dataclass.CardAndTags
import com.example.tangochoupdated.room.dataclass.File
import com.example.tangochoupdated.room.enumclass.CardStatus
import com.example.tangochoupdated.room.enumclass.FileStatus
import com.example.tangochoupdated.room.rvclasses.LibRVViewType
import com.example.tangochoupdated.room.rvclasses.LibraryRV
import kotlinx.coroutines.*


class LibraryRVViewModel(private val repository: MyRoomRepository) : ViewModel(){
    var fileId:Int = 0
    val parentList:LiveData<List<File>> = repository.getFileWithoutParent().asLiveData()
    fun  getSpecifiedFiles(fileId:Int):LiveData<List<File>> =  repository.getFileDataByFileId(fileId!!).asLiveData()

    fun getSpecifiedCards(fileId: Int):LiveData<List<CardAndTags>> = repository.getCardDataByFileId(fileId).asLiveData()
    private val _listData = MutableLiveData<List<LibraryRV>>().apply{


    }
    val listData: LiveData<List<LibraryRV>> = _listData

    private fun makeLibRVList(filelist:List<File>?,cardlist:List<CardAndTags>?):List<LibraryRV>{

        val a = mutableListOf<LibraryRV>()
        filelist?.onEach { a.add(convertFileToLibraryRV(it)!!) }
        cardlist?.onEach { a.add(convertCardToLibraryRV(it)!!) }
        return a


    }

    private fun convertFileToLibraryRV(file: File?): LibraryRV? {

        when (file!!.fileStatus) {

            FileStatus.FOLDER -> {
                return LibraryRV(
                    type = LibRVViewType.Folder,
                    position = file.libOrder,
                    file = file,
                    card = null,
                    tag = null,
                    id = file.fileId
                )

            }

            FileStatus.TANGO_CHO_COVER ->
                return LibraryRV(
                    type = LibRVViewType.FlashCardCover,
                    position = file.libOrder,
                    file = file,
                    card = null,
                    tag = null,
                    id = file.fileId
                )


            else -> return null
        }

    }
    fun convertCardToLibraryRV(card: CardAndTags?): LibraryRV? {
        when (card?.card?.cardStatus) {
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

            else -> return null
        }
    }

    private fun fetchData(a:ArrayList<LibraryRV>){
        list = a
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }
    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    var list = listOf<LibraryRV>()
}
class ViewModelFactory(private val repository: MyRoomRepository) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LibraryRVViewModel(repository) as T
    }
}