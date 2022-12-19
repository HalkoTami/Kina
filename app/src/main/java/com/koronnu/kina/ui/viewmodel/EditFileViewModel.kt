package com.koronnu.kina.ui.viewmodel

import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.opengl.Visibility
import android.view.View
import androidx.lifecycle.*
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import com.koronnu.kina.R
import com.koronnu.kina.actions.changeViewVisibility
import com.koronnu.kina.actions.hideKeyBoard
import com.koronnu.kina.application.RoomApplication
import com.koronnu.kina.db.MyRoomRepository
import com.koronnu.kina.db.dataclass.Card
import com.koronnu.kina.db.dataclass.File
import com.koronnu.kina.db.enumclass.ColorStatus
import com.koronnu.kina.db.enumclass.FileStatus
import com.koronnu.kina.customClasses.enumClasses.EditingMode
import com.koronnu.kina.customClasses.enumClasses.LibraryFragment
import com.koronnu.kina.tabLibrary.lib_frag_con.LibraryHomeFrag
import com.koronnu.kina.ui.animation.Animation
import com.koronnu.kina.ui.view_set_up.ColorPalletViewSetUp
import com.koronnu.kina.ui.view_set_up.GetCustomDrawables
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.launch
import kotlin.math.log

class EditFileViewModel(val repository: MyRoomRepository,
                        val resources: Resources,
                        val mainViewModel: MainViewModel) : ViewModel() {

    companion object{
        fun getFactory(mainViewModel: MainViewModel) : ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[APPLICATION_KEY]) as RoomApplication
                val repository = application.repository
                val resources = application.resources
                return EditFileViewModel(repository,resources,mainViewModel) as T
            }
        }
    }
    private val ankiBoxViewModel    get() = mainViewModel.ankiBaseViewModel.ankiBoxViewModel
    private val createCardViewModel get() = mainViewModel.createCardViewModel
    private val mainActivityBinding  get() = mainViewModel.mainActivityBinding
    private val editFilePopUpBinding get() = mainActivityBinding.editFileBinding
    private val colorPalletBinding  get() = editFilePopUpBinding.colPaletBinding
    private val parentOpenedFile    get() = mainViewModel.libraryBaseViewModel.returnParentFile()
    private val libraryBaseViewModel get() = mainViewModel.libraryBaseViewModel
    private val colorPalletViewSetUp get() =  ColorPalletViewSetUp(colorPalletBinding)
    private val ankiBoxCards get() = ankiBoxViewModel.returnAnkiBoxItems()
    private val parentFileIsNull get() = parentOpenedFile == null
    private val parentFileIsFlashCard get() = parentOpenedFile?.fileStatus == FileStatus.FLASHCARD_COVER
    private val parentFileIsNotFlashCard get() = parentFileIsFlashCard.not()
    private val isNotChooseFileMoveToFrag get() = libraryBaseViewModel.returnLibraryFragment()!=LibraryFragment.ChooseFileMoveTo
    private val parentFileIsFolder get() = parentOpenedFile?.fileStatus == FileStatus.FOLDER
    private val parentFileHasLessThan3Ancestors get() = (libraryBaseViewModel.getParentFileAncestors?.size ?:0) < 3
    private val isNotInBoxFrag get() = libraryBaseViewModel.returnLibraryFragment() != LibraryFragment.InBox

    val viewCoverVisibility = MutableLiveData<Boolean>().apply { value = false }
    val frameLayNewCardVisible = MutableLiveData<Boolean>().apply { value = true }
    val frameLayNewFlashCardVisible = MutableLiveData<Boolean>().apply { value = true }
    val frameLayNewFolderVisible = MutableLiveData<Boolean>().apply { value = true }
    val txvHintText = MutableLiveData<String>()
    private val _popUpShownFile = MutableLiveData<File>()
    val btnFinishText = MutableLiveData<String>()
    val txvParentFileTitleText = MutableLiveData<String>()
    val edtFileTitleText = MutableLiveData<String>()
    val edtFileTitleHint = MutableLiveData<String>()
    private val _colPalletStatus = MutableLiveData<ColorStatus>()
    val imvFileStatusDraw = MutableLiveData<Drawable>()


    fun getChildFilesByFileIdFromDB(fileId: Int?) = repository.getFileDataByParentFileId(fileId).asLiveData()


    fun onClickCreateCard() = createCardViewModel.onClickAddNewCardBottomBar()







    private var _mode:EditingMode? = null
    private fun setMode(mode: EditingMode){
        _mode = mode
    }
    private val mode get() = _mode!!



//    add Bottom Menu

    private fun setFrameLayNewCardVisible(){
        frameLayNewCardVisible.value =  (parentFileIsNull ||parentFileIsFlashCard)
                &&isNotChooseFileMoveToFrag

    }
    private fun setFrameLayNewFlashCardCoverVisible(){
        frameLayNewFlashCardVisible.value = parentFileIsNotFlashCard&&isNotInBoxFrag
    }
    private fun setFrameLayNewFolderVisible(){
        frameLayNewFolderVisible.value = (parentFileIsNull||parentFileIsFolder)
                &&isNotInBoxFrag
                &&parentFileHasLessThan3Ancestors
    }
    fun setUpFragConViewCoverVisibility(){
        viewCoverVisibility.value = editFilePopUpVisible||bottomMenuVisible
    }
    fun makeBothPopUpGone(){
        setBottomMenuVisible(false)
        setEditFilePopUpVisible(false)
        setUpFragConViewCoverVisibility()
    }
    private var _bottomMenuVisible:Boolean? = null
    fun setBottomMenuVisible(visible: Boolean){
        if(bottomMenuVisible == visible) return
        _bottomMenuVisible = visible
        doAfterSetBottomMenuVisible()
    }

    fun doAfterSetBottomMenuVisible(){
        if(bottomMenuVisible) setEditFilePopUpVisible(false)
        setFrameLayNewCardVisible()
        setFrameLayNewFlashCardCoverVisible()
        setFrameLayNewFolderVisible()
        Animation().animateFrameBottomMenu(mainActivityBinding.frameBottomMenu,bottomMenuVisible)
        setUpFragConViewCoverVisibility()
    }
    val bottomMenuVisible get() = _bottomMenuVisible ?:false



    fun onClickCreateFile(fileStatus: FileStatus){
        if(fileStatus==FileStatus.ANKI_BOX_FAVOURITE&&
                ankiBoxCards.isEmpty()) return
        setMode(EditingMode.New)
        makeEmptyFileToCreate(fileStatus)
        setEditFilePopUpVisible(true)
    }
    fun onClickEditFileInRV(editingFile:File){
        setMode(EditingMode.Edit)
        setFileToEdit(editingFile)
        setEditFilePopUpVisible(true)
    }

    //    edit file popup Visibility
    private var _editFilePopUpVisible :Boolean? = null
    fun setEditFilePopUpVisible(visible: Boolean){
        if(editFilePopUpVisible == visible) return
        if(visible)setBottomMenuVisible(false)
        _editFilePopUpVisible = visible
        doAfterSetEditFilePopUpVisible()
    }
    val editFilePopUpVisible get() = _editFilePopUpVisible?:false
    private fun doAfterSetEditFilePopUpVisible(){
        setUpPopUpView()
        Animation().animatePopUpAddFile(mainActivityBinding.frameLayEditFile,editFilePopUpVisible)
        setUpFragConViewCoverVisibility()
        if(editFilePopUpVisible.not())  hideKeyBoard(editFilePopUpBinding.edtFileTitle)
    }

//    Todo popupのクラス作る



    private fun setTxvHintText(){
        txvHintText.value = when(mode){
            EditingMode.New -> resources.getString(when(fileToCreate.fileStatus){
                FileStatus.FOLDER               ->  R.string.editFilePopUpBin_HintTxv_createNewFolder
                FileStatus.ANKI_BOX_FAVOURITE   ->  R.string.editFilePopUpBin_HintTxv_createNewAnkiBoxfavourite
                FileStatus.FLASHCARD_COVER      ->  R.string.editFilePopUpBin_HintTxv_createNewFlashCard
                else  -> throw IllegalArgumentException()
            })
            EditingMode.Edit -> resources.getString(R.string.editFilePopUpBin_HintTxv_edit,fileToEdit.title)
        }
    }

    private fun setEdtFileTitleText() {
        edtFileTitleText.value = getPopUpShownFile.title ?: String()
    }
    private fun setPopUpShownFile(){
        _popUpShownFile.value = when(mode){
            EditingMode.Edit -> fileToEdit
            EditingMode.New  -> fileToCreate
        }
    }
    private val getPopUpShownFile get() = _popUpShownFile.value!!
    private fun setEdtFileTitleHint(){
        edtFileTitleHint.value =
        resources.getString(when(mode){
            EditingMode.Edit -> R.string.editFilePopUpBin_edtFileTitleHint_edit
            EditingMode.New -> R.string.editFilePopUpBin_edtFileTitleHint_create
        })
    }
    private fun setBtnFinishText() {
        btnFinishText.value =
        resources.getString(when(mode){
            EditingMode.Edit -> R.string.editFilePopUpBin_btnFinish_update
            EditingMode.New -> R.string.editFilePopUpBin_btnFinish_create
        })
    }

    private fun setTxvParentFileTitleText(){
        txvParentFileTitleText.value = parentOpenedFile?.title ?: resources.getString(R.string.title_home)
    }


    private fun setUpPopUpView(){
        setPopUpShownFile()
        setTxvHintText()
        setBtnFinishText()
        setTxvParentFileTitleText()
        setEdtFileTitleText()
        setEdtFileTitleHint()
        setColPalletStatus(getPopUpShownFile.colorStatus)
        setImvFileStatusDraw()

    }


    fun setColPalletStatus( colorPalletStatus: ColorStatus){
        _colPalletStatus.value = colorPalletStatus
        doAfterColPalletStatusSet()
    }
    fun setImvFileStatusDraw(){
       imvFileStatusDraw.value = GetCustomDrawables(editFilePopUpBinding.root.context)
           .getFileIconByFileStatusAndColStatus(getPopUpShownFile.fileStatus,colPalletStatus)
    }
    fun doAfterColPalletStatusSet(){
        colorPalletViewSetUp.makeSelected(colPalletStatus)
        setImvFileStatusDraw()
    }
    val colPalletStatus get() =  _colPalletStatus.value!!


    fun observeLiveData(lifecycleOwner: LifecycleOwner){
        lastInsertedFile.observe(lifecycleOwner,Observer{ setLastInsertedFile(it)})
    }

    val lastInsertedFile:LiveData<File?> =  repository.lastInsertedFile.asLiveData()
    private val _lastInsertedFile = MutableLiveData<File?>()
    private fun setLastInsertedFile(file: File?){
        _lastInsertedFile.value = file
    }
     val getLastInsertedFile:File? get() = _lastInsertedFile.value

    private var _fileToCreate :File? = null
    private fun setFileToCreate(file: File){
        _fileToCreate = file
    }
    val fileToCreate get() = _fileToCreate!!
    private val parentFileSisters : LiveData<List<File>> get() =  repository.getFileDataByParentFileId(parentOpenedFile?.parentFileId).asLiveData()
    private fun getParentFileSisters():List<File>?{
        parentFileSisters.observeForever(object :Observer<List<File>>{
            override fun onChanged(t: List<File>?) {
                setParentFileSisters(t)
                parentFileSisters.removeObserver(this)
            }
        })
        while (parentFileSisters.hasObservers()){
            println("waiting")
        }
        return getParentFileSisters

    }
    private val _parentFileSisters = MutableLiveData<List<File>?>()
    fun setParentFileSisters(list: List<File>?){
        _parentFileSisters.value = list
    }
    val getParentFileSisters get() = _parentFileSisters.value!!
    private fun makeEmptyFileToCreate(fileStatus:FileStatus){
        setFileToCreate(
            File(fileId = 0,
                title = null,
                fileStatus = fileStatus ,
                colorStatus = ColorStatus.GRAY,
                deleted = false,
                fileBefore = getParentFileSisters()?.lastOrNull()?.fileId,
                parentFileId = parentOpenedFile?.fileId
            ))
    }
    private var _fileToEdit:File? = null
    private fun setFileToEdit(file: File){
        _fileToEdit= file
    }
    val fileToEdit get() = _fileToEdit!!

    fun makeFileInGuide(){
        onClickFinish()
        fileToCreate.fileBefore = null
        val first = getParentFileSisters()?.firstOrNull()
        first?.fileBefore = (getLastInsertedFile?.fileId?:0) + 1
        upDateFile(first ?:return)
    }

    fun onClickFinish(){
        val edtFileTitle = editFilePopUpBinding.edtFileTitle
        val title = edtFileTitle.text.toString()
        if(title.isBlank()) {
            edtFileTitle.hint = "タイトルが必要です"
            return
        }

        val color = colPalletStatus

        when(mode ){
            EditingMode.New -> {
                fileToCreate.title = title
                fileToCreate.colorStatus = color
                if(fileToCreate.fileStatus==FileStatus.ANKI_BOX_FAVOURITE){
                    val cardList = ankiBoxViewModel.returnAnkiBoxItems()
                    if(cardList.isEmpty()) return
                    addCardsToFavouriteAnkiBox(cardList,getLastInsertedFile?.fileId ?:0,fileToCreate)
                }
                else insertFile(fileToCreate)
            }
            EditingMode.Edit -> {
                fileToEdit.title = title
                fileToEdit.colorStatus = color
                upDateFile(fileToEdit)
            }
        }
        setEditFilePopUpVisible(false)
        doAfterNewFileCreated()
    }
    private var _doAfterNewFileCreated:()->Unit = {}
    fun setDoAfterNewFileCreated(func:()->Unit){
        _doAfterNewFileCreated = func
    }
    private val doAfterNewFileCreated get() = _doAfterNewFileCreated

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

    fun doOnBackPress(): Boolean {
        var backPressActionExists = true
        if(editFilePopUpVisible) setEditFilePopUpVisible(false)
        else if(bottomMenuVisible) setBottomMenuVisible(false)
        else backPressActionExists = false
        return backPressActionExists
    }


}