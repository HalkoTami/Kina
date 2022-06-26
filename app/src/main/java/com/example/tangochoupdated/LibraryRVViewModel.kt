package com.example.tangochoupdated

import androidx.compose.runtime.internal.illegalDecoyCallException
import androidx.lifecycle.*
import com.example.tangochoupdated.room.MyRoomRepository
import com.example.tangochoupdated.room.dataclass.CardAndTags
import com.example.tangochoupdated.room.dataclass.File
import com.example.tangochoupdated.room.dataclass.FileWithChild
import com.example.tangochoupdated.room.enumclass.CardStatus
import com.example.tangochoupdated.room.enumclass.ColorStatus
import com.example.tangochoupdated.room.enumclass.FileStatus
import com.example.tangochoupdated.room.enumclass.Tab
import com.example.tangochoupdated.room.rvclasses.LibRVViewType
import com.example.tangochoupdated.room.rvclasses.LibraryRV
import com.example.tangochoupdated.ui.library.LibraryViewModel
import kotlinx.coroutines.*



class BaseViewModel(private val repository: MyRoomRepository):ViewModel(){
    var fileId:Int? = null
    val createFragmentActive = MutableLiveData<Boolean>()
    fun changeCreateFragmentStatus(active:Boolean){
        createFragmentActive.value = active
    }
    val activeTab = MutableLiveData<Tab>()
    fun changeActiveTab (tab:Tab){
        activeTab.apply {
            value = tab
        }
    }


    fun  getListData(fileList: List<File>?, cardList: List<CardAndTags>?,childFiles:List<FileWithChild>?) :LiveData<List<LibraryRV>>{
        val liveD = MutableLiveData<List<LibraryRV>>()
        val a = mutableListOf<LibraryRV>()
        fileList?.onEach { a.add(convertFileToLibraryRV(it)!!) }
        cardList?.onEach { a.add(convertCardToLibraryRV(it)!!) }
        childFiles?.onEach { file -> file.childFiles.onEach { a.add(convertFileToLibraryRV(it)!!) } }
        a.sortBy { it.position }
        liveD.apply {
            value = a
        }

        return liveD


    }

    val parentList:LiveData<List<File>> = repository.getFileWithoutParent().asLiveData()
    val childFiles:LiveData<List<FileWithChild>> = getFileFromParentFile()
    val childCards:LiveData<List<CardAndTags>> = getCardsFromFileId()
    private fun getFileFromParentFile():LiveData<List<FileWithChild>>{
        return if (fileId!=null){
            return repository.getFileDataByFileId(fileId!!).asLiveData()
        } else MutableLiveData()

    }
    private fun getCardsFromFileId():LiveData<List<CardAndTags>> {
        return if (fileId!=null){
            repository.getCardDataByFileId(fileId!!).asLiveData()
        } else MutableLiveData()


    }
    val liveDNoParents:LiveData<List<LibraryRV>> =  Transformations.switchMap(parentList)
    {
            list -> getListData(list, null,null)
    }


    val liveDChildFiles:LiveData<List<LibraryRV>> = Transformations.switchMap(childFiles){
            list -> getListData(null,null,list)
    }
    val liveDChildCards:LiveData<List<LibraryRV>> = Transformations.switchMap(childCards){
            list -> getListData(null,list,null)
    }
    fun finalList():MediatorLiveData<List<LibraryRV>>{
        val a = MediatorLiveData<List<LibraryRV>>()
        if(fileId!=null){
            a.addSource(liveDChildFiles){
                a.value = it
            }
            a.addSource(liveDChildCards){
                a.value = it
            }
        } else {
            a.addSource(liveDNoParents){
                a.value = it
            }
        }
        return a
    }



    fun  getSpecifiedFiles(fileId:Int):LiveData<List<FileWithChild>> =  repository.getFileDataByFileId(fileId!!).asLiveData()





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
        val a = when(modelClass ){
            BaseViewModel::class.java-> BaseViewModel(repository)
            LibraryViewModel::class.java -> LibraryViewModel(repository)
            else -> throw illegalDecoyCallException("unknown ViewModel class")
        }
        return a as T

    }
}
