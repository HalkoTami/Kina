package com.korokoro.kina.actions

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.view.View
import android.view.ViewTreeObserver
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.doOnEnd
import androidx.core.view.children
import androidx.core.view.get
import androidx.core.view.size
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import com.korokoro.kina.R
import com.korokoro.kina.activity.MainActivity
import com.korokoro.kina.customClasses.enumClasses.BorderAttributes
import com.korokoro.kina.customClasses.enumClasses.MyHorizontalOrientation
import com.korokoro.kina.customClasses.enumClasses.MyOrientation
import com.korokoro.kina.customClasses.enumClasses.MyVerticalOrientation
import com.korokoro.kina.customClasses.normalClasses.BorderSet
import com.korokoro.kina.customClasses.normalClasses.MyOrientationSet
import com.korokoro.kina.customClasses.normalClasses.ViewAndPositionData
import com.korokoro.kina.customClasses.normalClasses.ViewAndSide
import com.korokoro.kina.databinding.CallOnInstallBinding
import com.korokoro.kina.db.enumclass.FileStatus
import com.korokoro.kina.ui.customViews.*
import com.korokoro.kina.ui.viewmodel.*


class CreateGuide(val activity:MainActivity,
                  private val frameLay:FrameLayout, ){
    private val onInstallBinding = activity.callOnInstallBinding
    private val createFileViewModel = activity.createFileViewModel
    private val mainViewModel = activity.mainActivityViewModel
    private val libraryViewModel = activity.libraryViewModel
    private val createCardViewModel = activity.createCardViewModel

    val actions = InstallGuide(activity,onInstallBinding)
    private val borderDataMap = mutableMapOf<View, BorderSet>()
//    private val globalLayoutSet = mutableMapOf<View, ViewTreeObserver.OnGlobalLayoutListener>()
//    private var characterBorderSet: BorderSet = BorderSet()
//    private var characterOrientation: MyOrientationSet =
//        MyOrientationSet(MyVerticalOrientation.MIDDLE,
//            MyHorizontalOrientation.MIDDLE,
//            BorderAttributes.FillIfOutOfBorder)
//        set(value) {
//            value.borderAttributes = BorderAttributes.FillIfOutOfBorder
//            field = value
//
//        }
//    private var createHoleShape:HoleShape = HoleShape.CIRCLE
//    private var createAnimateHole :Boolean = true
//    private var viewUnderHole:View? = null
//        set(value) {
//            field = value
//            actions.holeView.apply {
//                holeShape = createHoleShape
//                animate = createAnimateHole
//                viewUnderHole = value
//            }
//
//        }
    private fun setCharacterPos(){
        actions.characterOrientation.borderAttributes = BorderAttributes.FillIfOutOfBorder
        setPos(ViewAndPositionData(actions.character,actions.characterBorderSet,actions.characterOrientation))
    }
    private fun setPos(data: ViewAndPositionData){
        actions.setPositionByMargin(data,actions. globalLayoutSet)
    }
    private var freshCreated = true
//    private var textPosData: ViewAndSide = ViewAndSide(actions.character, MyOrientation.TOP)
//    set(value) {
//        field = value
//        spbBorderSet = ViewChangeActions().getSimpleBorderSet(value.view,value.side,textFit)
//        spbOrientation = ViewChangeActions().getOriSetByNextToPosition(value.side, BorderAttributes.FillIfOutOfBorder)
//    }
//    private var spbBorderSet: BorderSet = BorderSet(bottomSideSet = ViewAndSide(actions.character,
//        MyOrientation.TOP))
//    private var spbOrientation: MyOrientationSet = MyOrientationSet(MyVerticalOrientation.BOTTOM,
//        MyHorizontalOrientation.MIDDLE,
//        BorderAttributes.FillIfOutOfBorder)
//    private var textFit:Boolean = false
//    private var spbAppearOnEnd = true
    private fun setTextPos(string: String):ValueAnimator{

        val anim = actions.changeSpeakBubbleVisibility(false).apply {
            doOnEnd {
                changeViewVisibility(actions.conLaySpeakBubble,false)
                actions.textView.text = string
                val posData = ViewAndPositionData(
                    actions.conLaySpeakBubble,
                    borderSet = actions.spbBorderSet ,
                    orientation=actions. spbOrientation)
                setPos(posData)
                if(actions.spbAppearOnEnd) actions.speakBubbleTextAnimation().start()
            }
            duration = 100
        }

        return anim

    }
    private val arrowMargin = 0
    private fun setArrow(arrowPosition: MyOrientation, view: View){
        actions.setArrow(arrowPosition,view,actions.globalLayoutSet,arrowMargin)
    }
    private fun addTouchArea(view: View):View{
        return actions.copyViewInConLay(view,borderDataMap,actions.globalLayoutSet)
    }
    private fun cloneView(view: View) {
        addTouchArea(view).setOnClickListener {
            view.callOnClick()
        }
    }

    private fun goNextOnClickAnyWhere(func:()->Unit){
        actions.goNextOnClickAnyWhere { func() }
    }
    private fun goNextOnClickTouchArea(view: View, func: () -> Unit) {
        onInstallBinding.root.setOnClickListener(null)
        addTouchArea(view).setOnClickListener {
            actions.makeTouchAreaGone()
            func()
        }
    }

    private fun greeting1(){
        frameLay.removeAllViews()
        frameLay.addView(onInstallBinding.root)

        actions.setUpFirstView(actions.globalLayoutSet)
        setTextPos("やあ、僕はとさかくん").start()
        goNextOnClickAnyWhere { greeting2() }
    }
    private fun greeting2(){
        setTextPos("これから、KiNaの使い方を説明するね").start()
        goNextOnClickAnyWhere{createFlashCard0()}
    }
    private fun createFlashCard0(){
        setTextPos("KiNaでは、フォルダと単語帳が作れるよ").start()
        goNextOnClickAnyWhere{createFlashCard1prt1()}
    }
    private fun createFlashCard1prt1(){
        setTextPos("ボタンをタッチして、\n単語帳を作ってみよう" ).start()
        goNextOnClickAnyWhere{createFlashCard1prt2()}
    }
    private fun createFlashCard1prt2(){
        val bnvBtnAdd=activity.findViewById<ImageView>(R.id.bnv_imv_add)
        actions.changeArrowVisibility(true).start()
        setArrow(MyOrientation.TOP,bnvBtnAdd)
        actions.viewUnderHole = bnvBtnAdd
        goNextOnClickTouchArea(bnvBtnAdd){createFlashCard2()}
    }
    private fun createFlashCard2(){
        val createMenuImvFlashCard      =activity.findViewById<FrameLayout>(R.id.imvnewTangocho)
        setArrow(MyOrientation.TOP,createMenuImvFlashCard)
        actions.createAnimateHole = false
        actions.viewUnderHole = createMenuImvFlashCard
        activity.createFileViewModel.setBottomMenuVisible(true)
        goNextOnClickTouchArea(createMenuImvFlashCard){createFlashCard3()}
    }
    private fun createFlashCard3(){
        val btnFinish                   =activity.findViewById<Button>(R.id.btn_finish)
        val frameLayEditFile            =activity.findViewById<FrameLayout>(R.id.frameLay_edit_file)
        val edtCreatingFileTitle        =activity.findViewById<EditText>(R.id.edt_file_title)
        val imvColPalRed                =activity.findViewById<ImageView>(R.id.imv_col_red)
        val imvColPalBlue               =activity.findViewById<ImageView>(R.id.imv_col_blue)
        val imvColPaYellow              =activity.findViewById<ImageView>(R.id.imv_col_yellow)
        val imvColPalGray               =activity.findViewById<ImageView>(R.id.imv_col_gray)
        AnimatorSet().apply {
            playTogether(
                actions.appearAlphaAnimation(actions.character, false),
                actions.appearAlphaAnimation(actions.conLaySpeakBubble, false),
            )
            doOnEnd {
                goNextOnClickTouchArea(btnFinish){createFlashCard5(edtCreatingFileTitle)}
                createFileViewModel.onClickCreateFile(FileStatus.FLASHCARD_COVER)
                actions.createHoleShape = HoleShape.RECTANGLE
                actions.viewUnderHole = frameLayEditFile
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

    private fun createFlashCard5(edtCreatingFileTitle:EditText){
        val libraryRv                   =activity.findViewById<RecyclerView>(R.id.vocabCardRV)
        val title = edtCreatingFileTitle.text.toString()
        if(title == "") {
            makeToast(activity,"タイトルが必要です")

        }
        actions.removeHole()
        hideKeyBoard(edtCreatingFileTitle,activity)
        onInstallBinding.root.children.iterator().forEach {
            if(it.tag == 1)it.visibility = View.GONE
        }
        actions.makeTouchAreaGone()
        actions.changeArrowVisibility(false).start()
        createFileViewModel.makeFileInGuide(title)
        goNextOnClickAnyWhere {
            createFlashCard6(libraryRv)
        }


    }
    private fun createFlashCard6(libraryRv:RecyclerView){
        actions.createAnimateHole = true
        actions.viewUnderHole = libraryRv[0]
        actions.characterOrientation = MyOrientationSet(MyVerticalOrientation.BOTTOM,
            MyHorizontalOrientation.LEFT)
        actions.setCharacterSize(R.dimen.character_size_middle)
        setCharacterPos()

        goNextOnClickAnyWhere{checkInsideNewFlashCard1(libraryRv)}
    }
    private fun checkInsideNewFlashCard1(libraryRv: RecyclerView){

        actions.changeCharacterVisibility(true).start()
        actions.textPosData = ViewAndSide(actions.character, MyOrientation.RIGHT)
        actions.spbOrientation = MyOrientationSet(MyVerticalOrientation.BOTTOM, MyHorizontalOrientation.MIDDLE)

        setTextPos("おめでとう！単語帳が追加されたよ\n中身を見てみよう！").start()

        goNextOnClickTouchArea(libraryRv[0]){checkInsideNewFlashCard2()}
    }
    private fun checkInsideNewFlashCard2(){
        actions.changeSpeakBubbleVisibility(false).start()
        actions.makeTouchAreaGone()
        actions.removeHole()
        changeViewVisibility(actions.holeView,true)
        libraryViewModel.openNextFile(createFileViewModel.returnLastInsertedFile()!!)
        goNextOnClickAnyWhere{checkInsideNewFlashCard3()}
    }
    private fun checkInsideNewFlashCard3(){
        setTextPos("まだカードがないね\n早速作ってみよう",
        ).start()
        goNextOnClickAnyWhere{makeNewCard1()}
    }
    private fun makeNewCard1(){
        val bnvBtnAdd=activity.findViewById<ImageView>(R.id.bnv_imv_add)
        actions.appearAlphaAnimation(actions.character,false).start()
        actions.changeSpeakBubbleVisibility(false).start()
        actions.viewUnderHole = bnvBtnAdd
        goNextOnClickTouchArea(bnvBtnAdd){makeNewCard2()}
    }
    private fun makeNewCard2(){
        val createMenuImvNewCard        =activity.findViewById<FrameLayout>(R.id.imvnewCard)
        actions.createAnimateHole = false
        actions.viewUnderHole = createMenuImvNewCard
        createFileViewModel.setBottomMenuVisible(true)
        goNextOnClickTouchArea(createMenuImvNewCard){makeNewCard3()}
    }
    private fun makeNewCard3(){
        createFileViewModel.setBottomMenuVisible(false)
        actions.makeTouchAreaGone()
        actions.removeHole()
        createCardViewModel.onClickAddNewCardBottomBar()
        goNextOnClickAnyWhere{explainCreateCardFrag1()}
    }
    private fun explainCreateCardFrag1(){
        val edtCardFrontContent         =activity.findViewById<EditText>(R.id.edt_front_content)
        actions.characterBorderSet = actions.getSimplePosRelation(edtCardFrontContent,
            MyOrientation.TOP,false)
        setCharacterPos()
        actions.appearAlphaAnimation(actions.character,true).start()
        actions.textFit = true
        actions.textPosData = ViewAndSide(actions.character, MyOrientation.RIGHT)
        setTextPos("上半分は、カードの表" ).start()
        actions.createAnimateHole = true
        actions.viewUnderHole = edtCardFrontContent
        goNextOnClickAnyWhere{explainCreateCardFrag2()}
    }
    private fun explainCreateCardFrag2(){
        val edtCardBackContent          =activity.findViewById<EditText>(R.id.edt_back_content)
        setTextPos("下半分は、カードの裏になっているよ" ).start()
        actions.viewUnderHole = edtCardBackContent
        goNextOnClickAnyWhere{explainCreateCardFrag3()}

    }
    private fun explainCreateCardFrag3(){
        val edtCardFrontTitle           =activity.findViewById<EditText>(R.id.edt_front_title)
        val edtCardBackTitle            =activity.findViewById<EditText>(R.id.edt_back_title)
        actions.characterBorderSet = BorderSet(topSideSet = ViewAndSide(edtCardFrontTitle, MyOrientation.BOTTOM),
            bottomSideSet = ViewAndSide(edtCardBackTitle, MyOrientation.TOP) )
        actions.characterOrientation = MyOrientationSet(MyVerticalOrientation.TOP,
            MyHorizontalOrientation.LEFT)
        setCharacterPos()
        actions.textFit = false
        actions.viewUnderHole = edtCardFrontTitle
        setTextPos("カードの裏表にタイトルを付けることもできるんだ！").start()
        goNextOnClickAnyWhere{explainCreateCardFrag4(edtCardBackTitle)}
    }
    private fun explainCreateCardFrag4(edtCardBackTitle:EditText){
        actions.viewUnderHole = edtCardBackTitle
        actions.characterOrientation = MyOrientationSet(MyVerticalOrientation.BOTTOM,
            MyHorizontalOrientation.LEFT)
        setCharacterPos()
        actions.spbBorderSet = BorderSet(bottomSideSet = ViewAndSide(actions.character, MyOrientation.BOTTOM)
            , leftSideSet = ViewAndSide(actions.character, MyOrientation.RIGHT))
        actions.spbOrientation = MyOrientationSet(MyVerticalOrientation.BOTTOM, MyHorizontalOrientation.MIDDLE)
        setTextPos("好みのようにカスタマイズしてね").start()
        goNextOnClickAnyWhere{explainCreateCardNavigation1()}
    }
    private fun explainCreateCardNavigation1(){
        val edtCardBackContent          =activity.findViewById<EditText>(R.id.edt_back_content)
        val linLayCreateCardNavigation  =activity.findViewById<ConstraintLayout>(R.id.lay_navigate_buttons)
        actions.characterBorderSet = BorderSet(bottomSideSet = ViewAndSide(edtCardBackContent,
            MyOrientation.TOP))
        actions.characterOrientation = MyOrientationSet(MyVerticalOrientation.BOTTOM,
            MyHorizontalOrientation.MIDDLE)
        setTextPos("カードをめくるには、\n下のナビゲーションボタンを使うよ" ).start()
        actions.viewUnderHole = linLayCreateCardNavigation
        goNextOnClickAnyWhere{explainCreateCardNavigation2()}
    }
    private fun explainCreateCardNavigation2(){
        val createCardInsertNext        =activity.findViewById<ImageView>(R.id.btn_insert_next)
        setTextPos("新しいカードを前に挿入するのはここ").start()
        setArrow(MyOrientation.TOP,createCardInsertNext)
        goNextOnClickAnyWhere{explainCreateCardNavigation3()}
    }
    private fun explainCreateCardNavigation3(){
        val createCardInsertPrevious    =activity.findViewById<ImageView>(R.id.btn_insert_previous)
        setTextPos("後ろに挿入するのはここ！").start()
        setArrow(MyOrientation.TOP,createCardInsertPrevious)
        goNextOnClickAnyWhere{explainCreateCardNavigation4()}
    }
    private fun explainCreateCardNavigation4(){
        val createCardNavFlipNext       =activity.findViewById<NavigateBtnCreateCard>(R.id.btn_next)
        setTextPos("矢印ボタンでカードを前後にめくってね！" ).start()
        setArrow(MyOrientation.TOP, createCardNavFlipNext)
        goNextOnClickAnyWhere{explainCreateCardNavigation5()}
    }
    private fun explainCreateCardNavigation5(){
        val createCardNavFlipPrevious   =activity.findViewById<NavigateBtnCreateCard>(R.id.btn_previous)
        setArrow(MyOrientation.TOP, createCardNavFlipPrevious)
        goNextOnClickAnyWhere{goodBye1()}
    }
    private fun goodBye1(){
        actions.removeHole()
        actions.changeArrowVisibility(false).start()
        actions.characterBorderSet = BorderSet()
        actions.characterOrientation = MyOrientationSet(MyVerticalOrientation.MIDDLE,
            MyHorizontalOrientation.MIDDLE)
        setCharacterPos()
        actions.textPosData = ViewAndSide(actions.character, MyOrientation.TOP)
        setTextPos("これでガイドは終わりだよ").start()
        actions.appearAlphaAnimation(actions.character,true).start()
        goNextOnClickAnyWhere{goodBye2()}
    }
    private fun goodBye2(){
        setTextPos("KiNaを楽しんで！").start()
        goNextOnClickAnyWhere{end()}
    }
    private fun end(){
        mainViewModel.setGuideVisibility(false)
        val sharedPref =  activity.getSharedPreferences(
            "firstTimeGuide", Context.MODE_PRIVATE) ?: return
        val editor = sharedPref.edit()
        editor.putBoolean("firstTimeGuide", true)
        editor.apply()
    }
    fun callOnFirst(){
        mainViewModel.setGuideVisibility(true)
        freshCreated = false
        actions.makeTouchAreaGone()
        greeting1()

    }
}
