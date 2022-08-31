package com.example.tangochoupdated.ui.viewmodel

import android.text.Editable
import android.view.View
import androidx.lifecycle.*
import com.example.tangochoupdated.R
import com.example.tangochoupdated.db.MyRoomRepository
import com.example.tangochoupdated.db.dataclass.File
import com.example.tangochoupdated.db.dataclass.FileXRef
import com.example.tangochoupdated.db.enumclass.ColorStatus
import com.example.tangochoupdated.db.enumclass.FileStatus
import com.example.tangochoupdated.db.enumclass.Mode
import kotlinx.coroutines.launch

class CreateFileViewModel(val repository: MyRoomRepository) : ViewModel() {

    fun onCreate(){
        setAddFileActive(false)
    }
    private val _parentFile = MutableLiveData<File?>()
    val parentFile:LiveData<File?> = _parentFile
    fun setParentFile(file: File?){
        _parentFile.value = file
//        setPAndG(getpAndGP(file?.parentFileId))
//        setParentFileParent(getParentFile(file?.parentFileId))
//        when(file){
//            null ->{
//                setTxvLeftTop("home")
//            }
//            else -> {
//                when(_mode.value){
//                    Mode.New -> setTxvLeftTop("${file.title.toString()} >")
//                    Mode.Edit -> setTxvLeftTop("")
//                }
//            }
//        }
//        setParentFileStock(file)
    }
    val _position = MutableLiveData<Int>()
    fun setPosition (int: Int){
        _position.value = int
    }


    fun parentFileParent(id:Int?):LiveData<File> = repository.getFileByFileId(id).asLiveData()
    val _parentFileParent = MutableLiveData<File?>()
    fun setParentFileParent(file: File? ){
        _parentFileParent.value = file
    }


    fun allAncestors(fileId:Int?):LiveData<List<File>?> = repository.getAllAncestorsByFileId(fileId).asLiveData()
    val _pAndgGP = MutableLiveData<List<File>?>()
    fun setPAndG(list: List<File>?){
        _pAndgGP.value = list
        val file:Boolean
        val flashCardCover:Boolean
        val card:Boolean

//        when(_parentFile.value?.fileStatus){
//            null -> {
//                card = true
//                file = true
//                flashCardCover = true
//            }
//            FileStatus.FOLDER -> {
//
//                card = false
//                if(list!!.size < 3 ) {
//                    file = true
//                    flashCardCover = true
//                } else {
//                    file = false
//                    flashCardCover = list.size < 4
//                }
//            }
//            FileStatus.TANGO_CHO_COVER -> {
//                file = false
//                flashCardCover = false
//                card = true
//            }
//            else ->{
//                file = false
//                flashCardCover = false
//                card = false
//            }
//
//        }
//        setBottomMenuClickable(
//            BottomMenuClickable(file,flashCardCover,card
//            )
//        )
    }

    private val _parentFileStock = MutableLiveData<List<File>>()
    val parentFileStock:LiveData<List<File>> = _parentFileStock
    fun setParentFileStock(file: File?){
        val a = mutableListOf<File>()
        if(file!=null){
            if(_parentFileStock.value!=null){
                a.addAll(_parentFileStock.value!!)

            }
            if (a.contains(file)){
                a.removeAt(a.size-1)
            } else{
                a.add(file)
            }
        }

        _parentFileStock.value = a
    }


    private val _mode = MutableLiveData<Mode>()
    fun setMode(mode: Mode){
        _mode.value = mode
    }






    private val _addFileActive = MutableLiveData<Boolean>()
    val addFileActive : LiveData<Boolean> = _addFileActive

    fun setAddFileActive(boolean:Boolean){
        _addFileActive.value = boolean
        if(boolean){
            when(_mode.value){
                Mode.New -> setBottomMenuVisible(true)
                Mode.Edit -> setEditFilePopUpVisible(true)
                else -> return
            }
        }

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
            true,true,true
        )
        )
    }

    fun filterBottomMenuWhenInBox(){
        setBottomMenuClickable(
            BottomMenuClickable(
                createFile =  false, createFlashCardCover = false, createCard = true)
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
            createFile = file.fileStatus != FileStatus.TANGO_CHO_COVER,
            createFlashCardCover = file.fileStatus != FileStatus.TANGO_CHO_COVER,
            createCard = file.fileStatus == FileStatus.TANGO_CHO_COVER
        )

        )

    }



    private val _bottomMenuVisible = MutableLiveData<Boolean>()
    val bottomMenuVisible:LiveData<Boolean> = _bottomMenuVisible
    private fun setBottomMenuVisible(boolean: Boolean){
        _bottomMenuVisible.value = boolean
        if(boolean){
            setEditFilePopUpVisible(false)
        }

    }





    fun onClickCreateFolder(){
        makeNewFilePopUp(_parentFile.value,FileStatus.FOLDER)
        makeEmptyFileToCreate(FileStatus.FOLDER)
        setMode(Mode.New)
//        if(_bottomMenuClickable.value!!.createFile){
//            setEditFilePopUpVisible(true)
//            setCreatingFileType(FileStatus.FOLDER)
//        }
    }
    fun onCLickCreateFlashCardCover(){
        makeNewFilePopUp(_parentFile.value,FileStatus.TANGO_CHO_COVER)
        makeEmptyFileToCreate(FileStatus.TANGO_CHO_COVER)
        setMode(Mode.New)
//        if(_bottomMenuClickable.value!!.createFlashCardCover){
//            setEditFilePopUpVisible(true)
//            setCreatingFileType(FileStatus.TANGO_CHO_COVER)
//        }
    }
    fun onClickEditFileTopBar(){
        setMode(Mode.Edit)
        makeEditFilePopUp(_parentFileParent.value,_parentFile.value!!)
        setFileToEdit(_parentFile.value!!)
    }
    fun onClickEditFileInRV(editingFile:File){
        setMode(Mode.Edit)
        makeEditFilePopUp(_parentFile.value,editingFile)
        setFileToEdit(editingFile)
    }

    //    edit file popup Visibility
    private val _editFilePopUpVisible = MutableLiveData<Boolean>()
    val editFilePopUpVisible:LiveData<Boolean> = _editFilePopUpVisible
    private fun setEditFilePopUpVisible(boolean: Boolean){
        _editFilePopUpVisible.value = boolean
        if(boolean){
            setBottomMenuVisible(false)
        }

    }


    private val _creatingFileType = MutableLiveData<FileStatus>()
    val creatingFileType: LiveData<FileStatus> = _creatingFileType
    private fun setCreatingFileType(creatingFileType: FileStatus){
        _creatingFileType.value = creatingFileType
        when(creatingFileType){
            FileStatus.FOLDER -> {
                setDrawFileType(R.drawable.icon_file_1)
                setTxvHint("新しいフォルダを作る")
                setEdtHint("フォルダのタイトル")

            }
            FileStatus.TANGO_CHO_COVER ->{
                setDrawFileType(R.drawable.icon_library_plane)
                setTxvHint("新しい単語帳を作る")
                setEdtHint("単語帳のタイトル")

            }
            else -> return
        }
    }
//    Todo popupのクラス作る
    class PopUpUI(
        var visibility:Int,
        var txvLeftTopText:String,
        var txvHintText:String,
        var drawableId:Int,
        var edtTitleText:String,
        var edtTitleHint:String,
        var colorStatus: ColorStatus,
    )
    private val _filePopUpUIData = MutableLiveData<PopUpUI>()
    val filePopUpUIData:LiveData<PopUpUI> = _filePopUpUIData
    private fun setFilePopUpUIData(popUpUI: PopUpUI){
        _filePopUpUIData.value = popUpUI
    }
    private fun makeNewFilePopUp(parentfile: File?, fileType:FileStatus){
        val a = PopUpUI(
            visibility = View.VISIBLE,
            txvLeftTopText = if(parentfile!=null) "${parentfile.title} >" else "home",
            txvHintText = if(fileType == FileStatus.FOLDER) "新しいフォルダを作る"
            else "新しい単語帳を作る",
            drawableId = if(fileType == FileStatus.FOLDER) R.drawable.icon_file
            else R.drawable.icon_library_plane,
            edtTitleHint = "タイトル",
            edtTitleText = "",
            colorStatus = ColorStatus.GRAY
        )
        setFilePopUpUIData(a)

    }
    private fun makeEditFilePopUp(parentFile: File?, editingFile:File){
        val a = PopUpUI(
            visibility = View.VISIBLE,
            txvLeftTopText = if(parentFile!=null) "${parentFile.title} >" else "home",
            txvHintText = "${editingFile.title}を編集する",
            drawableId =  if(editingFile.fileStatus == FileStatus.FOLDER) R.drawable.icon_file
            else R.drawable.icon_library_plane,
            edtTitleHint = "タイトルを編集",
            edtTitleText = "${editingFile.title}",
            colorStatus = editingFile.colorStatus
        )
        setFilePopUpUIData(a)
    }
    private fun makePopUpUIInvisible(){
        val update = _filePopUpUIData.value
        update!!.visibility = View.GONE
        setFilePopUpUIData(update)
    }

    private val _txvLeftTop = MutableLiveData<String>()
    val txvLeftTop:LiveData<String> = _txvLeftTop

    private fun setTxvLeftTop(string: String){
        _txvLeftTop.value = string
    }
    private val _txvHint = MutableLiveData<String>()
    val txvHintText:LiveData<String> = _txvHint

    private fun setTxvHint(string: String){
        _txvHint.value = string
    }
    private val _txvFileTitleText = MutableLiveData<Editable>()
    val txvFileTitleText:LiveData<Editable> = _txvFileTitleText

    private fun setTxvFileTitleText(editable: Editable){
        _txvFileTitleText.value = editable
    }

    private val _drawFileType = MutableLiveData<Int>()
    val drawFileType:LiveData<Int> = _drawFileType
    private fun setDrawFileType(int: Int){
        _drawFileType.value = int
    }



    private val _edtHint = MutableLiveData<String>()
    val edtHint:LiveData<String> = _edtHint
    private fun setEdtHint(string: String){
        _edtHint.value = string
    }

    private val _fileColor = MutableLiveData<ColorStatus>()
    val fileColor: LiveData<ColorStatus> = _fileColor
    private fun changeFileColor(previous: PopUpUI, colorStatus: ColorStatus){
        previous.colorStatus = colorStatus
        setFilePopUpUIData(previous)
    }
    fun onClickColorPalet(colorStatus: ColorStatus){
        changeFileColor(_filePopUpUIData.value!! ,colorStatus)
    }
    var createXREF:Boolean = false
    var fileInserted:Boolean = false

    val lastInsetedFileId:LiveData<Int?> = repository.lastInsertedFile.asLiveData()
    private val _lastInsertedFileId = MutableLiveData<Int>()
    fun setLastInsertedFileId(int: Int?){
        val before = _lastInsertedFileId.value
        if(before == int|| int!=null){
            return
        } else{
            _lastInsertedFileId.value = int!!
            if(fileInserted){
//                val a = mutableListOf<File>()
//                if(_pAndgGP.value != null){
//                    a.addAll(_pAndgGP.value!!)
//                    a.onEach {
//                        when(_creatingFileType.value){
//                            FileStatus.FOLDER -> it.childFoldersAmount += 1
//                            FileStatus.TANGO_CHO_COVER -> it.childFlashCardCoversAmount += 1
//                        }
//
//                    }
//                    upDateMultipleFiles(a)
//
//                }

                fileInserted = false
            }
        }
    }

    private val _fileToCreate = MutableLiveData<File>()
    private fun setFileToCreate(file: File){
        _fileToCreate.value = file
    }
    private fun makeEmptyFileToCreate(fileStatus:FileStatus){
        setFileToCreate(
            File(fileId = 0,
                title = null,
                fileStatus = fileStatus ,
                colorStatus = ColorStatus.GRAY,
                deleted = false,
                hasParent = false,
                libOrder = _position.value ?:0,
                parentFileId = _parentFile.value?.fileId
            ))
    }
    private val _fileToEdit = MutableLiveData<File>()
    private fun setFileToEdit(file: File){
        _fileToEdit.value = file
    }



    fun onClickFinish(title:String){
        val data = _filePopUpUIData.value!!

        when(_mode.value ){
            Mode.New -> {
                val newFile = _fileToCreate.value!!
                newFile.title = title
                newFile.colorStatus = data.colorStatus
                insertFile(newFile)
                fileInserted = true
            }
            Mode.Edit -> {
                val editingFile = _fileToEdit.value!!
                editingFile.title = title
                editingFile.colorStatus = data.colorStatus
                upDateFile(editingFile)
            }
            else -> return
        }
        makePopUpUIInvisible()
        setAddFileActive(false)
    }


//    fun onClickFinish(title:String){
//        val parentFile = _parentFile.value
//
//        when(_mode.value ){
//            Mode.New -> {
//                val newFile = File(
//                    fileId = 0,
//                    title = title,
//                    fileStatus = _creatingFileType.value!!,
//                    colorStatus = _fileColor.value!!,
//                    childFlashCardCoversAmount = 0,
//                    childCardsAmount = 0,
//                    childFoldersAmount = 0,
//                    hasChild = false,
//                    deleted = false,
//                    hasParent = when(_parentFile.value){
//                        null -> false
//                        else -> true
//                    },
//                    libOrder = 0,
//                    parentFileId = _parentFile.value?.fileId
//                )
//                insertFile(newFile)
//                fileInserted = true
//            }
//            Mode.Edit -> {
//                val upDatingFile = File(
//                    fileId = parentFile!!.fileId,
//                    title = title,
//                    fileStatus = parentFile.fileStatus,
//                    colorStatus = _fileColor.value!!,
//                    childFlashCardCoversAmount = parentFile.childFlashCardCoversAmount,
//                    childCardsAmount = parentFile.childCardsAmount,
//                    childFoldersAmount = parentFile.childFoldersAmount,
//                    hasChild = parentFile.hasChild,
//                    deleted = false,
//                    hasParent = parentFile.hasParent,
//                    libOrder = parentFile.libOrder,
//                    parentFileId = parentFile.parentFileId
//                )
//                if(parentFile == upDatingFile) return
//                else upDateFile(upDatingFile)
//
//            }
//
//
//
//
//
//        }
//        setAddFileActive(false)
//    }
    fun afterNewFileInserted(fileId: Int){
        val parentFile = _parentFile.value
        when(parentFile ){
            null -> return
            else -> {
                val newXref = FileXRef(
                    id = 0,
                    parentFileId = parentFile.fileId,
                    childFileId = fileId
                )
                insertFileXRef(newXref)





            }
        }

    }

//    onclick Events
//    fun onClickEditFile(childFile: File?){
//        if(childFile!=null){
//            setParentFile(childFile)
//        }
//        setMode(Mode.Edit)
//        setAddFileActive(true)
//
//    }


    fun onClickImvAddBnv(){
        setMode(Mode.New)
        setAddFileActive(true)
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
    private fun upDateMultipleFiles(list:List<File>){
        viewModelScope.launch {
            repository.updateMultiple(list)
        }
    }
    private fun insertFileXRef(fileXRef: FileXRef){
        viewModelScope.launch {
            repository.insert(fileXRef)
        }
    }




    private val _text = MutableLiveData<String>().apply {
        value = "This is planner Fragment"
    }
    val text: LiveData<String> = _text
}