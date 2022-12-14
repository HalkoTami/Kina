package com.koronnu.kina.activity

import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.koronnu.kina.actions.changeViewVisibility
import com.koronnu.kina.actions.hideKeyBoard
import com.koronnu.kina.customClasses.normalClasses.ColorPalletStatus
import com.koronnu.kina.databinding.MainActivityBinding
import com.koronnu.kina.db.dataclass.File
import com.koronnu.kina.db.enumclass.FileStatus
import com.koronnu.kina.tabLibrary.LibraryBaseViewModel
import com.koronnu.kina.tabLibrary.chooseFileMoveTo.ChooseFileMoveToViewModel
import com.koronnu.kina.ui.animation.Animation
import com.koronnu.kina.ui.observer.LibraryOb
import com.koronnu.kina.ui.view_set_up.ColorPalletViewSetUp
import com.koronnu.kina.ui.view_set_up.GetCustomDrawables
import com.koronnu.kina.ui.viewmodel.*


class MainActivity : AppCompatActivity(){
    private lateinit var mainNavCon              : NavController

    private var _createFileViewModel   : EditFileViewModel? = null
    val createFileViewModel get() = _createFileViewModel!!
    private var _createCardViewModel   : CreateCardViewModel? = null
    val createCardViewModel get() = _createCardViewModel!!

    val mainActivityViewModel :MainViewModel by viewModels { MainViewModel.getViewModel(this) }

    private var _deletePopUpViewModel   : DeletePopUpViewModel? = null
    val deletePopUpViewModel get() = _deletePopUpViewModel!!

    lateinit var libraryViewModel : LibraryBaseViewModel
    private lateinit var ankiBaseViewModel       : AnkiBaseViewModel
    private lateinit var chooseFileMoveToViewModel : ChooseFileMoveToViewModel
    private lateinit var searchViewModel         : SearchViewModel

    private lateinit var binding                  : MainActivityBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fun setMainActivityLateInitVars(){

            binding = MainActivityBinding.inflate(layoutInflater)
            val navHostFragment = supportFragmentManager.findFragmentById(binding.fragContainerView.id) as NavHostFragment
            libraryViewModel        =ViewModelProvider(this,
                LibraryBaseViewModel.getFactory(mainActivityViewModel,this,this.baseContext))[LibraryBaseViewModel::class.java]
            chooseFileMoveToViewModel      = ViewModelProvider(this,
                ChooseFileMoveToViewModel.getFactory(libraryViewModel))[ChooseFileMoveToViewModel::class.java]
            searchViewModel       = ViewModelProvider(this,SearchViewModel.Factory)[SearchViewModel::class.java]
            mainNavCon            =   navHostFragment.navController


        }


        setMainActivityLateInitVars()
        setContentView(binding.root)
//       TODO checkIfInstall()

        val lastInsertedFileObserver      = Observer<File>{
            createFileViewModel.setLastInsertedFile(it)
        }
        val bnvVisibilityObserver            = Observer<Boolean>{
            changeViewVisibility(binding.frameBnv,it)
        }
        val bnvCoverObserver            = Observer<Boolean>{
            changeViewVisibility(binding.bnvCover,it)
        }

        val popUpEditFileUIDataObserver        = Observer<EditFileViewModel.PopUpUI>{
            LibraryOb().observeEditFilePopUp(binding.editFileBinding,it,this@MainActivity)
            createFileViewModel.onClickColorPallet(it.colorStatus)
        }
        val editFileColPalletObserver = Observer<ColorPalletStatus> {
            val colBinding = binding.editFileBinding.colPaletBinding
            ColorPalletViewSetUp().apply {
                changeColPalletCol(it.colNow,true,colBinding)
                changeColPalletCol(it.before,false,colBinding)
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
        mainActivityViewModel.observeLiveDataFromMainActivity(this,this)
        mainActivityViewModel.bnvVisibility         .observe(this@MainActivity,bnvVisibilityObserver)
        mainActivityViewModel.bnvCoverVisible       .observe(this,bnvCoverObserver)
//        ー－－－CreateFileViewModelの読み取りー－－－
        createFileViewModel.apply{
            onCreate()
            filePopUpUIData     .observe(this@MainActivity,popUpEditFileUIDataObserver)
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



    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        onBackPressedDispatcher.addCallback(this /* lifecycle owner */, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                mainActivityViewModel.doOnBackPress()
            }
        })
    }
}