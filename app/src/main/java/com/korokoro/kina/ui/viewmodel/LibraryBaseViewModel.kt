package com.korokoro.kina.ui.viewmodel

import androidx.lifecycle.*
import androidx.navigation.NavController
import com.korokoro.kina.db.MyRoomRepository
import com.korokoro.kina.db.dataclass.Card

import com.korokoro.kina.db.dataclass.File
import com.korokoro.kina.db.enumclass.FileStatus
import com.korokoro.kina.ui.customClasses.LibRVState
import com.korokoro.kina.ui.customClasses.LibraryFragment
import com.korokoro.kina.ui.customClasses.LibraryTopBarMode
import com.korokoro.kina.ui.customClasses.ParentFileAncestors
import com.korokoro.kina.ui.fragment.lib_frag_con.LibraryChooseFileMoveToFragDirections
import com.korokoro.kina.ui.fragment.lib_frag_con.LibraryFlashCardCoverFragDirections
import com.korokoro.kina.ui.fragment.lib_frag_con.LibraryFolderFragDirections
import com.korokoro.kina.ui.fragment.lib_frag_con.LibraryHomeFragDirections
import kotlinx.coroutines.cancel
class LibraryBaseViewModel(private val repository: MyRoomRepository) : ViewModel() {
    /**
     *
     */
    private val _parentFragment= MutableLiveData<LibraryFragment>()
    fun setLibraryFragment(fragment: LibraryFragment){
        _parentFragment.value = fragment
    }
//    －－－－初期設定－－－－

    private val _libraryNavCon = MutableLiveData<NavController>()
    fun setLibraryNavCon(navController: NavController){
        _libraryNavCon.value = navController
    }
    fun returnLibraryNavCon(): NavController?{
        return _libraryNavCon.value
    }

//    Fragment作成時に毎回呼び出す
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
    fun childFilesFromDB(int: Int?):LiveData<List<File>> = this.repository.getFileDataByParentFileId(int).asLiveData()

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
    fun openNextFile(item: File){
        val action =
        when(item.fileStatus){
            FileStatus.FOLDER->     LibraryFolderFragDirections.openFolder(intArrayOf(item.fileId))
            FileStatus.FLASHCARD_COVER -> LibraryFlashCardCoverFragDirections.openFlashCardCover(intArrayOf(item.fileId))
            else -> return
        }
        returnLibraryNavCon()?.navigate(action)
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
    private val _multiMenuVisibility = MutableLiveData<Boolean>()
    val multiMenuVisibility: LiveData<Boolean> = _multiMenuVisibility
    fun setMultiMenuVisibility (boolean: Boolean){
        _multiMenuVisibility.value = boolean
    }
    fun returnMultiMenuVisibility():Boolean{
        return _multiMenuVisibility.value ?:false
    }
    class RvCover(
        var visible:Boolean
    )
    private val _rvCover = MutableLiveData<RvCover>()
    val rvCover:LiveData<RvCover> = _rvCover

    fun setRVCover (rvCover: RvCover){
        _rvCover.value = rvCover
    }
    private val _rvCanceled = MutableLiveData<Boolean>()
    val rvCanceled:LiveData<Boolean> = _rvCanceled

    fun setRVCanceled (boolean: Boolean){
        _rvCanceled.value = boolean
    }
    fun returnRVCanceled ():Boolean{
        return _rvCanceled.value ?:false
    }

    private val _chooseFileMoveToMode = MutableLiveData<Boolean>()
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
    private fun changeRVMode(){
        setRecyclerViewMode(
            if(_multipleSelectMode.value == true) LibRVState.Selectable
        else if(_chooseFileMoveToMode.value == true) LibRVState.SelectFileMoveTo
        else LibRVState.Plane
        )
    }



//    －－－－TopBar－－－－


    private val _topBarMode = MutableLiveData<LibraryTopBarMode>()
    private fun setTopBarMode (topBarMode: LibraryTopBarMode){
        _topBarMode.value = topBarMode
    }
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
        val a = LibraryHomeFragDirections.openInbox()
        returnLibraryNavCon()?.navigate(a)

    }


    fun checkViewReset():Boolean{
        if(_multipleSelectMode.value == true){
            setMultipleSelectMode(false)
            return true
        }
        return false
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
        if(selected) setSelectedItems(returnParentRVItems().toMutableList())
        else setSelectedItems(mutableListOf())
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
//    －－－－ファイル移動－－－－
    fun onClickMoveInBoxCardToFlashCard(){
        setMultipleSelectMode(true)
    }
    fun openChooseFileMoveTo(file:File?){
        val a  = LibraryChooseFileMoveToFragDirections.selectFileMoveTo(if(file ==null) null else intArrayOf(file.fileId))
       returnLibraryNavCon()?.navigate(a)
    }


//    －－－－－－－－

//    －－－－－－－－
    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }

}