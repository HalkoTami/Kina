package com.example.tangochoupdated.ui.library

import android.view.View
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
        val a:String
        when{
            _multipleSelectMode.value == true -> {
                a = "${_selectedItems.value?.size.toString()}個　選択中"
            }
            else -> {
                when {
                    _parentItemId.value == null -> a = "home"
                    else -> a = _myParentItem.value?.file?.title.toString()
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
                a = when(_parentItemId.value){
                    null->{
                        R.drawable.icon_eye_opened
                    }
                    else -> {
                        when(_myParentItem.value?.type){
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

//    onCLick Left Icon
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
                when(_parentItemId.value){
                    null->{
                        R.drawable.icon_inbox
                    }
                    else -> R.drawable.icon_dot
                }

            }
        }
        setTopBarRightIMVDrawableId(a)

    }
    val topBarRightIMVDrawableId:LiveData<Int> = _topBarRightIMVDrawableId

//    top right IMV click

    fun topBarRightIMVOnClick(){
        val a:Unit = when (_menuOpened.value) {
            true -> setMenuStatus(false)
            false -> setMenuStatus(true)
            else -> throw IllegalArgumentException("is null")

        }

    }

//    RVList
    private val _myFinalList= MutableLiveData<List<LibraryRV>>()
    val myFinalList :LiveData<List<LibraryRV>> = _myFinalList
    private fun setValueToFinalList(list:List<LibraryRV>){
        _myFinalList.apply {
            value = list
        }

        setValueToSelectedItem()
        upDateTopText()
    }



//menu Visibility
    private val _menuOpened =  MutableLiveData<Boolean>()
    fun setMenuStatus(boolean: Boolean){
        _menuOpened.apply {
            value = boolean
        }
    }
    val menuViewMode:LiveData<Boolean> = _menuOpened

//multiSelectMode

    fun setMultipleSelectMode(boolean: Boolean){
        _multipleSelectMode.apply {
            value = boolean
        }
        upDateTopText()
        upDateTopBarLeftIMVDrawableId()
        if(_parentItemId.value == null){
            upDateTopBarRightIMVDrawableId()
        }
    }

    private val _multipleSelectMode =  MutableLiveData<Boolean>()
    val multipleSelectMode:LiveData<Boolean> = _multipleSelectMode


    private val _parentItemId = MutableLiveData<Int>()
    fun setParentItemId (id:Int?){
        _parentItemId.value = id
        upDateTopText()
        upDateTopBarLeftIMVDrawableId()
        upDateTopBarRightIMVDrawableId()
    }




     private val getCards:LiveData<List<CardAndTags>> =  if(_parentItemId.value == null){
         MutableLiveData()
     } else {
         this.repository.getCardDataByFileId(_parentItemId.value).asLiveData()
     }


     private val getFiles:LiveData<FileWithChild> = if(_parentItemId.value == null){
         MutableLiveData()
     } else {
         this.repository.getFileDataByParentFileId(_parentItemId.value).asLiveData()
     }
     val  fileWithoutParent:LiveData<List<File>> = this.repository.getFileWithoutParent.asLiveData()
    private val _fileWithoutParent= MutableLiveData<List<File>>()

    fun setValueToFileWithoutParent(list:List<File>){
        _fileWithoutParent.value = list
        val a = mutableListOf<LibraryRV>()
        list.onEach { a.add(convertFileToLibraryRV(it)) }
        setValueToNoParents(a)
    }
    val  parentFile:LiveData<File> = if(_parentItemId.value == null){
        MutableLiveData()
    } else {
        repository.getFileByFileId(_parentItemId.value).asLiveData()
    }







    val parentItem :LiveData<List<LibraryRV>> = Transformations.switchMap(parentFile){
        val a = mutableListOf<File>()
        a.add(it)
        convertDBItemsToLibRV(a,null,null)
    }
    private val _myParentItem = MutableLiveData<LibraryRV>()
    val myParentItem:LiveData<LibraryRV> = _myParentItem
    fun getParentItem(parentItem:File){
        val a = convertFileToLibraryRV(parentItem)
        setValueToMyParentItem(a)
    }
    private fun setValueToMyParentItem(item:LibraryRV?){
        _myParentItem.apply {
            value = item
        }
        upDateTopText()
    }




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
        if(_parentItemId.value ==null){
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
    fun setValueToSelectedItem(){
        val a = mutableListOf<LibraryRV>()
        _myFinalList.value?.onEach { if(it.selected){
            a.add(it)
        }
        }
        _selectedItems.value = a
    }

    fun deleteItem(){
        when{
            _multipleSelectMode.value == true -> deleteSelectedItems()
            else -> deleteParentItem()
        }
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
    fun deleteCard(id:Int){
        return
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