package com.example.tangochoupdated.ui.library

import android.view.View
import android.widget.ImageView
import androidx.compose.runtime.internal.illegalDecoyCallException
import androidx.core.view.children
import androidx.lifecycle.*
import androidx.navigation.NavDirections
import androidx.recyclerview.widget.RecyclerView
import com.example.tangochoupdated.R
import com.example.tangochoupdated.databinding.ItemCoverCardBaseBinding
import com.example.tangochoupdated.room.MyRoomRepository
import com.example.tangochoupdated.room.dataclass.Card
import com.example.tangochoupdated.room.dataclass.CardAndTags
import com.example.tangochoupdated.room.dataclass.File
//import com.example.tangochoupdated.room.dataclass.FileWithChild
import com.example.tangochoupdated.room.enumclass.CardStatus
import com.example.tangochoupdated.room.enumclass.FileStatus
import com.example.tangochoupdated.room.rvclasses.LibRVViewType
import com.example.tangochoupdated.room.rvclasses.LibraryRV
import com.example.tangochoupdated.ui.create.Mode
import io.reactivex.Single
import io.reactivex.rxkotlin.toObservable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.launch

class LibraryViewModel(private val repository: MyRoomRepository) : ViewModel() {

    fun onStart(){
        setMenuStatus(false)
        setMultipleSelectMode(false)
        clearFinalList()
        setSelectedItem(mutableListOf())
        setConfirmPopUpVisible(false,ConfirmMode.DeleteOnlyParent)
    }

    //    parentItemId
//    private val _parentItemId = MutableLiveData<Int>()
//    fun setParentItemId (id:Int?){
//        _parentItemId.value = id
//    }
//    val parentItemId:LiveData<Int> = _parentItemId

//    DBアクセス関連
//    parent File from DB
    fun  parentFileFromDB(int: Int?):LiveData<File?> = repository.getFileByFileId(int).asLiveData()
    fun setParentFileFromDB (file: File?,home: Boolean){
        setMyParentItem(
            if(file==null) null
            else convertFileToLibraryRV(file)
        )
        setHomeStatus(file==null)
        _parentFile.value = file
    }
    private val _parentFile = MutableLiveData<File>()
    val parentFile:LiveData<File> = _parentFile


    //    child Files from DB
    fun childFilesFromDB(int: Int?):LiveData<List<File>> = this.repository.mygetFileDataByParentFileId(int).asLiveData()
    private val _childFilesFromDB = MutableLiveData<List<File>>()
    fun setChildFilesFromDB (list:List<File>?,home: Boolean){

        _childFilesFromDB.value = list

        if(_myParentItem.value?.file?.fileStatus != FileStatus.TANGO_CHO_COVER){
            clearFinalList()
            val b = mutableListOf<LibraryRV>()
            list?.onEach { b.add(convertFileToLibraryRV(it)) }
            setValueToFinalList(b)
        }
    }
//    child Cards From DB
    fun childCardsFromDB(int: Int?):LiveData<List<CardAndTags>?> =  this.repository.getCardDataByFileId(int).asLiveData()
    private val _childCardsFromDB=MutableLiveData<List<CardAndTags>>()
    fun setChildCardsFromDB(list:List<CardAndTags>?,home: Boolean){
        _childCardsFromDB.value = list
        if(_myParentItem.value?.file?.fileStatus == FileStatus.TANGO_CHO_COVER){
            val b = mutableListOf<LibraryRV>()
            clearFinalList()
            list?.onEach { b.add(convertCardToLibraryRV(it)) }
            setValueToFinalList(b)
        }
    }
    val childCardsFromDB:LiveData<List<CardAndTags>> = _childCardsFromDB


    //    RVList
    private val _myFinalList= MutableLiveData<List<LibraryRV>>()
    val myFinalList :LiveData<List<LibraryRV>> = _myFinalList
    private fun setValueToFinalList(list:List<LibraryRV>){
        val comparator : Comparator<LibraryRV> = compareBy { it.position }
        val a = list.sortedWith(comparator)
        _myFinalList.apply {
            value = a
        }
        setFileEmptyStatus(a.isEmpty())
    }

    fun makeAllSelectable(){
        val a = mutableListOf<LibraryRV>()
        _myFinalList.value?.onEach { if(!it.selectable ){
            it.selectable = true
        }
            a.add(it)}
        setValueToFinalList(a)
    }
    fun makeAllUnSelectableUnSelected(){
        val a = mutableListOf<LibraryRV>()
        _myFinalList.value?.onEach { if(it.selectable ){
            it.selectable = false
        }
            a.add(it)}
        setValueToFinalList(a)
        setSelectedItem(mutableListOf())
    }



    fun makeItemLeftSwiped(){
//        val a = mutableListOf<LibraryRV>()
//        _myFinalList.value?.onEach { if(it.position == position){
//            it.leftSwiped = true
//        }
//            a.add(it)}
//        setValueToFinalList(a)
        setLeftSwipedItemExists(true)
    }
    private fun makeAllItemNotLeftSwiped(){
        val a = mutableListOf<LibraryRV>()
        _myFinalList.value?.onEach { if(it.leftSwiped){
            it.leftSwiped = false
        }
            a.add(it)}
        setValueToFinalList(a)
        setLeftSwipedItemExists(false)
    }

    private fun clearFinalList(){
        val a = mutableListOf<LibraryRV>()
        _myFinalList.value = a
    }

//    parent generation
    fun pAndGP(parentId:Int?):LiveData<List<File>?> = repository.getPAndGPFiles(parentId).asLiveData()
    private val _pAndgGP = MutableLiveData<List<File>?>()
    fun setPAndG(list: List<File>?){
        _pAndgGP.value = list
    }
    val allAncestors :LiveData<List<File>?> = _pAndgGP

//    fun getAllDescendants(parentIdList:List<Int>?):LiveData<List<File>?> = repository.getAllDescendantsByFileId(parentIdList).asLiveData()
//    private val _allDescendants = MutableLiveData<List<File>?>()
//    fun setAllDescendants(list: List<File>?){
//        _allDescendants.value = list
//    }

    //    parent item as Library RV
    private val _myParentItem = MutableLiveData<LibraryRV?>()

    private fun setMyParentItem(item:LibraryRV?){
        _myParentItem.apply {
            value = item
        }
        if(item == null){
            setHomeStatus(true)
        } else{
            setHomeStatus(false)
            if(_fileEmptyStatus.value == true){
                setFileEmptyText("${item.file!!.title}は空です")
            }
        }
        val b = mutableListOf<LibraryRV>()
        clearFinalList()
        when(item?.type){
            LibRVViewType.FlashCardCover-> {
                _childCardsFromDB.value?.onEach {
                    b.add(convertCardToLibraryRV(it))
                }
            }
            else -> {

                _childFilesFromDB.value?.onEach { b.add(convertFileToLibraryRV(it)) }

            }
        }
        setValueToFinalList(b)
    }

    //    home or not done
    private val _homeStatus =  MutableLiveData<Boolean>()
    private fun setHomeStatus(boolean: Boolean){
        _homeStatus.apply {
            value = boolean
        }
        when(boolean){
            true ->{
                setTopBarLeftIMVDrawableId(R.drawable.icon_eye_opened)
                setTopBarRightIMVDrawableId(R.drawable.icon_inbox)
                setMLDTopText("home")
            }
            false -> {
                setMLDTopText("${_myParentItem.value!!.file!!.fileId}")
                setTopBarLeftIMVDrawableId(
                    when(_myParentItem.value!!.file!!.fileStatus){
                        FileStatus.FOLDER ->R.drawable.icon_file
                        FileStatus.TANGO_CHO_COVER ->R.drawable.icon_library_plane
                        else -> throw  IllegalArgumentException("file status not appropriate")
                    }
                )
            }

        }

    }

    private fun makeRVUnSelectable(recyclerView: RecyclerView){
        recyclerView.children.iterator().forEachRemaining {
            val a = it.findViewById<ImageView>(R.id.btn_select)
            a.visibility = View.GONE
            a.isSelected = false

        }

    }

    //multiSelectMode
    private val _multipleSelectMode =  MutableLiveData<Boolean>()
    fun setMultipleSelectMode(boolean: Boolean){
        _multipleSelectMode.apply {
            value = boolean
        }

        when(boolean){
            true ->{
//                makeAllSelectable()
                setTopBarLeftIMVDrawableId(R.drawable.icon_close)
                setTopBarRightIMVDrawableId(R.drawable.icon_dot)

            }
            false -> {
                setSelectedItem(mutableListOf())
                if(_homeStatus.value==true){
                    setMLDTopText("home")
                    setTopBarRightIMVDrawableId(R.drawable.icon_inbox)
                    setTopBarLeftIMVDrawableId(R.drawable.icon_eye_opened)
                }


            }
        }


    }
    val multipleSlectMode:LiveData<Boolean> = _multipleSelectMode

    //selected Items
    private val _selectedItems = MutableLiveData<List<LibraryRV>>()
    private fun setSelectedItem(list:List<LibraryRV>){
        _selectedItems.value = list
        if(_multipleSelectMode.value==true){
            setMLDTopText("${list.size}個　選択中")
        }

    }
    fun addToSelectedItem(item: LibraryRV){
        if(_fileEmptyStatus.value==false){
            if(_multipleSelectMode.value == true){
                val a = mutableListOf<LibraryRV>()
                a.addAll(_selectedItems.value!!)
                item.selected = true
                a.add(item)
                setSelectedItem(a)
            }
        }

    }
    fun removeFromSelectedItem(item: LibraryRV){
        val a = mutableListOf<LibraryRV>()
        a.addAll(_selectedItems.value!!)
        a.remove(item)
        setSelectedItem(a)
    }


//    topBarText

    private val _topText = MutableLiveData<String>()
    private fun setMLDTopText(text:String){
        _topText.value= text
    }
    val topText:LiveData<String> = _topText

//    Top Left Icon
    class DrawableSetting(
    val drawableId:Int,
    val state:Int
    )
    private val _topBarLeftIMVDrawable = MutableLiveData<Int>()
    private fun setTopBarLeftIMVDrawableId(int: Int){
        val before = _topBarLeftIMVDrawable.value
        if(before == int){
            return
        }else {
            _topBarLeftIMVDrawable.value= int
        }

    }
    val topBarLeftIMVDrawableId:LiveData<Int> = _topBarLeftIMVDrawable

    //    top right imv
    private val _topBarRightIMVDrawableId = MutableLiveData<Int>()
    private fun setTopBarRightIMVDrawableId(int: Int){
        val before = _topBarRightIMVDrawableId.value
        if(before == int){
            return
        }else {
            _topBarRightIMVDrawableId.value= int
        }
    }
    val topBarRightIMVDrawableId:LiveData<Int> = _topBarRightIMVDrawableId

    //    file is  empty or not done
    private val _fileEmptyStatus =  MutableLiveData<Boolean>()
    private fun setFileEmptyStatus(empty: Boolean){
        _fileEmptyStatus.apply {
            value = empty
        }
        if(empty){
            if(_homeStatus.value==false){
                setFileEmptyText("${_myParentItem.value?.file?.title}は空です")
            }

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
    val fileEmptyText:LiveData<String> = _fileEmptyText


//menu Visibility done

    private val _menuOpened =  MutableLiveData<Boolean>()
    private fun setMenuStatus(boolean: Boolean){
        _menuOpened.apply {
            value = boolean
        }
    }
    val menuViewMode:LiveData<Boolean> = _menuOpened
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

    //menu Visibility done
    private val _leftSwipedItemExists =  MutableLiveData<Boolean>()
    fun setLeftSwipedItemExists(boolean: Boolean){
        _leftSwipedItemExists.apply {
            value = boolean
        }
    }



    fun checkReset():Boolean{
        if(_menuOpened.value == true||
            _leftSwipedItemExists.value == true){
            setMenuStatus(false)
            return true
        } else return false
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


//    val navCon= MutableLiveData<NavController>()
//    fun setNavCon(navController: NavController){
//        navCon.value = navController
//    }
//    navigation
private val _action = MutableLiveData<NavDirections>()
    val action: LiveData<NavDirections> = _action
    fun setAction(navDirections: NavDirections){
        _action.value = navDirections

    }


//    onclickEvents
    fun onClickimvSwitchMenu(){
        if(_homeStatus.value != true  ) {
            when (_menuOpened.value) {
                true -> setMenuStatus(false)
                false -> setMenuStatus(true)
                else -> throw IllegalArgumentException("is null")

            }
        }


    }
    fun changeItemSelected(item: LibraryRV, boolean: Boolean) {
        if (boolean)  {
            addToSelectedItem(item)

        } else {
            removeFromSelectedItem(item)


        }


    }
    fun changeItemSelected(item: LibraryRV, boolean: Boolean,btnSelect: ImageView) {
        if (boolean)  {
            btnSelect.isSelected = true
            addToSelectedItem(item)

        } else {
            removeFromSelectedItem(item)

            btnSelect.isSelected = false

        }


    }
    fun onClickSelectableItem(item: LibraryRV,boolean: Boolean){
        if (boolean)  {
            addToSelectedItem(item)
        } else {
            removeFromSelectedItem(item)
        }
    }

    fun onClickRVItem(rvBinding: ItemCoverCardBaseBinding,item: LibraryRV){
        if(_multipleSelectMode.value == true){
            val select = when(rvBinding.btnSelect.isSelected){
                true -> false
                false -> true
            }
            changeItemSelected(item,select,rvBinding.btnSelect)

        } else{
            val action = HomeFragmentDirections.libraryToLibrary()
            action.parentItemId = intArrayOf(item.id)
            setAction(action)
        }
    }
    fun openNextFile(item: LibraryRV){
        val action = HomeFragmentDirections.libraryToLibrary()
        action.parentItemId = intArrayOf(item.id)
        setAction(action)
    }


//    fun onDelete(){
//        when(_multipleSelectMode.value){
//            true -> deleteSelectedItems()
//            false -> deleteParentItem()
//            null -> throw IllegalArgumentException("multipleSelectMode not set yet ")
//        }
//        setMultipleSelectMode(false)
//        setMenuStatus(false)
//    }
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

    private fun safeDeleteSelectedItems(){
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
//    private fun deleteSelectedItems(){
//        val file = mutableListOf<File>()
//        val cards = mutableListOf<Card>()
//        _selectedItems.value?.onEach {
//            when(it.type){
//                LibRVViewType.Folder,LibRVViewType.FlashCardCover -> {
//                    file.add(it.file!!)
//                }
//                else -> {
//                    cards.add(it.card!!)
//
//                }
//            }
//
//        }
//        deleteMultiple(file)
//        deleteMultiple(cards)
//
//
//    }


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
    val deletingItemChildren:LiveData<List<File>?> = _deletingItemChildrenFiles



//    シングル削除
//    private fun deleteSingleItem(item:Any){
//        when(item){
//            is File
//        }
//
//    }


//    private fun deleteSingle(item: Any){
//        viewModelScope.launch {
//            repository.delete(item)
//        }
//    }


//    fun deleteMultiple(list:List<LibraryRV>,deleteChildren:Boolean){
//        val files = mutableListOf<File>()
//        val cards = mutableListOf<Card>()
//        list.onEach { when(it.type){
//            LibRVViewType.Folder,LibRVViewType.FlashCardCover -> files.add(it.file!!)
//            else -> cards.add(it.card!!) }
//        }
//        if (list.isNotEmpty()){
//            viewModelScope.launch {
//                if(deleteChildren){
//                    files.onEach { repository.upDateChildFilesOfDeletedFile() }
//                }
//                repository.deleteMultiple(files)
//
//                repository.deleteMultiple(cards)
//            }
//        }
//
//    }

    fun deleteSingleFile(file:File,deleteChildren:Boolean){
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