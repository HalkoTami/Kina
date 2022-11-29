package com.koronnu.kina.ui.viewmodel

import androidx.lifecycle.*
import androidx.navigation.NavController
import com.koronnu.kina.customClasses.enumClasses.LibraryFragment
import com.koronnu.kina.customClasses.enumClasses.LibRVState
import com.koronnu.kina.customClasses.enumClasses.LibraryTopBarMode
import com.koronnu.kina.customClasses.enumClasses.ListAttributes
import com.koronnu.kina.customClasses.normalClasses.MakeToastFromVM
import com.koronnu.kina.customClasses.normalClasses.ParentFileAncestors
import com.koronnu.kina.databinding.LibItemTopBarMenuBinding
import com.koronnu.kina.databinding.LibraryChildFragWithMulModeBaseBinding
import com.koronnu.kina.databinding.LibraryFragTopBarMultiselectModeBinding
import com.koronnu.kina.db.MyRoomRepository
import com.koronnu.kina.db.dataclass.Card

import com.koronnu.kina.db.dataclass.File
import com.koronnu.kina.db.enumclass.FileStatus
import com.koronnu.kina.ui.fragment.lib_frag_con.LibraryChooseFileMoveToFragDirections
import com.koronnu.kina.ui.fragment.lib_frag_con.LibraryFlashCardCoverFragDirections
import com.koronnu.kina.ui.fragment.lib_frag_con.LibraryFolderFragDirections
import com.koronnu.kina.ui.fragment.lib_frag_con.LibraryHomeFragDirections
import kotlinx.coroutines.cancel
class LibraryBaseViewModel(private val repository: MyRoomRepository) : ViewModel() {
    /**
     *
     */


    private val _childFragBinding= MutableLiveData<LibraryChildFragWithMulModeBaseBinding>()
    fun setChildFragBinding(childFragBinding: LibraryChildFragWithMulModeBaseBinding){
        _childFragBinding.value = childFragBinding
        doAfterSetChildFragBinding()
    }
    private fun doAfterSetChildFragBinding(){
        setMultiModeTopBarBinding(getChildFragBinding.topBarMultiselectBinding)
        setMultiModeMenuBinding(getChildFragBinding.multiSelectMenuBinding)
    }
    private val getChildFragBinding get() = _childFragBinding.value!!
    private val _multiModeMenuBinding= MutableLiveData<LibItemTopBarMenuBinding>()
    private fun setMultiModeMenuBinding(multiModeMenuBinding: LibItemTopBarMenuBinding){
        _multiModeMenuBinding.value = multiModeMenuBinding
    }
    val getMultiModeMenuBinding get() = _multiModeMenuBinding.value!!
    private val _multiModeTopBarBinding= MutableLiveData<LibraryFragTopBarMultiselectModeBinding>()
    private fun setMultiModeTopBarBinding(multiModeTopBarBinding: LibraryFragTopBarMultiselectModeBinding){
        _multiModeTopBarBinding.value = multiModeTopBarBinding
    }
    val getMultiModeTopBarBinding get() = _multiModeTopBarBinding.value!!
    private val _chooseFileMoveToViewModel= MutableLiveData<ChooseFileMoveToViewModel>()
    fun setChooseFileMoveToViewModel(chooseFileMoveToViewModel: ChooseFileMoveToViewModel){
        _chooseFileMoveToViewModel.value = chooseFileMoveToViewModel
    }
    private val getChooseFileMoveToViewModel get() = _chooseFileMoveToViewModel.value!!
    private val _deletePopUpViewModel= MutableLiveData<DeletePopUpViewModel>()
    fun setDeletePopUpViewModel(deletePopUpViewModel: DeletePopUpViewModel){
        _deletePopUpViewModel.value = deletePopUpViewModel
    }
    private val getDeletePopUpViewModel get() = _deletePopUpViewModel.value!!
    private val _parentFragment= MutableLiveData<LibraryFragment>()
    fun setLibraryFragment(fragment: LibraryFragment){
        _parentFragment.value = fragment
    }
    fun returnLibraryFragment(): LibraryFragment?{
        return _parentFragment.value
    }
    val parentFragment:LiveData<LibraryFragment> = _parentFragment

    private val _onlySwipeActive= MutableLiveData<Boolean>()
    fun setOnlySwipeActive(boolean: Boolean){
        _onlySwipeActive.value = boolean
    }
    val getOnlySwipeActive get() = _onlySwipeActive.value ?:false
    private val _onlyLongClickActive= MutableLiveData<Boolean>()
    fun setOnlyLongClickActive(boolean: Boolean){
        _onlyLongClickActive.value = boolean
    }
    val getOnlyLongClickActive get() = _onlyLongClickActive.value ?:false
    private val _doAfterLongClick= MutableLiveData<()->Unit>()
    fun setDoAfterLongClick(onlyOnce: Boolean, func:()->Unit){
        val finalFunc = if(onlyOnce){ {
            func()
            _doAfterLongClick.value = {} }
        } else func
        _doAfterLongClick.value = finalFunc
    }
    val getDoAfterLongClick get() = _doAfterLongClick.value ?:{}
//    －－－－初期設定－－－－

    private val _libraryNavCon = MutableLiveData<NavController>()
    fun setLibraryNavCon(navController: NavController){
        _libraryNavCon.value = navController
    }
    fun returnLibraryNavCon(): NavController?{
        return _libraryNavCon.value
    }
    private val _selectedItemParent  = MutableLiveData<File?>()
    fun setSelectedItemParent(parentFile: File?){
        _selectedItemParent.value = parentFile
    }
    val getSelectedItemParent = _selectedItemParent.value

//    Fragment作成時に毎回呼び出す
    fun onCreate(){
        clearSelectedItems()
    }

//
//    －－－－－－－－
//    －－－－observableなDBのデータを取ってくる－－－－

//    今開いてるファイル
    fun  parentFileFromDB(int: Int?):LiveData<File> = repository.getFileByFileId(int).asLiveData()
    fun setParentFile (file: File?){
        _parentFile.value = file
        changeTopBarMode()

    }
    private val _parentFile = MutableLiveData<File?>()
    val parentFile:LiveData<File?> = _parentFile
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
    fun childCardsFromDB(int: Int?):LiveData<List<Card>?> =  this.repository.getCardDataByFileIdSorted(int).asLiveData()

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
    private val _upDateSelectedItems = MutableLiveData<List<Any>>()
    private fun setUpdatedSelectedItems(list:List<Any>){
        _upDateSelectedItems.value = list
        doAfterSetUpdatedSelectedItems()
    }
    private fun doAfterSetUpdatedSelectedItems(){
        getChooseFileMoveToViewModel.setMovingItems(getUpdatedSelectedItems)
    }
    val getUpdatedSelectedItems get() = _upDateSelectedItems.value ?: mutableListOf()
    private val _selectedItems = MutableLiveData<List<Any>>()
    private fun setSelectedItems(list:List<Any>){
        _selectedItems.value = list
        setAllRVSelected((list.size == returnParentRVItems().size))
    }
    fun clearSelectedItems(){
        setSelectedItems(mutableListOf())
    }
    fun returnSelectedItems():List<Any>{
        return _selectedItems.value ?: mutableListOf()
    }

    val selectedItems:LiveData<List<Any>> = _selectedItems
    private val _reorderedLeftItems = MutableLiveData<List<Any>>()
    val reorderedLeftItems:LiveData<List<Any>> = _reorderedLeftItems
    private fun setReorderedLeftItems(list:List<Any>){
        _reorderedLeftItems.value = list
    }
    fun getReorderedLeftItems():List<Any>{
        return _reorderedLeftItems.value ?: mutableListOf()
    }
    fun filterReorderedItemsForUpdate():List<Any>{
        val updateNeeded = mutableListOf<Any>()
        getReorderedLeftItems().onEach { reOrderedItem ->
            val sameItem = returnParentRVItems().find { it == reOrderedItem }
            if(sameItem==null) updateNeeded.add(reOrderedItem)
        }
        return  updateNeeded
    }

    private val _toast = MutableLiveData<MakeToastFromVM>()
    private fun makeToastFromVM(string: String){
        _toast.value = MakeToastFromVM(string,true)
        _toast.value = MakeToastFromVM("",false)
    }

    val toast :LiveData<MakeToastFromVM> = _toast
    private fun updateLeftCardItems(parentCards:List<Card>){
        val updateList = mutableListOf<Card>()
        val selectedList =  returnSelectedItems().filterIsInstance<Card>()
        fun checkIsSelected(card:Card):Boolean{
            return selectedList.map { it.id }.contains(card.id)
        }
        var lastUnselectedCard:Card? = null
        fun getNextCard(cardId: Int?){
            val nextCard = parentCards.find { it.cardBefore == cardId } ?:return
            if(checkIsSelected(nextCard).not()) {
                val new = nextCard.copy()
                new.cardBefore = lastUnselectedCard?.id
                lastUnselectedCard = new
                updateList.add(new)
            }
            getNextCard(nextCard.id)
        }
        getNextCard(null)
        setReorderedLeftItems(updateList)
    }
    private fun updateLeftFileItems(parentCards:List<File>){
        val updateList = mutableListOf<File>()
        val selectedList =  returnSelectedItems().filterIsInstance<File>()
        fun checkIsSelected(file: File):Boolean{
            return selectedList.map { it.fileId }.contains(file.fileId)
        }
        var lastUnselectedFile:File? = null
        fun getNextFile(fileId: Int?){
            val nextFile = parentCards.find { it.fileBefore == fileId } ?:return
            if(checkIsSelected(nextFile).not()) {
                val new = nextFile.copy()
                new.fileBefore = lastUnselectedFile?.fileId
                lastUnselectedFile = new
                updateList.add(new)
            }
            getNextFile(nextFile.fileId)
        }
        getNextFile(null)
        setReorderedLeftItems(updateList)
    }
    private fun sortAndUpdateSelectedCards(){
        val sorted = returnSelectedItems().sortedBy { returnParentRVItems().indexOf(it) }.filterIsInstance<Card>()
        val newUpdatedList = mutableListOf<Card>()
        sorted.onEach {
            val newCard = it.copy()
            val posBefore = sorted.indexOf(it)-1
            newCard.cardBefore = if(posBefore <0) null else if (posBefore in 0..sorted.size) {
                sorted[posBefore].id

            } else throw IllegalArgumentException()
            newUpdatedList.add(newCard)
        }
        setUpdatedSelectedItems(newUpdatedList)
    }
    private fun sortAndUpdateSelectedFiles(){
        val sorted = returnSelectedItems().sortedBy { returnParentRVItems().indexOf(it) }.filterIsInstance<File>()
        val newUpdatedList = mutableListOf<File>()
        sorted.onEach {
            val newFile = it.copy()
            val posBefore = sorted.indexOf(it)-1
            newFile.fileBefore = if(posBefore <0) null else if (posBefore in 0..sorted.size) {
                sorted[posBefore].fileId

            } else throw IllegalArgumentException()
            newUpdatedList.add(newFile)
        }
        setUpdatedSelectedItems(newUpdatedList)
    }
    private fun onClickRvSelectCard(item: Card,listAttributes: ListAttributes){
        val list = returnSelectedItems().filterIsInstance<Card>()
        val parentList = returnParentRVItems().filterIsInstance<Card>()
        val mList = mutableListOf<Card>()
        mList.addAll(list)
        when(listAttributes){
            ListAttributes.Add ->{
                mList.add(item)

            }
            ListAttributes.Remove -> {
                mList.remove(item)
            }
        }

        setSelectedItems(mList)
        updateLeftCardItems(parentList)
        sortAndUpdateSelectedCards()

    }
    private fun onClickRvSelectFile(item: File,listAttributes: ListAttributes){
        val list = returnSelectedItems().filterIsInstance<File>()
        val parentList = returnParentRVItems().filterIsInstance<File>()
        val mList = mutableListOf<File>()
        mList.addAll(list)
        when(listAttributes){
            ListAttributes.Add ->{
                mList.add(item)
            }
            ListAttributes.Remove -> {
                mList.remove(item)
            }
        }
        setSelectedItems(mList)
        updateLeftFileItems(parentList)
        sortAndUpdateSelectedFiles()

    }
    fun onClickRvSelect(listAttributes: ListAttributes, item: Any){


        when(item){
            is Card -> onClickRvSelectCard(item,listAttributes)
            is File -> onClickRvSelectFile(item,listAttributes)
            else -> return
        }
    }

//    private fun addToSelectedItem(item: Any){
//        val list = returnSelectedItems()
//        list.add( item)
//        setSelectedItems(list)
//    }
//    private fun removeFromSelectedItem(item: Any){
//        val list = returnSelectedItems()
//        list.remove( item)
//        setSelectedItems(list)
//    }
//    －－－－click Events－－－－
//    fun onClickSelectableItem(item: Any,boolean: Boolean){
//        if (boolean)  {
//            addToSelectedItem(item)
//        } else {
//            removeFromSelectedItem(item)
//        }
//    }
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
    fun returnMultiSelectMode():Boolean{
        return _multipleSelectMode.value ?:false
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
        if(selected) {
            setSelectedItems(returnParentRVItems().toMutableList())
            setAllRVSelected(true)
        }
        else setSelectedItems(mutableListOf())
    }
    private val _allRVItemSelected = MutableLiveData<Boolean>()
    val allRVItemSelected:LiveData<Boolean> = _allRVItemSelected
    fun setAllRVSelected (selected:Boolean){
        _allRVItemSelected.value = selected
    }
    fun getAllRVItemSelected ():Boolean{
        return _allRVItemSelected.value ?:false
    }

    val changeAllRVSelectedStatus:LiveData<Boolean> = _changeAllRVSelectedStatus

    private val _leftSwipedItemExists = MutableLiveData<Boolean>()
    fun setLeftSwipedItemExists (boolean: Boolean){
        _leftSwipedItemExists.value = boolean

    }
    val leftSwipedItemExists:LiveData<Boolean> = _leftSwipedItemExists
    fun returnLeftSwipedItemExists ():Boolean{
        return  _leftSwipedItemExists.value ?:false
    }



//    －－－－－－－－
//    －－－－ファイル移動－－－－
    fun onClickImvChangeMultiMenuVisibility(){
        setMultiMenuVisibility(returnMultiMenuVisibility().not())
    }
    fun onClickCloseMultiMode(){
        setMultipleSelectMode(false)
    }
    fun onClickMultiMenuMakeAllSelected(){
        if(getAllRVItemSelected().not())
            changeAllRVSelectedStatus(true)
    }
    fun onClickMoveInBoxCardToFlashCard(){
        setMultipleSelectMode(true)
    }
    fun onClickMultiMenuMoveSelectedItemToFile(){
        val movableFileStatus = when(returnLibraryFragment()){
            LibraryFragment.Home,LibraryFragment.Folder-> FileStatus.FOLDER
            LibraryFragment.InBox,LibraryFragment.FlashCardCover -> FileStatus.FLASHCARD_COVER
            else -> throw IllegalArgumentException()
        }
        getChooseFileMoveToViewModel.setMovableFileStatus(movableFileStatus)
        getChooseFileMoveToViewModel.setMovingItemsParentFileId(returnParentFile()?.fileId)
        openChooseFileMoveTo(null)
    }
    fun onClickMultiMenuDeleteSelectedItems(){
        getDeletePopUpViewModel.setDeletingItem(returnSelectedItems().toMutableList())
        getDeletePopUpViewModel.setConfirmDeleteVisible(true)
    }
    fun openChooseFileMoveTo(file:File?){
        val a  = LibraryChooseFileMoveToFragDirections.selectFileMoveTo(if(file ==null) null else intArrayOf(file.fileId))
       returnLibraryNavCon()?.navigate(a)
    }
    private val _doOnSwipeEnd = MutableLiveData<()->Unit>()
    fun setDoOnSwipeEnd(onlyOnce:Boolean,unit:()->Unit){
        val unitBefore = _doOnSwipeEnd.value
        val finalUnit = if(onlyOnce){
            { unit()
                _doOnSwipeEnd.value = unitBefore ?:{} }
        } else unit
        _doOnSwipeEnd.value = finalUnit
    }
    val doOnSwipeEnd:()->Unit get() = _doOnSwipeEnd.value ?:{}



//    －－－－－－－－

//    －－－－－－－－
    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }

}