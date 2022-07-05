package com.example.tangochoupdated

import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import androidx.compose.runtime.internal.illegalDecoyCallException
import androidx.core.graphics.drawable.DrawableCompat
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

    private val _innerCircleDraw = MutableLiveData<Drawable>()
    private fun setInnerCircleDraw(drawable: Drawable){
        _strokeDraw.value = drawable
    }
    private val _strokeDraw = MutableLiveData<Drawable>()
    private fun setStrokeDraw(drawable: Drawable){
        _strokeDraw.value = drawable
    }

    private val _colorCircleDraw = MutableLiveData<Drawable>()

    private fun setColorCircleDraw(drawable: Drawable){
        _colorCircleDraw.value = drawable
    }


//on create
    fun setOnCreate(strokeDraw:Drawable,planeCircle: Drawable){

    setStrokeDraw(strokeDraw)
    setInnerCircleDraw(planeCircle)
    setAddFileActive(false)
    activateBnvLayout1View()
    deactivateBnvLayout3View()
    setActiveTab(Tab.TabLibrary)
    setFileColor(ColorStatus.GRAY)




    }



    //    parent File

    private val _parentFile = MutableLiveData<File?>()
    val parentFile:LiveData<File?> = _parentFile
    fun setParentFile(file: File?){
        _parentFile.value = file
        setTxvLeftTop(file?.title?.toString() ?:"")
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

// txv left top parent file title

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
        val previousColor = _fileColor.value
        if(previousColor == colorStatus){
            return
        } else{
            _fileColor.value = colorStatus
            if(previousColor == null){
                return
            } else{
                deactivateColorStatus(previousColor)
            }
            activateColorStatus(colorStatus)
        }





    }
    private fun activateColorStatus(colorStatus: ColorStatus){
        val planeCircle = _colorCircleDraw.value
        val color = when(colorStatus){
            ColorStatus.GRAY -> R.color.gray
            ColorStatus.RED -> R.color.red
            ColorStatus.BLUE -> R.color.blue
            ColorStatus.YELLOW -> R.color.yellow
        }
        DrawableCompat.setTint(planeCircle!!,color)
        val a = LayerDrawable(arrayOf(planeCircle))
        a.addLayer(_colorCircleDraw.value)
        when(colorStatus){
            ColorStatus.GRAY -> setImvGrayDrawable(a)
            ColorStatus.YELLOW -> setImvYellowDrawable(a)
            ColorStatus.BLUE -> setImvBlueDrawable(a)
            ColorStatus.RED -> setImvRedDrawable(a)
        }
    }
    private fun deactivateColorStatus(colorStatus: ColorStatus){
        val planeCircle = _colorCircleDraw.value
        when(colorStatus){
            ColorStatus.GRAY -> {
                DrawableCompat.setTint(planeCircle!!,R.color.gray)
                setImvGrayDrawable(planeCircle)

            }
            ColorStatus.RED -> {
                DrawableCompat.setTint(planeCircle!!,R.color.red)
                setImvRedDrawable(planeCircle)

            }
            ColorStatus.BLUE -> {
                DrawableCompat.setTint(planeCircle!!,R.color.blue)
                setImvBlueDrawable(planeCircle)

            }
            ColorStatus.YELLOW -> {
                DrawableCompat.setTint(planeCircle!!,R.color.yellow)
                setImvYellowDrawable(planeCircle)

            }
        }
    }



    enum class CreatingFileType{
        Folder, FlashCardCover
    }


    private val _creatingFileType = MutableLiveData<CreatingFileType>()
    val creatingFileType: LiveData<CreatingFileType> = _creatingFileType
    private fun setCreatingFileType(creatingFileType: CreatingFileType){
        _creatingFileType.value = creatingFileType
        when(creatingFileType){
            CreatingFileType.Folder -> {
                setDrawFileType(R.drawable.icon_file_1)
                setTxvHint("新しいフォルダを作る")
                setEdtHint("フォルダのタイトル")

            }
            CreatingFileType.FlashCardCover ->{
                setDrawFileType(R.drawable.icon_library_plane)
                setTxvHint("新しい単語帳を作る")
                setEdtHint("単語帳のタイトル")

            }
        }
    }


    private val _imvGrayDrawable = MutableLiveData<Drawable>()
    val  imvGrayDrawable:LiveData<Drawable> = _imvGrayDrawable
    private fun setImvGrayDrawable(drawable: Drawable){
        _imvGrayDrawable.value = drawable
    }


    private val _imvRedDrawable = MutableLiveData<Drawable>()
    val  imvRedDrawable:LiveData<Drawable> = _imvRedDrawable

    private fun setImvRedDrawable(drawable: Drawable){
        _imvRedDrawable.value = drawable
    }


    private val _imvYellowDrawable = MutableLiveData<Drawable>()
    val  imvYellowDrawable:LiveData<Drawable> = _imvYellowDrawable

    private fun setImvYellowDrawable(drawable: Drawable){
        _imvYellowDrawable.value = drawable
    }



    private val _imvBlueDrawable = MutableLiveData<Drawable>()
    val  imvBlueDrawable:LiveData<Drawable> = _imvBlueDrawable

    private fun setImvBlueDrawable(drawable: Drawable){
        _imvBlueDrawable.value = drawable
    }




//    onclick add
    fun onClickAddTab(){
        setBottomMenuVisible(true)


    }


    fun onClickCreateFolder(){

        setEditFilePopUpVisible(true)
        setCreatingFileType(CreatingFileType.Folder)
    }
    fun onCLickCreateFlashCardCover(){
        setEditFilePopUpVisible(true)
        setCreatingFileType(CreatingFileType.FlashCardCover)
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
