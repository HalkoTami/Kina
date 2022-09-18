package com.example.tangochoupdated.activity

import android.app.ActionBar
import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.Rect
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.Shape
import android.os.Bundle
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.tangochoupdated.R
import com.example.tangochoupdated.application.RoomApplication
import com.example.tangochoupdated.databinding.CallOnInstallBinding
import com.example.tangochoupdated.databinding.MainActivityBinding
import com.example.tangochoupdated.db.dataclass.File
import com.example.tangochoupdated.db.enumclass.FileStatus
import com.example.tangochoupdated.ui.viewmodel.customClasses.MainFragment
import com.example.tangochoupdated.ui.viewmodel.CreateFileViewModel

import com.example.tangochoupdated.ui.animation.Animation
import com.example.tangochoupdated.ui.animation.makeToast
import com.example.tangochoupdated.ui.customViews.HolePosition
import com.example.tangochoupdated.ui.listener.popUp.EditFilePopUpCL
import com.example.tangochoupdated.ui.observer.LibraryOb
import com.example.tangochoupdated.ui.view_set_up.ColorPalletViewSetUp
import com.example.tangochoupdated.ui.view_set_up.GetCustomDrawables
import com.example.tangochoupdated.ui.viewmodel.*
import com.example.tangochoupdated.ui.viewmodel.customClasses.ColorPalletStatus
import java.lang.reflect.Array


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

        fun doOnInstall(){
            val sharedPref = this.getSharedPreferences(
                "firstTimeGuide", Context.MODE_PRIVATE) ?: return
            val editor = sharedPref.edit()
            if (!sharedPref.getBoolean("firstTimeGuide", false)) {
                val onCallBinding = CallOnInstallBinding.inflate(layoutInflater)


                fun doSome(order:Int){
                    fun setText(string: String){
                        onCallBinding.txvExplain.text = string
                    }
                    fun goNextOnClickAnyWhere(){
                        onCallBinding.root.setOnClickListener {
                            doSome(order+1)
                        }
                    }
                    fun setFocusOn(view: View){
                        fun getWindowHeight(): Int {
                            val a = Rect()
                            binding.root.getWindowVisibleDisplayFrame(a)
                            val windowTop =  binding.root.top
                            val displayTop = a.top
                            return displayTop - windowTop
                        }
                        view.viewTreeObserver.addOnGlobalLayoutListener(object :ViewTreeObserver.OnGlobalLayoutListener{
                            override fun onGlobalLayout() {
                                onCallBinding.apply {

//                                    imvFocusRectangle.layoutParams.width = view.width
//                                    imvFocusRectangle.layoutParams.height = view.height
                                    val a = IntArray(2)
                                    view.getLocationInWindow(a)

                                    imvFocusRectangle.holePosition =HolePosition(a[0].toFloat()+view.width/2,a[1].toFloat() +view.height/2 -getWindowHeight() ,100f)
//                                    imvFocusRectangle.y = a[1].toFloat() - getWindowHeight()
//                                    imvFocusRectangle.scaleX = 1.3f
//                                    imvFocusRectangle.scaleY = 1.3f
//                                    imvFocusRectangle.requestLayout()
                                }
                                view.viewTreeObserver.removeOnGlobalLayoutListener(this)
                            }
                        })

                    }
                    when(order){
                        1 -> {
                            setText("やあ、僕はとさかくん")
                            binding.frameLayCallOnInstall.addView(onCallBinding.root)
                            goNextOnClickAnyWhere()
                        }
                        2 -> {
                            setText("これから、KiNaの使い方を説明するね")
                            goNextOnClickAnyWhere()
                        }
                        3 -> {
                            setText("KiNaでは、フォルダと単語帳が作れるよ\n" +
                                    "ボタンをタッチして、単語帳を作ってみよう")
                            setFocusOn(binding.bnvBinding.bnvImvAdd)
                            onCallBinding.imvFocusRectangle.setOnClickListener {
                                createFileViewModel.setBottomMenuVisible(true)
                            }
                        }
                        else -> {
                            editor.putBoolean("firstTimeGuide", true)
                            editor.apply()
                        }

                    }

                }
                doSome(1)

            }
        }
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
        fun changeViewVisibility(view:View,visibility: Boolean){
            view.visibility = if(visibility) VISIBLE else GONE
        }
        fun createAllViewModels(){
            ViewModelProvider(this,factory)[SearchViewModel::class.java]
            ViewModelProvider(this,factory)[StringCardViewModel::class.java]
            ViewModelProvider(this,factory)[ChooseFileMoveToViewModel::class.java]
            ViewModelProvider(this,factory)[AnkiFragBaseViewModel::class.java]
            ViewModelProvider(this,factory)[AnkiBoxFragViewModel::class.java]
            ViewModelProvider(this,factory)[AnkiFlipFragViewModel::class.java]
            ViewModelProvider(this)[AnkiSettingPopUpViewModel::class.java]
            ViewModelProvider(this,factory)[AnkiFlipTypeAndCheckViewModel::class.java]
            ViewModelProvider(this,factory)[DeletePopUpViewModel::class.java]
        }
        setMainActivityLateInitVars()
        createAllViewModels()
        setUpMainActivityLayout()
        addMainActivityClickListeners()
        setContentView(binding.root)
        doOnInstall()

        val childFragmentStatusObserver      = Observer<BaseViewModel.MainActivityChildFragmentStatus>{
            changeTabView(it.before,it.now)
        }
        val bnvVisibilityObserver            = Observer<Boolean>{
            changeViewVisibility(binding.frameBnv,it)
        }
        val popUpEditFileVisibilityObserver  = Observer<Boolean>{
            Animation().animatePopUpAddFile(binding.frameLayEditFile,it)
            changeViewVisibility(binding.fragConViewCover,it||createFileViewModel.returnBottomMenuVisible())
        }
        val popUpEditFileUIDataObserver        = Observer<CreateFileViewModel.PopUpUI>{
            LibraryOb().observeEditFilePopUp(binding.editFileBinding,it,this@MainActivity)
        }
        val editFileColPalletObserver = Observer<ColorPalletStatus> {
            val context = this@MainActivity
            val colBinding = binding.editFileBinding.colPaletBinding
            ColorPalletViewSetUp().apply {
                changeColPalletCol(context,it.colNow,true,colBinding)
                changeColPalletCol(context,it.before,false,colBinding)
            }
        }
        val bottomMenuVisibilityObserver       = Observer<Boolean>{
            Animation().animateFrameBottomMenu(binding.frameBottomMenu,it)
            changeViewVisibility(binding.fragConViewCover,it||createFileViewModel.returnEditFilePopUpVisible())
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
            createFileViewModel.setParentTokenFileParent(it)
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
            colorPalletStatus   .observe(this@MainActivity,editFileColPalletObserver)
        }
//        ー－－－LibraryViewModelの読み取りー－－－
        libraryViewModel. onCreate()
        libraryViewModel.parentFile.observe(this@MainActivity,libraryParentFileObserver)
        libraryViewModel.parentRVItems.observe(this@MainActivity,libraryParentRVItemsObserver)
        createCardViewModel.setMainActivityNavCon(mainNavCon)
 }


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return super.onTouchEvent(event)

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
                    imvnewCard                        -> {
                        createCardViewModel.onClickAddNewCardBottomBar()
                    }
                    imvnewTangocho                    -> createFileViewModel.onClickCreateFile(FileStatus.FLASHCARD_COVER)
                    imvnewfolder                      -> createFileViewModel.onClickCreateFile(FileStatus.FOLDER)
                }
            }
            }
        }
    }
}