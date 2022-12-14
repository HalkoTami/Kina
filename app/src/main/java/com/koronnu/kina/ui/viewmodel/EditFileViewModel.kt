package com.koronnu.kina.ui.viewmodel

import android.content.res.Resources
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
import com.koronnu.kina.customClasses.normalClasses.ColorPalletStatus
import com.koronnu.kina.customClasses.enumClasses.EditingMode
import com.koronnu.kina.ui.animation.Animation
import com.koronnu.kina.ui.view_set_up.ColorPalletViewSetUp
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
    private val ankiBoxViewModel    get() = mainViewModel.ankiBaseViewModel.ankiBoxViewModel
    private val createCardViewModel get() = mainViewModel.createCardViewModel
    private val mainActivityBinding get() = mainViewModel.mainActivityBinding
    private val editFilePopUpBinding get() = mainActivityBinding.editFileBinding
    private val colorPalletBinding  get() = editFilePopUpBinding.colPaletBinding
    private val addMenuBinding      get() = mainActivityBinding.bindingAddMenu
    fun afterSetMainActivityBinding(){
        ColorPalletViewSetUp().makeAllColPalletUnselected(colorPalletBinding)
        setClickListeners()
    }
    private fun setClickListeners(){
        mainActivityBinding.fragConViewCover.setOnClickListener { makeBothPopUpGone() }
        addMenuBinding.frameLayNewCard      .setOnClickListener { createCardViewModel.onClickAddNewCardBottomBar()}
        addMenuBinding.frameLayNewFlashcard .setOnClickListener{ onClickCreateFile(FileStatus.FLASHCARD_COVER)}
        addMenuBinding.frameLayNewFolder    .setOnClickListener{ onClickCreateFile(FileStatus.FOLDER)}
        addMenuBinding.root                 .setOnClickListener(null)
        colorPalletBinding.imvColBlue       .setOnClickListener{ onClickColorPallet(ColorStatus.BLUE)}
        colorPalletBinding.imvColGray       .setOnClickListener{ onClickColorPallet(ColorStatus.GRAY)}
        colorPalletBinding.imvColRed        .setOnClickListener{ onClickColorPallet(ColorStatus.RED)}
        colorPalletBinding.imvColYellow     .setOnClickListener{ onClickColorPallet(ColorStatus.YELLOW)}
        editFilePopUpBinding.btnClose       .setOnClickListener{ setEditFilePopUpVisible(false) }
        editFilePopUpBinding.btnFinish      .setOnClickListener{ onClickFinish() }
        editFilePopUpBinding.root           .setOnClickListener(null)

    }

    fun getChildFilesByFileIdFromDB(fileId: Int?) = repository.getFileDataByParentFileId(fileId).asLiveData()
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

    private var _mode:EditingMode? = null
    private fun setMode(mode: EditingMode){
        _mode = mode
    }
    val mode get() = _mode!!



//    add Bottom Menu
    private fun setBottomMenuClickable(createFolder: Boolean,
                               createFlashCardCover: Boolean,
                               createCard: Boolean){
        addMenuBinding.frameLayNewCard.isEnabled = createCard
        addMenuBinding.frameLayNewFlashcard.isEnabled = createFlashCardCover
        addMenuBinding.frameLayNewFolder.isEnabled = createFolder
    }


    fun makeAllBottomMenuClickable(){
        setBottomMenuClickable(
            createFolder =  true,
            createFlashCardCover = true,
            createCard = true
        )
    }

    fun filterBottomMenuOnlyCard(){
        setBottomMenuClickable(
            createFolder =  false,
            createFlashCardCover = false,
            createCard = true
        )
    }
    fun filterBottomMenuWhenInChooseFileMoveTo(flashCard:Boolean,home:Boolean,ancestors: List<File>?){
        setBottomMenuClickable(
            createFolder = if(!flashCard)  (ancestors?.size ?: 0) < 3 else false,
            createFlashCardCover = flashCard&&home,
            createCard = false
        )
    }
    fun filterBottomMenuByAncestors(ancestors:List<File>, parentFile:File){
        val parentFileIsNotFlashCard = parentFile.fileStatus != FileStatus.FLASHCARD_COVER
        val createFlashCardCoverEnabled = parentFile.fileStatus != FileStatus.FLASHCARD_COVER
        val createCardEnabled = parentFile.fileStatus == FileStatus.FLASHCARD_COVER
        val createFolderEnabled = if(parentFileIsNotFlashCard) ancestors.size < 3 else false
        setBottomMenuClickable(createFolderEnabled,createFlashCardCoverEnabled,createCardEnabled)
    }



    fun makeBothPopUpGone(){
        setBottomMenuVisible(false)
        setEditFilePopUpVisible(false)
    }
    private var _bottomMenuVisible:Boolean? = null
    fun setBottomMenuVisible(visible: Boolean){
        if(bottomMenuVisible == visible) return
        _bottomMenuVisible = visible
        if(visible) setEditFilePopUpVisible(false)
        doAfterSetBottomMenuVisible()
    }
    fun setUpFragConViewCoverVisibility(){
        changeViewVisibility(mainActivityBinding.fragConViewCover,
            editFilePopUpVisible||bottomMenuVisible)
    }
    fun doAfterSetBottomMenuVisible(){
        Animation().animateFrameBottomMenu(mainActivityBinding.frameBottomMenu,bottomMenuVisible)
        setUpFragConViewCoverVisibility()
    }
    val bottomMenuVisible get() = _bottomMenuVisible ?:false



    fun onClickCreateFile(fileStatus: FileStatus){
        if(fileStatus==FileStatus.ANKI_BOX_FAVOURITE&&
                returnAnkiBoxCards().isNullOrEmpty()) return
        setMode(EditingMode.New)
        makeEmptyFileToCreate(fileStatus)
        setTokenFile(fileToCreate)
        makeFilePopUp(returnParentTokenFileParent(), fileToCreate,mode)
        setEditFilePopUpVisible(true)
    }
    fun onClickEditFileInRV(editingFile:File){
        setMode(EditingMode.Edit)
        setFileToEdit(editingFile)
        setTokenFile(fileToEdit)
        makeFilePopUp(returnParentTokenFileParent(),fileToEdit,mode)
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
    val editFilePopUpVisible get() = _editFilePopUpVisible!!
    private fun doAfterSetEditFilePopUpVisible(){
        Animation().animatePopUpAddFile(mainActivityBinding.frameLayEditFile,editFilePopUpVisible)
        setUpFragConViewCoverVisibility()
        if(editFilePopUpVisible.not())  hideKeyBoard(editFilePopUpBinding.edtFileTitle)
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
        doAfterSetTokenFile()
    }
    fun doAfterSetTokenFile(){

    }
    fun returnTokenFile():File?{
        return _tokenFile.value
    }
    fun getHintTxvText():String{
        return when(mode){
            EditingMode.New -> resources.getString(when(fileToCreate.fileStatus){
                FileStatus.FOLDER ->  R.string.editFilePopUpBin_HintTxv_createNewFolder
                FileStatus.ANKI_BOX_FAVOURITE ->R.string.editFilePopUpBin_HintTxv_createNewAnkiBoxfavourite
                FileStatus.FLASHCARD_COVER -> R.string.editFilePopUpBin_HintTxv_createNewFlashCard
                else  -> throw IllegalArgumentException()
            })
            EditingMode.Edit -> resources.getString(R.string.editFilePopUpBin_HintTxv_edit,fileToEdit.title)
        }
    }
    private fun makeFilePopUp(tokenFileParent: File?,tokenFile:File, mode: EditingMode){
        fun getLeftTopText():String{
            return tokenFileParent?.title ?: resources.getString(R.string.title_home)
        }

        fun getTitleEdtHint():String{
            return resources.getString(when(mode){
                EditingMode.Edit -> R.string.editFilePopUpBin_edtFileTitleHint_edit
                EditingMode.New -> R.string.editFilePopUpBin_edtFileTitleHint_create
            })
        }
        fun getTitleEdtText():String{
            return when(mode){
                EditingMode.Edit -> tokenFile.title.toString()
                EditingMode.New -> String()
            }
        }
        fun getFinishBtnText():String{
            return resources.getString(when(mode){
                EditingMode.Edit -> R.string.editFilePopUpBin_btnFinish_update
                EditingMode.New -> R.string.editFilePopUpBin_btnFinish_create
            })
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

    private var _fileToCreate :File? = null
    private fun setFileToCreate(file: File){
        _fileToCreate = file
    }
    val fileToCreate get() = _fileToCreate!!
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
    private var _fileToEdit:File? = null
    private fun setFileToEdit(file: File){
        _fileToEdit= file
    }
    val fileToEdit get() = _fileToEdit!!

    fun makeFileInGuide(){
        onClickFinish()
        fileToCreate.fileBefore = null
        val first = returnParentFileSisters().firstOrNull()
        first?.fileBefore = (returnLastInsertedFileId()?:0) + 1
        upDateFile(first ?:return)
    }

    fun onClickFinish(){
        val edtFileTitle = editFilePopUpBinding.edtFileTitle
        val title = edtFileTitle.text.toString()
        if(title.isBlank()) {
            edtFileTitle.hint = "タイトルが必要です"
            return
        }
        setEditFilePopUpVisible(false)
        val color = returnColPalletStatus()?.colNow ?:ColorStatus.GRAY

        when(mode ){
            EditingMode.New -> {
                fileToCreate.title = title
                fileToCreate.colorStatus = color
                if(fileToCreate.fileStatus==FileStatus.ANKI_BOX_FAVOURITE){
                    val cardList = ankiBoxViewModel.returnAnkiBoxItems()
                    if(cardList.isEmpty()) return
                    addCardsToFavouriteAnkiBox(cardList,returnLastInsertedFileId() ?:0,fileToCreate)
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