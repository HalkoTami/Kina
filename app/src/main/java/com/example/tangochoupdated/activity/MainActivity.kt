package com.example.tangochoupdated.activity

import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.tangochoupdated.application.RoomApplication
import com.example.tangochoupdated.databinding.MainActivityBinding
import com.example.tangochoupdated.db.dataclass.Card
import com.example.tangochoupdated.db.dataclass.File
import com.example.tangochoupdated.db.enumclass.MainFragment
import com.example.tangochoupdated.ui.viewmodel.CreateFileViewModel
import com.example.tangochoupdated.ui.view_set_up.SearchViewModel
import com.example.tangochoupdated.ui.animation.Animation
import com.example.tangochoupdated.ui.fragment.base_frag_con.CreateCardFragmentBaseDirections
import com.example.tangochoupdated.ui.listener.popUp.EditFilePopUpCL
import com.example.tangochoupdated.ui.observer.LibraryOb
import com.example.tangochoupdated.ui.view_set_up.ColorPalletViewSetUp
import com.example.tangochoupdated.ui.viewmodel.*


class MainActivity : AppCompatActivity(),View.OnClickListener {
    private lateinit var factory                 : ViewModelFactory
    private lateinit var mainNavCon              : NavController
    private lateinit var mainActivityViewModel   : BaseViewModel
    private lateinit var createFileViewModel     : CreateFileViewModel
    private lateinit var createCardViewModel     : CreateCardViewModel
    private lateinit var libraryViewModel        : LibraryViewModel
    private lateinit var binding                  : MainActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
//      全部のデータを消したいとき
//        applicationContext.deleteDatabase("my_database")
//        ー－－－mainActivityのviewー－－－

        fun setMainActivityLateInitVars(){

            binding = MainActivityBinding.inflate(layoutInflater)
            val navHostFragment = supportFragmentManager.findFragmentById(binding.fragContainerView.id) as NavHostFragment
            factory               = ViewModelFactory((application as RoomApplication).repository)
            mainActivityViewModel = ViewModelProvider(this)[BaseViewModel::class.java]
            createFileViewModel   = ViewModelProvider(this,factory)[CreateFileViewModel::class.java]
            createCardViewModel   = ViewModelProvider(this,factory)[CreateCardViewModel::class.java]
            libraryViewModel      = ViewModelProvider(this,factory)[LibraryViewModel::class.java]
            mainNavCon            =   navHostFragment.navController


        }
        fun setUpMainActivityLayout(){
            binding.apply {
                frameLayEditFile.visibility = GONE
                frameBottomMenu.visibility = GONE
                mainTopConstrainLayout.requestFocus()
                ColorPalletViewSetUp().makeAllColPalletUnselected(this@MainActivity,editFileBinding.colPaletBinding)
            }
        }
        fun addMainActivityClickListeners(){
            binding.apply {
                arrayOf(
                    bindingAddMenu.imvnewCard,
                    bindingAddMenu.imvnewTangocho,
                    bindingAddMenu.imvnewfolder,
                    bindingAddMenu.root,
                    fragConViewCover,
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
                            imvColBlue,imvColGray,imvColRed,imvColYellow,imvIconPalet,btnClose,btnFinish,root,
                        ).onEach {
                            it.setOnClickListener(EditFilePopUpCL(binding.editFileBinding,createFileViewModel)) }
                    }
                }

            }
        }
        fun changeTabView(previous:MainFragment?,now: MainFragment){
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
        fun booleanToVisibility(visibility: Boolean):Int{
            return if(visibility) VISIBLE else GONE
        }
        fun changeViewVisibility(view:View,visibility: Boolean){
            view.visibility = if(visibility) VISIBLE else GONE
        }
        setMainActivityLateInitVars()
        ViewModelProvider(this,factory)[SearchViewModel::class.java]
        ViewModelProvider(this,factory)[StringCardViewModel::class.java]
        ViewModelProvider(this,factory)[ChooseFileMoveToViewModel::class.java]
        ViewModelProvider(this,factory)[AnkiFragBaseViewModel::class.java]
        ViewModelProvider(this,factory)[AnkiBoxFragViewModel::class.java]
        ViewModelProvider(this,factory)[AnkiFlipFragViewModel::class.java]
        ViewModelProvider(this)[AnkiSettingPopUpViewModel::class.java]
        ViewModelProvider(this,factory)[AnkiFlipTypeAndCheckViewModel::class.java]
        ViewModelProvider(this,factory)[DeletePopUpViewModel::class.java]
        setUpMainActivityLayout()
        addMainActivityClickListeners()
        setContentView(binding.root)

        val childFragmentStatusObserver      = Observer<BaseViewModel.MainActivityChildFragmentStatus>{
            changeTabView(it.before,it.now)
        }
        val bnvVisibilityObserver            = Observer<Boolean>{
            binding.frameBnv.visibility = if(it == true) VISIBLE else GONE
        }
        val popUpEditFileVisibilityObserver  = Observer<Boolean>{
            binding.frameLayEditFile.visibility = if(it)VISIBLE else GONE
            binding.fragConViewCover.visibility =  if(it||createFileViewModel.returnBottomMenuVisible())VISIBLE else GONE
        }
        val popUpEditFileUIDataObserver        = Observer<CreateFileViewModel.PopUpUI>{
            LibraryOb().observeEditFilePopUp(binding.editFileBinding,it,this@MainActivity)
        }
        val bottomMenuVisibilityObserver       = Observer<Boolean>{
            Animation().animatePopUpAddFile(binding.frameBottomMenu,booleanToVisibility(it))
            changeViewVisibility(binding.fragConViewCover,it)
        }
        val bottomMenuClickableStatusObserver = Observer<CreateFileViewModel.BottomMenuClickable>{
            binding.bindingAddMenu.apply {
                changeViewVisibility(imvnewfolder,it.createFile)
                changeViewVisibility(imvnewTangocho,it.createFlashCardCover)
                changeViewVisibility(imvnewCard,it.createCard)
            }
        }
        val parentFileParentObserver          = Observer<File>{
            createFileViewModel.setParentFileParent(it)
        }
        val editFileParentFileObserver           = Observer<File> {
            createFileViewModel.parentFileParent(it?.fileId).observe(this,parentFileParentObserver)
        }
        val libraryParentFileObserver           = Observer<File>{
            createCardViewModel.setParentFlashCardCover(it)
            createFileViewModel.setParentFile(it)
            createFileViewModel.parentFileParent(it?.parentFileId).observe(this@MainActivity,editFileParentFileObserver)
        }
        val libraryParentRVItemsObserver        = Observer<List<Any>>{
            createFileViewModel.setPosition(it.size+1)
        }
//        ー－－－mainActivityのviewModel 読み取りー－－－
        mainActivityViewModel.setMainActivityNavCon(mainNavCon)
        mainActivityViewModel.childFragmentStatus   .observe(this@MainActivity,childFragmentStatusObserver)
        mainActivityViewModel.bnvVisibility         .observe(this@MainActivity,bnvVisibilityObserver)
//        ー－－－CreateFileViewModelの読み取りー－－－
        createFileViewModel.apply{
            onCreate()
            editFilePopUpVisible.observe(this@MainActivity,popUpEditFileVisibilityObserver)
            filePopUpUIData     .observe(this@MainActivity,popUpEditFileUIDataObserver)
            bottomMenuVisible   .observe(this@MainActivity,bottomMenuVisibilityObserver)
            bottomMenuClickable .observe(this@MainActivity,bottomMenuClickableStatusObserver)
        }
//        ー－－－LibraryViewModelの読み取りー－－－
        libraryViewModel. onCreate()
        libraryViewModel.parentFile.observe(this@MainActivity,libraryParentFileObserver)
        libraryViewModel.parentRVItems.observe(this@MainActivity,libraryParentRVItemsObserver)
 }

    override fun onClick(v: View?) {

        binding.apply {
            bindingAddMenu.apply {
                bnvBinding.apply {
                when(v){
                    bnvImvTabLibrary,bnvTxvTabLibrary -> mainActivityViewModel.changeFragment(MainFragment.Library)
                    bnvImvTabAnki,bnvTxvTabAnki       -> mainActivityViewModel.changeFragment(MainFragment.Anki)
                    bnvImvAdd                         -> createFileViewModel.setBottomMenuVisible(true)
                    fragConViewCover                  -> createFileViewModel.makeBothPopUpGone()
                    imvnewCard                        -> {
                        createCardViewModel.onClickAddNewCardBottomBar(mainNavCon)
                    }
                    imvnewTangocho                    -> createFileViewModel.onCLickCreateFlashCardCover()
                    imvnewfolder                      -> createFileViewModel.onClickCreateFolder()
                }
            }
            }
        }
    }
}