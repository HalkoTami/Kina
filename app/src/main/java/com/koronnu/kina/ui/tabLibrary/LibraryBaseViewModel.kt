package com.koronnu.kina.ui.tabLibrary

import android.content.Context
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.navigation.NavController
import com.koronnu.kina.RoomApplication
import com.koronnu.kina.data.model.enumClasses.*
import com.koronnu.kina.data.model.normalClasses.MakeToastFromVM
import com.koronnu.kina.databinding.LibItemTopBarMenuBinding
import com.koronnu.kina.databinding.LibraryChildFragWithMulModeBaseBinding
import com.koronnu.kina.databinding.LibraryFragBinding
import com.koronnu.kina.databinding.LibraryFragTopBarMultiselectModeBinding
import com.koronnu.kina.db.MyRoomRepository
import com.koronnu.kina.data.source.local.entity.Card

import com.koronnu.kina.data.source.local.entity.File
import com.koronnu.kina.data.source.local.entity.enumclass.FileStatus
import com.koronnu.kina.ui.MainViewModel
import com.koronnu.kina.ui.tabLibrary.chooseFileMoveTo.ChooseFileMoveToViewModel
import com.koronnu.kina.ui.tabLibrary.chooseFileMoveTo.LibraryChooseFileMoveToFragDirections
import com.koronnu.kina.ui.tabLibrary.inBox.LibraryInBoxFragViewModel
import com.koronnu.kina.ui.tabLibrary.flashCardCover.LibraryFlashCardCoverFragDirections
import com.koronnu.kina.ui.tabLibrary.folder.LibraryFolderFragDirections
import com.koronnu.kina.ui.tabLibrary.home.LibraryHomeFragDirections
import com.koronnu.kina.ui.viewmodel.*
import kotlinx.coroutines.cancel

class LibraryBaseViewModel(private val repository: MyRoomRepository) : ViewModel() {

    private lateinit var mainViewModel: MainViewModel
    private lateinit var searchViewModel: SearchViewModel
    private lateinit var moveToViewModel : ChooseFileMoveToViewModel
    private lateinit var _libraryInBoxFragViewModel: LibraryInBoxFragViewModel
    val chooseFileMoveToViewModel get() = moveToViewModel

    /**
     *
     */
    companion object{
        fun getFactory(mainViewModel: MainViewModel,
                       viewModelStoreOwner: ViewModelStoreOwner,
                       context: Context): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]) as RoomApplication
                val repository = application.repository
                val libraryBaseViewModel = LibraryBaseViewModel(repository)
                val searchViewModel = ViewModelProvider(viewModelStoreOwner,
                    SearchViewModel.Factory)[SearchViewModel::class.java]
                val moveToViewModel = ViewModelProvider(viewModelStoreOwner,
                    ChooseFileMoveToViewModel.getFactory(libraryBaseViewModel))[ChooseFileMoveToViewModel::class.java]
                val inBoxFragViewModel = ViewModelProvider(viewModelStoreOwner,
                    LibraryInBoxFragViewModel.getFactory(libraryBaseViewModel,
                        context))[LibraryInBoxFragViewModel::class.java]
                libraryBaseViewModel.mainViewModel = mainViewModel
                libraryBaseViewModel.searchViewModel = searchViewModel
                libraryBaseViewModel.moveToViewModel = moveToViewModel
                libraryBaseViewModel._libraryInBoxFragViewModel = inBoxFragViewModel
                return libraryBaseViewModel as T
            }
        }
    }
    val cardsWithoutFlashCardCoverFromDB:LiveData<List<Card>> = repository.cardsWithoutFlashCardCover.asLiveData()





    fun onClickOpenRvItem(item:Any){
        if(returnLeftSwipedItemExists()) makeAllUnSwiped()
        else if(returnMultiSelectMode()){
            onClickRvSelect(
                if((_selectedItems.value ?: mutableListOf()).contains(item)) ListAttributes.Remove else ListAttributes.Add,item)
        }else{
            when(item){
                is File -> openNextFile(item)
                is Card -> mainViewModel.createCardViewModel.onClickEditCardFromRV(item)
            }
        }
    }
    fun onClickDelete(item:Any){
        mainViewModel.deletePopUpViewModel.setDeletingItem(mutableListOf(item))
        mainViewModel.deletePopUpViewModel.setConfirmDeleteVisible(true)
    }
    fun onClickEditItem(item: Any){
        when(item ){
            is File -> mainViewModel.editFileViewModel.onClickEditFileInRV(item)
            is Card -> mainViewModel.createCardViewModel.onClickEditCardFromRV(item)
            else -> return
        }
    }
    fun onClickBtnAddNewCard(item: Any){
        when(item){
            is Card -> {
                mainViewModel.createCardViewModel.onClickAddNewCardRV(item)
            }
            else -> return
        }
    }
    fun onLongClickRvItem(item: Any):Boolean{
        setMultipleSelectMode(true)
        onClickRvSelect(ListAttributes.Add,item)
        return true
    }


    private fun setChildFragBindingClickListeners(){
//        setPopUpJumpToGuideCL()
    }
    private val childFragBindingObserver = Observer<LibraryChildFragWithMulModeBaseBinding> {
        setChildFragBindingClickListeners()
    }


    fun observeLiveDataInFragment(lifecycleOwner: LifecycleOwner){
        childFragBinding.observe(lifecycleOwner,childFragBindingObserver)
    }
    private val _childFragBinding= MutableLiveData<LibraryChildFragWithMulModeBaseBinding>()
    fun setChildFragBinding(childFragBinding: LibraryChildFragWithMulModeBaseBinding){
        _childFragBinding.value = childFragBinding
        doAfterSetChildFragBinding()
    }
    private val childFragBinding :LiveData<LibraryChildFragWithMulModeBaseBinding> = _childFragBinding

    private var _libraryBaseBinding:LibraryFragBinding? = null
    fun setLibraryBaseBinding(libraryFragBinding: LibraryFragBinding){
        _libraryBaseBinding = libraryFragBinding
    }
    val libraryFragBinding get() = _libraryBaseBinding!!


    private fun doAfterSetChildFragBinding(){
        setMultiModeTopBarBinding(getChildFragBinding.topBarMultiselectBinding)
        setMultiModeMenuBinding(getChildFragBinding.multiSelectMenuBinding)
    }
     val getChildFragBinding get() = _childFragBinding.value!!
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

    fun returnLibraryFragment(): LibraryFragment {
        return _parentFragment.value!!
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
    val _parentFile = MutableLiveData<File?>()
    val parentFile:LiveData<File?> = _parentFile
    fun returnParentFile(): File?{
        return _parentFile.value
    }
//    今開いてるファイルの祖先
    fun  parentFileAncestorsFromDB(int: Int?):LiveData<List<File>> = repository.getAllAncestorsByFileId(int).asLiveData()
    fun setParentFileAncestorsFromDB (ancestors: List<File>){
        _parentFileAncestors.value = ancestors

    }


    private val _parentFileAncestors = MutableLiveData<List<File>?>()
    val getParentFileAncestors get() = _parentFileAncestors.value



//    ファイルの中のファイル（子供）
    fun childFilesFromDB(int: Int?):LiveData<List<File>?> = this.repository.getFileDataByParentFileId(int).asLiveData()

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
    private val getUpdatedSelectedItems get() = _upDateSelectedItems.value ?: mutableListOf()
    private val _selectedItems = MutableLiveData<List<Any>>()
    private fun setSelectedItems(list:List<Any>){
        _selectedItems.value = list
        setAllRVSelected((list.size == returnParentRVItems().size))
    }
    fun clearSelectedItems(){
        setSelectedItems(mutableListOf())
    }
    private fun returnSelectedItems():List<Any>{
        return _selectedItems.value ?: mutableListOf()
    }

    val selectedItems:LiveData<List<Any>> = _selectedItems
    private val _reorderedLeftItems = MutableLiveData<List<Any>>()
    val reorderedLeftItems:LiveData<List<Any>> = _reorderedLeftItems
    private fun setReorderedLeftItems(list:List<Any>){
        _reorderedLeftItems.value = list
    }
    private fun getReorderedLeftItems():List<Any>{
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

    val toast :LiveData<MakeToastFromVM> = _toast
    private fun updateLeftCardItems(parentCards:List<Card>){
        val updateList = mutableListOf<Card>()
        val selectedList =  returnSelectedItems().filterIsInstance<Card>()
        fun checkIsSelected(card: Card):Boolean{
            return selectedList.map { it.id }.contains(card.id)
        }
        var lastUnselectedCard: Card? = null
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
        var lastUnselectedFile: File? = null
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
    private fun onClickRvSelectCard(item: Card, listAttributes: ListAttributes){
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
    private fun onClickRvSelectFile(item: File, listAttributes: ListAttributes){
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
    val multiMenuVisibility = MutableLiveData<Boolean>()
    fun setMultiMenuVisibility (boolean: Boolean){
        multiMenuVisibility.value = boolean
    }
    private fun returnMultiMenuVisibility():Boolean{
        return multiMenuVisibility.value ?:false
    }


    private val _chooseFileMoveToMode = MutableLiveData<Boolean>()
    val chooseFileMoveToMode:LiveData<Boolean> =_chooseFileMoveToMode

    val multipleSelectMode =  MutableLiveData<Boolean>()
    fun setMultipleSelectMode(boolean: Boolean){
        multipleSelectMode.apply {
            value = boolean
        }
        if(!boolean) {
            setMultiMenuVisibility(false)
            clearSelectedItems()
            changeAllRVSelectedStatus(false)
        }
        changeTopBarMode()
        changeRVMode()
    }
    val searchModeActive = MutableLiveData<Boolean>()
    fun setSearchModeActive(boolean: Boolean){
        searchModeActive.value = boolean
    }
    fun returnMultiSelectMode():Boolean{
        return multipleSelectMode.value ?:false
    }

    private val _recyclerViewMode = MutableLiveData<LibRVState>()
    private fun setRecyclerViewMode(libRVState: LibRVState){
        _recyclerViewMode.value = libRVState
    }
    private fun changeRVMode(){
        setRecyclerViewMode(
            if(multipleSelectMode.value == true) LibRVState.Selectable
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
        setTopBarMode(if(this.multipleSelectMode.value == true) LibraryTopBarMode.Multiselect
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


    //    －－－－－－－－
//    －－－－recyclerView States－－－－
    private val _makeAllUnSwiped = MutableLiveData<Boolean>()
    fun makeAllUnSwiped (){
        _makeAllUnSwiped.value = true
        setLeftSwipedItemExists(false)
    }
    val makeAllUnSwiped:LiveData<Boolean> = _makeAllUnSwiped
    private val _changeAllRVSelectedStatus = MutableLiveData<Boolean>()
    private fun changeAllRVSelectedStatus (selected:Boolean){
        _changeAllRVSelectedStatus.value = selected
        if(selected) {
            setSelectedItems(returnParentRVItems().toMutableList())
            setAllRVSelected(true)
        }
        else setSelectedItems(mutableListOf())
    }
    private val _allRVItemSelected = MutableLiveData<Boolean>()
    private fun setAllRVSelected (selected:Boolean){
        _allRVItemSelected.value = selected
    }
    private fun getAllRVItemSelected ():Boolean{
        return _allRVItemSelected.value ?:false
    }

    val changeAllRVSelectedStatus:LiveData<Boolean> = _changeAllRVSelectedStatus

    private val _leftSwipedItemExists = MutableLiveData<Boolean>()
    fun setLeftSwipedItemExists (boolean: Boolean){
        _leftSwipedItemExists.value = boolean

    }

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
            LibraryFragment.Home, LibraryFragment.Folder-> FileStatus.FOLDER
            LibraryFragment.InBox, LibraryFragment.FlashCardCover -> FileStatus.FLASHCARD_COVER
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
    fun openChooseFileMoveTo(file: File?){
        val a  = LibraryChooseFileMoveToFragDirections.selectFileMoveTo(if(file ==null) null else intArrayOf(file.fileId))
       returnLibraryNavCon()?.navigate(a)
    }
    private var _doOnSwipeEnd :()->Unit = {}

    fun setDoOnSwipeEnd(onlyOnce:Boolean,unit:()->Unit){
        val unitBefore = _doOnSwipeEnd
        val finalUnit = if(onlyOnce){
            { unit()
                _doOnSwipeEnd= unitBefore  }
        } else unit
        _doOnSwipeEnd = finalUnit
    }
    val doOnSwipeEnd:()->Unit get() = _doOnSwipeEnd




//    －－－－－－－－

//    －－－－－－－－
    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }

    fun doOnBackPress(): Boolean {
        val isActive = mainViewModel.getFragmentStatus.now == MainFragment.Library
        if(!isActive) return false
        val isHomeFragment = (returnLibraryFragment()== LibraryFragment.Home)
        if(isHomeFragment
            &&!searchViewModel.doOnBackPress()
            &&!returnMultiSelectMode()
            &&!returnMultiMenuVisibility()
            &&!returnLeftSwipedItemExists()) return false
        if(returnLeftSwipedItemExists()) {
            makeAllUnSwiped()
            return true
        }
        if(returnMultiSelectMode()){
            if(returnMultiMenuVisibility())
                setMultiMenuVisibility(false)
            else setMultipleSelectMode(false)
            return true
        }
        if(!isHomeFragment){
            returnLibraryNavCon()?.popBackStack()
            return true
        }
        return true
    }

}