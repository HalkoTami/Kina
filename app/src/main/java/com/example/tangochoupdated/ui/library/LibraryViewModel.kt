package com.example.tangochoupdated.ui.library

import android.view.View
import android.widget.ImageView
import androidx.compose.runtime.internal.illegalDecoyCallException
import androidx.core.view.children
import androidx.lifecycle.*
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.recyclerview.widget.RecyclerView
import com.example.tangochoupdated.R
import com.example.tangochoupdated.databinding.CreateCardBaseBinding
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
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class LibraryViewModel(private val repository: MyRoomRepository) : ViewModel() {

    fun onStart(){
        setMenuStatus(false)
        setMultipleSelectMode(false)
        clearChildrenStock()
        clearFinalList()
        setSelectedItem(mutableListOf())
    }

    //    parentItemId
    private val _parentItemId = MutableLiveData<Int>()
    fun setParentItemId (id:Int?){
        _parentItemId.value = id








    }

//    DBアクセス関連
    //    child Files from DB
    fun childFilesFromDB(int: Int?):LiveData<List<File>> = this.repository.mygetFileDataByParentFileId(int).asLiveData()
    private val _childFilesFromDB = MutableLiveData<List<File>>()
    fun setChildFilesFromDB (list:List<File>?,home: Boolean){

        _childFilesFromDB.value = list
        if(home){
            return
        } else{
            val b = mutableListOf<LibraryRV>()
            val a = mutableListOf<File>()
//            b.addAll(childrenStock)
//            clearChildrenStock()
            a.addAll(list!!)
            a.onEach {  b.add(convertFileToLibraryRV(it)) }

            if(b.size != 0){

                clearFinalList()

                setValueToFinalList(b.distinct())


                setFileEmptyStatus(false,false)
            } else setFileEmptyStatus(true,false)


        }




    }
//    child Cards From DB
    fun childCardsFromDB(int: Int?):LiveData<List<CardAndTags>?> =  this.repository.getCardDataByFileId(int).asLiveData()
    private val _childCardsFromDB=MutableLiveData<List<CardAndTags>>()
    fun setChildCardsFromDB(list:List<CardAndTags>?,home: Boolean){

        _childCardsFromDB.value = list

        when(home){
            true -> return
            false ->{
                if(_myParentItem.value?.file?.fileStatus == FileStatus.TANGO_CHO_COVER){
                    val a = mutableListOf<LibraryRV>()
                    list!!.onEach {
                        a.add(convertCardToLibraryRV(it))
                    }

                    if(a.size != 0){
                        setFileEmptyStatus(false,false)
                    } else setFileEmptyStatus(true,false)
                    setValueToFinalList(a)

                }

            }
        }


    }



//file without parent from DB

    fun  fileWithoutParentFromDB():LiveData<List<File>?> = this.repository.mygetFileDataByParentFileId(null).asLiveData()
    private val _fileWithoutParentFromDB= MutableLiveData<List<File>>()

    fun setFileWithoutParentFromDB(list:List<File>?,home: Boolean){

        _fileWithoutParentFromDB.value = list


        when(home ){
            true -> {
               if(list!=null){
                   setFileEmptyStatus(false,true)
                   val a = mutableListOf<LibraryRV>()
                   list.onEach {
                       a.add(convertFileToLibraryRV(it))
                   }
                   setValueToFinalList(a)
               } else setFileEmptyStatus(true,true)
            }
            else -> {
                return
            }
        }


    }



    //    RVList
    private val _myFinalList= MutableLiveData<List<LibraryRV>>()
    val myFinalList :LiveData<List<LibraryRV>> = _myFinalList
    private fun setValueToFinalList(list:List<LibraryRV>){
        val comparator : Comparator<LibraryRV> = compareBy { it.id }
        val a = list.sortedWith(comparator)
        _myFinalList.apply {
            value = a
        }


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
    val childrenStock = mutableListOf<LibraryRV>()
    private fun clearChildrenStock(){
        childrenStock.removeAll(childrenStock)
    }





    //    parent File from DB
    fun  parentFileFromDB(int: Int?):LiveData<File?> = repository.getFileByFileId(int).asLiveData()

    fun setParentFileFromDB (file: File?,home: Boolean){
        val a = mutableListOf<File>()

        when(file){
            null -> {
                setMyParentItem(null)
            }
            else ->{
                val a = convertFileToLibraryRV(file)
                setMyParentItem(a)

            }
        }
    }
    fun pAndGP(parentId:Int?):LiveData<List<File>?> = repository.getPAndGPFiles(parentId).asLiveData()
    val _pAndgGP = MutableLiveData<List<File>?>()
    fun setPAndG(list: List<File>?){
           _pAndgGP.value = list
    }


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
            when(_fileEmptyStatus.value){
                null -> setFileEmptyText("ロード中")
                true -> setFileEmptyText("${item.file?.title}は空です")
            }
        }

    }

    //    home or not done
    private val _homeStatus =  MutableLiveData<Boolean>()
    private fun setHomeStatus(boolean: Boolean){
        _homeStatus.apply {
            value = boolean
        }
        when(boolean){
            true ->{
                setMLDTopText("home")
                setTopBarLeftIMVDrawableId(R.drawable.icon_eye_opened)
                setTopBarRightIMVDrawableId(R.drawable.icon_inbox)
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
            a.tag = HomeFragment.MyState.Unselected
        }

    }

    //multiSelectMode
    val _multipleSelectMode =  MutableLiveData<Boolean>()

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
//                makeAllUnSelectableUnSelected()
                when(_homeStatus.value){
                    true -> setTopBarRightIMVDrawableId(R.drawable.icon_inbox)
                }


            }
        }


    }



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
//    fun changeItemSelected(item: LibraryRV,boolean: Boolean){
//        val a = mutableListOf<LibraryRV>()
//        a.addAll(_myFinalList.value!!)
//        a.remove(item)
//        item.selected = boolean
//        a.add(item)
//        setValueToFinalList(a)
//
//
//    }

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

    private val _topBarLeftIMVDrawableId = MutableLiveData<Int>()
    private fun setTopBarLeftIMVDrawableId(int: Int){
        _topBarLeftIMVDrawableId.value= int

    }

    val topBarLeftIMVDrawableId:LiveData<Int> = _topBarLeftIMVDrawableId



//    top right imv
    private val _topBarRightIMVDrawableId = MutableLiveData<Int>()
    private fun setTopBarRightIMVDrawableId(int: Int){
        val before = _topBarLeftIMVDrawableId.value
        if(before == int){
            return
        }else {
            _topBarRightIMVDrawableId.value= int
        }


    }

    val topBarRightIMVDrawableId:LiveData<Int> = _topBarRightIMVDrawableId

//    top right IMV click



//    file is  empty or not done
    private val _fileEmptyStatus =  MutableLiveData<Boolean>()
    private fun setFileEmptyStatus(empty: Boolean,home: Boolean){
        _fileEmptyStatus.apply {
            value = empty
        }
        if(empty){
            when(home){
                true -> setFileEmptyText("まだデータがありません")
                false -> {
                    if(_myParentItem.value == null){
                        setFileEmptyText("ロード中")
                    } else{
                        setFileEmptyText("${_myParentItem.value?.file?.title}は空です")
                    }
                }
                null -> return
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


    val navCon= MutableLiveData<NavController>()
    fun setNavCon(navController: NavController){
        navCon.value = navController
    }
//    navigation
private val _action = MutableLiveData<NavDirections>()
    val action: LiveData<NavDirections> = _action
    fun setAction(navDirections: NavDirections){
        _action.value = navDirections
        navCon.value!!.navigate(navDirections)
    }


//    onclickEvents

    fun onClickImvFileStatusOrClose(recyclerView: RecyclerView){
        when(_multipleSelectMode.value){
            true -> {
                makeRVUnSelectable(recyclerView)

                setMultipleSelectMode(false)
                setHomeStatus(_homeStatus.value!!)

            }
            else -> return
        }
    }
    fun onClickimvSwitchMenu(){
        if(_homeStatus.value != true  ) {
            when (_menuOpened.value) {
                true -> setMenuStatus(false)
                false -> setMenuStatus(true)
                else -> throw IllegalArgumentException("is null")

            }
        }


    }
    fun changeItemSelected(item: LibraryRV, boolean: Boolean,btnSelect: ImageView) {
        if (boolean)  {
            btnSelect.isSelected = true
            btnSelect.tag = HomeFragment.MyState.Selected
            addToSelectedItem(item)

        } else {
            removeFromSelectedItem(item)

            btnSelect.isSelected = false
            btnSelect.tag = HomeFragment.MyState.Unselected
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