package com.example.tangochoupdated.activity

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.content.ContextCompat
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
import com.example.tangochoupdated.ui.customViews.CirclePosition
import com.example.tangochoupdated.ui.customViews.HoleShape
import com.example.tangochoupdated.ui.customViews.RecPosition
import com.example.tangochoupdated.ui.listener.popUp.EditFilePopUpCL
import com.example.tangochoupdated.ui.observer.LibraryOb
import com.example.tangochoupdated.ui.view_set_up.ColorPalletViewSetUp
import com.example.tangochoupdated.ui.viewmodel.*
import com.example.tangochoupdated.ui.viewmodel.customClasses.ColorPalletStatus
import com.example.tangochoupdated.ui.viewmodel.customClasses.MyOrientation


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
        fun installGuide(order:Int, onInstallBinding: CallOnInstallBinding ){

            val arrow = binding.imvFocusArrow
            val touchArea = onInstallBinding.viewTouchArea
            val holeView = onInstallBinding.viewWithHole
            val globalLayoutSet = mutableMapOf<View,ViewTreeObserver.OnGlobalLayoutListener>()

            fun getExplainTxv(orientation: MyOrientation):TextView{
                val textView = when(orientation){
                    MyOrientation.BOTTOM-> onInstallBinding.txvExplainBottom
                    MyOrientation.LEFT -> onInstallBinding.txvExplainLeft
                    MyOrientation.RIGHT -> onInstallBinding.txvExplainRight
                    MyOrientation.TOP -> onInstallBinding.txvExplainTop
                    else -> onInstallBinding.txvExplainTop
                }
                return textView
            }
            fun getCharacterImv(drawableId:Int?):ImageView{
                val imv = onInstallBinding.imvCharacter
                if(drawableId!=null){
                    val draw = ContextCompat.getDrawable(this, drawableId)
                    imv.setImageDrawable(draw)
                }
                return imv
            }
            fun setScale(v: View, x:Float,y:Float){
                v.scaleX = x
                v.scaleY = y
            }
            fun setAlpha(v: View,alpha:Float){
                v.alpha = alpha
            }
            fun removeGlobalListener(){
                globalLayoutSet.onEach {
                    it.key.viewTreeObserver.removeOnGlobalLayoutListener(it.value)
                }
            }
            fun makeTxvGone(){
            onInstallBinding.apply {
            arrayOf(txvExplainBottom,
            txvExplainLeft,
            txvExplainRight,
            txvExplainTop).onEach {
            if(it.visibility!= GONE)
            it.visibility = GONE
            }
            }
            }
            fun makeArrowGone(){
                changeViewVisibility(arrow,false)
            }
            fun makeTouchAreaGone(){
                changeViewVisibility(touchArea,false)
            }
            fun appearAlphaAnimation(views :Array<View>,visible:Boolean):ValueAnimator{
            val appear = ValueAnimator.ofFloat(0f,1f)
            val disappear = ValueAnimator.ofFloat(1f,0f)
            arrayOf(appear,disappear).onEach {
            it.addUpdateListener { animator ->
            views.onEach { setAlpha(it, animator.animatedValue as Float) }
            }
            it.duration = 300
            }
            appear.doOnStart { views.onEach { changeViewVisibility(it,true) } }
            disappear.doOnEnd { views.onEach { changeViewVisibility(it,false) } }
            return if(visible) appear else disappear
            }
            fun explainTextAnimation(string: String, orientation: MyOrientation):AnimatorSet{
                makeTxvGone()
                val textView = getExplainTxv(orientation)
                textView.visibility = VISIBLE
                textView.text = string
                val finalDuration:Long = 100
                val anim2 = ValueAnimator.ofFloat(1.1f,1f)
                anim2.addUpdateListener {
                val progressPer = it.animatedValue as Float
                setScale(textView,progressPer,progressPer)
                }
                val anim = ValueAnimator.ofFloat(0.7f,1.1f)
                anim.addUpdateListener {
                val progressPer = it.animatedValue as Float
                setScale(textView,progressPer,progressPer)
                }
                val animAlpha = ValueAnimator.ofFloat(0f,1f)
                anim.addUpdateListener {
                setAlpha(textView,it.animatedValue as Float)
                }
                val scaleAnim = AnimatorSet().apply {
                playSequentially(anim,anim2)
                anim.duration = finalDuration*0.3.toLong()
                anim.duration = finalDuration*0.3.toLong()
                }
                val finalAnim = AnimatorSet().apply {
                playTogether(animAlpha,scaleAnim)
                scaleAnim.duration = finalDuration
                }

                return finalAnim
            }
            fun goNextOnClickAnyWhere(){
                onInstallBinding.root.setOnClickListener {
                    installGuide(order+1,onInstallBinding)
                }
            }
            fun goNextOnClickOnFocus(){
                onInstallBinding.root.setOnClickListener(null)
                touchArea.setOnClickListener {
                    removeGlobalListener()
                    installGuide(order+1,onInstallBinding)
                }
            }
            fun getWindowDisplayDiff(): Int {
    val a = Rect()
    binding.root.getWindowVisibleDisplayFrame(a)
    val windowTop =  binding.root.top
    val displayTop = a.top
    return displayTop - windowTop
    }
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
            fun setPosition(mainView:View,subView: View,position:MyOrientation,margin:Int,matchSize:Boolean){
                fun setXAndY(x:Float,y:Float){
                    subView.x = x
                    subView.y = y
                }
                mainView.viewTreeObserver.addOnGlobalLayoutListener(object :ViewTreeObserver.OnGlobalLayoutListener{
                    override fun onGlobalLayout() {
                        globalLayoutSet[mainView] = this
                        val a = IntArray(2)
                        mainView.getLocationInWindow(a)
                        val mainViewCenterPosX = a[0].toFloat()+mainView.width/2
                        val mainViewCenterPosY = a[1].toFloat() +mainView.height/2 -getWindowDisplayDiff()
                        val mainViewLeftTopPosX = a[0].toFloat()
                        val mainViewLeftTopPosY = a[1].toFloat()
                        if(matchSize){
                            subView.layoutParams.height = mainView.height
                            subView.layoutParams.width = mainView.width
                            subView.requestLayout()
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
                })

            }
            fun setArrow(arrowPosition:MyOrientation,view:View){
                changeViewVisibility(arrow,true)
                when(arrowPosition){
                    MyOrientation.BOTTOM-> setArrowDirection(MyOrientation.TOP)
                    MyOrientation.LEFT -> setArrowDirection(MyOrientation.RIGHT)
                    MyOrientation.RIGHT -> setArrowDirection(MyOrientation.LEFT)
                    MyOrientation.TOP -> setArrowDirection(MyOrientation.BOTTOM)
                    else ->  {
                        makeTouchAreaGone()
                        return
                    }
                }
                setPosition(view,arrow,arrowPosition,20,false)
            }
            fun setTouchArea(view: View){
                changeViewVisibility(touchArea,true)
                setPosition(view,touchArea,MyOrientation.MIDDLE,0,true)
            }
            fun setHole(view: View, shape:HoleShape){
                fun setHole(){
                val a = IntArray(2)
                view.getLocationInWindow(a)
                val viewCenterPosX = a[0].toFloat()+view.width/2
                val viewCenterPosY = a[1].toFloat() +view.height/2 -getWindowDisplayDiff()
                onInstallBinding.viewWithHole.circleHolePosition =CirclePosition(viewCenterPosX,viewCenterPosY ,100f)
                }
                fun setRec(margin:Float){
                val a = IntArray(2)
                view.getLocationInWindow(a)
                val left = a[0].toFloat()
                val top = a[1].toFloat() - view.height
                onInstallBinding.viewWithHole.recHolePosition =
                RecPosition(left-margin,top-margin,left+view.width+margin,top+view.height+margin)
                }
                holeView.circleHolePosition = CirclePosition(0f,0f,0f)
                holeView.recHolePosition = RecPosition(0f,0f,0f,0f)
                changeViewVisibility(holeView,true)
                view.viewTreeObserver.addOnGlobalLayoutListener(object :ViewTreeObserver.OnGlobalLayoutListener{
                    override fun onGlobalLayout() {
                        globalLayoutSet[view] = this
                        when(shape){
                        HoleShape.CIRCLE -> setHole()
                        HoleShape.RECTANGLE -> setRec(50f)
                        }
                    }
                })
            }

            fun greeting1(){
                binding.frameLayCallOnInstall.addView(onInstallBinding.root)
                explainTextAnimation("やあ、僕はとさかくん",MyOrientation.TOP)
                goNextOnClickAnyWhere()
            }
            fun greeting2(){
                explainTextAnimation("これから、KiNaの使い方を説明するね",MyOrientation.TOP).start()
                goNextOnClickAnyWhere()
            }
            fun createFlashCard1(){
                explainTextAnimation("KiNaでは、フォルダと単語帳が作れるよ\n" +
                        "ボタンをタッチして、単語帳を作ってみよう",MyOrientation.TOP).start()
                val focus = binding.bnvBinding.bnvImvAdd
                setHole(focus,HoleShape.CIRCLE)
                setTouchArea(focus)
                goNextOnClickOnFocus()
            }
            fun createFlashCard2(){
                val focus = binding.bindingAddMenu.imvnewTangocho
                setHole(focus,HoleShape.CIRCLE)
                setTouchArea(focus)
                binding
                createFileViewModel.setBottomMenuVisible(true)
                goNextOnClickOnFocus()
            }
            fun createFlashCard3(){
                changeViewVisibility(onInstallBinding.imvCharacter,false)
                makeTxvGone()
                createFileViewModel.onClickCreateFile(FileStatus.FLASHCARD_COVER)
                setHole(binding.frameLayEditFile,HoleShape.RECTANGLE)
                setTouchArea(binding.editFileBinding.edtCreatefile)
                goNextOnClickOnFocus()
            }
            fun createFlashCard4(){
                binding.frameLayEditFile.requestFocus()
                setArrow(MyOrientation.BOTTOM,binding.editFileBinding.btnFinish)
                setTouchArea(binding.editFileBinding.btnFinish)
                goNextOnClickOnFocus()
            }
            fun createFlashCard5(){
                val title = binding.editFileBinding.edtCreatefile.text.toString()
                if(title!=""){
                    createFileViewModel.onClickFinish(title)
                    val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(binding.editFileBinding.edtCreatefile.windowToken, 0 )
                } else {
                    makeToast(this,"タイトルが必要です")
                    return
                }
                changeViewVisibility(holeView,false)
                val a = ConstraintSet()
                a.clone(onInstallBinding.root)
                a.setVerticalBias(onInstallBinding.imvCharacter.id,0.8f)
                a.setHorizontalBias(onInstallBinding.imvCharacter.id,0f)
                a.applyTo(onInstallBinding.root)
                changeViewVisibility(onInstallBinding.imvCharacter,true)
                explainTextAnimation("おめでとう！単語帳が追加されたよ",MyOrientation.RIGHT).start()
                goNextOnClickAnyWhere()
            }
            fun checkInsideNewFlashCard1(){
                explainTextAnimation("中身を見てみよう！",MyOrientation.RIGHT).start()
                goNextOnClickAnyWhere()
            }
            fun checkInsideNewFlashCard2(){
                makeTxvGone()
                libraryViewModel.openNextFile(createFileViewModel.returnLastInsertedFile()!!)
                goNextOnClickAnyWhere()
            }
            fun checkInsideNewFlashCard3(){
                explainTextAnimation("まだカードがないね\n早速作ってみよう",MyOrientation.RIGHT).start()
                goNextOnClickAnyWhere()
            }
            fun makeNewCard1(){
                makeTxvGone()
                changeViewVisibility(onInstallBinding.imvCharacter,false)
                changeViewVisibility(onInstallBinding.viewWithHole,true)
                setHole(binding.bnvBinding.bnvImvAdd,HoleShape.CIRCLE)
                goNextOnClickOnFocus()
            }
            fun makeNewCard2(){
                setHole(binding.bindingAddMenu.imvnewCard,HoleShape.CIRCLE)
                createFileViewModel.setBottomMenuVisible(true)
                goNextOnClickOnFocus()
            }
            fun makeNewCard3(){
                createCardViewModel.onClickAddNewCardBottomBar()
                appearAlphaAnimation(arrayOf(touchArea,arrow),false)
                goNextOnClickAnyWhere()
            }
            fun explainCreateCardFrag1(){
                appearAlphaAnimation(arrayOf(getCharacterImv(null)),true)
                setHole(this.findViewById(R.id.edt_front_content),HoleShape.RECTANGLE)
                explainTextAnimation("上半分は、カードの表",MyOrientation.RIGHT).start()
                goNextOnClickAnyWhere()
            }
            fun explainCreateCardFrag2(){
                setHole(this.findViewById(R.id.edt_back_content),HoleShape.RECTANGLE)
                explainTextAnimation("下半分は、カードの裏になっているよ",MyOrientation.RIGHT).start()
                goNextOnClickAnyWhere()
            }
            fun explainCreateCardFrag3(){
                setHole(this.findViewById(R.id.edt_back_title),HoleShape.RECTANGLE)
                explainTextAnimation("カードの裏表にタイトルを付けることもできるんだ！",MyOrientation.LEFT).start()
                goNextOnClickAnyWhere()
            }
            fun explainCreateCardFrag4(){
                setHole(this.findViewById(R.id.edt_front_title),HoleShape.RECTANGLE)
                explainTextAnimation("好みのようにカスタマイズしてね",MyOrientation.RIGHT).start()
                goNextOnClickAnyWhere()
            }
            fun explainCreateCardNavigation1(){
                explainTextAnimation("カードをめくるには、下のナビゲーションボタンを使うよ",MyOrientation.RIGHT).start()
                setHole(this.findViewById(R.id.lay_navigate_buttons),HoleShape.RECTANGLE)
                goNextOnClickAnyWhere()
            }
            fun explainCreateCardNavigation2(){
                explainTextAnimation("新しいカードを前に挿入するのはここ",MyOrientation.RIGHT).start()
                setArrow(MyOrientation.TOP,this.findViewById(R.id.btn_insert_next))
                goNextOnClickAnyWhere()
            }
            fun explainCreateCardNavigation3(){
                explainTextAnimation("後ろに挿入するのはここ！",MyOrientation.RIGHT).start()
                setArrow(MyOrientation.TOP,this.findViewById(R.id.btn_insert_previous))
                goNextOnClickAnyWhere()
            }
            fun explainCreateCardNavigation4(){
                explainTextAnimation("矢印ボタンでカードを前後にめくってね！",MyOrientation.RIGHT).start()
                setArrow(MyOrientation.TOP,this.findViewById(R.id.btn_next))
                goNextOnClickAnyWhere()
            }
            fun explainCreateCardNavigation5(){
                binding.frameLayCallOnInstall.visibility = GONE
            }


            when(order){
                1   ->  greeting1()
                2   ->  greeting2()
                3   ->  createFlashCard1()
                4   ->  createFlashCard2()
                5   ->  createFlashCard3()
                6   ->  createFlashCard4()
                7   ->  createFlashCard5()
                8   ->  checkInsideNewFlashCard1()
                9   ->  checkInsideNewFlashCard2()
                10  ->  checkInsideNewFlashCard3()
                11  ->  makeNewCard1()
                12  ->  makeNewCard2()
                13  ->  makeNewCard3()
                14  ->  explainCreateCardFrag1()
                15  ->  explainCreateCardFrag2()
                16  ->  explainCreateCardFrag3()
                17  ->  explainCreateCardFrag4()
                18  ->  explainCreateCardNavigation1()
                19  ->  explainCreateCardNavigation2()
                20  ->  explainCreateCardNavigation3()
                21  ->  explainCreateCardNavigation4()
                22  ->  explainCreateCardNavigation5()
                else -> {val sharedPref = this.getSharedPreferences(
                    "firstTimeGuide", Context.MODE_PRIVATE) ?: return
                    val editor = sharedPref.edit()
                    editor.putBoolean("firstTimeGuide", true)
                    editor.apply()
                }
            }
        }
        fun checkIfInstall(){
            val sharedPref = this.getSharedPreferences(
                "firstTimeGuide", Context.MODE_PRIVATE) ?: return
            if (!sharedPref.getBoolean("firstTimeGuide", false)) {
                installGuide(1,CallOnInstallBinding.inflate(layoutInflater))

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
        checkIfInstall()

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