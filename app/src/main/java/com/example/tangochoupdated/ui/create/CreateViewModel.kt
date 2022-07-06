package com.example.tangochoupdated.ui.planner

import androidx.lifecycle.*
import com.example.tangochoupdated.R
import com.example.tangochoupdated.room.MyRoomRepository
import com.example.tangochoupdated.room.dataclass.File
import com.example.tangochoupdated.room.dataclass.FileXRef
import com.example.tangochoupdated.room.enumclass.ColorStatus
import com.example.tangochoupdated.room.enumclass.FileStatus
import kotlinx.coroutines.launch

class CreateViewModel(val repository: MyRoomRepository) : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is planner Fragment"
    }
    val text: LiveData<String> = _text
}
class CreateFileViewModel(val repository: MyRoomRepository) : ViewModel() {

    fun onCreate(){
        setAddFileActive(false)
        setFileColor(ColorStatus.GRAY)
    }

    private val _parentFile = MutableLiveData<File?>()
    val parentFile:LiveData<File?> = _parentFile
    fun setParentFile(file: File?){
        _parentFile.value = file
        setTxvLeftTop(when(file){
            null -> "home"
            else -> file.title.toString()
        })
    }

    enum class Mode{
        Create, Edit
    }
    private val _mode = MutableLiveData<Mode>()
    fun setMode(mode: Mode){
        _mode.value = mode
    }





    private val _newPosition = MutableLiveData<Int>()
    fun setNewPosition(int: Int){
        _newPosition.value = int
    }

    private val _addFileActive = MutableLiveData<Boolean>()
    val addFileActive : LiveData<Boolean> = _addFileActive

    fun setAddFileActive(boolean:Boolean){
        val before = _addFileActive.value
        if(before == boolean){
            return
        } else{
            _addFileActive.value = boolean
            setBottomMenuVisible(true)
        }

    }
    private val _bottomMenuVisible = MutableLiveData<Boolean>()
    val bottomMenuVisible:LiveData<Boolean> = _bottomMenuVisible
    private fun setBottomMenuVisible(boolean: Boolean){
        _bottomMenuVisible.value = boolean
        if(boolean){
            setEditFilePopUpVisible(false)
        }

    }

    fun reset():Boolean{
        if(_bottomMenuVisible.value==true&&_editFilePopUpVisible.value == false){
            setBottomMenuVisible(false)
            setAddFileActive(false)
            return true
        }
        else if (_editFilePopUpVisible.value == true){
            setEditFilePopUpVisible(false)
            setAddFileActive(false)
            return true
        } else return false

    }


    fun onClickAddTab(){
        setBottomMenuVisible(true)


    }

    fun onClickCreateFolder(){

        setEditFilePopUpVisible(true)
        setCreatingFileType(FileStatus.FOLDER)
    }
    fun onCLickCreateFlashCardCover(){
        setEditFilePopUpVisible(true)
        setCreatingFileType(FileStatus.TANGO_CHO_COVER)
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
    val txvHint:LiveData<String> = _txvHint

    private fun setTxvHint(string: String){
        _txvHint.value = string
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

    val lastInsetedFileId:LiveData<Int> = repository.lastInsertedFile.asLiveData()
    private val _lastInsertedFileId = MutableLiveData<Int>()
    fun setLastInsertedFileId(int: Int){
        _lastInsertedFileId.value = int
    }

    fun onClickFinish(title:String){
        if(_mode.value == Mode.Create){
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
                hasParent = true,
                libOrder = _newPosition.value
            )
            insertFile(newFile)
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