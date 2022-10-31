package com.korokoro.kina.actions

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup.LayoutParams
import android.view.ViewTreeObserver
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.view.children
import androidx.core.view.get
import androidx.core.view.size
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import com.korokoro.kina.R
import com.korokoro.kina.customClasses.*
import com.korokoro.kina.databinding.CallOnInstallBinding
import com.korokoro.kina.databinding.TouchAreaBinding
import com.korokoro.kina.db.dataclass.File
import com.korokoro.kina.db.enumclass.FileStatus
import com.korokoro.kina.ui.animation.Animation
import com.korokoro.kina.ui.customViews.*
import com.korokoro.kina.ui.fragment.lib_frag_con.LibraryChooseFileMoveToFragDirections
import com.korokoro.kina.ui.fragment.lib_frag_con.LibraryHomeFragDirections
import com.korokoro.kina.ui.listener.MyTouchListener
import com.korokoro.kina.ui.viewmodel.*
import kotlin.math.abs


class InstallGuide(val activity:AppCompatActivity,val onInstallBinding: CallOnInstallBinding){
    private val frameLayCallOnInstall       =activity.findViewById<FrameLayout>(R.id.frameLay_call_on_install)
    private val arrow = onInstallBinding.imvFocusArrow
    private val touchArea = onInstallBinding.viewTouchArea
    private val character = onInstallBinding.imvCharacter
    private val holeView = onInstallBinding.viewWithHole
    private val globalLayoutSet = mutableMapOf<View, ViewTreeObserver.OnGlobalLayoutListener>()
    private val textView = onInstallBinding.txvExplain
    private val heightDiff = getWindowDisplayHeightDiff(activity.resources)


    private fun setScale(v: View, x:Float, y:Float){
        v.scaleX = x
        v.scaleY = y
    }
    private fun setAlpha(v: View, alpha:Float){
        v.alpha = alpha
    }
    private fun setPositionByXY(mainView: View, subView: View, position: MyOrientation, margin:Int, matchSize:Boolean){
        val before = mainView.rotation

        mainView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener{
            override fun onGlobalLayout() {
                    fun setPositionXAndY(x:Float,y:Float){
                        setXAndY(subView,x,y)
                    }

                mainView.rotation = 0f
                globalLayoutSet[mainView] = this
                    val a = IntArray(2)
                    mainView.getLocationInWindow(a)
                    val mainViewCenterPosX = a[0].toFloat()+(mainView.width/2).toFloat()
                    val mainViewCenterPosY = a[1].toFloat() +mainView.height/2 -heightDiff
                    val mainViewLeftTopPosX = a[0].toFloat()
                    val mainViewLeftTopPosY = a[1].toFloat()
                    if(matchSize){
                        subView.layoutParams.height = mainView.height
                        subView.layoutParams.width = mainView.width
                        subView.requestLayout()
                    }
                    when(position){
                        MyOrientation.BOTTOM->
                            setPositionXAndY(
                                mainViewCenterPosX-subView.width/2,
                                mainViewLeftTopPosY + mainView.height - heightDiff +margin,
                            )
                        MyOrientation.LEFT ->
                            setPositionXAndY(
                                mainViewLeftTopPosX-subView.width -margin,
                                mainViewCenterPosY - subView.height/2   ,
                            )
                        MyOrientation.RIGHT ->
                            setPositionXAndY(
                                mainViewLeftTopPosX + mainView.width + margin,
                                mainViewCenterPosY - subView.height/2   ,
                            )
                        MyOrientation.TOP ->
                            setPositionXAndY(
                                mainViewCenterPosX-(subView.width/2).toFloat(),
                                mainViewLeftTopPosY - subView.height -heightDiff -margin,
                            )
                        MyOrientation.MIDDLE ->
                            setPositionXAndY(mainViewCenterPosX-subView.width/2,
                                mainViewCenterPosY-subView.height/2)
                    }
                mainView.rotation = before
                }
            })
    }
    var borderLeftObject:View? = null
    private fun calculatePositionInBorder(view: View,
                                          borderPosition: RecPosition,
                                          verticalOrientation: MyOrientation,
                                          horizontalOrientation: MyOrientation
    ):RecPosition{
        val subMiddleTop :Float
        val subMiddleBottom :Float
        val verticalCenter = borderPosition.top + (borderPosition.bottom-borderPosition.top)/2
        when(verticalOrientation){
            MyOrientation.TOP-> {
                subMiddleTop = borderPosition.top
                subMiddleBottom = borderPosition.top + view.height

            }
            MyOrientation.MIDDLE -> {
                subMiddleTop = verticalCenter-view.height/2
                subMiddleBottom = verticalCenter + view.height/2
            }
            MyOrientation.BOTTOM ->{
                subMiddleTop = borderPosition.bottom - view.height
                subMiddleBottom = borderPosition.bottom


            }
            else -> return RecPosition(0f,0f,0f,0f)
        }

//        val top = (if(subMiddleTop>borderPosition.top)subMiddleTop else borderPosition.top)
//        val bottom = if(subMiddleBottom<borderPosition.bottom)subMiddleBottom else borderPosition.bottom
        val top = subMiddleTop
        val bottom = subMiddleBottom


        val horizontalCenter = borderPosition.left + (borderPosition.right-borderPosition.left)/2
        val subMiddleLeft :Float
        val subMiddleRight :Float
        when(horizontalOrientation){
            MyOrientation.LEFT-> {
                subMiddleLeft = borderPosition.left
                subMiddleRight = borderPosition.left + view.width
            }
            MyOrientation.MIDDLE -> {
                subMiddleLeft = horizontalCenter-view.width/2
                subMiddleRight = horizontalCenter + view.width/2
            }
            MyOrientation.RIGHT ->{
                subMiddleLeft = borderPosition.right - view.width
                subMiddleRight = borderPosition.right
            }
            else -> return RecPosition(0f,0f,0f,0f)
        }
        val left = (if(subMiddleLeft>borderPosition.left) subMiddleLeft else borderPosition.left)
        val right = (if(subMiddleRight<borderPosition.right)subMiddleRight else borderPosition.right)
        return RecPosition(left,top, right, bottom)
    }
    fun getNavBarHeight():Int{
        val a = activity.resources.getIdentifier("navigation_bar_height", "dimen", "android")
        val b = activity.resources.getDimensionPixelSize(a)
        return  b
    }
    var borderRight:Float? = null
    var borderLeft:Float? = null
    var borderTop:Float? = null
    var borderBottom:Float? = null
    fun setMultipleBorderObject(view: View,position: MyOrientation):Float{
        return when(position){
            MyOrientation.RIGHT-> getRecPos(view).right
            MyOrientation.LEFT -> getRecPos(view).left
            MyOrientation.BOTTOM -> getRecPos(view).bottom
            MyOrientation.TOP   -> getRecPos(view).top
            else -> 0f
        }
    }
    fun getScreenWidth():Int{
        return activity.resources.displayMetrics.widthPixels
    }
    fun getScreenHeight():Int{
        return activity.resources.displayMetrics.heightPixels
    }
    fun getDisplayBorder(side: MyOrientation):Float{
        return when(side){
            MyOrientation.RIGHT->  activity.resources.displayMetrics.widthPixels/2f
            MyOrientation.LEFT -> -activity.resources.displayMetrics.widthPixels/2f
            MyOrientation.BOTTOM -> (activity.resources.displayMetrics.heightPixels)/2f+heightDiff
            MyOrientation.TOP   ->  -(activity.resources.displayMetrics.heightPixels)/2f+heightDiff
            else    -> 0f
        }
    }

    class MyOrientationSet(
        val verticalOrientation: MyOrientation,
        val horizontalOrientation: MyOrientation
    )

    fun getRecPos(view: View):RecPosition{
        val a = IntArray(2)
        view.getLocationInWindow(a)
        val viewX = a[0].toFloat()
        val viewY = a[1].toFloat()
        val left = viewX -getScreenWidth()/2
        val top = viewY -getScreenHeight()/2
        val right = viewX + view.width -getScreenWidth()/2
        val bottom = viewY + view.height- getScreenHeight()/2
        val pos = RecPosition(left = left,top=top,right, bottom)
        return pos
    }
    fun setPositionByMargin(viewPos:RecPosition,borderPos:RecPosition,margin: Int){
        val con = ConstraintSet()
        con.clone(onInstallBinding.root)
        val marginTop = (abs(-viewPos.top)   ).toInt()
        val marginBottom = (abs(borderPos.bottom-viewPos.bottom)    ).toInt()
        val marginStart = abs(borderLeft!!-viewPos.left).toInt()
        val marginEnd = abs(borderRight!!-viewPos.right).toInt()
//
        con.setMargin(character.id,ConstraintSet.TOP, marginTop)
        con.setMargin(character.id,ConstraintSet.BOTTOM, marginBottom)
        con.setMargin(character.id,ConstraintSet.START, marginStart)
        con.setMargin(character.id,ConstraintSet.END, marginEnd)

        con.applyTo(onInstallBinding.root)
    }
    class ViewAndSide(
        var view:View,
        var side: MyOrientation
    )
    class BorderSet(
        var leftSideSet:ViewAndSide?=null,
        var topSideSet:ViewAndSide?=null,
        var rightSideSet:ViewAndSide?=null,
        var bottomSideSet:ViewAndSide?=null
    )
    val positionDataMap = mutableMapOf<View,BorderSet>()


    private fun setPositionNextTo(mainView: View, subView: View, matchSize:Boolean,myOrientationSet:MyOrientationSet){
        mainView.viewTreeObserver.addOnGlobalLayoutListener {
            val leftSideSet = positionDataMap[subView]?.leftSideSet
            val borderLeft = if(leftSideSet!=null) setMultipleBorderObject(leftSideSet.view,leftSideSet.side) else getDisplayBorder(
                MyOrientation.LEFT)
            val topSideSet = positionDataMap[subView]?.topSideSet
            val borderTop = if(topSideSet!=null) setMultipleBorderObject(topSideSet.view,topSideSet.side) else getDisplayBorder(
                MyOrientation.TOP)
            val rightSideSet = positionDataMap[subView]?.rightSideSet
            val borderRight = if(rightSideSet!=null) setMultipleBorderObject(rightSideSet.view,rightSideSet.side) else getDisplayBorder(
                MyOrientation.RIGHT)
            val bottomSideSet = positionDataMap[subView]?.bottomSideSet
            val borderBottom = if(bottomSideSet!=null) setMultipleBorderObject(bottomSideSet.view,bottomSideSet.side) else getDisplayBorder(
                MyOrientation.BOTTOM)

            val mainViewRecPos = getRecPos(mainView)
            val borderRecPos =  if(matchSize) mainViewRecPos else
                RecPosition(borderLeft,borderTop,borderRight,borderBottom)
            val result =calculatePositionInBorder(
                subView,
                borderRecPos,
                myOrientationSet.verticalOrientation,
                myOrientationSet.horizontalOrientation
            )
            val topDiff = abs(borderTop-getDisplayBorder(MyOrientation.TOP))
            val rightDiff = abs(borderRight-getDisplayBorder(MyOrientation.RIGHT))
            val leftDiff = abs(borderLeft-getDisplayBorder(MyOrientation.LEFT))
            val bottomDiff = abs(borderBottom-getDisplayBorder(MyOrientation.BOTTOM))
            fun setPosition(recPosition: RecPosition){
                val con = ConstraintSet()
                con.clone(onInstallBinding.root)
                val marginTop = (abs(borderTop!!-recPosition.top)  -bottomDiff ).toInt()
                val marginBottom = (abs(borderBottom!!-recPosition.bottom)  - topDiff  ).toInt()
                val marginStart = (abs(borderLeft!!-recPosition.left)-rightDiff).toInt()
                val marginEnd = (abs(borderRight!!-recPosition.right)-leftDiff).toInt()
//
                con.setMargin(character.id,ConstraintSet.TOP, marginTop)
                con.setMargin(character.id,ConstraintSet.BOTTOM, marginBottom)
                con.setMargin(character.id,ConstraintSet.START, marginStart)
                con.setMargin(character.id,ConstraintSet.END, marginEnd)

                con.applyTo(onInstallBinding.root)

            }
            setPosition(result)


        }

    }
    private fun removeGlobalListener(){
        globalLayoutSet.onEach {
            it.key.viewTreeObserver.removeOnGlobalLayoutListener(it.value)
        }
    }
    private fun makeTxvGone(){
        appearAlphaAnimation(textView,false).start()
    }
    private fun makeArrowGone(){
        changeViewVisibility(arrow,false)
    }
    private fun makeTouchAreaGone(){
        changeViewVisibility(touchArea,false)
    }
    private fun appearAlphaAnimation(view :View, visible:Boolean): ValueAnimator {

        val appear =if(view == holeView)  ValueAnimator.ofFloat(0f,0.7f) else ValueAnimator.ofFloat(0f,1f)
        val disappear = ValueAnimator.ofFloat(1f,0f)
        arrayOf(appear,disappear).onEach { eachAnimator->
            eachAnimator.addUpdateListener { thisAnimator ->
                 setAlpha(view, thisAnimator.animatedValue as Float)
            }
            eachAnimator.duration = 300
        }
        appear.doOnStart {
            view.alpha = 0f
            changeViewVisibility(view,true)  }
        disappear.doOnEnd {  changeViewVisibility(view,false)  }
        return if(visible) appear else disappear


    }
    private fun checkIfOutOfBound(checkingView:View, boundView:View,margin: Int){
        checkingView.viewTreeObserver.addOnGlobalLayoutListener (
            object:ViewTreeObserver.OnGlobalLayoutListener{
                override fun onGlobalLayout() {
                    val width = activity.resources.displayMetrics.widthPixels
                    val textviewWidth = checkingView.width
                    val right =checkingView.x+checkingView.width
                    if(right>width){
                        checkingView.layoutParams.width = textviewWidth- (right-width).toInt()-margin
                        checkingView.requestLayout()
                        return
                    }

                    val left= checkingView.x
                    if(left<0){
                        checkingView.layoutParams.width = textviewWidth-abs(left).toInt()
                        checkingView.requestLayout()
                    }
                    checkingView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            }
        )



    }
    private fun explainTextAnimation(string: String, orientation: MyOrientation, view: View): AnimatorSet {


        textView.text = string
        textView.layoutParams.width = LayoutParams.WRAP_CONTENT
        textView.requestLayout()
        setPositionByXY(view,textView,orientation,0,false)
        checkIfOutOfBound(textView,onInstallBinding.root,10)
        textView.visibility = if(string=="") View.GONE else View.VISIBLE

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
            anim2.duration = finalDuration*0.3.toLong()
            anim.duration = finalDuration*0.7.toLong()
        }
        val finalAnim = AnimatorSet().apply {
            playTogether(animAlpha,scaleAnim)
            scaleAnim.duration = finalDuration
        }

        return finalAnim
    }
    private fun setArrowDirection(direction: MyOrientation){
        arrow.rotation =
            when(direction){
                MyOrientation.BOTTOM-> -450f
                MyOrientation.LEFT -> 0f
                MyOrientation.RIGHT -> 900f
                MyOrientation.TOP -> 450f
                else -> return
            }

    }
    private fun setArrow(arrowPosition: MyOrientation, view: View){
        appearAlphaAnimation(arrow,true).start()
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
        setPositionByXY(view,arrow,arrowPosition,10,false)
    }
    private fun setTouchArea(view: View){
        changeViewVisibility(touchArea,true)
        setPositionByXY(view,touchArea, MyOrientation.MIDDLE,0,true)
    }
    private fun setHole(view: View, shape: HoleShape){
        removeGlobalListener()
        holeView.removeAllHoles = false
        val before = view.rotation
        fun setHole(){
            val a = IntArray(2)
            view.getLocationInWindow(a)
            val viewCenterPosX = a[0].toFloat()+view.width/2
            val viewCenterPosY = a[1].toFloat() +view.height/2 -heightDiff
            holeView.circleHolePosition =
                CirclePosition(viewCenterPosX,viewCenterPosY ,100f)
        }
        fun setRec(margin:Float){
            val a = IntArray(2)
            view.getLocationInWindow(a)
            val left = a[0].toFloat()
            val top = a[1].toFloat() -heightDiff
            holeView.recHolePosition =
                RecPosition(left-margin,top-margin,left+view.width+margin,top+view.height+margin)
        }
        changeViewVisibility(holeView,true)
        view.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener{
            override fun onGlobalLayout() {
                globalLayoutSet.put(view,this)
                view.rotation = 0f
                holeView.circleHolePosition = CirclePosition(0f,0f,0f)
                holeView.recHolePosition = RecPosition(0f,0f,0f,0f)
                when(shape){
                    HoleShape.CIRCLE -> setHole()
                    HoleShape.RECTANGLE -> setRec(0f)
                }
                view.rotation =before
            }
        })
    }
    private fun addTouchArea(view:View):View{
        onInstallBinding.root.setOnClickListener(null)
        val a = TouchAreaBinding.inflate(activity.layoutInflater)
        a.touchView.tag = 1
        onInstallBinding.root.addView(a.touchView)
        setPositionByXY(view,a.touchView, MyOrientation.MIDDLE,0,true)
        return a.touchView
    }
    private fun cloneView(view: View) {
        addTouchArea(view).setOnClickListener {
            view.callOnClick()
        }
    }
    private fun removeHole(){
        removeGlobalListener()
        holeView.removeAllHoles = true
    }
    fun createGuide(startOrder:Int,
                    createCardViewModel:CreateCardViewModel,
                    createFileViewModel:EditFileViewModel,
                    libraryViewModel:LibraryBaseViewModel,
                    mainViewModel: MainViewModel){
        mainViewModel.setGuideVisibility(true)
        fun guideInOrder(order:Int){
            val frameLayCallOnInstall       =activity.findViewById<FrameLayout>(R.id.frameLay_call_on_install)
            val bnvBtnAdd                   =activity.findViewById<ImageView>(R.id.bnv_imv_add)
            val createMenuImvFlashCard      =activity.findViewById<FrameLayout>(R.id.imvnewTangocho)
            val createMenuImvNewCard        =activity.findViewById<FrameLayout>(R.id.imvnewCard)
            val frameLayEditFile            =activity.findViewById<FrameLayout>(R.id.frameLay_edit_file)
            val edtCreatingFileTitle        =activity.findViewById<EditText>(R.id.edt_file_title)
            val imvColPalRed                =activity.findViewById<ImageView>(R.id.imv_col_red)
            val imvColPalBlue               =activity.findViewById<ImageView>(R.id.imv_col_blue)
            val imvColPaYellow              =activity.findViewById<ImageView>(R.id.imv_col_yellow)
            val imvColPalGray               =activity.findViewById<ImageView>(R.id.imv_col_gray)



            val btnFinish                   =activity.findViewById<Button>(R.id.btn_finish)
            val libraryRv                   =activity.findViewById<RecyclerView>(R.id.vocabCardRV)
            val imvTabLibrary               =activity.findViewById<ImageView>(R.id.bnv_imv_tab_library)
            val barrier                     =onInstallBinding.barrier1
            val edtCardFrontTitle           =activity.findViewById<EditText>(R.id.edt_front_title)
            val edtCardFrontContent         =activity.findViewById<EditText>(R.id.edt_front_content)
            val edtCardBackTitle            =activity.findViewById<EditText>(R.id.edt_back_title)
            val edtCardBackContent          =activity.findViewById<EditText>(R.id.edt_back_content)
            val linLayCreateCardNavigation  =activity.findViewById<ConstraintLayout>(R.id.lay_navigate_buttons)
            val createCardNavFlipNext       =activity.findViewById<NavigateBtnCreateCard>(R.id.btn_next)
            val createCardNavFlipPrevious   =activity.findViewById<NavigateBtnCreateCard>(R.id.btn_previous)
            val createCardInsertNext        =activity.findViewById<ImageView>(R.id.btn_insert_next)
            val createCardInsertPrevious    =activity.findViewById<ImageView>(R.id.btn_insert_previous)



            fun goNextOnClickAnyWhere(){
                onInstallBinding.root.setOnClickListener {
                    guideInOrder(order+1)
                }
            }
            fun goNextOnClickTouchArea(view: View) {
                onInstallBinding.root.setOnClickListener(null)
                addTouchArea(view).setOnClickListener {
                    guideInOrder(order + 1)

                }
            }
            fun addTouchArea(view:View):View{
                onInstallBinding.root.setOnClickListener(null)
                val a = TouchAreaBinding.inflate(activity.layoutInflater)
                a.touchView.tag = 1
                onInstallBinding.root.addView(a.touchView)
                setPositionByXY(view,a.touchView, MyOrientation.MIDDLE,0,true)
                return a.touchView
            }
            fun cloneView(view: View) {
                addTouchArea(view).setOnClickListener {
                    view.callOnClick()
                }
            }
            fun tameshi(a:Int){
                when(a){
                    1    -> setPositionNextTo(touchArea,character,false, MyOrientationSet( MyOrientation.TOP       ,
                        MyOrientation.LEFT))
                    2    -> setPositionNextTo(touchArea,character,false, MyOrientationSet( MyOrientation.TOP       ,
                        MyOrientation.MIDDLE))
                    3    -> setPositionNextTo(touchArea,character,false, MyOrientationSet( MyOrientation.TOP       ,
                        MyOrientation.RIGHT))
                    4    -> setPositionNextTo(touchArea,character,false, MyOrientationSet( MyOrientation.MIDDLE    ,
                        MyOrientation.LEFT))
                    5    -> setPositionNextTo(touchArea,character,false, MyOrientationSet( MyOrientation.MIDDLE    ,
                        MyOrientation.MIDDLE))
                    6    -> setPositionNextTo(touchArea,character,false, MyOrientationSet( MyOrientation.MIDDLE    ,
                        MyOrientation.RIGHT))
                    7    -> setPositionNextTo(touchArea,character,false, MyOrientationSet( MyOrientation.BOTTOM    ,
                        MyOrientation.LEFT))
                    8    -> setPositionNextTo(touchArea,character,false, MyOrientationSet( MyOrientation.BOTTOM    ,
                        MyOrientation.MIDDLE))
                    9    -> setPositionNextTo(touchArea,character,false, MyOrientationSet( MyOrientation.BOTTOM    ,
                        MyOrientation.RIGHT))
                    else -> return
                }

            }

            fun greeting1(){



                appearAlphaAnimation(character,true).start()
              positionDataMap[character] = BorderSet(bottomSideSet = ViewAndSide(touchArea,
                  MyOrientation.TOP))
                explainTextAnimation("やあ、僕はとさかくん", MyOrientation.TOP,character).start()
                var a = 1
                onInstallBinding.root.setOnClickListener {
                    tameshi(a)
                    a+=1
                }
                  setPositionNextTo(touchArea,character, false,MyOrientationSet(
                      MyOrientation.TOP,
                      MyOrientation.MIDDLE))
//                goNextOnClickAnyWhere()
            }
            fun greeting2(){
                explainTextAnimation("これから、KiNaの使い方を説明するね", MyOrientation.TOP,character).start()
//                setPositionNextTo(bnvBtnAdd,character, MyOrientationSet(MyOrientation.RIGHT, MyOrientation.BOTTOM,MyOrientation.LEFT))
                goNextOnClickAnyWhere()
            }
            fun createFlashCard1(){
//                setPositionNextTo(bnvBtnAdd,character, MyOrientationSet(MyOrientation.RIGHT, MyOrientation.BOTTOM,MyOrientation.LEFT))
                explainTextAnimation("KiNaでは、フォルダと単語帳が作れるよ\n" +
                        "ボタンをタッチして、単語帳を作ってみよう", MyOrientation.TOP,character).start()

                setArrow(MyOrientation.TOP,bnvBtnAdd)
                setHole(bnvBtnAdd, HoleShape.CIRCLE)
                goNextOnClickTouchArea(bnvBtnAdd)
            }
            fun createFlashCard2(){
                setArrow(MyOrientation.TOP,createMenuImvFlashCard)
                setHole(createMenuImvFlashCard, HoleShape.CIRCLE)
                createFileViewModel.setBottomMenuVisible(true)
                goNextOnClickTouchArea(createMenuImvFlashCard)
            }
            fun createFlashCard3(){
                AnimatorSet().apply {
                    playTogether(
                        appearAlphaAnimation(character, false),
                        appearAlphaAnimation(textView, false),
                    )
                    doOnEnd {
                        goNextOnClickTouchArea(btnFinish)
                        createFileViewModel.onClickCreateFile(FileStatus.FLASHCARD_COVER)
                        setHole(frameLayEditFile, HoleShape.RECTANGLE)
                        setTouchArea(edtCreatingFileTitle)
                        addTouchArea(edtCardFrontTitle).setOnClickListener {
                            showKeyBoard(edtCreatingFileTitle,activity)
                        }
                        edtCreatingFileTitle.requestFocus()
                        showKeyBoard(edtCreatingFileTitle,activity)
                        setArrow(MyOrientation.BOTTOM,btnFinish)
                        arrayOf(imvColPalRed,imvColPalBlue,imvColPalGray,imvColPaYellow).onEach {
                            cloneView(it)
                        } }
                    start()
                }

            }
            fun createFlashCard5(){
                val title = edtCreatingFileTitle.text.toString()
                if(title == "") {
                    makeToast(activity,"タイトルが必要です")
                    return
                }
                val lastId = libraryRv.size
                var newLastId = libraryRv.size
                var fixedSize = true
                hideKeyBoard(edtCreatingFileTitle,activity)
                onInstallBinding.root.children.iterator().forEach {
                    if(it.tag == 1)it.visibility = View.GONE
                }
                makeTouchAreaGone()
                makeArrowGone()
                createFileViewModel.makeFilePos0()

                createFileViewModel.onClickFinish(title)
                removeHole()
                libraryRv.itemAnimator = object:DefaultItemAnimator(){
                    override fun onAnimationFinished(viewHolder: RecyclerView.ViewHolder) {
                        super.onAnimationFinished(viewHolder)
                        val rv = activity.findViewById<RecyclerView>(R.id.vocabCardRV)
                        newLastId = rv.size
                        if(lastId+1==newLastId){
                            guideInOrder(order+1)
                        }

                    }
                }

            }
            fun createFlashCard6(){
                setHole(libraryRv[0], HoleShape.RECTANGLE)
                setPositionByXY(imvTabLibrary, character, MyOrientation.TOP,20,false)
                appearAlphaAnimation(character,true).start()
                goNextOnClickAnyWhere()
            }
            fun checkInsideNewFlashCard1(){
                explainTextAnimation("おめでとう！単語帳が追加されたよ\n中身を見てみよう！", MyOrientation.RIGHT,character).start()
                 goNextOnClickTouchArea(libraryRv[0])
            }
            fun checkInsideNewFlashCard2(){
                makeTxvGone()
                makeTouchAreaGone()
                changeViewVisibility(holeView,true)
                removeHole()
                libraryViewModel.openNextFile(createFileViewModel.returnLastInsertedFile()!!)
                goNextOnClickAnyWhere()
            }
            fun checkInsideNewFlashCard3(){
                explainTextAnimation("まだカードがないね\n早速作ってみよう", MyOrientation.RIGHT,character).start()
                goNextOnClickAnyWhere()
            }
            fun makeNewCard1(){
                appearAlphaAnimation(character,false).start()
                makeTxvGone()

                setHole(bnvBtnAdd, HoleShape.CIRCLE)
                goNextOnClickTouchArea(bnvBtnAdd)
            }
            fun makeNewCard2(){
                setHole(createMenuImvNewCard, HoleShape.CIRCLE)
                createFileViewModel.setBottomMenuVisible(true)
                goNextOnClickTouchArea(createMenuImvNewCard)
            }
            fun makeNewCard3(){
                createFileViewModel.setBottomMenuVisible(false)
                makeTouchAreaGone()
                removeHole()
                createCardViewModel.onClickAddNewCardBottomBar()
                goNextOnClickAnyWhere()
            }
            fun explainCreateCardFrag1(){
                setPositionByXY(edtCardFrontTitle,character, MyOrientation.RIGHT,0,false)
                appearAlphaAnimation(character,true).start()
                explainTextAnimation("上半分は、カードの表", MyOrientation.BOTTOM,character).start()
                setHole(edtCardFrontContent, HoleShape.RECTANGLE)
                goNextOnClickAnyWhere()
            }
            fun explainCreateCardFrag2(){
                setPositionByXY(edtCardFrontTitle,character, MyOrientation.RIGHT,0,false)
                explainTextAnimation("下半分は、カードの裏になっているよ", MyOrientation.BOTTOM,character)
                setHole(edtCardBackContent, HoleShape.RECTANGLE)
                goNextOnClickAnyWhere()

            }
            fun explainCreateCardFrag3(){
                setPositionByXY(edtCardFrontTitle,character, MyOrientation.RIGHT,0,false)
                setHole(edtCardFrontTitle, HoleShape.RECTANGLE)
                explainTextAnimation("カードの裏表にタイトルを付けることもできるんだ！", MyOrientation.BOTTOM,character).start()
                goNextOnClickAnyWhere()
            }
            fun explainCreateCardFrag4(){
                setPositionByXY(edtCardFrontTitle,character, MyOrientation.RIGHT,0,false)
                setHole(edtCardBackTitle, HoleShape.RECTANGLE)
                explainTextAnimation("好みのようにカスタマイズしてね", MyOrientation.MIDDLE,edtCardFrontContent).start()
                goNextOnClickAnyWhere()
            }
            fun explainCreateCardNavigation1(){
                setPositionByXY(edtCardFrontContent,character, MyOrientation.BOTTOM,70,false)
                explainTextAnimation("カードをめくるには、\n下のナビゲーションボタンを使うよ", MyOrientation.TOP,character).start()
                setHole(linLayCreateCardNavigation, HoleShape.RECTANGLE)
                goNextOnClickAnyWhere()
            }
            fun explainCreateCardNavigation2(){
                explainTextAnimation("新しいカードを前に挿入するのはここ", MyOrientation.TOP,character).start()
                setArrow(MyOrientation.TOP,createCardInsertNext)
                goNextOnClickAnyWhere()
            }
            fun explainCreateCardNavigation3(){
                explainTextAnimation("後ろに挿入するのはここ！", MyOrientation.TOP,character).start()
                setArrow(MyOrientation.TOP,createCardInsertPrevious)
                goNextOnClickAnyWhere()
            }
            fun explainCreateCardNavigation4(){
                explainTextAnimation("矢印ボタンでカードを前後にめくってね！", MyOrientation.TOP,character).start()
                setArrow(MyOrientation.TOP, createCardNavFlipNext)
                goNextOnClickAnyWhere()
            }
            fun explainCreateCardNavigation5(){
                appearAlphaAnimation(character,false).start()
                explainTextAnimation("", MyOrientation.TOP,character).start()
                makeArrowGone()
                goNextOnClickAnyWhere()
                setArrow(MyOrientation.TOP, createCardNavFlipPrevious)
            }
            fun goodBye1(){
                setPositionByXY(onInstallBinding.root,character, MyOrientation.MIDDLE,0,false)
                explainTextAnimation("これでガイドは終わりだよ", MyOrientation.TOP,character).start()
                appearAlphaAnimation(character,true).start()
                goNextOnClickAnyWhere()
            }
            fun goodBye2(){
                explainTextAnimation("KiNaを楽しんで！", MyOrientation.TOP,character).start()
                goNextOnClickAnyWhere()
            }
            fun end(){
                mainViewModel.setGuideVisibility(false)
                removeGlobalListener()
                val sharedPref =  activity.getSharedPreferences(
                    "firstTimeGuide", Context.MODE_PRIVATE) ?: return
                val editor = sharedPref.edit()
                editor.putBoolean("firstTimeGuide", true)
                editor.apply()
            }

            when(order){
                1   ->  greeting1()
                2   ->  greeting2()
                3   ->  createFlashCard1()
                4   ->  createFlashCard2()
                5   ->  createFlashCard3()
                6   ->  createFlashCard5()
                7   ->  createFlashCard6()
                8   -> checkInsideNewFlashCard1()
                9   -> checkInsideNewFlashCard2()
                10  -> checkInsideNewFlashCard3()
                11  -> makeNewCard1()
                12  -> makeNewCard2()
                13  -> makeNewCard3()
                14  -> explainCreateCardFrag1()
                15  -> explainCreateCardFrag2()
                16  -> explainCreateCardFrag3()
                17  -> explainCreateCardFrag4()
                18  -> explainCreateCardNavigation1()
                19  -> explainCreateCardNavigation2()
                20  -> explainCreateCardNavigation3()
                21  -> explainCreateCardNavigation4()
                22  -> explainCreateCardNavigation5()
                23  -> goodBye1()
                24  -> goodBye2()
                25  -> end()
                else->  return
            }
        }
        guideInOrder(startOrder)

    }
    fun editGuide(startOrder:Int,mainViewModel: MainViewModel,
                  libraryViewModel: LibraryBaseViewModel,
                  createFileViewModel: EditFileViewModel){
        mainViewModel.setGuideVisibility(true)
        fun guideInOrder(order:Int){

            val libraryRv                   =activity.findViewById<RecyclerView>(R.id.vocabCardRV)
            val btnEditFile                   =activity.findViewById<ImageView>(R.id.btn_edit_whole)
            val frameLayEditFile            =activity.findViewById<FrameLayout>(R.id.frameLay_edit_file)
            val edtCreatingFileTitle        =activity.findViewById<EditText>(R.id.edt_file_title)
            val imvColPalRed                =activity.findViewById<ImageView>(R.id.imv_col_red)
            val imvColPalBlue               =activity.findViewById<ImageView>(R.id.imv_col_blue)
            val imvColPaYellow              =activity.findViewById<ImageView>(R.id.imv_col_yellow)
            val imvColPalGray               =activity.findViewById<ImageView>(R.id.imv_col_gray)
            val btnFinish                   =activity.findViewById<Button>(R.id.btn_finish)
            onInstallBinding.root.children.iterator().forEach { if(it.tag == 1) it.visibility = View.GONE }
            fun goNextOnClickAnyWhere(){
                onInstallBinding.root.setOnClickListener {
                    guideInOrder(order+1)
                }
            }
            fun goNextOnClickTouchArea(touchArea: View) {
                onInstallBinding.root.setOnClickListener(null)
                addTouchArea(touchArea).setOnClickListener {
                    guideInOrder(order + 1)
                }
            }
            fun greeting1(){
                if(mainViewModel.returnFragmentStatus()?.now!= MainFragment.Library){
                    mainViewModel.changeFragment(MainFragment.Library)
                }
                if(libraryViewModel.returnLibraryFragment()!= LibraryFragment.Home){
                    libraryViewModel.returnLibraryNavCon()?.navigate(LibraryHomeFragDirections.toLibHome())
                }
                appearAlphaAnimation(holeView,true).start()
                removeHole()
                appearAlphaAnimation(character,true).start()
                explainTextAnimation("これから、\n単語帳を編集する方法を説明するよ", MyOrientation.TOP,character).start()
                goNextOnClickAnyWhere()
            }
            fun explainBtn(){
                setHole(libraryRv[0],HoleShape.RECTANGLE)
                setPositionByXY(libraryRv[0],character, MyOrientation.BOTTOM,20,false)
                explainTextAnimation("このアイテムを見てみよう", MyOrientation.BOTTOM,character).start()
                goNextOnClickAnyWhere()

            }
            fun explainBtn2(){
                explainTextAnimation("編集ボタンを表示するには、" +
                        "\nアイテムを横にスライドするよ", MyOrientation.BOTTOM,
                    character).start()
                val area = addTouchArea(libraryRv)
                area.setOnTouchListener(
                    object :MyTouchListener(libraryRv.context){
                        override fun onScrollLeft(distanceX: Float, motionEvent: MotionEvent?) {
                            super.onScrollLeft(distanceX, motionEvent)
                            val started = libraryRv.findChildViewUnder(motionEvent!!.x,motionEvent.y)
                            if(started!=null&&libraryRv.indexOfChild(started)==0){
                                val lineLaySwipeShow = started.findViewById<LinearLayoutCompat>(R.id.linLay_swipe_show) ?:return
                                started.apply {
                                    if(started.tag== LibRVState.Plane){
                                        lineLaySwipeShow.layoutParams.width = 1
                                        lineLaySwipeShow.requestLayout()
                                        lineLaySwipeShow.children.iterator().forEach {
                                            it.visibility = View.VISIBLE
                                        }
                                        lineLaySwipeShow.visibility = View.VISIBLE
                                        started.tag = LibRVState.LeftSwiping

                                    }else if(started.tag== LibRVState.LeftSwiping) {

                                        lineLaySwipeShow.layoutParams.width = distanceX.toInt()/5 + 1
                                        lineLaySwipeShow.requestLayout()

                                    }

                                }
                            }
                        }
                        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                            if((event?.actionMasked== MotionEvent.ACTION_UP||event?.actionMasked == MotionEvent.ACTION_CANCEL)){
                                val started = libraryRv[0]
                                val lineLaySwipeShow = started.findViewById<LinearLayoutCompat>(R.id.linLay_swipe_show) ?:return false
                                if(started.tag== LibRVState.LeftSwiping){
                                    if(lineLaySwipeShow.layoutParams.width <25){
                                        Animation().animateLibRVLeftSwipeLay(lineLaySwipeShow,false)
                                        started.tag = LibRVState.Plane
                                    }
                                    else if (lineLaySwipeShow.layoutParams.width>=25){
                                        Animation().animateLibRVLeftSwipeLay(lineLaySwipeShow ,true)
                                        started.tag = LibRVState.LeftSwiped
                                        libraryViewModel.setLeftSwipedItemExists(true)
                                        area.visibility = View.GONE
                                    }

                                }

                            }
                            return super.onTouch(v, event)
                        }
                    }
                )
                onInstallBinding.root.setOnClickListener{
                    if(libraryViewModel.returnLeftSwipedItemExists())
                        guideInOrder(order+1)
                }

            }
            fun explainBtn3(){
                goNextOnClickAnyWhere()
                explainTextAnimation("編集してみよう", MyOrientation.BOTTOM,character).start()
            }
            fun explainBtn4(){
                setHole(btnEditFile,HoleShape.CIRCLE)
                goNextOnClickTouchArea(btnEditFile)
                setArrow(MyOrientation.LEFT,btnEditFile)
                AnimatorSet().apply {
                    playTogether(appearAlphaAnimation(character,false),
                        appearAlphaAnimation(textView,false))
                    start()
                }
            }
            fun editFile1(){
                setHole(frameLayEditFile,HoleShape.RECTANGLE)
                createFileViewModel.onClickEditFileInRV(
                    libraryViewModel.returnParentRVItems()[0] as File)
                goNextOnClickAnyWhere()

            }
            fun editFile2(){
                setPositionByXY(frameLayEditFile,character, MyOrientation.BOTTOM,10,false)
                appearAlphaAnimation(character,true).start()
                explainTextAnimation("じゃじゃん！", MyOrientation.TOP,frameLayEditFile).start()
                goNextOnClickAnyWhere()
            }
            fun editFile3(){
                setPositionByXY(frameLayEditFile,character, MyOrientation.BOTTOM,10,false)
                explainTextAnimation("タイトルを変えたり", MyOrientation.TOP,frameLayEditFile).start()
                setArrow(MyOrientation.BOTTOM,edtCreatingFileTitle)
                goNextOnClickAnyWhere()
            }
            fun editFile4(){
                setPositionByXY(frameLayEditFile,character, MyOrientation.BOTTOM,10,false)
                explainTextAnimation("色で分けて整理してみてね", MyOrientation.TOP,frameLayEditFile).start()
                arrayOf(imvColPaYellow,imvColPalBlue,imvColPalRed,imvColPalGray).onEach {
                    cloneView(it)
                }
                setArrow(MyOrientation.BOTTOM,imvColPalRed)
                goNextOnClickAnyWhere()
            }
            fun editFile5(){
                addTouchArea(edtCreatingFileTitle).setOnClickListener {
                    edtCreatingFileTitle.requestFocus()
                    showKeyBoard(edtCreatingFileTitle,activity)
                }
                AnimatorSet().apply {
                    playTogether(appearAlphaAnimation(character,false),
                        appearAlphaAnimation(textView,false))
                    start()
                }
                setArrow(MyOrientation.BOTTOM,btnFinish)
                goNextOnClickTouchArea(btnFinish)
            }
            fun editFile6(){
                createFileViewModel.onClickFinish(edtCreatingFileTitle.text.toString())
                appearAlphaAnimation(holeView,false).start()
                hideKeyBoard(edtCreatingFileTitle,activity)
                mainViewModel.setGuideVisibility(false)
            }

            when(order){
                0   -> greeting1()
                1   -> explainBtn()
                2   -> explainBtn2()
                3   -> explainBtn3()
                4   -> explainBtn4()
                5   -> editFile1()
                6   -> editFile2()
                7   -> editFile3()
                8   -> editFile4()
                9   -> editFile5()
                10  -> editFile6()
            }
        }
        guideInOrder(startOrder)
    }
    fun deleteGuide(startOrder: Int,mainViewModel: MainViewModel,
                    libraryViewModel:LibraryBaseViewModel,editFileViewModel: EditFileViewModel,
                    deletePopUpViewModel: DeletePopUpViewModel
                    ){
        mainViewModel.setGuideVisibility(true)
        fun guideInOrder(order: Int){
            onInstallBinding.root.children.iterator().forEach { if(it.tag == 1) it.visibility = View.GONE }
            val libraryRv                   =activity.findViewById<RecyclerView>(R.id.vocabCardRV)
            val btnDeleteFile           =activity.findViewById<ImageView>(R.id.btn_delete)
            val btnConfirmDelete           =activity.findViewById<Button>(R.id.btn_commit_delete_only_parent)
            val btnConfirmDeleteOnlyParent           =activity.findViewById<Button>(R.id.delete_only_file)
            val frameLayConfirmDelete       =activity.findViewById<FrameLayout>(R.id.frameLay_confirm_delete)
            val frameLayConfirmDeleteWithChildren       =activity.findViewById<FrameLayout>(R.id.frameLay_confirm_delete_with_children)
            val txvContainingCardAmount       =activity.findViewById<TextView>(R.id.txv_containing_card)
            val frameLayInBox       =activity.findViewById<ConstraintLayout>(R.id.frameLay_inBox)
            val txvInBoxCardAmount       =activity.findViewById<TextView>(R.id.txv_inBox_card_amount)
            val imvMultiModeMenu           =activity.findViewById<ImageView>(R.id.imv_change_menu_visibility)
            val frameLayMultiModeMenu       =activity.findViewById<FrameLayout>(R.id.frameLay_multi_mode_menu)

            fun goNextOnClickAnyWhere(){
                onInstallBinding.root.setOnClickListener {
                    guideInOrder(order+1)
                }
            }
            fun goNextOnClickTouchArea(touchArea: View) {
                onInstallBinding.root.setOnClickListener(null)
                addTouchArea(touchArea).setOnClickListener {
                    guideInOrder(order + 1)
                }
            }
            fun greeting(){
                if(mainViewModel.returnFragmentStatus()?.now!= MainFragment.Library){
                    mainViewModel.changeFragment(MainFragment.Library)
                }
                if(libraryViewModel.returnLibraryFragment()!= LibraryFragment.Home){
                    libraryViewModel.returnLibraryNavCon()?.navigate(LibraryHomeFragDirections.toLibHome())
                }
                removeHole()
                appearAlphaAnimation(character,true).start()
                explainTextAnimation("これから、\nアイテムを削除する方法を説明するよ", MyOrientation.TOP,character).start()
                goNextOnClickAnyWhere()
            }
            fun explainBtn(){
                editGuide(1,mainViewModel,libraryViewModel,editFileViewModel)
                goNextOnClickAnyWhere()
            }
            fun explainBtn2(){
                editGuide(2,mainViewModel,libraryViewModel,editFileViewModel)
                onInstallBinding.root.setOnClickListener{
                    if(libraryViewModel.returnLeftSwipedItemExists())
                        guideInOrder(order+1)
                }
            }
            fun explainBtn3(){
                setHole(btnDeleteFile,HoleShape.CIRCLE)
                setPositionByXY(libraryRv[0],character, MyOrientation.BOTTOM,10,false)
                explainTextAnimation("このボタンで、アイテムを削除できるよ", MyOrientation.BOTTOM,character).start()
                goNextOnClickAnyWhere()
            }
            fun explainBtn4(){
                goNextOnClickTouchArea(btnDeleteFile)
                explainTextAnimation("実際には削除しないので\n" +
                        "一度押してみよう", MyOrientation.BOTTOM,character).start()
                setArrow(MyOrientation.LEFT,btnDeleteFile)

            }
            fun explainDeleteSystem(){
                val exampleFile = File(
                    fileId = 0,
                    title = "サンプル単語帳",
                    fileStatus = FileStatus.FLASHCARD_COVER)
                deletePopUpViewModel.setDeletingItem(mutableListOf(exampleFile))
                deletePopUpViewModel.setConfirmDeleteVisible(true)
                setHole(frameLayConfirmDelete,HoleShape.RECTANGLE)
                setPositionByXY(frameLayConfirmDelete,character, MyOrientation.BOTTOM,50,false)
                setArrow(MyOrientation.BOTTOM,btnConfirmDelete)
                explainTextAnimation("消されないので、試しに削除を押してね！", MyOrientation.TOP,frameLayConfirmDelete).start()
                goNextOnClickTouchArea(btnConfirmDelete)
            }
            fun explainDeleteSystem2(){
                deletePopUpViewModel.apply {
                    setConfirmDeleteVisible(false)
                    setConfirmDeleteWithChildrenVisible(true)
                }
                txvContainingCardAmount.text = "15"
                setHole(frameLayConfirmDeleteWithChildren,HoleShape.RECTANGLE)
                setArrow(MyOrientation.BOTTOM,txvContainingCardAmount)
                explainTextAnimation("単語帳やフォルダに中身が入っていると\n確認画面に映るよ！",
                    MyOrientation.TOP,frameLayConfirmDeleteWithChildren).start()
                goNextOnClickAnyWhere()
            }
            fun explainDeleteSystem3(){
                explainTextAnimation("中身もすべて消すか、\n残すか選べるよ", MyOrientation.TOP,frameLayConfirmDelete).start()
                makeArrowGone()
                goNextOnClickAnyWhere()
            }
            fun explainDeleteSystem4(){
                setHole(btnConfirmDeleteOnlyParent,HoleShape.CIRCLE)
                explainTextAnimation("中身を残す場合、\n中のカードはどの単語帳にも\n入っていないことになるんだ",
                    MyOrientation.TOP,frameLayConfirmDelete).start()
                goNextOnClickAnyWhere()
            }
            fun explainDeleteSystem5(){
                explainTextAnimation("そのカードはどこに行くかというと...",
                    MyOrientation.TOP,frameLayConfirmDelete).start()
                removeHole()
                deletePopUpViewModel.setConfirmDeleteWithChildrenVisible(false)
                goNextOnClickAnyWhere()
            }
            fun explainDeleteSystem6(){
                explainTextAnimation("InBoxの中に移るよ！",
                    MyOrientation.TOP,character).start()
                setHole(frameLayInBox,HoleShape.CIRCLE)
                txvInBoxCardAmount.text = "15"
                changeViewVisibility(txvInBoxCardAmount,true)
                goNextOnClickAnyWhere()
            }
            fun explainMultiSelectMode(){
                removeHole()
                txvInBoxCardAmount.text = ""
                changeViewVisibility(txvInBoxCardAmount,false)
                setPositionByXY(frameLayCallOnInstall,character, MyOrientation.MIDDLE,0,false)
                explainTextAnimation("アイテムをまとめて削除したいときは",
                    MyOrientation.TOP,character).start()
                goNextOnClickAnyWhere()
            }
            fun explainMultiSelectMode2(){
                setHole(libraryRv[0],HoleShape.RECTANGLE)
                addTouchArea(libraryRv[0]).setOnTouchListener(object :MyTouchListener(libraryRv.context){
                    override fun onLongClick(motionEvent: MotionEvent?) {
                        super.onLongClick(motionEvent)
                        guideInOrder(order+1)
                    }
                })
                setPositionByXY(libraryRv[0],character, MyOrientation.BOTTOM,50,false)
                explainTextAnimation("アイテムを長押ししてみて！",
                    MyOrientation.BOTTOM,character).start()
            }
            fun explainMultiSelectMode3(){
                libraryRv[0].findViewById<ImageView>(R.id.btn_select).isSelected = true
                libraryViewModel.setMultipleSelectMode(true)
                goNextOnClickAnyWhere()
            }
            fun explainMultiSelectMode4(){
                explainTextAnimation("こんなふうにまとめて選択できるよ",
                    MyOrientation.BOTTOM,character).start()
                goNextOnClickAnyWhere()
            }
            fun explainMultiSelectMode5(){
                setHole(imvMultiModeMenu,HoleShape.CIRCLE)
                addTouchArea(imvMultiModeMenu).setOnClickListener {
                    libraryViewModel.setMultiMenuVisibility(true)
                    guideInOrder(order+1)
                }
                explainTextAnimation("上のメニューで、削除を選んでね",
                    MyOrientation.BOTTOM,character).start()
            }
            fun explainMultiSelectMode6(){
                setHole(frameLayMultiModeMenu,HoleShape.RECTANGLE)
                setPositionByXY(frameLayMultiModeMenu,character, MyOrientation.BOTTOM,50,false)
                makeTxvGone()
                goNextOnClickAnyWhere()
            }
            fun end(){
                mainViewModel.setGuideVisibility(false)
                libraryViewModel.setMultipleSelectMode(false)
                libraryViewModel.setLeftSwipedItemExists(false)
            }
            when(order){
                0   -> greeting()
                1   -> explainBtn()
                2   -> explainBtn2()
                3   -> explainBtn3()
                4   -> explainBtn4()
                5   -> explainDeleteSystem()
                6   -> explainDeleteSystem2()
                7   -> explainDeleteSystem3()
                8   -> explainDeleteSystem4()
                9   -> explainDeleteSystem5()
                10  -> explainDeleteSystem6()
                11  -> explainMultiSelectMode()
                12  -> explainMultiSelectMode2()
                13  -> explainMultiSelectMode3()
                14  -> explainMultiSelectMode4()
                15  -> explainMultiSelectMode5()
                16  -> explainMultiSelectMode6()
                17  -> end()
            }
        }
        guideInOrder(startOrder)
    }
    fun moveGuide(startOrder: Int,mainViewModel: MainViewModel,
                    libraryViewModel:LibraryBaseViewModel,editFileViewModel: EditFileViewModel,
                    moveToViewModel: ChooseFileMoveToViewModel,
                  createCardViewModel: CreateCardViewModel
    ){
        mainViewModel.setGuideVisibility(true)
        var selectedView:View? = null
        fun guideInOrder(order: Int){

            onInstallBinding.root.children.iterator().forEach { if(it.tag == 1) it.visibility = View.GONE }
            val libraryRv                   =activity.findViewById<RecyclerView>(R.id.vocabCardRV)
            val imvMultiModeMenu           =activity.findViewById<ImageView>(R.id.imv_change_menu_visibility)
            val frameLayMultiModeMenu       =activity.findViewById<FrameLayout>(R.id.frameLay_multi_mode_menu)
            val lineLayMoveSelectedItem       =activity.findViewById<LinearLayoutCompat>(R.id.linLay_move_selected_items)
            val bnvBtnAdd                   =activity.findViewById<ImageView>(R.id.bnv_imv_add)
            val createMenuImvFlashCard      =activity.findViewById<FrameLayout>(R.id.imvnewTangocho)
            val createMenuImvFolder      =activity.findViewById<FrameLayout>(R.id.imvnewfolder)
            val frameLayBottomMenu      =activity.findViewById<FrameLayout>(R.id.frame_bottomMenu)
            val btnFinish                   =activity.findViewById<Button>(R.id.btn_finish)
            val frameLayPopUpConfirmMove       =activity.findViewById<FrameLayout>(R.id.frameLay_confirm_move)
            fun goNextOnClickAnyWhere(){
                onInstallBinding.root.setOnClickListener {
                    guideInOrder(order+1)
                }
            }
            fun goNextOnClickTouchArea(touchArea: View) {
                onInstallBinding.root.setOnClickListener(null)
                addTouchArea(touchArea).setOnClickListener {
                    guideInOrder(order + 1)
                }
            }
            fun greeting(){
                removeHole()
                appearAlphaAnimation(holeView,true).start()
                appearAlphaAnimation(character,true).start()
                explainTextAnimation("これから、\nアイテムを移動する方法を説明するよ", MyOrientation.TOP,character).start()
                goNextOnClickAnyWhere()
            }
            fun greeting2(){
                explainTextAnimation("フォルダを整理したり、\nインボックスのカードを移動するよ！", MyOrientation.TOP,character).start()
                goNextOnClickAnyWhere()
            }
            fun guideMultiMode(){
                explainTextAnimation("アイテムを長押ししてみよう", MyOrientation.TOP,character).start()
                goNextOnClickAnyWhere()

            }
            fun guideMultiMode2of1(){
                appearAlphaAnimation(character,false).start()
                makeTxvGone()
                setHole(libraryRv,HoleShape.RECTANGLE)
                addTouchArea(libraryRv).setOnTouchListener(object :MyTouchListener(libraryRv.context){
                    override fun onLongClick(motionEvent: MotionEvent?) {
                        super.onLongClick(motionEvent)
                        selectedView = libraryRv.findChildViewUnder(motionEvent!!.x,motionEvent.y)
                        if(selectedView!=null){
                            guideInOrder(order+1)
                        }
                    }
                })
            }
            fun guideMultiMode2(){
                libraryViewModel.setMultipleSelectMode(true)
                setHole(selectedView!!,HoleShape.RECTANGLE)
                selectedView!!.findViewById<ImageView>(R.id.btn_select).apply {
                    isSelected = true
                }
                val position = libraryRv.getChildAdapterPosition(selectedView!!)
                libraryViewModel.onClickRvSelect(ListAttributes.Add,libraryViewModel.returnParentRVItems()[position])
                goNextOnClickAnyWhere()
            }
            fun guideMultiMode3(){
                explainTextAnimation("メニューを開くよ", MyOrientation.TOP,character).start()
                setHole(imvMultiModeMenu,HoleShape.CIRCLE)
                goNextOnClickTouchArea(imvMultiModeMenu)
            }
            fun guideMultiMode4(){
                makeTxvGone()
                appearAlphaAnimation(character,false).start()
                libraryViewModel.setMultiMenuVisibility(true)
                setHole(frameLayMultiModeMenu,HoleShape.RECTANGLE)
                setArrow(MyOrientation.LEFT,lineLayMoveSelectedItem)
                goNextOnClickTouchArea(lineLayMoveSelectedItem)
            }
            fun moveToFragment(){
                appearAlphaAnimation(holeView,false).apply {
                    doOnEnd {
                        changeViewVisibility(holeView,false)
                        libraryViewModel.returnLibraryNavCon()?.navigate(LibraryChooseFileMoveToFragDirections.selectFileMoveTo(null))
                    }
                    start()
                }
                goNextOnClickAnyWhere()
            }
            fun moveToFragment2(){
                setPositionByXY(bnvBtnAdd,character, MyOrientation.TOP,0,false)
                appearAlphaAnimation(character,true).start()
                explainTextAnimation("アイテムの移動先が表示されるよ", MyOrientation.TOP,character).start()
                onInstallBinding.root.setOnClickListener {
                    if(libraryRv.size==0) guideInOrder(order+1) else guideInOrder(15)
                }
            }
            fun moveToFragment3(){
                explainTextAnimation("また移動先がないね", MyOrientation.TOP,character).start()
                goNextOnClickAnyWhere()
            }
            fun createMovableItem(){
                explainTextAnimation("追加してみよう", MyOrientation.TOP,character).start()
                goNextOnClickAnyWhere()
            }
            fun createMovableItem2(){
                setHole(bnvBtnAdd,HoleShape.CIRCLE)
                appearAlphaAnimation(holeView,true).start()
                goNextOnClickTouchArea(bnvBtnAdd)
            }
            fun createMovableItem3(){
                editFileViewModel.setBottomMenuVisible(true)
                addTouchArea(createMenuImvFolder).setOnClickListener {
                    editFileViewModel.onClickCreateFile(FileStatus.FOLDER)
                    guideInOrder(order+1)
                }
                addTouchArea(createMenuImvFlashCard).setOnClickListener {
                    editFileViewModel.onClickCreateFile(FileStatus.FLASHCARD_COVER)
                    guideInOrder(order+1)
                }
                setHole(frameLayBottomMenu,HoleShape.RECTANGLE)
            }
            fun createMovableItem4(){
                createGuide(5,createCardViewModel,editFileViewModel,libraryViewModel,mainViewModel)
                goNextOnClickTouchArea(btnFinish)
            }
            fun createMovableItem5(){
                val lastId = libraryRv.size
                createGuide(6,createCardViewModel,editFileViewModel,libraryViewModel,mainViewModel)
                libraryRv.itemAnimator = object:DefaultItemAnimator(){
                    override fun onAnimationFinished(viewHolder: RecyclerView.ViewHolder) {
                        super.onAnimationFinished(viewHolder)
                        val rv = activity.findViewById<RecyclerView>(R.id.vocabCardRV)
                        val newLastId = rv.size
                        if (lastId + 1 == newLastId) {
                            guideInOrder(order + 1)
                        }
                    }
                }
            }
            fun createMovableItem6(){
                createGuide(7,createCardViewModel,editFileViewModel,libraryViewModel,mainViewModel)
                goNextOnClickAnyWhere()
            }
            fun selectMovableItem(){
                setHole(libraryRv,HoleShape.RECTANGLE)
                explainTextAnimation("移動先を選択できるよ", MyOrientation.TOP,character).start()
                goNextOnClickAnyWhere()
            }
            fun selectMovableItem2(){
                explainTextAnimation("カードは単語帳、\n単語帳はフォルダへ移動する\nことしかできないよ！",
                    MyOrientation.TOP,character).start()
                goNextOnClickAnyWhere()
            }
            fun selectMovableItem3(){
                val linLayMove = libraryRv[0].findViewById<FrameLayout>(R.id.rv_base_frameLay_left)
                setHole(linLayMove,HoleShape.RECTANGLE)
                setPositionByXY(libraryRv[0],character, MyOrientation.BOTTOM,10,false)
                explainTextAnimation("ここをタップして移動するよ", MyOrientation.BOTTOM,character).start()
                setArrow(MyOrientation.BOTTOM,linLayMove)
                goNextOnClickTouchArea(linLayMove)
            }
            fun explainPopUp(){
                moveToViewModel.setPopUpVisible(true)
                makeArrowGone()
                setHole(frameLayPopUpConfirmMove,HoleShape.RECTANGLE)
                setPositionByXY(frameLayPopUpConfirmMove,character, MyOrientation.BOTTOM,0,false)
                explainTextAnimation("最後は確認画面が表示されるよ", MyOrientation.TOP,frameLayPopUpConfirmMove).start()
                goNextOnClickAnyWhere()
            }
            fun end(){
                appearAlphaAnimation(holeView,false).start()
                appearAlphaAnimation(character,false).doOnEnd {
                    mainViewModel.setConfirmEndGuidePopUpVisible(true)
                }
                makeTxvGone()
                makeArrowGone()
                moveToViewModel.setPopUpVisible(false)
            }
            when(order){
                0   -> greeting()
                1   -> greeting2()
                2   -> guideMultiMode()
                3   -> guideMultiMode2of1()
                4   -> guideMultiMode2()
                5   -> guideMultiMode3()
                6   -> guideMultiMode4()
                7   -> moveToFragment()
                8   -> moveToFragment2()
                9   -> moveToFragment3()
                10  -> createMovableItem()
                11  -> createMovableItem2()
                12  -> createMovableItem3()
                13  -> createMovableItem4()
                14  -> createMovableItem5()
                15  -> createMovableItem6()
                16  -> selectMovableItem()
                17  -> selectMovableItem2()
                18  -> selectMovableItem3()
                19  -> explainPopUp()
                20  -> end()
            }
        }
        guideInOrder(startOrder)
    }
}
