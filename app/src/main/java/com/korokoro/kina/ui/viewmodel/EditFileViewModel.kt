package com.korokoro.kina.ui.viewmodel

import androidx.lifecycle.*
import com.korokoro.kina.db.MyRoomRepository
import com.korokoro.kina.db.dataclass.Card
import com.korokoro.kina.db.dataclass.File
import com.korokoro.kina.db.enumclass.ColorStatus
import com.korokoro.kina.db.enumclass.FileStatus
import com.korokoro.kina.customClasses.ColorPalletStatus
import com.korokoro.kina.customClasses.EditingMode
import com.korokoro.kina.customClasses.MakeToastFromVM
import kotlinx.coroutines.launch

class EditFileViewModel(val repository: MyRoomRepository) : ViewModel() {

    fun getChildFilesByFileIdFromDB(fileId: Int?) = repository.getFileDataByParentFileId(fileId).asLiveData()
    private val _toast = MutableLiveData<MakeToastFromVM>()
    private fun makeToastFromVM(string: String){
        _toast.value = MakeToastFromVM(string,true)
        _toast.value = MakeToastFromVM("",false)
    }

    val toast :LiveData<MakeToastFromVM> = _toast
    fun onCreate(){
        setBottomMenuVisible(false)
    }
    private val _parentTokenFileParent = MutableLiveData<File?>()
    fun setParentTokenFileParent(file: File?){
        _parentTokenFileParent.value = file
    }
    private fun returnParentTokenFileParent():File?{
        return _parentTokenFileParent.value
    }
    private val _position = MutableLiveData<Int>()
    fun setPosition (int: Int){
        _position.value = int
    }
    private fun returnPosition ():Int?{
        return _position.value
    }
    private val _ankiBoxCards = MutableLiveData<List<Card>>()
    fun setAnkiBoxCards (list: List<Card>){
        _ankiBoxCards.value = list
    }
    private fun returnAnkiBoxCards ():List<Card>?{
        return _ankiBoxCards.value
    }

    fun parentFileParent(id:Int?):LiveData<File> = repository.getFileByFileId(id).asLiveData()
    private val _parentFileParent = MutableLiveData<File?>()
    fun setParentFileParent(file: File? ){
        _parentFileParent.value = file
    }

    private val _mode = MutableLiveData<EditingMode>()
    private fun setMode(mode: EditingMode){
        _mode.value = mode
    }
    private fun returnMode(): EditingMode?{
        return _mode.value
    }



//    add Bottom Menu
    class BottomMenuClickable(
        var createFile:Boolean,
        var createFlashCardCover: Boolean,
        var createCard: Boolean
    )
    private val _bottomMenuClickable = MutableLiveData<BottomMenuClickable>()
    val bottomMenuClickable: LiveData<BottomMenuClickable> = _bottomMenuClickable

    private fun setBottomMenuClickable(bottomMenuClickable: BottomMenuClickable){
        _bottomMenuClickable.value = bottomMenuClickable
    }
    fun makeAllBottomMenuClickable(){
        setBottomMenuClickable(
            BottomMenuClickable(
                createFile =  true,
                createFlashCardCover = true,
                createCard = true
            )
        )
    }

    fun filterBottomMenuOnlyCard(){
        setBottomMenuClickable(
            BottomMenuClickable(
                createFile =  false,
                createFlashCardCover = false, createCard = true)
        )
    }
    fun filterBottomMenuWhenInChooseFileMoveTo(flashCard:Boolean,home:Boolean,ancestors: List<File>?){
        setBottomMenuClickable(
            BottomMenuClickable(
                createFile = if(!flashCard)  (ancestors?.size ?: 0) < 3 else false,
                createFlashCardCover = flashCard&&home,
                createCard = false
            )
        )

    }
    fun filterBottomMenuByAncestors(ancestors:List<File>, parentFile:File){
        filterBottomMenuByFile(parentFile)
        val folder = if(_bottomMenuClickable.value!!.createFile){
            ancestors.size < 3
        } else false
        val change = _bottomMenuClickable.value!!
        change.createFile = folder
        setBottomMenuClickable(change)
    }
    private fun filterBottomMenuByFile(file:File){
        setBottomMenuClickable(
            BottomMenuClickable(
            createFile = file.fileStatus != FileStatus.FLASHCARD_COVER,
            createFlashCardCover = file.fileStatus != FileStatus.FLASHCARD_COVER,
            createCard = file.fileStatus == FileStatus.FLASHCARD_COVER
        )

        )

    }



    fun makeBothPopUpGone(){
        setBottomMenuVisible(false)
        setEditFilePopUpVisible(false)
    }
    private val _bottomMenuVisible = MutableLiveData<Boolean>()
    val bottomMenuVisible:LiveData<Boolean> = _bottomMenuVisible
    fun setBottomMenuVisible(boolean: Boolean){
        val before = _bottomMenuVisible.value
        if(before!=boolean){
            _bottomMenuVisible.value = boolean
            if(boolean){
                setEditFilePopUpVisible(false)
            }
        }
    }
    fun returnBottomMenuVisible():Boolean{
        return _bottomMenuVisible.value ?:false
    }



    fun onClickCreateFile(fileStatus: FileStatus){
        if(fileStatus==FileStatus.ANKI_BOX_FAVOURITE&&
                returnAnkiBoxCards().isNullOrEmpty()) return
        setMode(EditingMode.New)
        makeEmptyFileToCreate(fileStatus)
        setTokenFile(returnFileToCreate()!!)
        makeFilePopUp(returnParentTokenFileParent(),
            returnFileToCreate()!!,returnMode()!!)
        setEditFilePopUpVisible(true)
    }
    fun onClickEditFileInRV(editingFile:File){
        setMode(EditingMode.Edit)
        setFileToEdit(editingFile)
        setTokenFile(returnFileToEdit()!!)
        makeFilePopUp(returnParentTokenFileParent(),returnFileToEdit()!!,returnMode()!!)
        setEditFilePopUpVisible(true)
    }

    //    edit file popup Visibility
    private val _editFilePopUpVisible = MutableLiveData<Boolean>()
    val editFilePopUpVisible:LiveData<Boolean> = _editFilePopUpVisible
    fun setEditFilePopUpVisible(boolean: Boolean){
        val before = _editFilePopUpVisible.value
        if(before!=boolean){
            if(boolean){
                setBottomMenuVisible(false)
            }
            _editFilePopUpVisible.value = boolean
        }

    }
    fun returnEditFilePopUpVisible():Boolean{
        return _editFilePopUpVisible.value ?:false
    }

//    Todo popupのクラス作る
    class PopUpUI(
        var txvLeftTopText:String,
        var txvHintText:String,
        var fileStatus:FileStatus,
        var edtTitleText:String,
        var edtTitleHint:String,
        var colorStatus: ColorStatus,
        val parentTokenFile:File,
        var finishBtnText:String
    )
    private val _filePopUpUIData = MutableLiveData<PopUpUI>()
    val filePopUpUIData:LiveData<PopUpUI> = _filePopUpUIData
    private fun setFilePopUpUIData(popUpUI: PopUpUI){
        _filePopUpUIData.value = popUpUI
    }
    private val _tokenFile = MutableLiveData<File>()
    val tokenFile:LiveData<File> = _tokenFile
    private fun setTokenFile(file: File){
        _tokenFile.value = file
    }
    fun returnTokenFile():File?{
        return _tokenFile.value
    }

    private fun makeFilePopUp(tokenFileParent: File?,tokenFile:File, mode: EditingMode){
        fun getLeftTopText():String{
            return if(tokenFileParent!=null) "${tokenFileParent.title} " else "Home"
        }
        fun getHintTxvText():String{
            return when(mode){
                EditingMode.New -> when(tokenFile.fileStatus){
                    FileStatus.FOLDER -> "新しいフォルダを作る"
                    FileStatus.ANKI_BOX_FAVOURITE -> "お気に入りに登録する"
                    FileStatus.FLASHCARD_COVER -> "新しい単語帳を作る"
                    else -> ""
                }
                EditingMode.Edit -> tokenFile.title + "を編集する"
            }
        }
        fun getTitleEdtHint():String{
            return when(mode){
                EditingMode.Edit -> "タイトルを編集"
                EditingMode.New ->  "タイトル"
            }
        }
        fun getTitleEdtText():String{
            return when(mode){
                EditingMode.Edit -> tokenFile.title.toString()
                EditingMode.New ->  ""
            }
        }
        fun getFinishBtnText():String{
            return when(mode){
                EditingMode.Edit -> "更新"
                EditingMode.New -> "作成"
            }
        }
        val a = PopUpUI(
            txvLeftTopText = getLeftTopText(),
            txvHintText = getHintTxvText(),
            edtTitleHint = getTitleEdtHint(),
            edtTitleText = getTitleEdtText(),
            colorStatus = tokenFile.colorStatus,
            fileStatus = tokenFile.fileStatus,
            parentTokenFile = tokenFile,
            finishBtnText = getFinishBtnText()
        )
        setFilePopUpUIData(a)

    }

    private val _colPalletStatus = MutableLiveData<ColorPalletStatus>()
    val colorPalletStatus:LiveData<ColorPalletStatus> = _colPalletStatus
    private fun setColPalletStatus( colorPalletStatus: ColorPalletStatus){
        _colPalletStatus.value = colorPalletStatus
    }
    private fun returnColPalletStatus(): ColorPalletStatus?{
        return _colPalletStatus.value
    }
    fun onClickColorPallet(colorStatus: ColorStatus){
        val beforeStatus = _colPalletStatus.value
        if(beforeStatus?.colNow!=colorStatus){
            val new = ColorPalletStatus(colorStatus, beforeStatus?.colNow)
            setColPalletStatus(new)
        }
    }


    val lastInsertedFile:LiveData<File> = repository.lastInsertedFile.asLiveData()
    private val _lastInsertedFile = MutableLiveData<File>()
    fun setLastInsertedFile(file: File?){
        val before = _lastInsertedFile.value
        if(before == file|| file==null)
            return
        else _lastInsertedFile.value = file!!
    }
    private fun returnLastInsertedFileId():Int?{
       return _lastInsertedFile.value?.fileId
    }
     fun returnLastInsertedFile():File?{
        return _lastInsertedFile.value
    }

    private val _fileToCreate = MutableLiveData<File>()
    private fun setFileToCreate(file: File){
        _fileToCreate.value = file
    }
    private fun returnFileToCreate():File?{
        return _fileToCreate.value
    }
    private val _parentFileSisters = MutableLiveData<List<File>>()
    val parentFileSisters:LiveData<List<File>> = _parentFileSisters
    fun setParentFileSisters(list: List<File>){
        _parentFileSisters.value = list
    }
    fun returnParentFileSisters():List<File>{
        return _parentFileSisters.value ?: mutableListOf()
    }
    private fun makeEmptyFileToCreate(fileStatus:FileStatus){
        setFileToCreate(
            File(fileId = 0,
                title = null,
                fileStatus = fileStatus ,
                colorStatus = ColorStatus.GRAY,
                deleted = false,
                fileBefore = returnParentFileSisters().lastOrNull()?.fileId,
                parentFileId = returnParentTokenFileParent()?.fileId
            ))
    }
    private val _fileToEdit = MutableLiveData<File>()
    private fun setFileToEdit(file: File){
        _fileToEdit.value = file
    }
    private fun returnFileToEdit():File?{
        return _fileToEdit.value
    }

    fun makeFilePos0(){
        val a = returnFileToCreate()?:return
        a.fileBefore = 0
        setFileToCreate(a)
    }

    fun onClickFinish(title:String){
        val color = returnColPalletStatus()?.colNow ?:ColorStatus.GRAY
        when(_mode.value ){
            EditingMode.New -> {
                val newFile = returnFileToCreate() ?:return
                newFile.title = title
                newFile.colorStatus = color
                if(newFile.fileStatus==FileStatus.ANKI_BOX_FAVOURITE){
                    val cardList = returnAnkiBoxCards()
                    if(cardList == null) makeToastFromVM("anki box card not set")
                    else if(cardList.isEmpty()) makeToastFromVM("anki box card empty")
                    addCardsToFavouriteAnkiBox(cardList ?:return,returnLastInsertedFileId() ?:0,newFile)
                }
                else insertFile(newFile)
            }
            EditingMode.Edit -> {
                val editingFile = returnFileToEdit()
                if(editingFile==null){
                    makeToastFromVM("file to edit なし")
                    return
                }else{
                    editingFile.title = title
                    editingFile.colorStatus = color
                    upDateFile(editingFile)
                }
            }
            else -> return
        }
        setEditFilePopUpVisible(false)
    }
    private fun addCardsToFavouriteAnkiBox(list:List<Card>,lastInsertedFileId:Int,favFile:File){
        viewModelScope.launch {
            repository.saveCardsToFavouriteAnkiBox(list,lastInsertedFileId,favFile)
        }
    }

    private fun insertFile(file: File){
        viewModelScope.launch {
            repository.insert(file)
        }
    }

    private fun upDateFile(file: File){
        viewModelScope.launch {
            repository.update(file)
        }
    }





    private val _text = MutableLiveData<String>().apply {
        value = "This is planner Fragment"
    }
    val text: LiveData<String> = _text
}