package com.koronnu.kina.ui.viewmodel

import android.animation.ValueAnimator
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.view.View
import androidx.lifecycle.*
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import com.koronnu.kina.R
import com.koronnu.kina.actions.hideKeyBoard
import com.koronnu.kina.application.RoomApplication
import com.koronnu.kina.customClasses.enumClasses.EditingMode
import com.koronnu.kina.customClasses.enumClasses.LibraryFragment
import com.koronnu.kina.db.MyRoomRepository
import com.koronnu.kina.db.dataclass.Card
import com.koronnu.kina.db.dataclass.File
import com.koronnu.kina.db.enumclass.ColorStatus
import com.koronnu.kina.db.enumclass.FileStatus
import com.koronnu.kina.ui.animation.Animation
import com.koronnu.kina.ui.view_set_up.ColorPalletViewSetUp
import com.koronnu.kina.ui.view_set_up.GetCustomDrawables
import kotlinx.coroutines.launch

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

    private val lastInsertedFile:LiveData<File?> =  repository.lastInsertedFile.asLiveData()
    private val parentFileSisters : LiveData<List<File>> get() =  repository.getFileDataByParentFileId(parentOpenedFile?.parentFileId).asLiveData()

    val viewCoverVisibility = MutableLiveData<Boolean>().apply { value = false }
    val frameLayNewCardVisible = MutableLiveData<Boolean>().apply { value = true }
    val frameLayNewFlashCardVisible = MutableLiveData<Boolean>().apply { value = true }
    val frameLayNewFolderVisible = MutableLiveData<Boolean>().apply { value = true }
    val txvHintText = MutableLiveData<String>()
    val btnFinishText = MutableLiveData<String>()
    val txvParentFileTitleText = MutableLiveData<String>()
    val edtFileTitleText = MutableLiveData<String>()
    val edtFileTitleHint = MutableLiveData<String>()
    val imvFileStatusDraw = MutableLiveData<Drawable>()
    val _editFilePopUpVisible = MutableLiveData<Boolean>()

    private val _colPalletStatus = MutableLiveData<ColorStatus>()
    private val _popUpShownFile = MutableLiveData<File>()
    private val _lastInsertedFile = MutableLiveData<File?>()

    private var _bottomMenuVisible:Boolean? = null
    private var _mode:EditingMode? = null
    private var _fileToCreate :File? = null
    private var _fileToEdit:File? = null
    private var _doAfterNewFileCreated:()->Unit = {}
    private val editFilePopUpVisible get() = _editFilePopUpVisible.value?:false


    //    getter
    private val doAfterNewFileCreated get() = _doAfterNewFileCreated
    private val fileToEdit get() = _fileToEdit!!
    private val fileToCreate get() = _fileToCreate!!
    private val colPalletStatus get() =  _colPalletStatus.value!!
    private val ankiBoxViewModel    get() = mainViewModel.ankiBaseViewModel.ankiBoxViewModel
    private val createCardViewModel get() = mainViewModel.createCardViewModel
    private val mainActivityBinding  get() = mainViewModel.mainActivityBinding
    private val editFilePopUpBinding get() = mainActivityBinding.bindingWidgetPwEditFile
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
    val getLastInsertedFile:File? get() = _lastInsertedFile.value
    private val mode get() = _mode!!
    private val getPopUpShownFile get() = _popUpShownFile.value!!
    private val bottomMenuVisible get() = _bottomMenuVisible ?:false

    //    setter
    fun setDoAfterNewFileCreated(func:()->Unit){
        _doAfterNewFileCreated = func
    }
    private fun setFileToCreate(file: File){
        _fileToCreate = file
    }
    private fun setFileToEdit(file: File){
        _fileToEdit= file
    }
    private fun setMode(mode: EditingMode){ _mode = mode }

    private fun setLastInsertedFile(file: File?){
        _lastInsertedFile.value = file
        doAfterNewFileCreated()
    }

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
    private fun setUpFragConViewCoverVisibility(){
        viewCoverVisibility.value = editFilePopUpVisible||bottomMenuVisible
    }
    fun setBottomMenuVisible(visible: Boolean){
        if(bottomMenuVisible == visible) return
        _bottomMenuVisible = visible
        doAfterSetBottomMenuVisible()
    }

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
    fun setEditFilePopUpVisible(visible: Boolean){
        if(editFilePopUpVisible == visible) return
        if(visible)setBottomMenuVisible(false)
        _editFilePopUpVisible.value = visible
        doAfterSetEditFilePopUpVisible()
    }
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
    fun setColPalletStatus( colorPalletStatus: ColorStatus){
        _colPalletStatus.value = colorPalletStatus
        doAfterColPalletStatusSet()
    }
    private fun setImvFileStatusDraw(){
        imvFileStatusDraw.value = GetCustomDrawables(editFilePopUpBinding.root.context)
            .getFileIconByFileStatusAndColStatus(getPopUpShownFile.fileStatus,colPalletStatus)
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
    private fun doAfterSetBottomMenuVisible(){
        if(bottomMenuVisible) setEditFilePopUpVisible(false)
        setFrameLayNewCardVisible()
        setFrameLayNewFlashCardCoverVisible()
        setFrameLayNewFolderVisible()
        Animation().animateFrameBottomMenu(mainActivityBinding.flPmAddItem,bottomMenuVisible)
        setUpFragConViewCoverVisibility()
    }

    val editFilePopUpAnim:(v:View,visible:Boolean)-> ValueAnimator = { v, visible ->
        Animation().appearAlphaAnimation(v,visible,1f){}
    }

    private fun doAfterSetEditFilePopUpVisible(){
        setUpPopUpView()
        setUpFragConViewCoverVisibility()
        if(editFilePopUpVisible.not())  hideKeyBoard(editFilePopUpBinding.edtFileTitle)
    }

    fun onClickCreateFile(fileStatus: FileStatus){
        if(fileStatus==FileStatus.ANKI_BOX_FAVOURITE&&
            ankiBoxCards.isEmpty()) return
        setMode(EditingMode.New)
        getParentFileSisters {
            makeEmptyFileToCreate(fileStatus,it)
            setEditFilePopUpVisible(true)
        }

    }
    fun onClickCreateCard() = createCardViewModel.onClickAddNewCardBottomBar()
    fun onClickFragConViewCover(){
        setBottomMenuVisible(false)
        setEditFilePopUpVisible(false)
        setUpFragConViewCoverVisibility()
    }
    fun onClickEditFileInRV(editingFile:File){
        setMode(EditingMode.Edit)
        setFileToEdit(editingFile)
        setEditFilePopUpVisible(true)
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
    }
    private fun doAfterColPalletStatusSet(){
        colorPalletViewSetUp.makeSelected(colPalletStatus)
        setImvFileStatusDraw()
    }


    fun observeLiveData(lifecycleOwner: LifecycleOwner){
        lastInsertedFile.observe(lifecycleOwner,Observer{ setLastInsertedFile(it)})
    }



    private fun getParentFileSisters(unit:(it:List<File>?)->Unit){
        val livedata = parentFileSisters
        livedata.observeForever(object:Observer<List<File>?>{
            override fun onChanged(t: List<File>?) {
                livedata.removeObserver(this)
                unit(t)
            }
        })
    }
    private fun makeEmptyFileToCreate(fileStatus:FileStatus,list: List<File>?){
        setFileToCreate(
            File(fileId = 0,
                title = null,
                fileStatus = fileStatus ,
                colorStatus = ColorStatus.GRAY,
                deleted = false,
                fileBefore = list?.lastOrNull()?.fileId,
                parentFileId = parentOpenedFile?.fileId
            ))
    }


    fun makeFileInGuide(){
        getParentFileSisters {
            val first = it?.firstOrNull()
            first?.fileBefore = (getLastInsertedFile?.fileId?:0) + 1
            upDateFile(first ?:return@getParentFileSisters)
        }
        val before = fileToCreate
        val after = run {before.fileBefore = null
            before
        }
        setFileToCreate(after)
        onClickFinish()
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

    fun doOnBackPress(): Boolean {
        var backPressActionExists = true
        if(editFilePopUpVisible) setEditFilePopUpVisible(false)
        else if(bottomMenuVisible) setBottomMenuVisible(false)
        else backPressActionExists = false
        return backPressActionExists
    }


}