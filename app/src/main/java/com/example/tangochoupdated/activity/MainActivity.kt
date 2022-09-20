package com.example.tangochoupdated.activity

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.util.AttributeSet
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.view.marginRight
import androidx.core.view.setPadding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
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
import com.example.tangochoupdated.ui.viewmodel.*
import com.example.tangochoupdated.ui.viewmodel.customClasses.ColorPalletStatus
import com.example.tangochoupdated.ui.viewmodel.customClasses.MyOrientation
import java.lang.Math.abs
import kotlin.concurrent.thread
import kotlin.math.roundToInt


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

        fun changeViewVisibility(view:View,visibility: Boolean){
            view.visibility = if(visibility) VISIBLE else GONE
        }
        fun doOnInstall(){
            val sharedPref = this.getSharedPreferences(
                "firstTimeGuide", Context.MODE_PRIVATE) ?: return
            val editor = sharedPref.edit()
            if (!sharedPref.getBoolean("firstTimeGuide", false)) {
                val onCallBinding = CallOnInstallBinding.inflate(layoutInflater)


                fun doSome(order:Int){
                    var globalView :View? = null
                    var globalListener:ViewTreeObserver.OnGlobalLayoutListener? = null
                    fun removeGlobalListener(){
                        globalView?.viewTreeObserver?.removeOnGlobalLayoutListener(globalListener)
                    }
                    fun makeTxvGone(){
                        onCallBinding.apply {
                            arrayOf(txvExplainBottom,
                                txvExplainLeft,
                                txvExplainRight,
                                txvExplainTop).onEach {
                                if(it.visibility!= GONE)
                                    it.visibility = GONE
                            }
                        }
                    }
                    fun setText(string: String,orientation: MyOrientation):ValueAnimator{

                        makeTxvGone()
                        val textView = when(orientation){
                            MyOrientation.BOTTOM-> onCallBinding.txvExplainBottom
                            MyOrientation.LEFT -> onCallBinding.txvExplainLeft
                            MyOrientation.RIGHT -> onCallBinding.txvExplainRight
                            MyOrientation.TOP -> onCallBinding.txvExplainTop
                            else -> return ValueAnimator()
                        }
                        textView.visibility = VISIBLE
                        textView.text = string
                        val anim2 = ValueAnimator.ofFloat(1.1f,1f)
                        anim2.addUpdateListener {
                            val progressPer = it.animatedValue as Float
                            textView.scaleX = progressPer
                            textView.scaleY = progressPer
                        }
                        val anim = ValueAnimator.ofFloat(0.5f,1.1f)
                        anim.duration = 500
                        anim.addUpdateListener {
                            val progressPer = it.animatedValue as Float

                            textView.scaleX = progressPer
                            textView.scaleY = progressPer
                            textView.alpha = progressPer
                        }
                        anim.doOnEnd { anim2.start() }
                        anim.duration = 250

                        return anim
                    }
                    fun goNextOnClickAnyWhere(){
                        onCallBinding.root.setOnClickListener {
                            doSome(order+1)
                        }
                    }
                    fun goNextOnClickOnFocus(){
                        onCallBinding.root.setOnClickListener(null)
                        onCallBinding.viewFocusedArea.setOnClickListener {
                            removeGlobalListener()
                            doSome(order+1)
                        }
                    }
                    fun getWindowDisplayDiff(): Int {
                        val a = Rect()
                        binding.root.getWindowVisibleDisplayFrame(a)
                        val windowTop =  binding.root.top
                        val displayTop = a.top
                        return displayTop - windowTop
                    }
                    fun changeExplanationVisibility(visible:Boolean,unit: Unit):ValueAnimator{
                        val start = if(visible)0f else 1f
                        val end = if(visible)1f else 0f
                        val animation1 = ValueAnimator.ofFloat(start,end)
                        animation1.addUpdateListener {

                            onCallBinding.imvCharacter.alpha = it.animatedValue as Float
                            binding.imvFocusArrow.alpha = it.animatedValue as Float

                        }
                        animation1.duration = 1000
                        animation1.doOnEnd {
                            changeViewVisibility(onCallBinding.imvCharacter,visible)
                            changeViewVisibility(binding.imvFocusArrow,visible)
                            unit
                        }
                        animation1.doOnStart { onCallBinding.imvCharacter.visibility = VISIBLE }
                        return animation1
                    }

                    fun setPosition(mainView:View,subView: View,position:MyOrientation,margin:Int){
                        val a = IntArray(2)
                        mainView.getLocationInWindow(a)
                        val mainViewCenterPosX = a[0].toFloat()+mainView.width/2
                        val mainViewCenterPosY = a[1].toFloat() +mainView.height/2 -getWindowDisplayDiff()
                        val mainViewLeftTopPosX = a[0].toFloat()
                        val mainViewLeftTopPosY = a[1].toFloat()
                        fun setXAndY(x:Float,y:Float){
                            subView.x = x
                            subView.y = y
                        }
                        when(position){
                            MyOrientation.BOTTOM->
                                setXAndY(
                                    mainViewCenterPosX-subView.width/2,
                                    mainViewLeftTopPosY + mainView.height - getWindowDisplayDiff() +margin,
                                )
                            MyOrientation.LEFT ->
                                setXAndY(
                                    mainViewLeftTopPosX-subView.width -margin,
                                    mainViewCenterPosY - subView.height/2   ,
                                    )
                            MyOrientation.RIGHT ->
                                setXAndY(
                                    mainViewLeftTopPosX + mainView.width + margin,
                                    mainViewCenterPosY - subView.height/2   ,
                                )
                            MyOrientation.TOP ->
                                setXAndY(
                                    mainViewCenterPosX-subView.width/2,
                                    mainViewLeftTopPosY - subView.height -getWindowDisplayDiff() -margin,
                                    )
                            MyOrientation.MIDDLE ->
                                setXAndY(mainViewCenterPosX-subView.width/2,
                                    mainViewCenterPosY-subView.height/2)
                        }
                    }

                    fun setFocusOn(view: View,arrowPosition:MyOrientation){
                        val arrow = binding.imvFocusArrow
                        arrow.visibility = VISIBLE

                        fun setArrowDirection(direction:MyOrientation){
                            arrow.rotation =
                                when(direction){
                                    MyOrientation.BOTTOM-> -450f
                                    MyOrientation.LEFT -> 0f
                                    MyOrientation.RIGHT -> 900f
                                    MyOrientation.TOP -> 450f
                                    else -> return
                                }
                        }
                        fun setArrow(arrowPosition:MyOrientation){
                            when(arrowPosition){
                                MyOrientation.BOTTOM-> setArrowDirection(MyOrientation.TOP)
                                MyOrientation.LEFT -> setArrowDirection(MyOrientation.RIGHT)
                                MyOrientation.RIGHT -> setArrowDirection(MyOrientation.LEFT)
                                MyOrientation.TOP -> setArrowDirection(MyOrientation.BOTTOM)
                                else -> return
                            }
                        }
                        fun setHole(){
                            val a = IntArray(2)
                            view.getLocationInWindow(a)
                            val viewCenterPosX = a[0].toFloat()+view.width/2
                            val viewCenterPosY = a[1].toFloat() +view.height/2 -getWindowDisplayDiff()
                            onCallBinding.viewWithHole.holePosition =HolePosition(viewCenterPosX,viewCenterPosY ,100f)
                        }

                        view.viewTreeObserver.addOnGlobalLayoutListener(object :ViewTreeObserver.OnGlobalLayoutListener{

                            override fun onGlobalLayout() {
                                globalView = view
                                globalListener = this
                                setHole()
                                onCallBinding.viewFocusedArea.apply {
                                    layoutParams.width = view.width
                                    layoutParams.height = view.height
                                    requestLayout()
                                }
                                setPosition(view,onCallBinding.viewFocusedArea,MyOrientation.MIDDLE,0)
                                setPosition(view,arrow,arrowPosition,20)

                                setArrow(arrowPosition)

                            }
                        })

                    }
                    when(order){
                        1 -> {
                            binding.frameLayCallOnInstall.addView(onCallBinding.root)
                            setText("やあ、僕はとさかくん",MyOrientation.TOP)
                            goNextOnClickAnyWhere()
                        }
                        2 -> {
                            setText("これから、KiNaの使い方を説明するね",MyOrientation.TOP).start()
                            goNextOnClickAnyWhere()
                        }
                        3 -> {
                            setText("KiNaでは、フォルダと単語帳が作れるよ\n" +
                                    "ボタンをタッチして、単語帳を作ってみよう",MyOrientation.TOP).start()
                            setFocusOn(binding.bnvBinding.bnvImvAdd,MyOrientation.TOP)
                            goNextOnClickOnFocus()
                        }
                        4 -> {
                            setFocusOn(binding.bindingAddMenu.imvnewCard,MyOrientation.RIGHT)
                            createFileViewModel.setBottomMenuVisible(true)
                            goNextOnClickOnFocus()
                        }
                        5 -> {
                            changeViewVisibility(onCallBinding.imvCharacter,false)
                            makeTxvGone()
                            createFileViewModel.onClickCreateFile(FileStatus.FLASHCARD_COVER)
                            setFocusOn(binding.editFileBinding.btnFinish,MyOrientation.BOTTOM)
                            binding.editFileBinding.root.setOnClickListener(null)
                            binding.editFileBinding.btnFinish.setOnClickListener{
                                val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                                imm.hideSoftInputFromWindow(binding.editFileBinding.edtCreatefile.windowToken, 0 )
                                doSome(6)
                            }
                        }
                        6 ->{
                            createFileViewModel.onClickFinish(binding.editFileBinding.edtCreatefile.text.toString())
                            onCallBinding.viewWithHole.visibility = GONE
                            val a = ConstraintSet()
                            a.clone(onCallBinding.root)
                            a.setVerticalBias(onCallBinding.imvCharacter.id,0.8f)
                            a.setHorizontalBias(onCallBinding.imvCharacter.id,0f)
                            a.applyTo(onCallBinding.root)
                            changeViewVisibility(onCallBinding.imvCharacter,true)
                            setText("おめでとう！単語帳が追加されたよ",MyOrientation.RIGHT).start()
                            goNextOnClickAnyWhere()
                        }
                        7 ->{
                            setText("中身を見てみよう！",MyOrientation.RIGHT).start()
                            goNextOnClickAnyWhere()
                        }
                        8 ->{
                            makeTxvGone()
                            libraryViewModel.openNextFile(createFileViewModel.returnLastInsertedFile()!!)
                            goNextOnClickAnyWhere()
                        }
                        9->{
                            setText("まだカードがないね\n早速作ってみよう",MyOrientation.RIGHT).start()
                            goNextOnClickAnyWhere()

                        }
                        10 ->{
                            makeTxvGone()
                            changeViewVisibility(onCallBinding.imvCharacter,false)
                            changeViewVisibility(onCallBinding.viewWithHole,true)
                            setFocusOn(binding.bnvBinding.bnvImvAdd,MyOrientation.TOP)
                            goNextOnClickOnFocus()
                        }
                        11 ->{
                            setFocusOn(binding.bindingAddMenu.imvnewCard,MyOrientation.TOP)
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
        val lastInsertedFileObserver      = Observer<File>{
            createFileViewModel.setLastInsertedFile(it)
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
            createFileViewModel.lastInsertedFile.observe(this,lastInsertedFileObserver)
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
            lastInsertedFile.observe(this@MainActivity,lastInsertedFileObserver)
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