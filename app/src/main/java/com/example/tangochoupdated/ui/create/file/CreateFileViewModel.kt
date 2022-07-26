package com.example.tangochoupdated.ui.create.file

import android.text.Editable
import android.text.SpannableStringBuilder
import androidx.lifecycle.*
import com.example.tangochoupdated.R
import com.example.tangochoupdated.room.MyRoomRepository
import com.example.tangochoupdated.room.dataclass.File
import com.example.tangochoupdated.room.dataclass.FileXRef
import com.example.tangochoupdated.room.enumclass.ColorStatus
import com.example.tangochoupdated.room.enumclass.FileStatus
import com.example.tangochoupdated.room.rvclasses.LibraryRV
import com.example.tangochoupdated.ui.create.Mode
import kotlinx.coroutines.launch

class CreateFileViewModel(val repository: MyRoomRepository) : ViewModel() {

    fun onCreate(){
        setAddFileActive(false)
        setFileColor(ColorStatus.GRAY)
    }


    private val _parentFile = MutableLiveData<File?>()
    val parentFile:LiveData<File?> = _parentFile
    fun setParentFile(file: File?){
        _parentFile.value = file

        when(file){
            null ->{
                setTxvLeftTop("home")


            }
            else -> {
                when(_mode.value){
                    Mode.New -> setTxvLeftTop("${file.title.toString()} >")
                    Mode.Edit -> setTxvLeftTop("")
                }



            }
        }
//        setParentFileStock(file)

    }
    val _pAndgGP = MutableLiveData<List<File>>()
    fun setPAndG(list: List<File>?){
        _pAndgGP.value = list
        val file:Boolean
        val flashCardCover:Boolean
        val card:Boolean

        when(_parentFile.value?.fileStatus){
            null -> {
                card = true
                file = true
                flashCardCover = true
            }
            FileStatus.FOLDER -> {

                card = false
                if(list!!.size < 3 ) {
                    file = true
                    flashCardCover = true
                } else {
                    file = false
                    flashCardCover = list.size < 4
                }
            }
            FileStatus.TANGO_CHO_COVER -> {
                file = false
                flashCardCover = false
                card = true
            }
            else ->{
                file = false
                flashCardCover = false
                card = false
            }

        }
        setBottomMenuClickable(
            BottomMenuClickable(file,flashCardCover,card
            )
        )
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
        when(mode){
            Mode.Edit -> {
                setDrawFileType(
                    when(_parentFile.value!!.fileStatus){
                        FileStatus.FOLDER -> R.drawable.icon_file_1
                        FileStatus.TANGO_CHO_COVER -> R.drawable.icon_library_plane
                        else -> throw IllegalArgumentException("editing FileStatus not correct")
                    })
                setTxvHint("タイトルを編集する")
                setEdtHint("")
                setTxvLeftTop("")
                setTxvFileTitleText(SpannableStringBuilder(_parentFile.value!!.title))
                setFileColor(parentFile.value!!.colorStatus)
            }
        }
    }





    private val _newPosition = MutableLiveData<Int>()
    fun setNewPosition(int: Int){
        _newPosition.value = int
    }

    private val _addFileActive = MutableLiveData<Boolean>()
    val addFileActive : LiveData<Boolean> = _addFileActive

    fun setAddFileActive(boolean:Boolean){
        _addFileActive.value = boolean
        if(boolean){
            when(_mode.value){
                Mode.New -> setBottomMenuVisible(true)
                Mode.Edit -> setEditFilePopUpVisible(true)
            }
        }

    }

//    add Bottom Menu
    inner class BottomMenuClickable(
        val createFile:Boolean,
        val createFlashCardCover: Boolean,
        val createCard: Boolean
    )
    private val _bottomMenuClickable = MutableLiveData<BottomMenuClickable>()
    val bottomMenuClickable: LiveData<BottomMenuClickable> = _bottomMenuClickable

    fun setBottomMenuClickable(bottomMenuClickable: BottomMenuClickable){
        _bottomMenuClickable.value = bottomMenuClickable
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
        if(_bottomMenuClickable.value!!.createFile){
            setEditFilePopUpVisible(true)
            setCreatingFileType(FileStatus.FOLDER)
        }


    }
    fun onCLickCreateFlashCardCover(){
        if(_bottomMenuClickable.value!!.createFlashCardCover){
            setEditFilePopUpVisible(true)
            setCreatingFileType(FileStatus.TANGO_CHO_COVER)
        }

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
        }
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
    fun setFileColor(colorStatus: ColorStatus){
        val previous = _fileColor.value
        if(previous == colorStatus){
            return
        } else{
            _fileColor.value = colorStatus
        }


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
            _lastInsertedFileId.value = int
            if(fileInserted){
                val a = mutableListOf<File>()
                if(_pAndgGP.value != null){
                    a.addAll(_pAndgGP.value!!)
                    a.onEach {
                        when(_creatingFileType.value){
                            FileStatus.FOLDER -> it.childFoldersAmount += 1
                            FileStatus.TANGO_CHO_COVER -> it.childFlashCardCoversAmount += 1
                        }

                    }
                    upDateMultipleFiles(a)

                }

                fileInserted = false
            }
        }

    }




    fun onClickFinish(title:String){
        val parentFile = _parentFile.value

        when(_mode.value ){
            Mode.New -> {
                val newFile = File(
                    fileId = 0,
                    title = title,
                    fileStatus = _creatingFileType.value!!,
                    colorStatus = _fileColor.value!!,
                    childFlashCardCoversAmount = 0,
                    childCardsAmount = 0,
                    childFoldersAmount = 0,
                    hasChild = false,
                    deleted = false,
                    hasParent = when(_parentFile.value){
                        null -> false
                        else -> true
                    },
                    libOrder = 0,
                    parentFileId = _parentFile.value?.fileId
                )
                insertFile(newFile)
                fileInserted = true
            }
            Mode.Edit -> {
                val upDatingFile = File(
                    fileId = parentFile!!.fileId,
                    title = title,
                    fileStatus = parentFile.fileStatus,
                    colorStatus = _fileColor.value!!,
                    childFlashCardCoversAmount = parentFile.childFlashCardCoversAmount,
                    childCardsAmount = parentFile.childCardsAmount,
                    childFoldersAmount = parentFile.childFoldersAmount,
                    hasChild = parentFile.hasChild,
                    deleted = false,
                    hasParent = parentFile.hasParent,
                    libOrder = parentFile.libOrder,
                    parentFileId = parentFile.parentFileId
                )
                if(parentFile == upDatingFile) return
                else upDateFile(upDatingFile)

            }





        }
        setAddFileActive(false)
    }
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
    fun onClickEditFile(childFile: File?){
        if(childFile!=null){
            setParentFile(childFile)
        }
        setMode(Mode.Edit)
        setAddFileActive(true)

    }


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