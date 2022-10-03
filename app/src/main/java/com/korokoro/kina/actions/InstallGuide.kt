package com.korokoro.kina.actions

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import com.korokoro.kina.R
import com.korokoro.kina.databinding.CallOnInstallBinding
import com.korokoro.kina.db.enumclass.FileStatus
import com.korokoro.kina.ui.viewmodel.CreateCardViewModel
import com.korokoro.kina.ui.viewmodel.EditFileViewModel
import com.korokoro.kina.ui.viewmodel.LibraryBaseViewModel
import com.korokoro.kina.ui.customClasses.MyOrientation
import com.korokoro.kina.ui.customViews.*
import kotlin.math.abs

class InstallGuide(val activity:AppCompatActivity){
    private val onInstallBinding = CallOnInstallBinding.inflate(activity.layoutInflater)
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
    private fun setPosition(mainView: View, subView: View, position: MyOrientation, margin:Int, matchSize:Boolean){
                    mainView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener{
                override fun onGlobalLayout() {
                    fun setPositionXAndY(x:Float,y:Float){
                        setXAndY(subView,x,y)
                    }
                    globalLayoutSet[mainView] = this
                    val a = IntArray(2)
                    mainView.getLocationInWindow(a)
                    val mainViewCenterPosX = a[0].toFloat()+mainView.width/2
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
                                mainViewCenterPosX-subView.width/2,
                                mainViewLeftTopPosY - subView.height -heightDiff -margin,
                            )
                        MyOrientation.MIDDLE ->
                            setPositionXAndY(mainViewCenterPosX-subView.width/2,
                                mainViewCenterPosY-subView.height/2)
                    }
                }
            })
    }
    private fun removeGlobalListener(){
        globalLayoutSet.onEach {
            it.key.viewTreeObserver.removeOnGlobalLayoutListener(it.value)
        }
    }
    private fun makeTxvGone(){
        changeViewVisibility(textView,false)
    }
    private fun makeArrowGone(){
        changeViewVisibility(arrow,false)
    }
    private fun makeTouchAreaGone(){
        changeViewVisibility(touchArea,false)
    }
    private fun appearAlphaAnimation(views :Array<View>, visible:Boolean): ValueAnimator {
        val appear = ValueAnimator.ofFloat(0f,1f)
        val disappear = ValueAnimator.ofFloat(1f,0f)
        arrayOf(appear,disappear).onEach { eachAnimator->
            eachAnimator.addUpdateListener { thisAnimator ->
                views.onEach { setAlpha(it, thisAnimator.animatedValue as Float) }
            }
            eachAnimator.duration = 300
        }
        appear.doOnStart { views.onEach { changeViewVisibility(it,true) } }
        disappear.doOnEnd { views.onEach { changeViewVisibility(it,false) } }
        return if(visible) appear else disappear
    }
    private fun explainTextAnimation(string: String, orientation: MyOrientation): AnimatorSet {
        setPosition(character,textView,orientation,10,false)
        textView.text = string
        textView.visibility = View.VISIBLE
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
    private fun setTouchArea(view: View){
        changeViewVisibility(touchArea,true)
        setPosition(view,touchArea, MyOrientation.MIDDLE,0,true)
    }
    private fun setHole(view: View, shape: HoleShape){
        removeGlobalListener()
        fun setHole(){
            val a = IntArray(2)
            view.getLocationInWindow(a)
            val viewCenterPosX = a[0].toFloat()+view.width/2
            val viewCenterPosY = a[1].toFloat() +view.height/2 -heightDiff
            onInstallBinding.viewWithHole.circleHolePosition =
                CirclePosition(viewCenterPosX,viewCenterPosY ,100f)
        }
        fun setRec(margin:Float){
            val a = IntArray(2)
            view.getLocationInWindow(a)
            val left = a[0].toFloat()
            val top = a[1].toFloat() -heightDiff
            onInstallBinding.viewWithHole.recHolePosition =
                RecPosition(left-margin,top-margin,left+view.width+margin,top+view.height+margin)
        }
        changeViewVisibility(holeView,true)
        view.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener{
            override fun onGlobalLayout() {
                globalLayoutSet[view] = this
                holeView.circleHolePosition = CirclePosition(0f,0f,0f)
                holeView.recHolePosition = RecPosition(0f,0f,0f,0f)
                when(shape){
                    HoleShape.CIRCLE -> setHole()
                    HoleShape.RECTANGLE -> setRec(50f)
                }
            }
        })
    }
    fun createGuide(startOrder:Int,
                    createCardViewModel:CreateCardViewModel,
                    createFileViewModel:EditFileViewModel,
                    libraryViewModel:LibraryBaseViewModel){
        fun guideInOrder(order:Int){
            val frameLayCallOnInstall       =activity.findViewById<FrameLayout>(R.id.frameLay_call_on_install)
            val bnvBtnAdd                   =activity.findViewById<ImageView>(R.id.bnv_imv_add)
            val createMenuImvFlashCard      =activity.findViewById<FrameLayout>(R.id.imvnewTangocho)
            val createMenuImvNewCard        =activity.findViewById<FrameLayout>(R.id.imvnewCard)
            val frameLayEditFile            =activity.findViewById<FrameLayout>(R.id.frameLay_edit_file)
            val edtCreatingFileTitle        =activity.findViewById<EditText>(R.id.edt_file_title)
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
            fun goNextOnClickTouchArea() {
                onInstallBinding.root.setOnClickListener(null)
                touchArea.setOnClickListener {
                    guideInOrder(order + 1)
                }
            }
            fun greeting1(){
                frameLayCallOnInstall.addView(onInstallBinding.root)
                setPosition(character,textView, MyOrientation.TOP,10,false)
                explainTextAnimation("やあ、僕はとさかくん", MyOrientation.TOP)
                goNextOnClickAnyWhere()
            }
            fun greeting2(){
                explainTextAnimation("これから、KiNaの使い方を説明するね", MyOrientation.TOP).start()
                goNextOnClickAnyWhere()
            }
            fun createFlashCard1(){
                explainTextAnimation("KiNaでは、フォルダと単語帳が作れるよ\n" +
                        "ボタンをタッチして、単語帳を作ってみよう", MyOrientation.TOP).start()

                setHole(bnvBtnAdd, HoleShape.CIRCLE)
                setTouchArea(bnvBtnAdd)
                goNextOnClickTouchArea()
            }
            fun createFlashCard2(){
                setHole(createMenuImvFlashCard, HoleShape.CIRCLE)
                setTouchArea(createMenuImvFlashCard)
                createFileViewModel.setBottomMenuVisible(true)
                goNextOnClickTouchArea()
            }
            fun createFlashCard3(){
                changeViewVisibility(onInstallBinding.imvCharacter,false)
                makeTxvGone()
                createFileViewModel.onClickCreateFile(FileStatus.FLASHCARD_COVER)
                setHole(frameLayEditFile, HoleShape.RECTANGLE)
                setTouchArea(edtCreatingFileTitle)
                goNextOnClickTouchArea()
            }
            fun createFlashCard4(){
                edtCreatingFileTitle.requestFocus()
                showKeyBoard(edtCreatingFileTitle,activity)
                setArrow(MyOrientation.BOTTOM,btnFinish)
                setTouchArea(btnFinish)
                goNextOnClickTouchArea()

            }
            fun createFlashCard5(){
                val title = edtCreatingFileTitle.text.toString()
                if(title == "") {
                    makeToast(activity,"タイトルが必要です")
                    return
                }
                makeTouchAreaGone()
                makeArrowGone()
                createFileViewModel.makeFilePos0()
                createFileViewModel.onClickFinish(title)
                hideKeyBoard(edtCreatingFileTitle,activity)
                setPosition(imvTabLibrary, character, MyOrientation.TOP,20,false)
                setPosition(character,textView, MyOrientation.RIGHT,0,false)
                textView.layoutParams.width = abs(barrier.x + 10 - activity.resources.displayMetrics.widthPixels)
                    .toInt()
                textView.requestLayout()
                changeViewVisibility(holeView,false)
                explainTextAnimation("おめでとう！単語帳が追加されたよ", MyOrientation.RIGHT).start()
                appearAlphaAnimation(arrayOf(character),true).start()
                goNextOnClickAnyWhere()
            }
            fun checkInsideNewFlashCard1(){
                setHole(libraryRv[0], HoleShape.RECTANGLE)
                setTouchArea(libraryRv[0])
                explainTextAnimation("中身を見てみよう！", MyOrientation.RIGHT).start()
                goNextOnClickTouchArea()
            }
            fun checkInsideNewFlashCard2(){
                makeTxvGone()
                makeTouchAreaGone()
                changeViewVisibility(holeView,false)
                libraryViewModel.openNextFile(createFileViewModel.returnLastInsertedFile()!!)
                goNextOnClickAnyWhere()
            }
            fun checkInsideNewFlashCard3(){
                explainTextAnimation("まだカードがないね\n早速作ってみよう", MyOrientation.RIGHT).start()
                goNextOnClickAnyWhere()
            }
            fun makeNewCard1(){
                makeTxvGone()
                changeViewVisibility(onInstallBinding.imvCharacter,false)
                changeViewVisibility(onInstallBinding.viewWithHole,true)
                setTouchArea(bnvBtnAdd)
                setHole(bnvBtnAdd, HoleShape.CIRCLE)
                goNextOnClickTouchArea()
            }
            fun makeNewCard2(){
                setHole(createMenuImvNewCard, HoleShape.CIRCLE)
                setTouchArea(createMenuImvNewCard)
                createFileViewModel.setBottomMenuVisible(true)
                goNextOnClickTouchArea()
            }
            fun makeNewCard3(){
                createFileViewModel.setBottomMenuVisible(false)
                changeViewVisibility(holeView,false)
                makeTouchAreaGone()
                createCardViewModel.onClickAddNewCardBottomBar()
                appearAlphaAnimation(arrayOf(touchArea,arrow,holeView),false)
                goNextOnClickAnyWhere()
            }
            fun explainCreateCardFrag1(){
                setPosition(edtCardFrontTitle,character, MyOrientation.RIGHT,10,false)
                explainTextAnimation("上半分は、カードの表", MyOrientation.LEFT).start()
                setPosition(edtCardFrontContent,textView,
                    MyOrientation.BOTTOM,10,false)
                appearAlphaAnimation(arrayOf(character),true).start()
                setHole(edtCardFrontContent, HoleShape.RECTANGLE)
                goNextOnClickAnyWhere()
            }
            fun explainCreateCardFrag2(){
                setHole(edtCardBackContent, HoleShape.RECTANGLE)
                explainTextAnimation("下半分は、カードの裏になっているよ", MyOrientation.BOTTOM).start()
                setPosition(edtCardFrontContent,textView, MyOrientation.MIDDLE,0,false)
                goNextOnClickAnyWhere()
            }
            fun explainCreateCardFrag3(){
                setHole(edtCardFrontTitle, HoleShape.RECTANGLE)
                explainTextAnimation("カードの裏表にタイトルを付けることもできるんだ！", MyOrientation.LEFT).start()
                setPosition(edtCardFrontContent,textView, MyOrientation.MIDDLE,0,false)
                goNextOnClickAnyWhere()
            }
            fun explainCreateCardFrag4(){
                setHole(edtCardBackTitle, HoleShape.RECTANGLE)
                explainTextAnimation("好みのようにカスタマイズしてね", MyOrientation.LEFT).start()
                setPosition(edtCardFrontContent,textView, MyOrientation.MIDDLE,0,false)
                goNextOnClickAnyWhere()
            }
            fun explainCreateCardNavigation1(){
                setPosition(edtCardBackContent,character, MyOrientation.TOP,10,false)
                explainTextAnimation("カードをめくるには、下のナビゲーションボタンを使うよ", MyOrientation.BOTTOM).start()
                setHole(linLayCreateCardNavigation, HoleShape.RECTANGLE)
                goNextOnClickAnyWhere()
            }
            fun explainCreateCardNavigation2(){
                explainTextAnimation("新しいカードを前に挿入するのはここ", MyOrientation.TOP).start()
                setArrow(MyOrientation.TOP,createCardInsertNext)
                goNextOnClickAnyWhere()
            }
            fun explainCreateCardNavigation3(){
                explainTextAnimation("後ろに挿入するのはここ！", MyOrientation.TOP).start()
                setArrow(MyOrientation.TOP,createCardInsertPrevious)
                goNextOnClickAnyWhere()
            }
            fun explainCreateCardNavigation4(){
                explainTextAnimation("矢印ボタンでカードを前後にめくってね！", MyOrientation.TOP).start()
                setArrow(MyOrientation.TOP,createCardNavFlipNext)
                goNextOnClickAnyWhere()
            }
            fun explainCreateCardNavigation5(){
                setArrow(MyOrientation.TOP,createCardNavFlipPrevious)
                goNextOnClickAnyWhere()
            }
            fun goodBye1(){
                setPosition(onInstallBinding.root,character, MyOrientation.MIDDLE,0,false)
                explainTextAnimation("これでガイドは終わりだよ", MyOrientation.TOP).start()
                appearAlphaAnimation(arrayOf(character),true).start()
                goNextOnClickAnyWhere()
            }
            fun goodBye2(){
                explainTextAnimation("KiNaを楽しんで！", MyOrientation.TOP).start()
                appearAlphaAnimation(arrayOf(character),false).start()
                goNextOnClickAnyWhere()
            }
            fun end(){
                appearAlphaAnimation(arrayOf(frameLayCallOnInstall),false).start()
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
                23  ->  goodBye1()
                24  ->  goodBye2()
                25  ->  end()
                else->  return
            }
        }
        guideInOrder(startOrder)

    }
    fun editGuide(startOrder:Int){
        fun guideInOrder(order:Int){

            fun greeting(){
                appearAlphaAnimation(arrayOf(character),true)
            }
            when(order){
                0 -> greeting()
            }
        }
    }
}
