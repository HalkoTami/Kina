package com.korokoro.kina.actions

import android.animation.AnimatorSet
import android.content.Context
import android.graphics.drawable.GradientDrawable.Orientation
import android.view.View
import android.view.ViewGroup.LayoutParams
import android.view.ViewTreeObserver
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
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
import com.korokoro.kina.db.enumclass.FileStatus
import com.korokoro.kina.ui.customViews.*
import com.korokoro.kina.ui.viewmodel.*
import kotlin.math.abs


class CreateGuide(val activity:AppCompatActivity, val onInstallBinding: CallOnInstallBinding){
    val actions = InstallGuide(activity,onInstallBinding)
    val borderDataMap = mutableMapOf<View,BorderSet>()
    val globalLayoutSet = mutableMapOf<View, ViewTreeObserver.OnGlobalLayoutListener>()
//
    fun saveBorderDataMap(view: View,set:BorderSet){
        actions.saveBorderDataMap(view,set, borderDataMap)
    }
//
//
    private fun setPositionByMargin(view: View,myOrientationSet:MyOrientationSet){
         ViewChangeActions().setPositionByMargin(view,false,myOrientationSet,borderDataMap,onInstallBinding.root)
    }
    var textPosData:ViewAndSide = ViewAndSide(actions.character,MyOrientation.TOP)
    var textFit:Boolean = false
    private fun explainTextAnimation(string: String):AnimatorSet{
        return actions.explainTextAnimation(string= string,textPosData.side,textPosData.view,borderDataMap,textFit,globalLayoutSet)
    }
    private fun setArrow(arrowPosition: MyOrientation,view: View){
        actions.setArrow(arrowPosition,view,borderDataMap)
    }
    private fun addTouchArea(view: View):View{
        return actions.addTouchArea(view,borderDataMap)
    }
    private fun saveSimplePosRelation(movingView: View,standardView:View,orientation: MyOrientation,fit:Boolean){
        actions.saveSimplePosRelation(movingView,standardView,orientation,fit,borderDataMap)
    }
//
//    private fun makeTxvGone(){
//        appearAlphaAnimation(textView,false).start()
//    }
//    private fun makeArrowGone(){
//        changeViewVisibility(arrow,false)
//    }
//    private fun makeTouchAreaGone(){
//        changeViewVisibility(touchArea,false)
//    }
//    private fun appearAlphaAnimation(view :View, visible:Boolean): ValueAnimator {
//
//
//        return if(visible) appear else disappear
//
//
//    }
//
//    private fun explainTextAnimation(string: String, orientation: MyOrientation,view: View ): AnimatorSet {
//
//
//        textView.text = string
//        textView.layoutParams.width = LayoutParams.WRAP_CONTENT
//        textView.requestLayout()
//        saveSimplePosRelation(textView,view,orientation)
//        val set = when(orientation){
//            MyOrientation.TOP     -> MyOrientationSet(verticalOrientation = MyOrientation.BOTTOM,horizontalOrientation = MyOrientation.MIDDLE)
//            MyOrientation.LEFT    -> MyOrientationSet(verticalOrientation = MyOrientation.MIDDLE,horizontalOrientation = MyOrientation.RIGHT)
//            MyOrientation.RIGHT   -> MyOrientationSet(verticalOrientation = MyOrientation.MIDDLE,horizontalOrientation = MyOrientation.LEFT)
//            MyOrientation.BOTTOM  -> MyOrientationSet(verticalOrientation = MyOrientation.TOP,horizontalOrientation = MyOrientation.MIDDLE)
//            MyOrientation.MIDDLE  -> MyOrientationSet(verticalOrientation = MyOrientation.MIDDLE,horizontalOrientation = MyOrientation.MIDDLE)
//        }
//        setPositionByMargin(textView, set)
//        checkIfOutOfBound(textView,onInstallBinding.root,10)
//        textView.visibility = if(string=="") View.GONE else View.VISIBLE
//
//        val finalDuration:Long = 100
//        val anim2 = ValueAnimator.ofFloat(1.1f,1f)
//        anim2.addUpdateListener {
//            val progressPer = it.animatedValue as Float
//            setScale(textView,progressPer,progressPer)
//        }
//        val anim = ValueAnimator.ofFloat(0.7f,1.1f)
//        anim.addUpdateListener {
//            val progressPer = it.animatedValue as Float
//            setScale(textView,progressPer,progressPer)
//        }
//        val animAlpha = ValueAnimator.ofFloat(0f,1f)
//        anim.addUpdateListener {
//            setAlpha(textView,it.animatedValue as Float)
//        }
//        val scaleAnim = AnimatorSet().apply {
//            playSequentially(anim,anim2)
//            anim2.duration = finalDuration*0.3.toLong()
//            anim.duration = finalDuration*0.7.toLong()
//        }
//        val finalAnim = AnimatorSet().apply {
//            playTogether(animAlpha,scaleAnim)
//            scaleAnim.duration = finalDuration
//        }
//
//        return finalAnim
//    }
//    private fun explainTextAnimationManual(string: String, orientation: MyOrientationSet ): AnimatorSet {
//
//
//        textView.text = string
//        textView.layoutParams.width = LayoutParams.WRAP_CONTENT
//        textView.requestLayout()
//        setPositionByMargin(textView, orientation)
//        checkIfOutOfBound(textView,onInstallBinding.root,10)
//        textView.visibility = if(string=="") View.GONE else View.VISIBLE
//
//        val finalDuration:Long = 100
//        val anim2 = ValueAnimator.ofFloat(1.1f,1f)
//        anim2.addUpdateListener {
//            val progressPer = it.animatedValue as Float
//            setScale(textView,progressPer,progressPer)
//        }
//        val anim = ValueAnimator.ofFloat(0.7f,1.1f)
//        anim.addUpdateListener {
//            val progressPer = it.animatedValue as Float
//            setScale(textView,progressPer,progressPer)
//        }
//        val animAlpha = ValueAnimator.ofFloat(0f,1f)
//        anim.addUpdateListener {
//            setAlpha(textView,it.animatedValue as Float)
//        }
//        val scaleAnim = AnimatorSet().apply {
//            playSequentially(anim,anim2)
//            anim2.duration = finalDuration*0.3.toLong()
//            anim.duration = finalDuration*0.7.toLong()
//        }
//        val finalAnim = AnimatorSet().apply {
//            playTogether(animAlpha,scaleAnim)
//            scaleAnim.duration = finalDuration
//        }
//
//        return finalAnim
//    }
//    private fun setArrowDirection(direction: MyOrientation){
//        arrow.rotation =
//            when(direction){
//                MyOrientation.BOTTOM-> -450f
//                MyOrientation.LEFT -> 0f
//                MyOrientation.RIGHT -> 900f
//                MyOrientation.TOP -> 450f
//                else -> return
//            }
//
//    }
//    private fun setArrow(arrowPosition: MyOrientation, view: View){
//        appearAlphaAnimation(arrow,true).start()
//        when(arrowPosition){
//            MyOrientation.BOTTOM-> setArrowDirection(MyOrientation.TOP)
//            MyOrientation.LEFT -> setArrowDirection(MyOrientation.RIGHT)
//            MyOrientation.RIGHT -> setArrowDirection(MyOrientation.LEFT)
//            MyOrientation.TOP -> setArrowDirection(MyOrientation.BOTTOM)
//            else ->  {
//                makeTouchAreaGone()
//                return
//            }
//        }
//        setPositionByXY(view,arrow,arrowPosition,10,false)
//    }
//    private fun setTouchArea(view: View){
//        changeViewVisibility(touchArea,true)
//        setPositionByXY(view,touchArea, MyOrientation.MIDDLE,0,true)
//    }
//
//    fun setHole(viewUnderHole:View){
//        holeView.viewUnderHole = viewUnderHole
//    }
//    private fun cloneView(view: View) {
//        addTouchArea(view).setOnClickListener {
//            view.callOnClick()
//        }
//    }
//    fun addTouchArea(view:View):View{
//        onInstallBinding.root.setOnClickListener(null)
//        val a = TouchAreaBinding.inflate(activity.layoutInflater)
//        a.touchView.tag = 1
//
//        val id = View.generateViewId()
//        a.touchView.id = id
//        onInstallBinding.root.addView(a.touchView)
//        val con = ConstraintSet()
//        con.clone(onInstallBinding.root)
////
//        con.connect(id, ConstraintSet.RIGHT ,ConstraintSet.PARENT_ID,ConstraintSet.RIGHT,)
//        con.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID,ConstraintSet.TOP,)
//        con.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID,ConstraintSet.BOTTOM,)
//        con.connect(id, ConstraintSet.LEFT, ConstraintSet.PARENT_ID,ConstraintSet.LEFT,)
//
//        con.applyTo(onInstallBinding.root)
//        saveBorderDataMap(a.touchView, BorderSet(
//            topSideSet = ViewAndSide(view,MyOrientation.TOP),
//            bottomSideSet = ViewAndSide(view,MyOrientation.BOTTOM),
//            leftSideSet = ViewAndSide(view,MyOrientation.LEFT),
//            rightSideSet = ViewAndSide(view,MyOrientation.RIGHT),
//
//            )
//        )
//        ViewChangeActions().setPositionByMargin(
//            view = a.touchView,
//            myOrientationSet = MyOrientationSet(MyOrientation.MIDDLE,MyOrientation.MIDDLE),
//            constraintLayout = onInstallBinding.root,
//            matchSize = true,
//            positionDataMap = borderDataMap)
//        return a.touchView
//    }
//    private fun removeHole(){
//        holeView.removeGlobalLayout()
//        holeView.noHole = true
//    }
    fun createGuide(startOrder:Int,
                    createCardViewModel:CreateCardViewModel,
                    createFileViewModel:EditFileViewModel,
                    libraryViewModel:LibraryBaseViewModel,
                    mainViewModel: MainViewModel){
        mainViewModel.setGuideVisibility(true)
        fun guideInOrder(order:Int){
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

            fun cloneView(view: View) {
                addTouchArea(view).setOnClickListener {
                    view.callOnClick()
                }
            }

            fun greeting1(){
                actions.appearAlphaAnimation(actions.character,true).start()
                setPositionByMargin(actions.character, MyOrientationSet(
                    MyOrientation.MIDDLE,MyOrientation.MIDDLE))
                explainTextAnimation("やあ、僕はとさかくん").start()
                goNextOnClickAnyWhere()
            }
            fun greeting2(){
                explainTextAnimation("これから、KiNaの使い方を説明するね").start()
                goNextOnClickAnyWhere()
            }
            fun createFlashCard1(){
                explainTextAnimation("KiNaでは、フォルダと単語帳が作れるよ\n" +
                        "ボタンをタッチして、単語帳を作ってみよう", ).start()
                setArrow(MyOrientation.TOP,bnvBtnAdd)
                actions.setHoleMargin(20)
                actions.setHole(bnvBtnAdd)
                goNextOnClickTouchArea(bnvBtnAdd)
            }
            fun createFlashCard2(){
                setArrow(MyOrientation.TOP,createMenuImvFlashCard)
                actions.setHoleRecRadius(20)
                actions.setHole(createMenuImvFlashCard)
                createFileViewModel.setBottomMenuVisible(true)
                goNextOnClickTouchArea(createMenuImvFlashCard)
            }
            fun createFlashCard3(){
                AnimatorSet().apply {
                    playTogether(
                        actions.appearAlphaAnimation(actions.character, false),
                        actions.appearAlphaAnimation(actions.textView, false),
                    )
                    doOnEnd {
                        goNextOnClickTouchArea(btnFinish)
                        createFileViewModel.onClickCreateFile(FileStatus.FLASHCARD_COVER)
                        actions.holeView.holeShape = HoleShape.RECTANGLE
                        actions.setHole(frameLayEditFile)
                        addTouchArea(edtCreatingFileTitle)
                        addTouchArea(edtCreatingFileTitle).setOnClickListener {
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
                actions.makeTouchAreaGone()
                actions.makeArrowGone()

                createFileViewModel.makeFileInGuide(title)
                actions.removeHole()
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
                actions.setHole(libraryRv[0])
                setPositionByMargin( actions.character, MyOrientationSet(MyOrientation.BOTTOM,MyOrientation.LEFT))
                actions.appearAlphaAnimation(actions.character,true).start()
                goNextOnClickAnyWhere()
            }
            fun checkInsideNewFlashCard1(){
                textPosData = ViewAndSide(actions.character,MyOrientation.RIGHT)
                explainTextAnimation("おめでとう！単語帳が追加されたよ\n中身を見てみよう！",
                ).start()
                 goNextOnClickTouchArea(libraryRv[0])
            }
            fun checkInsideNewFlashCard2(){
                actions.makeTxvGone()
                actions.makeTouchAreaGone()
                actions.removeHole()
                changeViewVisibility(actions.holeView,true)
                libraryViewModel.openNextFile(createFileViewModel.returnLastInsertedFile()!!)
                goNextOnClickAnyWhere()
            }
            fun checkInsideNewFlashCard3(){
                explainTextAnimation("まだカードがないね\n早速作ってみよう",
                ).start()
                goNextOnClickAnyWhere()
            }
            fun makeNewCard1(){
                actions.appearAlphaAnimation(actions.character,false).start()
                actions.makeTxvGone()
                actions.setHole(bnvBtnAdd)
                goNextOnClickTouchArea(bnvBtnAdd)
            }
            fun makeNewCard2(){
                actions.setHole(createMenuImvNewCard)
                createFileViewModel.setBottomMenuVisible(true)
                goNextOnClickTouchArea(createMenuImvNewCard)
            }
            fun makeNewCard3(){
                createFileViewModel.setBottomMenuVisible(false)
                actions.makeTouchAreaGone()
                actions.removeHole()
                createCardViewModel.onClickAddNewCardBottomBar()
                goNextOnClickAnyWhere()
            }
            fun explainCreateCardFrag1(){
                saveSimplePosRelation(actions.character,edtCardFrontContent,MyOrientation.TOP,false)
                setPositionByMargin(actions.character, MyOrientationSet(MyOrientation.BOTTOM,MyOrientation.LEFT))
                actions.appearAlphaAnimation(actions.character,true).start()
                explainTextAnimation("上半分は、カードの表", ).start()
                actions.setHole(edtCardFrontContent)
                goNextOnClickAnyWhere()
            }
            fun explainCreateCardFrag2(){
                setPositionByMargin(actions.character, MyOrientationSet(MyOrientation.BOTTOM,MyOrientation.LEFT))
                explainTextAnimation("下半分は、カードの裏になっているよ", )
                actions.setHole(edtCardBackContent)
                goNextOnClickAnyWhere()

            }
            fun explainCreateCardFrag3(){
                saveBorderDataMap(actions.character, BorderSet(bottomSideSet = ViewAndSide(edtCardBackTitle,MyOrientation.TOP)))
                setPositionByMargin(actions.character, MyOrientationSet(MyOrientation.BOTTOM,MyOrientation.LEFT))
                actions.setHole(edtCardFrontTitle)
                explainTextAnimation("カードの裏表にタイトルを付けることもできるんだ！").start()
                goNextOnClickAnyWhere()
            }
            fun explainCreateCardFrag4(){
                actions.setHole(edtCardBackTitle)
                textPosData = ViewAndSide(edtCardBackTitle,MyOrientation.TOP)
                textFit = false
                explainTextAnimation("好みのようにカスタマイズしてね").start()
                goNextOnClickAnyWhere()
            }
            fun explainCreateCardNavigation1(){
                saveBorderDataMap(actions.character,BorderSet(bottomSideSet = ViewAndSide(edtCardBackContent,MyOrientation.TOP)))
                setPositionByMargin(actions.character, MyOrientationSet(MyOrientation.BOTTOM,MyOrientation.MIDDLE))
                explainTextAnimation("カードをめくるには、\n下のナビゲーションボタンを使うよ", ).start()
                actions.setHole(linLayCreateCardNavigation)
                goNextOnClickAnyWhere()
            }
            fun explainCreateCardNavigation2(){
                explainTextAnimation("新しいカードを前に挿入するのはここ",).start()
                setArrow(MyOrientation.TOP,createCardInsertNext)
                goNextOnClickAnyWhere()
            }
            fun explainCreateCardNavigation3(){
                explainTextAnimation("後ろに挿入するのはここ！",).start()
                setArrow(MyOrientation.TOP,createCardInsertPrevious)
                goNextOnClickAnyWhere()
            }
            fun explainCreateCardNavigation4(){
                explainTextAnimation("矢印ボタンでカードを前後にめくってね！", ).start()
                setArrow(MyOrientation.TOP, createCardNavFlipNext)
                goNextOnClickAnyWhere()
            }
            fun explainCreateCardNavigation5(){
                actions.appearAlphaAnimation(actions.character,false).start()
                actions.makeArrowGone()
                goNextOnClickAnyWhere()
                setArrow(MyOrientation.TOP, createCardNavFlipPrevious)
            }
            fun goodBye1(){
                setPositionByMargin(actions.character, MyOrientationSet(MyOrientation.MIDDLE,MyOrientation.MIDDLE))
                explainTextAnimation("これでガイドは終わりだよ",).start()
                actions.appearAlphaAnimation(actions.character,true).start()
                goNextOnClickAnyWhere()
            }
            fun goodBye2(){
                explainTextAnimation("KiNaを楽しんで！").start()
                goNextOnClickAnyWhere()
            }
            fun end(){
                mainViewModel.setGuideVisibility(false)
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
}
