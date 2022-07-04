package com.example.tangochoupdated

import androidx.compose.runtime.internal.illegalDecoyCallException
import androidx.lifecycle.*
import androidx.room.Update
import com.example.tangochoupdated.room.MyRoomRepository
import com.example.tangochoupdated.room.dataclass.CardAndTags
import com.example.tangochoupdated.room.dataclass.File
import com.example.tangochoupdated.room.dataclass.FileWithChild
import com.example.tangochoupdated.room.dataclass.FileXRef
import com.example.tangochoupdated.room.enumclass.CardStatus
import com.example.tangochoupdated.room.enumclass.ColorStatus
import com.example.tangochoupdated.room.enumclass.FileStatus
import com.example.tangochoupdated.room.enumclass.Tab
import com.example.tangochoupdated.room.rvclasses.LibRVViewType
import com.example.tangochoupdated.room.rvclasses.LibraryRV
import com.example.tangochoupdated.ui.library.LibraryViewModel
import com.example.tangochoupdated.ui.planner.CreateViewModel
import kotlinx.coroutines.*



class BaseViewModel(private val repository: MyRoomRepository):ViewModel(){


    val createFragmentActive = MutableLiveData<Boolean>()





    //    parent File
    private val _parentFile = MutableLiveData<File>()
    fun setParentFile(file: File){
        _parentFile.value = file
    }

//    bottom menu Visibility
    private val _bottomMenuVisible = MutableLiveData<Boolean>()
    val bottomMenuVisible:LiveData<Boolean> = _bottomMenuVisible
    private fun setBottomMenuVisible(boolean: Boolean){
        _bottomMenuVisible.value = boolean

    }

    //    edit file popup Visibility
    private val _editFilePopUpVisible = MutableLiveData<Boolean>()
    val editFilePopUpVisible:LiveData<Boolean> = _editFilePopUpVisible
    private fun setEditFilePopUpVisible(boolean: Boolean){
        _editFilePopUpVisible.value = boolean
    }

//    onclick add
    fun onClickAddTab(){
    setEditFilePopUpVisible(false)
        setBottomMenuVisible(true)
        setEditFilePopUpVisible(false)

    }

    fun onClickFolderOrFlashCard(){
        setEditFilePopUpVisible(true )
    }
    fun setOnCreate(){
        setEditFilePopUpVisible(false)
        setBottomMenuVisible(false)


    }


    fun changeCreateFragmentStatus(active:Boolean){
        createFragmentActive.value = active
    }
    val activeTab = MutableLiveData<Tab>()
    fun changeActiveTab (tab:Tab){
        activeTab.apply {
            value = tab
        }
    }

    fun reset(){
        setBottomMenuVisible(false)
    }


    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }
    var parentFileId:Int = 0
    var hasParent:Boolean = false
    var libOrder:Int =0
    var fileStatus:FileStatus? = null
    var title:String = ""

    fun insertFile(){
        if(!hasParent){
            parentFileId = 0
        }
        val a = File(
        fileId = 0,
        title = title,
        colorStatus=  ColorStatus.RED,
        fileStatus = fileStatus!!,
        hasChild = false,
        hasParent = hasParent,
        libOrder = libOrder,
        )
        insert(a)
        val id = 0
    }


    fun insert(item: Any) = viewModelScope.launch {
        repository.insert(item)
    }


}

class ViewModelFactory(private val repository: MyRoomRepository) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val a = when{
            modelClass.isAssignableFrom(BaseViewModel::class.java)-> BaseViewModel(repository)
            modelClass.isAssignableFrom(LibraryViewModel::class.java)->LibraryViewModel(repository)
            modelClass.isAssignableFrom(CreateViewModel::class.java)-> CreateViewModel(repository)
            else ->  illegalDecoyCallException("unknown ViewModel class")
        }
        return a as T

    }
}
