package com.example.tangochoupdated

import androidx.compose.runtime.internal.illegalDecoyCallException
import androidx.lifecycle.*
import com.example.tangochoupdated.room.MyRoomRepository
import com.example.tangochoupdated.room.dataclass.File
import com.example.tangochoupdated.room.enumclass.ColorStatus
import com.example.tangochoupdated.room.enumclass.FileStatus
import com.example.tangochoupdated.room.enumclass.Tab
import com.example.tangochoupdated.ui.library.LibraryViewModel
import com.example.tangochoupdated.ui.planner.CreateViewModel
import kotlinx.coroutines.*



class BaseViewModel(private val repository: MyRoomRepository):ViewModel(){


    val createFragmentActive = MutableLiveData<Boolean>()


//on create
    fun setOnCreate(){


    setAddFileActive(false)
    activateBnvLayout1View()
    deactivateBnvLayout3View()
    setActiveTab(Tab.TabLibrary)




    }



    //    parent File

    private val _parentFile = MutableLiveData<File?>()
    val parentFile:LiveData<File?> = _parentFile
    fun setParentFile(file: File?){
        _parentFile.value = file
    }

    private val _addFileActive = MutableLiveData<Boolean>()
    val addFileActive : LiveData<Boolean> = _addFileActive

    private fun setAddFileActive(boolean:Boolean){
        _addFileActive.value = boolean
    }



//    bottom menu Visibility
    private val _bottomMenuVisible = MutableLiveData<Boolean>()
    val bottomMenuVisible:LiveData<Boolean> = _bottomMenuVisible
    private fun setBottomMenuVisible(boolean: Boolean){
        _bottomMenuVisible.value = boolean
        if(boolean){
           setAddFileActive(true)
            setEditFilePopUpVisible(false)
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

//    popUpFile data

    enum class EditFilePopUpData(
        val txvLeftTop:String,
        val txvHint:String,
        val drawFileType:Int,
        val edtHint:String,

    )


//    onclick add
    fun onClickAddTab(){
        setBottomMenuVisible(true)


    }

    fun onClickFolderOrFlashCard(){

        setEditFilePopUpVisible(true)
    }


    fun changeCreateFragmentStatus(active:Boolean){
        createFragmentActive.value = active
    }
//    bnv each View Change
    inner class BnvLayout(
        val drawableId:Int,
        val padding:Int,
        val tabNameVisibility:Boolean,
        val tabName:String
        )
    private val _bnvLayout1View = MutableLiveData<BnvLayout>()
    val bnvLayout1View:LiveData<BnvLayout> = _bnvLayout1View
    private fun setBnvLayout1View(bnvLayout: BnvLayout){
        _bnvLayout1View.value = bnvLayout
    }

    fun activateBnvLayout1View(){
        val a =BnvLayout(
            drawableId = R.drawable.icon_library_active,
            padding = 0,
            tabNameVisibility = true,
            tabName = "ライブラリ")
        setBnvLayout1View(a)
    }
    fun deactivateBnvLayout1View(){
        val a =  BnvLayout(
            drawableId = R.drawable.icon_library_plane,
            padding = 10,
            tabNameVisibility = false,
            tabName = "")
        setBnvLayout1View(a)
    }

    private val _bnvLayout3View = MutableLiveData<BnvLayout>()
    val bnvLayout3View:LiveData<BnvLayout> = _bnvLayout3View
    private fun setBnvLayout3View(bnvLayout: BnvLayout){
        _bnvLayout3View.value = bnvLayout
    }
    fun activateBnvLayout3View(){
        val a = BnvLayout(
            drawableId = R.drawable.icon_anki_active,
            padding = 0,
            tabNameVisibility = true,
            tabName = "暗記する")
        setBnvLayout3View(a)
    }
    fun deactivateBnvLayout3View(){
        val a = BnvLayout(
            drawableId = R.drawable.iconanki,
            padding = 10,
            tabNameVisibility = false,
            tabName = "")
        setBnvLayout3View(a)
    }

//    which tab is active

    private val _activeTab = MutableLiveData<Tab>()
    val activeTab:LiveData<Tab> = _activeTab
    private fun setActiveTab (tab:Tab){
        val previousTab = _activeTab.value
        _activeTab.apply {
            value = tab
        }
        when(previousTab){
            Tab.TabLibrary -> deactivateBnvLayout1View()
            Tab.TabAnki -> deactivateBnvLayout3View()
        }

        when(tab){
            Tab.TabLibrary -> activateBnvLayout1View()
            Tab.TabAnki -> activateBnvLayout3View()
        }



    }
    fun onClickTab1(){
        when(_activeTab.value){
            Tab.TabLibrary -> return
            else -> {
                setActiveTab(Tab.TabLibrary)
            }
        }
    }

    fun onClickTab3(){
        when(_activeTab.value){
            Tab.TabAnki -> return
            else -> {
                setActiveTab(Tab.TabAnki)
            }
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
