package com.example.tangochoupdated.ui.library

import androidx.compose.runtime.internal.illegalDecoyCallException
import androidx.lifecycle.*
import com.example.tangochoupdated.R
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
import kotlinx.coroutines.launch

class LibraryViewModel(private val repository: MyRoomRepository) : ViewModel() {



//    topBarText

    private val _topText = MutableLiveData<String>()
    private fun setMLDTopText(text:String){
        _topText.value= text

    }
    private fun upDateTopText(){
        val a:String = when (_multipleSelectMode.value) {
            true -> {
                "${_selectedItems.value?.size.toString()}個　選択中"
            }
            else -> {
                when (_homeStatus.value) {
                    true -> "home"
                    else -> _myParentItem.value?.file?.title.toString()
                }
            }
        }
        setMLDTopText(a)

    }
    val topText:LiveData<String> = _topText

//    Top Left Icon

    private val _topBarLeftIMVDrawableId = MutableLiveData<Int>()
    private fun setTopBarLeftIMVDrawableId(int: Int){
        _topBarLeftIMVDrawableId.value= int

    }
    private fun upDateTopBarLeftIMVDrawableId(){
        val a:Int
        when (_multipleSelectMode.value) {
            true -> {
                a = R.drawable.icon_close
            }
            else -> {
                a = when(_homeStatus.value){
                    true,null->{
                        R.drawable.icon_eye_opened
                    }
                    false-> {
                        when(_myParentItem.value!!.type){
                            LibRVViewType.FlashCardCover -> R.drawable.icon_library
                            LibRVViewType.Folder -> R.drawable.icon_file
                            else -> throw IllegalArgumentException("upDateTopBarLeft() unknown Type")
                        }
                    }
                }

            }
        }
        setTopBarLeftIMVDrawableId(a)

    }
    val topBarLeftIMVDrawableId:LiveData<Int> = _topBarLeftIMVDrawableId
//multiSelectMode

    fun setMultipleSelectMode(boolean: Boolean){
        _multipleSelectMode.apply {
            value = boolean
        }
        upDateTopText()
        upDateTopBarLeftIMVDrawableId()
        if(_homeStatus.value == true){
            upDateTopBarRightIMVDrawableId()
        }
        changeAllSelectableState(boolean)

    }

    private val _multipleSelectMode =  MutableLiveData<Boolean>()

    //    onCLick Left Icon done
    fun onClickLeftIcon(){
        when(_multipleSelectMode.value){
            true -> setMultipleSelectMode(false)
            else -> return
        }
    }

//    top right imv
    private val _topBarRightIMVDrawableId = MutableLiveData<Int>()
    private fun setTopBarRightIMVDrawableId(int: Int){
        _topBarRightIMVDrawableId.value= int

    }
    private fun upDateTopBarRightIMVDrawableId(){
        val a:Int = when (_multipleSelectMode.value) {
            true -> {
                R.drawable.icon_dot
            }
            else -> {
                when(_homeStatus.value){
                    true->{
                        R.drawable.icon_inbox
                    }
                    false,null -> R.drawable.icon_dot
                }

            }
        }
        setTopBarRightIMVDrawableId(a)

    }
    val topBarRightIMVDrawableId:LiveData<Int> = _topBarRightIMVDrawableId

//    top right IMV click

    fun topBarRightIMVOnClick(){
        when (_menuOpened.value) {
            true -> setMenuStatus(false)
            false -> setMenuStatus(true)
            else -> throw IllegalArgumentException("is null")

        }

    }
    //    home or not done
    private val _homeStatus =  MutableLiveData<Boolean>()
    private fun setHomeStatus(boolean: Boolean){
        _homeStatus.apply {
            value = boolean
        }
        upDateTopText()
        upDateTopBarLeftIMVDrawableId()
        upDateTopBarRightIMVDrawableId()
        upDateFinalList()
        upDateFileEmptyText()

    }
    private fun upDateHomeStatus(){
        when(_myParentItem.value){
            null-> {
                setHomeStatus(true)
            }
            else -> {
                setHomeStatus(false)
            }
        }

    }

    //    RVList
    private val _myFinalList= MutableLiveData<List<LibraryRV>>()
    val myFinalList :LiveData<List<LibraryRV>> = _myFinalList
    private fun setValueToFinalList(list:List<LibraryRV>){
        _myFinalList.apply {
            value = list
        }

        upDateSelectedItem()
        upDateTopText()
        upDateFileEmptyStatus()

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
        private fun upDateFinalList (){
        val mutableList = mutableListOf<LibraryRV>()
        when(_homeStatus.value){
            true -> {
                _fileWithoutParentFromDB.value?.onEach {
                    mutableList.add(convertFileToLibraryRV(it)) }
            }
            false -> {
                _childFilesFromDB.value?.childFiles?.onEach {
                    mutableList.add(convertFileToLibraryRV(it))
                }
                _childCardsFromDB.value?.onEach {
                    mutableList.add(convertCardToLibraryRV(it))
                }
            }
            null -> throw IllegalArgumentException("homeStatus is null")

        }
        setValueToFinalList(mutableList)
    }
//    file is  empty or not done
    private val _fileEmptyStatus =  MutableLiveData<Boolean>()
    private fun setFileEmptyStatus(boolean: Boolean){
        _fileEmptyStatus.apply {
            value = boolean
        }
        upDateFileEmptyText()
    }
    private fun upDateFileEmptyStatus(){
        when(_myFinalList.value?.size){
            0-> {
                setFileEmptyStatus(true)
            }
            else -> setFileEmptyStatus(false)
        }
    }
    val fileEmptyStatus:LiveData<Boolean> = _fileEmptyStatus



// text in the middle when empty done

    private val _fileEmptyText =  MutableLiveData<String>()
    private fun setFileEmptyText(string: String){
        _fileEmptyText.apply {
            value = string
        }
    }
    private fun upDateFileEmptyText(){
        when(_fileEmptyStatus.value){
            true->{
                when(_homeStatus.value){
                true ->setFileEmptyText("まだデータがありません")
                false -> setFileEmptyText("${_myParentItem.value?.file?.title}は空です")
                    null -> throw IllegalArgumentException("home status not set yet and is null")
                }
            }
            false,null -> return
        }

    }
    val fileEmptyText:LiveData<String> = _fileEmptyText


//menu Visibility done
    private val _menuOpened =  MutableLiveData<Boolean>()
    fun setMenuStatus(boolean: Boolean){
        _menuOpened.apply {
            value = boolean
        }
    }
    val menuViewMode:LiveData<Boolean> = _menuOpened



//    parentItemId

    private val _parentItemId = MutableLiveData<Int>()
    fun setParentItemId (id:Int?){
        _parentItemId.value = id


    }





//    child Files from DB
     val childFilesFromDB:LiveData<FileWithChild?> = this.repository.getFileDataByParentFileId(_parentItemId.value).asLiveData()
     private val _childFilesFromDB = MutableLiveData<FileWithChild>()
    fun setChildFilesFromDB (list:FileWithChild?){
        val a = mutableListOf<FileWithChild>()
        if(list!= null){
            a.add(list)
        }
        _childFilesFromDB.value = list


    }
//    child Cards From DB

    val childCardsFromDB:LiveData<List<CardAndTags>?> =  this.repository.getCardDataByFileId(_parentItemId.value).asLiveData()
    private val _childCardsFromDB=MutableLiveData<List<CardAndTags>>()
    fun setChildCardsFromDB(list:List<CardAndTags>?){
        val a = mutableListOf<CardAndTags>()
        if(list!= null){
            a.addAll(list)
        }
        _childCardsFromDB.value = list
    }



//file without parent from DB

    val  fileWithoutParentFromDB:LiveData<List<File>?> = this.repository.getFileWithoutParent.asLiveData()
    private val _fileWithoutParentFromDB= MutableLiveData<List<File>>()

    fun setFileWithoutParentFromDB(list:List<File>?){
        val a = mutableListOf<File>()
        if(list!= null){
            a.addAll(list)
        }
        _fileWithoutParentFromDB.value = list

    }


//    parent File from DB
    fun  parentFileFromDB(id: Int?):LiveData<File?> = repository.getFileByFileId(id).asLiveData()

    fun setParentFileFromDB (file: File?){
        when(file){
            null -> setMyParentItem(null)
            else ->{
                val a = convertFileToLibraryRV(file)
                setMyParentItem(a)
            }
        }
    }

//    parent item as Library RV

    private val _myParentItem = MutableLiveData<LibraryRV?>()




    private fun setMyParentItem(item:LibraryRV?){
        _myParentItem.apply {
            value = item
        }
        upDateHomeStatus()
        upDateTopText()

        upDateTopBarLeftIMVDrawableId()
        upDateFileEmptyText()
        upDateTopBarRightIMVDrawableId()
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










//selected Items

    private val _selectedItems = MutableLiveData<List<LibraryRV>>()
    private fun setSelectedItem(list:List<LibraryRV>){
        _selectedItems.value = list
    }
    private fun upDateSelectedItem(){
        val a = mutableListOf<LibraryRV>()
        _myFinalList.value?.onEach { if(it.selected){
            a.add(it)
        }
        }
        setSelectedItem(a)
    }




//    clickEvents

    fun onDelete(){
        when(_multipleSelectMode.value){
            true -> deleteSelectedItems()
            false -> deleteParentItem()
            null -> throw IllegalArgumentException("multipleSelectMode not set yet ")
        }
        setMultipleSelectMode(false)
        setMenuStatus(false)
    }
    private fun deleteParentItem(){
        val file = mutableListOf<File>()
        val cards = mutableListOf<Card>()
        _myParentItem.value?.apply {
            when(this.type){
                LibRVViewType.Folder,LibRVViewType.FlashCardCover -> {
                    this.file!!.deleted = true
                    file.add(this.file)

                }
                else -> {
                    this.card!!.deleted = true
                    cards.add(this.card)

                }
            }
        }
        updateMultiple(file)
        updateMultiple(cards)

    }
    private fun deleteSelectedItems(){
        val file = mutableListOf<File>()
        val cards = mutableListOf<Card>()
        _selectedItems.value?.onEach {
            when(it.type){
                LibRVViewType.Folder,LibRVViewType.FlashCardCover -> {
                    it.file!!.deleted = true
                    file.add(it.file)
                }
                else -> {
                    it.card!!.deleted = true
                    cards.add(it.card)

                }
            }

        }
        updateMultiple(file)
        updateMultiple(cards)


    }
    private fun updateMultiple(list:List<Any>){
        if (list.isNotEmpty()){
            viewModelScope.launch {
                repository.updateMultiple(list)
            }
        }

    }


    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }

}