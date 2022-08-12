package com.example.tangochoupdated.ui.library

import androidx.compose.runtime.internal.illegalDecoyCallException
import androidx.lifecycle.*
import androidx.navigation.NavDirections
import com.example.tangochoupdated.db.MyRoomRepository
import com.example.tangochoupdated.db.dataclass.Card
import com.example.tangochoupdated.db.dataclass.CardAndTags
import com.example.tangochoupdated.db.dataclass.File
//import com.example.tangochoupdated.room.dataclass.FileWithChild
import com.example.tangochoupdated.db.enumclass.CardStatus
import com.example.tangochoupdated.db.enumclass.FileStatus
import com.example.tangochoupdated.db.enumclass.LibRVState
import com.example.tangochoupdated.db.rvclasses.LibRVViewType
import com.example.tangochoupdated.db.rvclasses.LibraryRV
import com.example.tangochoupdated.ui.library.fragment.*
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class LibraryViewModel(private val repository: MyRoomRepository) : ViewModel() {
    /**
     *
     */
//    －－－－初期設定－－－－


//    Fragment作成時に毎回呼び出す
    fun onStart(){
    }
    fun onCreate(){
        clearSelectedItems()
    }

//
//    －－－－－－－－
//    －－－－observableなDBのデータを取ってくる－－－－

//    今開いてるファイル
    fun  parentFileFromDB(int: Int?):LiveData<File?> = repository.getFileByFileId(int).asLiveData()
    fun setParentFileFromDB (file: File?){
        _parentFile.value = file
        changeTopBarMode()

    }
    private val _parentFile = MutableLiveData<File?>()
    val parentFile:LiveData<File?> = _parentFile
    fun returnParentFile():File?{
        return _parentFile.value
    }
//    今開いてるファイルの祖先
    fun  parentFileAncestorsFromDB(int: Int?):LiveData<List<File>?> = repository.getAllAncestorsByFileId(int).asLiveData()
    fun setParentFileAncestorsFromDB (ancestors: List<File>?){
        if(ancestors != null){
            val a = ParentFileAncestors(
                gGrandPFile  = if(ancestors.size>=3) ancestors[2] else null,
                gParentFile = if(ancestors.size>=2) ancestors[1] else null,
                ParentFile = if(ancestors.isNotEmpty()) ancestors[0] else null
            )
            _parentFileAncestors.value = a
        }

    }

    private val _parentFileAncestors = MutableLiveData<ParentFileAncestors>()
    val parentFileAncestors:LiveData<ParentFileAncestors> = _parentFileAncestors



//    ファイルの中のファイル（子供）
    fun childFilesFromDB(int: Int?):LiveData<List<File>> = this.repository.mygetFileDataByParentFileId(int).asLiveData()
    private val _childFilesFromDB = MutableLiveData<List<File>?>()
    fun setChildFilesFromDB (list: List<File>?){
        _childFilesFromDB.value = list
        setValueToFinalList(list?: mutableListOf())
    }
//    ファイルの中のカード
    fun childCardsFromDB(int: Int?):LiveData<List<Card>?> =  this.repository.getCardDataByFileId(int).asLiveData()
    private val _childCardsFromDB=MutableLiveData<List<Card>?>()
    fun setChildCardsFromDB(list: List<Card>?){
        _childCardsFromDB.value = list
        setValueToFinalList(list?: mutableListOf())
    }
    val childCardsFromDB:LiveData<List<Card>?> = _childCardsFromDB

//    －－－－－－－－
//    －－－－RecyclerView－－－－

//    最終的なRVのアイテムリスト
    private val _finalRVList= MutableLiveData<List<Any>>()
    val finalRVList :LiveData<List<Any>> = _finalRVList
    fun setValueToFinalList(list:List<Any>){
        _finalRVList.apply {
            value = list
        }
    }
//    空にする
    fun clearFinalList(){
        setValueToFinalList(mutableListOf())
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
    private val _selectedFiles = MutableLiveData<MutableList<File>>()
    private fun setSelectedFiles(list:MutableList<File>){
        _selectedFiles.value = list
    }
    private val _selectedCards = MutableLiveData<MutableList<Card>>()
    private fun setSelectedCards(list:MutableList<Card>){
        _selectedCards.value = list
    }
    fun returnSelectedCards():MutableList<Card>?{
         return _selectedCards.value
    }
    fun returnSelectedFiles():MutableList<File>?{
        return _selectedFiles.value
    }
    val selectedCards:LiveData<MutableList<Card>> = _selectedCards
    val selectedFiles:LiveData<MutableList<File>> = _selectedFiles
    fun clearSelectedItems(){
        setSelectedCards(mutableListOf())
        setSelectedFiles(mutableListOf())
    }
    private fun addToSelectedItem(item: Any){
        when(item){
            is Card -> {
                val a = _selectedCards.value!!
                a.add(item)
                setSelectedCards(a)
            }
            is File -> {
                val a = _selectedFiles.value!!
                a.add(item)
                setSelectedFiles(a)
            }
        }

    }
    private fun removeFromSelectedItem(item: Any){
        when(item){
            is Card -> {
                val a = _selectedCards.value!!
                a.remove(item)
                setSelectedCards(a)
            }
            is File -> {
                val a = _selectedFiles.value!!
                a.remove(item)
                setSelectedFiles(a)
            }
        }
    }
//    －－－－click Events－－－－
    fun onClickSelectableItem(item: Any,boolean: Boolean){
        if (boolean)  {
            addToSelectedItem(item)
        } else {
            removeFromSelectedItem(item)
        }
    }
    fun openNextFile(item: File){
        val action =
        when(item.fileStatus){
            FileStatus.FOLDER->     LibraryFragFolderDirections.openFolder(intArrayOf(item.fileId))
            FileStatus.TANGO_CHO_COVER -> LibraryFragFlashCardCoverDirections.openFlashCardCover(intArrayOf(item.fileId))
            else -> return
        }
        setAction(action)
    }


//    －－－－－－－－
//    －－－－FragmentのUIデータ－－－－
    private val _modeInBox = MutableLiveData<Boolean>()
    fun setModeInBox (boolean: Boolean){
        _modeInBox.value = boolean
    }
    fun returnModeInBox():Boolean?{
        return _modeInBox.value
    }
    val modeInBox:LiveData<Boolean> = _modeInBox

    private val _chooseFileMoveToMode = MutableLiveData<Boolean>()
    private fun setChooseFileMoveToMode (boolean: Boolean){
        _chooseFileMoveToMode.value = boolean
        changeTopBarMode()
        changeRVMode()
    }

    private val _multipleSelectMode =  MutableLiveData<Boolean>()
    val multipleSelectMode:LiveData<Boolean> = _multipleSelectMode
    fun setMultipleSelectMode(boolean: Boolean){
        _multipleSelectMode.apply {
            value = boolean
        }
        if(!boolean) {
            clearSelectedItems()
            changeAllRVSelectedStatus(false)
        }
        changeTopBarMode()
        changeRVMode()
    }
    fun returnMultiSelectMode():Boolean?{
        return _multipleSelectMode.value
    }

    private val _recyclerViewMode = MutableLiveData<LibRVState>()
    private fun setRecyclerViewMode(libRVState: LibRVState){
        _recyclerViewMode.value = libRVState
    }
    val recyclerViewMode:LiveData<LibRVState> = _recyclerViewMode
    private fun changeRVMode(){
        setRecyclerViewMode(
            if(_multipleSelectMode.value == true)LibRVState.Selectable
        else if(_chooseFileMoveToMode.value == true)LibRVState.SelectFileMoveTo
        else LibRVState.Plane
        )
    }
    fun returnRVMode():LibRVState?{
        return _recyclerViewMode.value
    }



//    －－－－TopBar－－－－


    private val _topBarMode = MutableLiveData<LibraryTopBarMode>()
    private fun setTopBarMode (topBarMode: LibraryTopBarMode){
        _topBarMode.value = topBarMode
    }
    val topBarMode :LiveData<LibraryTopBarMode> = _topBarMode
    private fun changeTopBarMode(){
        setTopBarMode(if(_multipleSelectMode.value == true) LibraryTopBarMode.Multiselect
        else if (_chooseFileMoveToMode.value == true) LibraryTopBarMode.ChooseFileMoveTo
        else{
            if(_modeInBox.value == true) LibraryTopBarMode.InBox else
            if(parentFile.value != null) LibraryTopBarMode.File else LibraryTopBarMode.Home
        })
    }

//    ClickEvents
    fun onClickInBox(){


        val a = LibraryFragHomeDirections.openInbox()
        setAction(a)

    }


//    －－－－－－－－

//    空の際の表示
    private val _fileEmptyText =  MutableLiveData<String>()
    private fun setFileEmptyText(string: String){
        _fileEmptyText.apply {
            value = string
        }
    }
    val fileEmptyText:LiveData<String> = _fileEmptyText

//    －－－－Confirm PopUp－－－－


    class ConfirmPopUpView(
        var visible:Boolean,
        var confirmMode: ConfirmMode
    )
    private val _confirmPopUp =  MutableLiveData<ConfirmPopUpView>()
    fun setConfirmPopUpVisible(boolean: Boolean,confirmMode: ConfirmMode){
        val single = (_deletingItems.value != null) && (_deletingItems.value!!.size == 1)
        val singleItem = if(single){ _deletingItems.value!![0]} else null
        val btnDenialText:String
        val txvConfirmText:String
        when(confirmMode){
            ConfirmMode.DeleteOnlyParent -> {
                btnDenialText = "キャンセル"
                txvConfirmText =
                    if(single && singleItem is File) "${singleItem.title}を削除しますか？"
                    else "選択中のアイテムを削除しますか？"
            }
            ConfirmMode.DeleteWithChildren ->{
                btnDenialText = "削除しない"
                txvConfirmText =
                    if(single && singleItem is File) "${singleItem.title }の中身をすべて削除しますか？"
                    else "選択中のアイテムの中身をすべて削除しますか？"
            }
        }
        _confirmPopUp.apply {
            value = ConfirmPopUpView(boolean,confirmMode)
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
        if(_modeInBox.value ==true){
            setModeInBox(false)
        }
        if(_multipleSelectMode.value == true){
            setMultipleSelectMode(false)
        }
        if(_confirmPopUp.value?.visible == true){
            setConfirmPopUpVisible(false,ConfirmMode.DeleteOnlyParent)
        }
    }


//    －－－－－－－－
//    －－－－recyclerView States－－－－
    private val _makeAllUnSwiped = MutableLiveData<Boolean>()
    fun makeAllUnSwiped (){
        _makeAllUnSwiped.value = true
        setLeftSwipedItemExists(false)
    }
    val makeAllUnSwiped:LiveData<Boolean> = _makeAllUnSwiped
    private val _changeAllRVSelectedStatus = MutableLiveData<Boolean>()
    fun changeAllRVSelectedStatus (selected:Boolean){
        _changeAllRVSelectedStatus.value = selected

        if(selected){
            if(returnModeInBox()==true||returnParentFile()?.fileStatus==FileStatus.TANGO_CHO_COVER){
            val a = mutableListOf<Card>()
            if(_childCardsFromDB.value!=null) a.addAll(_childCardsFromDB.value!!)
            setSelectedCards(a)
        } else {
            val a = mutableListOf<File>()
            if(_childFilesFromDB.value!=null) a.addAll(_childFilesFromDB.value!!)
            setSelectedFiles(a)
        }

        } else {
            clearSelectedItems()
        }

    }
    val changeAllRVSelectedStatus:LiveData<Boolean> = _changeAllRVSelectedStatus

    private val _leftSwipedItemExists = MutableLiveData<Boolean>()
    fun setLeftSwipedItemExists (boolean: Boolean){
        _leftSwipedItemExists.value = boolean

    }
    fun returnLeftSwipedItemExists ():Boolean?{
        return  _leftSwipedItemExists.value
    }


//    －－－－－－－－


//    －－－－DB操作－－－－

//    －－－－削除－－－－
//    clickEvents
    fun onClickDeleteRVItem(item:Any){
        setDeletingItem(mutableListOf(item))
        setConfirmPopUpVisible(true,ConfirmMode.DeleteOnlyParent)
    }
    fun onClickDeleteParentItem(){
        setDeletingItem(mutableListOf(convertFileToLibraryRV(_parentFile.value!!)))
        setConfirmPopUpVisible(true,ConfirmMode.DeleteOnlyParent)
    }
    fun onClickDeleteSelectedItems(){
        val a = mutableListOf<Any>()
        if(_selectedCards.value!=null) a.addAll(_selectedCards.value!!)
        if(_selectedFiles.value!=null) a.addAll(_selectedFiles.value!!)
        setDeletingItem(a)
        setConfirmPopUpVisible(true,ConfirmMode.DeleteOnlyParent)
    }
    fun onClickBtnCommitConfirm(mode: ConfirmMode){
        when(mode){
            ConfirmMode.DeleteOnlyParent ->{

                if(_deletingItemChildrenFiles.value!!.isNotEmpty()){
                    setConfirmPopUpVisible(true,ConfirmMode.DeleteWithChildren)
                } else{
                    if(_parentFile.value?.fileStatus != FileStatus.TANGO_CHO_COVER)
                    deleteSingleFile(_deletingItems.value!![0] as File,false)
                    setConfirmPopUpVisible(false,mode)
                }
            }
            ConfirmMode.DeleteWithChildren -> {
                if(_parentFile.value?.fileStatus != FileStatus.TANGO_CHO_COVER)
                deleteSingleFile(_deletingItems.value!![0] as File,true)
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
                deleteSingleFile(_deletingItems.value!![0] as File,false)
                setConfirmPopUpVisible(false,ConfirmMode.DeleteOnlyParent)
            }
        }

    }
    fun getAllDescendantsByFileId(fileIdList: List<Int>): LiveData<List<File>> = repository.getAllDescendantsFilesByMultipleFileId(fileIdList).asLiveData()
    fun getCardsByMultipleFileId(fileIdList: List<Int>): LiveData<List<Card>> = repository.getAllDescendantsCardsByMultipleFileId(fileIdList).asLiveData()
    private val _deletingItems = MutableLiveData<List<Any>>()
    private fun setDeletingItem(list:List<Any>){
        _deletingItems.value = list
    }
    val deletingItem:LiveData<List<Any>> = _deletingItems

    private val _deletingItemChildrenFiles = MutableLiveData<List<File>?>()
    fun setDeletingItemChildrenFiles(list:List<File>?){
        _deletingItemChildrenFiles.value = list
    }
    val deletingItemChildrenFiles:LiveData<List<File>?> = _deletingItemChildrenFiles
    private val _deletingItemChildrenCards = MutableLiveData<List<Card>?>()
    fun setDeletingItemChildrenCards(list:List<Card>?){
        _deletingItemChildrenCards.value = list
    }
    val deletingItemChildrenCards:LiveData<List<Card>?> = _deletingItemChildrenCards

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
    private fun updateCards(cards:List<Card>){
        viewModelScope.launch {
            repository.updateMultiple(cards)
        }
    }
    private fun updateFiles(files:List<File>){
        viewModelScope.launch {
            repository.updateMultiple(files)
        }
    }
    private fun update(any:Any){
        viewModelScope.launch {
            repository.update(any)
        }
    }
    fun upDateCardPosition(position:Int,card:Card){
        val update = card
        update.libOrder = position
        update(card)
    }
    fun upDateContainingCardAmount(cardAmount:Int){
        val update = _parentFile.value ?:return
        update.childCardsAmount = cardAmount
        update(update)
    }
    fun upDateContainingFolderAmount(folderAmount:Int){
        val update = _parentFile.value ?:return
        update.childFoldersAmount = folderAmount
        update(update)
    }
    fun upDateContainingFlashCardAmount(flashCardCoverAmount:Int){
        val update = _parentFile.value ?:return
        update.childFlashCardCoversAmount = flashCardCoverAmount
        update(update)
    }

//    －－－－－－－－
//    －－－－ファイル移動－－－－
    fun onClickMoveInBoxCardToFlashCard(){
        setMultipleSelectMode(true)
    }
//    fun chooseFlashCardMoveTo(){
//        setChooseFileMoveToMode(true)
//        val a  = LibraryFragmentDirections.openFile()
//        a.parentItemId =if(_parentFile.value == null) null else intArrayOf(_parentFile.value!!.fileId)
//        setAction(a)
//    }
    fun openChooseFileMoveTo(item: File?){
        setChooseFileMoveToMode(true)
        val a  = LibraryFragChooseFileMoveToDirections.selectFileMoveTo(if(item!=null)intArrayOf(item.fileId) else null)
        setAction(a)
    }


    fun moveSelectedItemToFile(item:File){
        val cards = mutableListOf<Card>()
        val files = mutableListOf<File>()
        cards.addAll(_selectedCards.value!!)
        files.addAll(_selectedFiles.value!!)

        cards.onEach {
            it.belongingFileId = item.fileId
            it.libOrder = item.childCardsAmount + 1
        }
        files.onEach {
            it.parentFileId = item.fileId
            files.add(it)
        }
        updateFiles(files)
        clearSelectedItems()
        setChooseFileMoveToMode(false)
    }

//    －－－－－－－－

//    －－－－－－－－
    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }

}