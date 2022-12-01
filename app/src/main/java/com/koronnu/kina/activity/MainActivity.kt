package com.koronnu.kina.activity

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.koronnu.kina.actions.*
import com.koronnu.kina.application.RoomApplication
import com.koronnu.kina.databinding.MainActivityBinding
import com.koronnu.kina.db.dataclass.File
import com.koronnu.kina.db.enumclass.FileStatus
import com.koronnu.kina.ui.animation.Animation
import com.koronnu.kina.customClasses.enumClasses.AnkiFragments
import com.koronnu.kina.customClasses.enumClasses.Guides
import com.koronnu.kina.customClasses.normalClasses.ColorPalletStatus
import com.koronnu.kina.customClasses.enumClasses.MainFragment
import com.koronnu.kina.databinding.CallOnInstallBinding
import com.koronnu.kina.ui.listener.KeyboardListener
import com.koronnu.kina.ui.listener.popUp.EditFilePopUpCL
import com.koronnu.kina.ui.observer.LibraryOb
import com.koronnu.kina.ui.view_set_up.ColorPalletViewSetUp
import com.koronnu.kina.ui.view_set_up.GetCustomDrawables
import com.koronnu.kina.ui.viewmodel.*


class MainActivity : AppCompatActivity(),View.OnClickListener {
    private lateinit var factory                 : ViewModelFactory
    private lateinit var mainNavCon              : NavController

    private var _createFileViewModel   : EditFileViewModel? = null
    val createFileViewModel get() = _createFileViewModel!!
    private var _createCardViewModel   : CreateCardViewModel? = null
    val createCardViewModel get() = _createCardViewModel!!

    val mainActivityViewModel :MainViewModel by viewModels { MainViewModel.getViewModel(this,layoutInflater,GuideActions(this)) }

    private var _deletePopUpViewModel   : DeletePopUpViewModel? = null
    val deletePopUpViewModel get() = _deletePopUpViewModel!!

    lateinit var libraryViewModel :LibraryBaseViewModel
    private lateinit var ankiFlipBaseViewModel   : AnkiFlipBaseViewModel
    private lateinit var ankiBaseViewModel       : AnkiBaseViewModel
    private lateinit var chooseFileMoveToViewModel : ChooseFileMoveToViewModel
    private lateinit var searchViewModel         : SearchViewModel

    private lateinit var binding                  : MainActivityBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
//      全部のデータを消したいとき
//        applicationContext.deleteDatabase("my_database")
//        ー－－－mainActivityのviewー－－－

        fun checkIfInstall(){
            val sharedPref = this.getSharedPreferences(
                "firstTimeGuide", Context.MODE_PRIVATE) ?: return
            if (!sharedPref.getBoolean("firstTimeGuide", false)) {
                mainActivityViewModel.guideViewModel.startGuide(Guides.CreateItems)

            }
        }
        fun setMainActivityLateInitVars(){

            binding = MainActivityBinding.inflate(layoutInflater)
            val navHostFragment = supportFragmentManager.findFragmentById(binding.fragContainerView.id) as NavHostFragment
            factory               = ViewModelFactory((application as RoomApplication).repository)
            _createFileViewModel   = ViewModelProvider(this,factory)[EditFileViewModel::class.java]
            libraryViewModel        =ViewModelProvider(this,LibraryBaseViewModel.getFactory(mainActivityViewModel,this))[LibraryBaseViewModel::class.java]
            _createCardViewModel   = ViewModelProvider(this,factory)[CreateCardViewModel::class.java]
            ankiFlipBaseViewModel =  ViewModelProvider(this,factory)[AnkiFlipBaseViewModel::class.java]
            ankiBaseViewModel     = ViewModelProvider(this,factory)[AnkiBaseViewModel::class.java]
            chooseFileMoveToViewModel      = ViewModelProvider(this,factory)[ChooseFileMoveToViewModel::class.java]
            _deletePopUpViewModel  = ViewModelProvider(this,factory)[DeletePopUpViewModel::class.java]
            searchViewModel       = ViewModelProvider(this,factory)[SearchViewModel::class.java]
            mainNavCon            =   navHostFragment.navController


        }
        fun checkBnvVisible():Boolean{
            return !(mainActivityViewModel.returnFragmentStatus()?.now== MainFragment.EditCard
                    ||ankiBaseViewModel.returnActiveFragment()== AnkiFragments.Flip
                    )
        }
        fun setUpMainActivityLayout(){
            binding.apply {
                frameLayEditFile.visibility = GONE
                frameBottomMenu.visibility = GONE
                mainTopConstrainLayout.requestFocus()
                mainTopConstrainLayout.viewTreeObserver.addOnGlobalLayoutListener(
                    object :KeyboardListener(mainTopConstrainLayout){
                        override fun onKeyBoardAppear() {
                            super.onKeyBoardAppear()
                            mainActivityViewModel.setBnvVisibility(false)
                        }

                        override fun onKeyBoardDisappear() {
                            super.onKeyBoardDisappear()
                            mainActivityViewModel.setBnvVisibility(checkBnvVisible())
                        }
                    }
                )
                ColorPalletViewSetUp().makeAllColPalletUnselected(this@MainActivity,editFileBinding.colPaletBinding)
            }
        }
        fun addMainActivityClickListeners(){
            binding.apply {
                arrayOf(
                    bindingAddMenu.frameLayNewCard,
                    bindingAddMenu.frameLayNewFlashcard,
                    bindingAddMenu.frameLayNewFolder,
                    bindingAddMenu.root,
                    fragConViewCover,
                    bnvCover,
                    bnvBinding.bnvImvAdd,
                    bnvBinding.bnvTxvTabAnki,
                    bnvBinding.bnvTxvTabLibrary,
                    bnvBinding.bnvImvTabAnki,
                    bnvBinding.bnvImvTabLibrary,
                ).onEach {
                    it.setOnClickListener(this@MainActivity)
                }
                editFileBinding.apply {
                    colPaletBinding.apply {
                        arrayOf(
                            imvColBlue,imvColGray,imvColRed,imvColYellow,imvIconPallet,btnClose,btnFinish,editFileBinding.root
                        ).onEach {
                            it.setOnClickListener(EditFilePopUpCL(binding.editFileBinding,createFileViewModel)) }
                    }
                }
            }
        }
        fun changeTabView(previous: MainFragment?, now: MainFragment){
            binding.bnvBinding.apply {
                fun getImv(mainFragment: MainFragment):ImageView?{
                    return when(mainFragment){
                        MainFragment.Anki -> bnvImvTabAnki
                        MainFragment.Library ->bnvImvTabLibrary
                        else -> null
                    }
                }
                fun getTxv(mainFragment: MainFragment):TextView?{
                    return when(mainFragment){
                        MainFragment.Anki -> bnvTxvTabAnki
                        MainFragment.Library ->bnvTxvTabLibrary
                        else -> null
                    }
                }
                val nowImv = getImv(now) ?:return
                val nowTxv = getTxv(now) ?:return
                nowTxv.visibility = GONE
                nowImv.isSelected = true
                val preImv = getImv(previous ?:return) ?:return
                val preTxv = getTxv(previous ) ?:return
                preTxv.visibility = VISIBLE
                preImv.isSelected = false
            }

        }
        fun createAllViewModels(){
            ViewModelProvider(this,factory)[CardTypeStringViewModel::class.java]
            ViewModelProvider(this,factory)[AnkiBoxViewModel::class.java]
            ViewModelProvider(this)[AnkiSettingPopUpViewModel::class.java]
            ViewModelProvider(this,factory)[FlipTypeAndCheckViewModel::class.java]
        }

        setMainActivityLateInitVars()
        createAllViewModels()
        setUpMainActivityLayout()
        addMainActivityClickListeners()
        setContentView(binding.root)
        checkIfInstall()
        val childFragmentStatusObserver      = Observer<MainViewModel.MainActivityChildFragmentStatus>{
            changeTabView(it.before,it.now)
        }
//        val confirmEndGuideObserver         = Observer<Boolean>{
//            changeViewVisibility(callOnInstallBinding.frameLayConfirmEndGuidePopUp,it)
//        }

//        val helpOptionVisibilityObserver      = Observer<Boolean>{
//            val frameLayHelp = binding.frameLayCallOnInstall
//            changeViewVisibility(frameLayHelp,it)
//
//            fun setUpMenuMode(){
//                val helpBinding = HelpOptionsBinding.inflate(layoutInflater)
//                helpBinding.apply {
//                    when(mainActivityViewModel.returnFragmentStatus()?.now){
//                        MainFragment.Library -> {
//                            changeViewVisibility(linLayHelpMenuLibrary,true)
//                            changeViewVisibility(linLayHelpMenuAnki,false)
//                        }
//                        else -> {
//                            changeViewVisibility(linLayHelpMenuLibrary,false)
//                            changeViewVisibility(linLayHelpMenuAnki,true)
//                        }
//                    }
//                    if(libraryViewModel.returnParentRVItems().isEmpty()){
//                        arrayOf(menuHowToDeleteItems,
//                            menuHowToEditItems
//                        ).onEach {v-> changeViewVisibility(v,false) }
//                    }
//                }
//                helpBinding.apply {
//                    arrayOf(
//                        root,
//                        menuHowToDeleteItems,
//                        menuHowToCreateItems,
//                        menuHowToEditItems,
//                        menuHowToMoveItems
//                    ).onEach { view ->
//                        view.setOnClickListener { v->
//                            when(v){
//                                menuHowToDeleteItems -> GuideActions(this@MainActivity,binding.frameLayCallOnInstall).deleteGuide()
//                                menuHowToCreateItems -> {
//                                    CreateGuide(this@MainActivity,
//                                        binding.frameLayCallOnInstall).callOnFirst()
//                                }
//                                menuHowToEditItems -> {
//                                   GuideActions(this@MainActivity,binding.frameLayCallOnInstall).editGuide()
//                                }
//                                menuHowToMoveItems -> {
//                                    MoveGuide(this@MainActivity,callOnInstallBinding).moveGuide(0,mainActivityViewModel,libraryViewModel,createFileViewModel,chooseFileMoveToViewModel,createCardViewModel)
//                                }
//                            }
//                        }
//                    }
//                }
//                frameLayHelp.addView(helpBinding.root)
//
//            }
//            if(it) setUpMenuMode() else frameLayHelp.removeAllViews()
//        }
        val guideVisibilityObserver      = Observer<Boolean>{
            changeViewVisibility(binding.frameLayCallOnInstall,mainActivityViewModel.checkIfFrameLayHelpIsVisible())
            if(it.not()){
              binding.frameLayCallOnInstall.removeAllViews()
            }
        }
        val lastInsertedFileObserver      = Observer<File>{
            createFileViewModel.setLastInsertedFile(it)
        }
        val bnvVisibilityObserver            = Observer<Boolean>{
            changeViewVisibility(binding.frameBnv,it)
        }
        val bnvCoverObserver            = Observer<Boolean>{
            changeViewVisibility(binding.bnvCover,it)
        }
        val popUpEditFileVisibilityObserver  = Observer<Boolean>{
            Animation().animatePopUpAddFile(binding.frameLayEditFile,it)
            changeViewVisibility(binding.fragConViewCover,it||createFileViewModel.getBottomMenuVisible())
            if(it.not())  hideKeyBoard(binding.editFileBinding.edtFileTitle,this)
        }
        val popUpEditFileUIDataObserver        = Observer<EditFileViewModel.PopUpUI>{
            LibraryOb().observeEditFilePopUp(binding.editFileBinding,it,this@MainActivity)
            createFileViewModel.onClickColorPallet(it.colorStatus)
        }
        val editFileColPalletObserver = Observer<ColorPalletStatus> {
            val context = this@MainActivity
            val colBinding = binding.editFileBinding.colPaletBinding
            ColorPalletViewSetUp().apply {
                changeColPalletCol(context,it.colNow,true,colBinding)
                changeColPalletCol(context,it.before,false,colBinding)
            }
            val get = GetCustomDrawables(this)
            val draw:Drawable =
            when(createFileViewModel.returnTokenFile()?.fileStatus){
                FileStatus.FOLDER -> get.getFolderIconByCol(it.colNow)
                FileStatus.FLASHCARD_COVER -> get.getFlashCardIconByCol(it.colNow)
                else -> return@Observer
            }
            binding.editFileBinding.imvFileType.setImageDrawable(draw)

        }
        val bottomMenuVisibilityObserver       = Observer<Boolean>{
            Animation().animateFrameBottomMenu(binding.frameBottomMenu,it)
            changeViewVisibility(binding.fragConViewCover,it||createFileViewModel.returnEditFilePopUpVisible())
        }
        val bottomMenuClickableStatusObserver = Observer<EditFileViewModel.BottomMenuClickable>{
            binding.bindingAddMenu.apply {
                changeViewVisibility(frameLayNewFolder,it.createFile)
                changeViewVisibility(frameLayNewFlashcard,it.createFlashCardCover)
                changeViewVisibility(frameLayNewCard,it.createCard)
            }
        }
        val parentFileParentObserver          = Observer<File>{
            createFileViewModel.setParentFileParent(it)
        }
        val editFileParentFileObserver           = Observer<File> {
            createFileViewModel.parentFileParent(it?.fileId).observe(this,parentFileParentObserver)
        }
        val libraryParentFileObserver           = Observer<File?>{
            createCardViewModel.setParentFlashCardCover(it)
            createFileViewModel.setParentTokenFileParent(it)
            createFileViewModel.getChildFilesByFileIdFromDB(it?.fileId).observe(this){list->
                createFileViewModel.setParentFileSisters(list)
            }
            createFileViewModel.parentFileParent(it?.parentFileId).observe(this@MainActivity,editFileParentFileObserver)
            createFileViewModel.lastInsertedFile.observe(this,lastInsertedFileObserver)
        }
        val libraryParentRVItemsObserver        = Observer<List<Any>>{
            createFileViewModel.setPosition(it.size+1)
        }

//        ー－－－mainActivityのviewModel 読み取りー－－－
        mainActivityViewModel.setMainActivityNavCon(mainNavCon)
        mainActivityViewModel.setMainActivityBinding(binding)
//        mainActivityViewModel.setLayoutInflater(layoutInflater)
        mainActivityViewModel.observeLiveDataFromMainActivity(this)
        mainActivityViewModel.childFragmentStatus   .observe(this@MainActivity,childFragmentStatusObserver)
        mainActivityViewModel.bnvVisibility         .observe(this@MainActivity,bnvVisibilityObserver)
        mainActivityViewModel.bnvCoverVisible       .observe(this,bnvCoverObserver)
//        mainActivityViewModel.helpOptionVisibility  .observe(this,helpOptionVisibilityObserver)
        mainActivityViewModel.guideVisibility       .observe(this,guideVisibilityObserver)
//        mainActivityViewModel.confirmEndGuidePopUpVisible.observe(this,confirmEndGuideObserver)
//        ー－－－CreateFileViewModelの読み取りー－－－
        createFileViewModel.apply{
            onCreate()
            editFilePopUpVisible.observe(this@MainActivity,popUpEditFileVisibilityObserver)
            filePopUpUIData     .observe(this@MainActivity,popUpEditFileUIDataObserver)
            bottomMenuVisible   .observe(this@MainActivity,bottomMenuVisibilityObserver)
            bottomMenuClickable .observe(this@MainActivity,bottomMenuClickableStatusObserver)
            colorPalletStatus   .observe(this@MainActivity,editFileColPalletObserver)
            lastInsertedFile.observe(this@MainActivity,lastInsertedFileObserver)
        }
//        ー－－－LibraryViewModelの読み取りー－－－
        libraryViewModel. onCreate()
        libraryViewModel.parentFile.observe(this@MainActivity,libraryParentFileObserver)
        libraryViewModel.parentRVItems.observe(this@MainActivity,libraryParentRVItemsObserver)

        createCardViewModel.setMainActivityNavCon(mainNavCon)
        createCardViewModel.lastInsertedCardFromDB.observe(this) {
            createCardViewModel.setLastInsertedCard(it)
        }

    }



    fun onBackPress(){
        var actionDone = true
        if(!mainActivityViewModel.guideViewModel.doOnBackPress()){}
        else if (mainActivityViewModel.returnHelpOptionVisibility())
            mainActivityViewModel.setHelpOptionVisibility(false)
        else if(createFileViewModel.getBottomMenuVisible())
            createFileViewModel.setBottomMenuVisible(false)
        else if(createFileViewModel.returnEditFilePopUpVisible())
            createFileViewModel.setEditFilePopUpVisible(false)
        else if(deletePopUpViewModel.returnConfirmDeleteWithChildrenVisible())
            deletePopUpViewModel.setConfirmDeleteWithChildrenVisible(false)
        else if(deletePopUpViewModel.returnConfirmDeleteVisible())
            deletePopUpViewModel.setConfirmDeleteVisible(false)
        else if(libraryViewModel.returnMultiSelectMode()){
            if(libraryViewModel.returnMultiMenuVisibility())
                libraryViewModel.setMultiMenuVisibility(false)
            else libraryViewModel.setMultipleSelectMode(false)
        }
        else if(libraryViewModel.returnLeftSwipedItemExists())
            libraryViewModel.makeAllUnSwiped()
        else if (searchViewModel.returnSearchModeActive())
            searchViewModel.setSearchModeActive(false)
        else if(ankiBaseViewModel.returnSettingVisible())
            ankiBaseViewModel.setSettingVisible(false)
        else if (mainActivityViewModel.returnHelpOptionVisibility())
            mainActivityViewModel.setHelpOptionVisibility(false)
        else if (mainActivityViewModel.returnFragmentStatus()?.now==MainFragment.Library)
            libraryViewModel.returnLibraryNavCon()?.popBackStack()

    }

    var a :OnBackPressedCallback? = null

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        onBackPressedDispatcher.addCallback(this /* lifecycle owner */, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if(!mainActivityViewModel.doOnBackPress()) mainNavCon.popBackStack()
            }
        })
    }
    override fun onClick(v: View?) {

        binding.apply {
            bindingAddMenu.apply {
                bnvBinding.apply {
                when(v){
                    bnvImvTabLibrary,bnvTxvTabLibrary -> mainActivityViewModel.changeFragment(
                        MainFragment.Library)
                    bnvImvTabAnki,bnvTxvTabAnki       -> mainActivityViewModel.changeFragment(
                        MainFragment.Anki)
                    bnvImvAdd                         -> createFileViewModel.setBottomMenuVisible(true)
                    fragConViewCover                  -> createFileViewModel.makeBothPopUpGone()
                    frameLayNewCard                        -> createCardViewModel.onClickAddNewCardBottomBar()
                    frameLayNewFlashcard                    -> createFileViewModel.onClickCreateFile(FileStatus.FLASHCARD_COVER)
                    frameLayNewFolder                      -> createFileViewModel.onClickCreateFile(FileStatus.FOLDER)
                }
            }
            }

        }

    }
}