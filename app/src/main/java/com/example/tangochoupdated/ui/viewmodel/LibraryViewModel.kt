package com.example.tangochoupdated.ui.viewmodel

import androidx.compose.runtime.internal.illegalDecoyCallException
import androidx.lifecycle.*
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import com.example.tangochoupdated.db.MyRoomRepository
import com.example.tangochoupdated.db.dataclass.Card

import com.example.tangochoupdated.db.dataclass.File
//import com.example.tangochoupdated.room.dataclass.FileWithChild
import com.example.tangochoupdated.db.enumclass.FileStatus
import com.example.tangochoupdated.db.enumclass.LibRVState
import com.example.tangochoupdated.db.enumclass.LibraryFragment
import com.example.tangochoupdated.db.rvclasses.LibRVViewType
import com.example.tangochoupdated.db.rvclasses.LibraryRV
import com.example.tangochoupdated.ui.view_set_up.ConfirmMode
import com.example.tangochoupdated.ui.view_set_up.LibraryTopBarMode
import com.example.tangochoupdated.ui.view_set_up.ParentFileAncestors
import com.example.tangochoupdated.ui.fragment.lib_frag_con.LibraryFragChooseFileMoveToDirections
import com.example.tangochoupdated.ui.fragment.lib_frag_con.LibraryFragFlashCardCoverDirections
import com.example.tangochoupdated.ui.fragment.lib_frag_con.LibraryFragFolderDirections
import com.example.tangochoupdated.ui.fragment.lib_frag_con.LibraryFragHomeDirections
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class LibraryViewModel(private val repository: MyRoomRepository) : ViewModel() {
    /**
     *
     */
    private val _parentFragment= MutableLiveData<LibraryFragment>()
    fun setLibraryFragment(fragment: LibraryFragment){
        _parentFragment.value = fragment
    }
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
    fun  parentFileFromDB(int: Int?):LiveData<File> = repository.getFileByFileId(int).asLiveData()
    fun setParentFileFromDB (file: File){
        _parentFile.value = file
        changeTopBarMode()

    }
    private val _parentFile = MutableLiveData<File>()
    val parentFile:LiveData<File> = _parentFile
    fun returnParentFile():File?{
        return _parentFile.value
    }
//    今開いてるファイルの祖先
    fun  parentFileAncestorsFromDB(int: Int?):LiveData<List<File>> = repository.getAllAncestorsByFileId(int).asLiveData()
    fun setParentFileAncestorsFromDB (ancestors: List<File>){
        val a = ParentFileAncestors(
            gGrandPFile  = if(ancestors.size>=3) ancestors[2] else null,
            gParentFile = if(ancestors.size>=2) ancestors[1] else null,
            ParentFile = if(ancestors.isNotEmpty()) ancestors[0] else null
        )
        _parentFileAncestors.value = a

    }

    private val _parentFileAncestors = MutableLiveData<ParentFileAncestors>()
    val parentFileAncestors:LiveData<ParentFileAncestors> = _parentFileAncestors



//    ファイルの中のファイル（子供）
    fun childFilesFromDB(int: Int?):LiveData<List<File>> = this.repository.mygetFileDataByParentFileId(int).asLiveData()

//    ファイルの中のカード
    fun childCardsFromDB(int: Int?):LiveData<List<Card>?> =  this.repository.getCardDataByFileId(int).asLiveData()

//    －－－－－－－－



//    －－－－RecyclerView－－－－

//    最終的なRVのアイテムリスト
    private val _parentRVItems= MutableLiveData<List<Any>>()
    val parentRVItems :LiveData<List<Any>> = _parentRVItems
    fun setParentRVItems(list:List<Any>){
        _parentRVItems.apply {
            value = list
        }
    }
    fun returnParentRVItems():List<Any>{
        return _parentRVItems.value ?: mutableListOf()
    }
//    空にする
    fun clearFinalList(){
        setParentRVItems(mutableListOf())
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
//    private fun convertCardToLibraryRV(card: CardAndTags): LibraryRV {
//        when (card.card.cardStatus) {
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
//            else -> illegalDecoyCallException("unknown class")
//        }
//    }
//    selected Items
    private val _selectedItems = MutableLiveData<MutableList<Any>>()
    private fun setSelectedItems(list:MutableList<Any>){
        _selectedItems.value = list
    }
    fun clearSelectedItems(){
        setSelectedItems(mutableListOf())
    }
    fun returnSelectedItems():MutableList<Any>{
        return _selectedItems.value ?: mutableListOf()
    }
    val selectedItems:LiveData<MutableList<Any>> = _selectedItems
//
//    private val _selectedFiles = MutableLiveData<MutableList<File>>()
//    private fun setSelectedFiles(list:MutableList<File>){
//        _selectedFiles.value = list
//    }
//    private val _selectedCards = MutableLiveData<MutableList<Card>>()
//    private fun setSelectedCards(list:MutableList<Card>){
//        _selectedCards.value = list
//    }
//    fun returnSelectedCards():MutableList<Card>?{
//         return _selectedCards.value
//    }
//    fun returnSelectedFiles():MutableList<File>?{
//        return _selectedFiles.value
//    }
//    val selectedCards:LiveData<MutableList<Card>> = _selectedCards
//    val selectedFiles:LiveData<MutableList<File>> = _selectedFiles
//    fun clearSelectedItems(){
//        setSelectedCards(mutableListOf())
//        setSelectedFiles(mutableListOf())
//    }
    private fun addToSelectedItem(item: Any){
        val list = returnSelectedItems()
        list.add( item)
        setSelectedItems(list)
    }
    private fun removeFromSelectedItem(item: Any){
        val list = returnSelectedItems()
        list.remove( item)
        setSelectedItems(list)
    }
//    －－－－click Events－－－－
    fun onClickSelectableItem(item: Any,boolean: Boolean){
        if (boolean)  {
            addToSelectedItem(item)
        } else {
            removeFromSelectedItem(item)
        }
    }
    fun openNextFile(item: File,navController: NavController){
        val action =
        when(item.fileStatus){
            FileStatus.FOLDER->     LibraryFragFolderDirections.openFolder(intArrayOf(item.fileId))
            FileStatus.TANGO_CHO_COVER -> LibraryFragFlashCardCoverDirections.openFlashCardCover(intArrayOf(item.fileId))
            else -> return
        }
        navController.navigate(action)
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
    class RvCover(
        var height:Float,
        var visible:Boolean
    )
    private val _rvCover = MutableLiveData<RvCover>()
    val rvCover:LiveData<RvCover> = _rvCover

    fun setRVCover (rvCover: RvCover){
        _rvCover.value = rvCover
    }

    val modeInBox:LiveData<Boolean> = _modeInBox

    private val _chooseFileMoveToMode = MutableLiveData<Boolean>()
    private fun setChooseFileMoveToMode (boolean: Boolean){
        _chooseFileMoveToMode.value = boolean
        changeTopBarMode()
        changeRVMode()
    }
    val chooseFileMoveToMode:LiveData<Boolean> =_chooseFileMoveToMode

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
    fun onClickInBox(navController: NavController){
        val a = LibraryFragHomeDirections.openInbox()
        navController.navigate(a)

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




//    －－－－－－－－

//    －－－－－－－－
//    －－－－navigation－－－－

    private val _action = MutableLiveData<NavDirections>()
    val action: LiveData<NavDirections> = _action
    private fun setAction(navDirections: NavDirections){
        _action.value = navDirections
    }
    fun checkViewReset():Boolean{
        if(_multipleSelectMode.value == true){
            setMultipleSelectMode(false)
            return true
        }
        return false
//        if(_confirmPopUp.value?.visible == true){
//            setConfirmPopUpVisible(false, ConfirmMode.DeleteItem)
//        }
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


    }
    val changeAllRVSelectedStatus:LiveData<Boolean> = _changeAllRVSelectedStatus

    private val _leftSwipedItemExists = MutableLiveData<Boolean>()
    fun setLeftSwipedItemExists (boolean: Boolean){
        _leftSwipedItemExists.value = boolean

    }
    val leftSwipedItemExists:LiveData<Boolean> = _leftSwipedItemExists
    fun returnLeftSwipedItemExists ():Boolean?{
        return  _leftSwipedItemExists.value
    }


//    －－－－－－－－


//    －－－－DB操作－－－－

//    －－－－削除－－－－
//    clickEvents

//    fun onClickDeleteSelectedItems(){
//        val a = mutableListOf<Any>()
//        if(_selectedCards.value!=null) a.addAll(_selectedCards.value!!)
//        if(_selectedFiles.value!=null) a.addAll(_selectedFiles.value!!)
//        setDeletingItem(a)
//        setConfirmPopUpVisible(true, ConfirmMode.DeleteItem)
//    }

//    fun onClickBtnCommitConfirm(mode: ConfirmMode){
//        when(mode){
//            ConfirmMode.DeleteItem ->{
//
//                if(_deletingItemChildrenFiles.value!!.isNotEmpty()){
//                    setConfirmPopUpVisible(true, ConfirmMode.DeleteWithChildren)
//                } else{
//                    if(_parentFile.value?.fileStatus != FileStatus.TANGO_CHO_COVER)
//                    deleteSingleFile(_deletingItems.value!![0] as File,false)
//                    setConfirmPopUpVisible(false,mode)
//                }
//            }
//            ConfirmMode.DeleteWithChildren -> {
//                if(_parentFile.value?.fileStatus != FileStatus.TANGO_CHO_COVER)
//                deleteSingleFile(_deletingItems.value!![0] as File,true)
//                setConfirmPopUpVisible(false, ConfirmMode.DeleteItem)
//            }
//        }
//    }
//    fun onClickBtnDenial(mode: ConfirmMode){
//        when(mode){
//            ConfirmMode.DeleteItem -> {
//                setConfirmPopUpVisible(false,mode)
//                setDeletingItem(arrayListOf())
//            }
//            ConfirmMode.DeleteWithChildren ->{
//                deleteSingleFile(_deletingItems.value!![0] as File,false)
//                setConfirmPopUpVisible(false, ConfirmMode.DeleteItem)
//            }
//        }
//
//    }



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
    fun openChooseFileMoveTo(file:File?,navController: NavController){
        setChooseFileMoveToMode(true)
        val a  = LibraryFragChooseFileMoveToDirections.selectFileMoveTo(if(file ==null) null else intArrayOf(file.fileId))
        navController.navigate(a)
    }


    fun moveSelectedItemToFile(item:File){

        val change = returnSelectedItems()

        change.onEach {
            when(it ){
                is Card -> {
                    it.belongingFlashCardCoverId = item.fileId
                    it.libOrder = item.childData.childCardsAmount + 1

                }
                is File -> {
                    it.parentFileId = item.fileId

                }
            }
            update(it)
        }

    clearSelectedItems()
    //        setChooseFileMoveToMode(false)
    }

//    －－－－－－－－

//    －－－－－－－－
    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }

}