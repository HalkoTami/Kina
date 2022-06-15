package com.example.tangochoupdated.ui.library

import androidx.lifecycle.*
import com.example.tangochoupdated.room.MyRoomRepository
import com.example.tangochoupdated.room.dataclass.CardAndTags
import com.example.tangochoupdated.room.dataclass.File
import com.example.tangochoupdated.room.dataclass.FileWithChild
import com.example.tangochoupdated.room.enumclass.CardStatus
import com.example.tangochoupdated.room.enumclass.ColorStatus
import com.example.tangochoupdated.room.enumclass.FileStatus
import com.example.tangochoupdated.room.rvclasses.LibRVViewType
import com.example.tangochoupdated.room.rvclasses.LibraryRV
import kotlinx.coroutines.*


//class LibraryRVViewModel(private val repository: MyRoomRepository) : BaseViewModel(repository){
//    var fileId:Int = 0
//    fun  getListData(fileList: List<File>?, cardList: List<CardAndTags>?) :LiveData<List<LibraryRV>>{
//        val liveD = MutableLiveData<List<LibraryRV>>()
//        val a = mutableListOf<LibraryRV>()
//        fileList?.onEach { a.add(convertFileToLibraryRV(it)!!) }
//        cardList?.onEach { a.add(convertCardToLibraryRV(it)!!) }
//        liveD.apply {
//            value = a
//        }
//
//        return liveD
//
//
//    }
//
//    val parentList:LiveData<List<File>> = repository.getFileWithoutParent().asLiveData()
//    val finalList:LiveData<List<LibraryRV>> = Transformations.switchMap(parentList){
//        list -> getListData(list,null)
//    }
//
//    fun  getSpecifiedFiles(fileId:Int):LiveData<List<File>> =  repository.getFileDataByFileId(fileId!!).asLiveData()
//
//    fun getSpecifiedCards(fileId: Int):LiveData<List<CardAndTags>> = repository.getCardDataByFileId(fileId).asLiveData()
//
//
//
//    fun convertFileToLibraryRV(file: File?): LibraryRV? {
//
//        when (file!!.fileStatus) {
//
//            FileStatus.FOLDER -> {
//                return LibraryRV(
//                    type = LibRVViewType.Folder,
//                    position = file.libOrder!!,
//                    file = file,
//                    card = null,
//                    tag = null,
//                    id = file.fileId
//                )
//
//            }
//
//            FileStatus.TANGO_CHO_COVER ->
//                return LibraryRV(
//                    type = LibRVViewType.FlashCardCover,
//                    position = file.libOrder!!,
//                    file = file,
//                    card = null,
//                    tag = null,
//                    id = file.fileId
//                )
//
//
//            else -> return null
//        }
//
//    }
//    fun convertCardToLibraryRV(card: CardAndTags?): LibraryRV? {
//        when (card?.card?.cardStatus) {
//            CardStatus.STRING -> return LibraryRV(
//                type = LibRVViewType.StringCard,
//                position = card.card.libOrder,
//                file = null,
//                card = card.card,
//                tag = card.tags,
//                id = card.card.id
//            )
//            CardStatus.CHOICE -> return LibraryRV(
//                type = LibRVViewType.ChoiceCard,
//                position = card.card.libOrder,
//                file = null,
//                card = card.card,
//                tag = card.tags,
//                id = card.card.id
//            )
//
//            CardStatus.MARKER -> return LibraryRV(
//                type = LibRVViewType.MarkerCard,
//                position = card.card.libOrder,
//                file = null,
//                card = card.card,
//                tag = card.tags,
//                id = card.card.id
//            )
//
//            else -> return null
//        }
//    }
//
//
//
//    override fun onCleared() {
//        super.onCleared()
//        viewModelScope.cancel()
//    }
//    /**
//     * Launching a new coroutine to insert the data in a non-blocking way
//     */
//    var list = listOf<LibraryRV>()
//}

class BaseViewModel(private val repository: MyRoomRepository):ViewModel(){
    var fileId:Int = 0
    fun  getListData(fileList: List<File>?, cardList: List<CardAndTags>?) :LiveData<List<LibraryRV>>{
        val liveD = MutableLiveData<List<LibraryRV>>()
        val a = mutableListOf<LibraryRV>()
        fileList?.onEach { a.add(convertFileToLibraryRV(it)!!) }
        cardList?.onEach { a.add(convertCardToLibraryRV(it)!!) }
        liveD.apply {
            value = a
        }

        return liveD


    }

    val parentList:LiveData<List<File>> = repository.getFileWithoutParent().asLiveData()
    val finalList:LiveData<List<LibraryRV>> = Transformations.switchMap(parentList){
            list -> getListData(list,null)
    }

    fun  getSpecifiedFiles(fileId:Int):LiveData<List<FileWithChild>> =  repository.getFileDataByFileId(fileId!!).asLiveData()

    fun getSpecifiedCards(fileId: Int):LiveData<List<CardAndTags>> = repository.getCardDataByFileId(fileId).asLiveData()



    fun convertFileToLibraryRV(file: File?): LibraryRV? {

        when (file!!.fileStatus) {

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



    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }
    var parentFileId:Int = 0
    var hasParent:Boolean = false
    var libOrder:Int? = null
    var fileStatus:FileStatus? = null
    var title:String = ""

    fun insertFile(){
        if(!hasParent){
            parentFileId = 0
            libOrder = finalList.value!!.size
        }
        val a = File(
        fileId = 0,
        title = title,
        colorStatus=  ColorStatus.RED,
        fileStatus = fileStatus!!,
        hasChild = false,
        hasParent = hasParent,
        libOrder = libOrder,
        )
        insert(a)

    }

     fun insert(item: Any) = viewModelScope.launch {
        repository.insert(item)
    }


}
class ViewModelFactory(private val repository: MyRoomRepository) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return BaseViewModel(repository) as T
    }
}