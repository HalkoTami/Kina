package com.example.tangochoupdated.ui.library

import androidx.compose.runtime.internal.illegalDecoyCallException
import androidx.lifecycle.*
import androidx.navigation.NavDirections
import com.example.tangochoupdated.R
import com.example.tangochoupdated.room.MyRoomRepository
import com.example.tangochoupdated.room.dataclass.CardAndTags
import com.example.tangochoupdated.room.dataclass.File
//import com.example.tangochoupdated.room.dataclass.FileWithChild
import com.example.tangochoupdated.room.enumclass.CardStatus
import com.example.tangochoupdated.room.enumclass.FileStatus
import com.example.tangochoupdated.room.rvclasses.LibRVViewType
import com.example.tangochoupdated.room.rvclasses.LibraryRV
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class LibraryViewModel(private val repository: MyRoomRepository) : ViewModel() {
    /**
     *
     */
//    －－－－初期設定－－－－


//    Fragment作成時に毎回呼び出す
    fun onStart(){
        setMultipleSelectMode(false)
        setSelectedItem(mutableListOf())
        setConfirmPopUpVisible(false,ConfirmMode.DeleteOnlyParent)
    }

//
//    －－－－－－－－
//    －－－－observableなDBのデータを取ってくる－－－－

//    今開いてるファイル
    fun  parentFileFromDB(int: Int?):LiveData<File?> = repository.getFileByFileId(int).asLiveData()
    fun setParentFileFromDB (file: File?){
        _parentFile.value = file
        if(_modeInBox.value == true) makeTopBarInBox() else
        makeTopBarUIUnselected(file)
    }
    private val _parentFile = MutableLiveData<File?>()
    val parentFile:LiveData<File?> = _parentFile
//    ファイルの中のファイル（子供）
    fun childFilesFromDB(int: Int?):LiveData<List<File>> = this.repository.mygetFileDataByParentFileId(int).asLiveData()
    private val _childFilesFromDB = MutableLiveData<List<File>>()
    fun setChildFilesFromDB (list: List<File>?){
        _childFilesFromDB.value = list
        if(_parentFile.value?.fileStatus != FileStatus.TANGO_CHO_COVER){
            clearFinalList()
            val b = mutableListOf<LibraryRV>()
            list?.onEach { b.add(convertFileToLibraryRV(it)) }
            setValueToFinalList(b)
        }
    }
//    ファイルの中のカード
    fun childCardsFromDB(int: Int?):LiveData<List<CardAndTags>?> =  this.repository.getCardDataByFileId(int).asLiveData()
    private val _childCardsFromDB=MutableLiveData<List<CardAndTags>>()
    fun setChildCardsFromDB(list: List<CardAndTags>?){
        _childCardsFromDB.value = list
        if(_parentFile.value?.fileStatus == FileStatus.TANGO_CHO_COVER||_modeInBox.value == true){
            val b = mutableListOf<LibraryRV>()
            clearFinalList()
            list?.onEach { b.add(convertCardToLibraryRV(it)) }
            setValueToFinalList(b)
        }
    }
    val childCardsFromDB:LiveData<List<CardAndTags>> = _childCardsFromDB

//    －－－－－－－－
//    －－－－RecyclerView－－－－

//    最終的なRVのアイテムリスト
    private val _myFinalList= MutableLiveData<List<LibraryRV>>()
    val myFinalList :LiveData<List<LibraryRV>> = _myFinalList
    private fun setValueToFinalList(list:List<LibraryRV>){
        val comparator : Comparator<LibraryRV> = compareBy { it.position }
        val a = list.sortedWith(comparator)
        _myFinalList.apply {
            value = a
        }
        if(a.isEmpty()){
            setFileEmptyText(if(_parentFile.value == null) "まだデータがありません"
            else "${_parentFile.value?.title}は空です")
        }
    }
//    空にする
    private fun clearFinalList(){
        val a = mutableListOf<LibraryRV>()
        _myFinalList.value = a
    }
//    LibraryRVへの変換
    private fun convertFileToLibraryRV(file: File):LibraryRV{
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
            FileStatus.TANGO_CHO_COVER -> {
                return LibraryRV(
                    type = LibRVViewType.FlashCardCover,
                    position = file.libOrder!!,
                    file = file,
                    card = null,
                    tag = null,
                    id = file.fileId

                )
            }
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
//    selected Items
    private val _selectedItems = MutableLiveData<MutableList<LibraryRV>>()
    private fun setSelectedItem(list:MutableList<LibraryRV>){
        _selectedItems.value = list
        if(list.isEmpty().not()){
            makeTopBarSelected(list.size)
        }
    }
    private fun addToSelectedItem(item: LibraryRV){
        val a = _selectedItems.value!!
        a.add(item)
        setSelectedItem(a)
    }
    private fun removeFromSelectedItem(item: LibraryRV){
        val a = _selectedItems.value!!
        a.remove(item)
        setSelectedItem(a)
    }
//    －－－－click Events－－－－
    fun onClickSelectableItem(item: LibraryRV,boolean: Boolean){
        if (boolean)  {
            addToSelectedItem(item)
        } else {
            removeFromSelectedItem(item)
        }
    }
    fun openNextFile(item: LibraryRV){
        val action = LibraryFragmentDirections.toLib()
        action.parentItemId = intArrayOf(item.file!!.fileId)
        setAction(action)
    }

//    －－－－－－－－
//    －－－－FragmentのUIデータ－－－－

//    －－－－TopBar－－－－

    class LibraryTopBar(
        var topBarLeftIMVDrawableId:Int,
        var topBarRightDrawableId:Int,
        var topBarText:String
    )
    private val _topBarUI = MutableLiveData<LibraryTopBar>()
    private fun setTopBarUI (libraryTopBar: LibraryTopBar){
        _topBarUI.value = libraryTopBar
    }
    val topBarUI :LiveData<LibraryTopBar> = _topBarUI
    private fun makeTopBarUIUnselected(file:File?){
        val a = LibraryTopBar(
            topBarLeftIMVDrawableId =
            if(file== null) R.drawable.icon_eye_opened else {
                when(file.fileStatus){
                    FileStatus.FOLDER -> R.drawable.icon_file
                    FileStatus.TANGO_CHO_COVER -> R.drawable.icon_library_plane
                    else -> throw IllegalArgumentException()
                }
            }
           ,
            topBarText =  if(file== null) "Home" else "${file.title}",
            topBarRightDrawableId = if(file== null) R.drawable.icon_inbox else R.drawable.icon_dot
        )
        setTopBarUI(a)
    }
    private fun makeTopBarSelected(selectedItemSize:Int){
        val a = LibraryTopBar(
            topBarLeftIMVDrawableId = R.drawable.icon_close,
            topBarText = "${selectedItemSize}個　選択中",
            topBarRightDrawableId = R.drawable.icon_dot
        )
        setTopBarUI(a)
    }
    private fun makeTopBarInBox(){
        val a = LibraryTopBar(
            topBarLeftIMVDrawableId = R.drawable.icon_inbox,
            topBarText = "単語帳に入っていません",
            topBarRightDrawableId = R.drawable.icon_dot
        )
        setTopBarUI(a)
    }

//    ClickEvents
    fun onClickInBox(){
        setModeInBox(true)
        val a = LibraryFragmentDirections.toLib()
        a.parentItemId = null
        setAction(a)

    }
    private val _modeInBox =  MutableLiveData<Boolean>()
    private fun setModeInBox(boolean: Boolean){
        _modeInBox.apply {
            value = boolean
        }
    }

//    －－－－－－－－
    private val _multipleSelectMode =  MutableLiveData<Boolean>()
    val multipleSelectMode:LiveData<Boolean> = _multipleSelectMode
    fun setMultipleSelectMode(boolean: Boolean){
        _multipleSelectMode.apply {
            value = boolean
        }
        when(boolean){
            true ->makeTopBarSelected(_selectedItems.value?.size ?:0)
            false -> {
                if(_modeInBox.value == true) makeTopBarInBox()
                else makeTopBarUIUnselected(_parentFile.value)
                setSelectedItem(mutableListOf())
            }
        }
    }
//    空の際の表示
    private val _fileEmptyText =  MutableLiveData<String>()
    private fun setFileEmptyText(string: String){
        _fileEmptyText.apply {
            value = string
        }
    }
    val fileEmptyText:LiveData<String> = _fileEmptyText

//    －－－－Confirm PopUp－－－－

    enum class ConfirmMode{
        DeleteOnlyParent,DeleteWithChildren
    }
    class ConfirmPopUpView(
        var visible:Boolean,
        var btnDenialText:String,
        var btnCommitConfirmText:String,
        var txvConfirmText:String,
        var confirmMode: ConfirmMode
    )
    private val _confirmPopUp =  MutableLiveData<ConfirmPopUpView>()
    private fun setConfirmPopUpVisible(boolean: Boolean,confirmMode: ConfirmMode){
        val single = (_deletingItems.value != null) && (_deletingItems.value!!.size == 1)
        val btnDenialText:String
        val txvConfirmText:String
        when(confirmMode){
            ConfirmMode.DeleteOnlyParent -> {
                btnDenialText = "キャンセル"
                txvConfirmText =
                    if(single) "${_deletingItems.value!![0].file?.title ?:"カード"}を削除しますか？"
                    else "選択中のアイテムを削除しますか？"
            }
            ConfirmMode.DeleteWithChildren ->{
                btnDenialText = "削除しない"
                txvConfirmText =
                    if(single) "${_deletingItems.value!![0].file?.title }の中身をすべて削除しますか？"
                    else "選択中のアイテムの中身をすべて削除しますか？"
            }
        }
        _confirmPopUp.apply {
            value = ConfirmPopUpView(boolean,btnDenialText,"削除する",txvConfirmText,confirmMode)
        }
    }
    val confirmPopUp:LiveData<ConfirmPopUpView> = _confirmPopUp

//    －－－－－－－－

//    －－－－－－－－
//    －－－－navigation－－－－

    private val _action = MutableLiveData<NavDirections>()
    val action: LiveData<NavDirections> = _action
    private fun setAction(navDirections: NavDirections){
        _action.value = navDirections
    }
    fun onClickBack(){
        if(_modeInBox.value==true){
            setModeInBox(false)
        }
    }

//    －－－－－－－－
//    －－－－DB操作－－－－

//    －－－－削除－－－－
//    clickEvents
    fun onClickDeleteRVItem(item:LibraryRV){
        setDeletingItem(mutableListOf(item))
        setConfirmPopUpVisible(true,ConfirmMode.DeleteOnlyParent)
    }
    fun onClickBtnCommitConfirm(mode: ConfirmMode){
        when(mode){
            ConfirmMode.DeleteOnlyParent ->{

                if(_deletingItemChildrenFiles.value!!.isNotEmpty()){
                    setConfirmPopUpVisible(true,ConfirmMode.DeleteWithChildren)
                } else{
                    deleteSingleFile(_deletingItems.value!![0].file!!,false)
                    setConfirmPopUpVisible(false,mode)
                }
            }
            ConfirmMode.DeleteWithChildren -> {
                deleteSingleFile(_deletingItems.value!![0].file!!,true)
                setConfirmPopUpVisible(false,ConfirmMode.DeleteOnlyParent)
            }
        }
    }
    fun onClickBtnDenial(mode: ConfirmMode){
        when(mode){
            ConfirmMode.DeleteOnlyParent -> {
                setConfirmPopUpVisible(false,mode)
                setDeletingItem(arrayListOf())
            }
            ConfirmMode.DeleteWithChildren ->{
                val deletingIds = mutableListOf<Int>()
                _deletingItems.value!!.onEach { deletingIds.add(it.id) }
                deleteSingleFile(_deletingItems.value!![0].file!!,false)
                setConfirmPopUpVisible(false,ConfirmMode.DeleteOnlyParent)
            }
        }

    }
    fun getAllDescendantsByFileId(fileIdList: Int?): LiveData<List<File>> = repository.getAllDescendantsByFileId(fileIdList).asLiveData()
    private val _deletingItems = MutableLiveData<List<LibraryRV>>()
    private fun setDeletingItem(list:List<LibraryRV>){
        _deletingItems.value = list
    }
    val deletingItem:LiveData<List<LibraryRV>> = _deletingItems
    private val _deletingItemChildrenFiles = MutableLiveData<List<File>?>()
    fun setDeletingItemChildrenFiles(list:List<File>?){
        _deletingItemChildrenFiles.value = list
    }
    private fun deleteSingleFile(file:File, deleteChildren:Boolean){
        viewModelScope.launch {
            if(!deleteChildren){
                repository.upDateChildFilesOfDeletedFile(file.fileId,file.parentFileId)
                repository.delete(file)
            } else {
                repository.deleteFileAndAllDescendants(file.fileId)
            }
        }
    }

//    －－－－－－－－

//    －－－－－－－－
    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }

}