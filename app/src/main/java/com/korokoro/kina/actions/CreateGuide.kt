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
import com.korokoro.kina.customClasses.*
import com.korokoro.kina.databinding.CallOnInstallBinding
import com.korokoro.kina.db.enumclass.FileStatus
import com.korokoro.kina.ui.customViews.*
import com.korokoro.kina.ui.viewmodel.*


class CreateGuide(val activity:AppCompatActivity,
                  private val onInstallBinding: CallOnInstallBinding){
    val actions = InstallGuide(activity,onInstallBinding)
    private val borderDataMap = mutableMapOf<View,BorderSet>()
    private val globalLayoutSet = mutableMapOf<View, ViewTreeObserver.OnGlobalLayoutListener>()

    fun removeAllGlobalLayoutSet(){
        globalLayoutSet.onEach {
            it.key.viewTreeObserver.removeOnGlobalLayoutListener(it.value)
        }
    }



    private var characterBorderSet:BorderSet = BorderSet()
        set(value) {
            field = value
        }
    private var characterOrientation:MyOrientationSetNew =
        MyOrientationSetNew(MyVerticalOrientation.MIDDLE,
            MyHorizontalOrientation.MIDDLE,
            BorderAttributes.FillIfOutOfBorder)
        set(value) {
            value.borderAttributes = BorderAttributes.FillIfOutOfBorder
            field = value

        }
    private var createHoleShape:HoleShape = HoleShape.CIRCLE
    private var createAnimateHole :Boolean = true

    private var viewUnderHole:View? = null
        set(value) {
            field = value
            actions.holeView.apply {
                holeShape = createHoleShape
                animate = createAnimateHole
                viewUnderHole = value
            }

        }
    private fun setCharacterPos(){
        characterOrientation.borderAttributes = BorderAttributes.FillIfOutOfBorder
        setPos(ViewAndPositionData(actions.character,characterBorderSet,characterOrientation))
    }
    private fun setPos(data: ViewAndPositionData){
        actions.setPositionByMargin(data,globalLayoutSet)
    }

    private var speakBubbleMargin:MyMargin = MyMargin()
        set(value) {
            field = value
            spbBorderSet.margin = value
        }
    private var textPosData:ViewAndSide = ViewAndSide(actions.character,MyOrientation.TOP)
    set(value) {
        field = value
        spbBorderSet = ViewChangeActions().getSimpleBorderSet(value.view,value.side,textFit)
        spbOrientation = ViewChangeActions().getOriSetByNextToPosition(value.side,BorderAttributes.FillIfOutOfBorder)
    }
    private var spbBorderSet:BorderSet = BorderSet(bottomSideSet = ViewAndSide(actions.character,MyOrientation.TOP))
    private var spbOrientation:MyOrientationSetNew = MyOrientationSetNew(MyVerticalOrientation.BOTTOM,MyHorizontalOrientation.MIDDLE)

    private var textFit:Boolean = false

    private var spbAppearOnEnd = true
    private fun setTextPos(string: String):ValueAnimator{

        val anim = actions.changeSpeakBubbleVisibility(false).apply {
            doOnEnd {
                changeViewVisibility(actions.conLaySpeakBubble,false)
                actions.textView.text = string
                val posData = ViewAndPositionData(
                    actions.conLaySpeakBubble,
                    borderSet = spbBorderSet ,
                    orientation= spbOrientation)
                setPos(posData)
                if(spbAppearOnEnd) actions.speakBubbleTextAnimation().start()
            }
            duration = 100
        }

        return anim

    }
    private val arrowMargin = 0
    private fun setArrow(arrowPosition: MyOrientation,view: View){
        actions.setArrow(arrowPosition,view,globalLayoutSet,arrowMargin)
    }
    private fun addTouchArea(view: View):View{
        return actions.copyViewInConLay(view,borderDataMap,globalLayoutSet)
    }
    private fun cloneView(view: View) {
        addTouchArea(view).setOnClickListener {
            view.callOnClick()
        }
    }

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



            fun greeting1(){
                actions.setUpFirstView(globalLayoutSet)
                setTextPos("やあ、僕はとさかくん").start()
                actions.changeSpeakBubbleVisibility(true).start()
                goNextOnClickAnyWhere()
            }
            fun greeting2(){
                setTextPos("これから、KiNaの使い方を説明するね").start()
                goNextOnClickAnyWhere()
            }
            fun createFlashCard0(){
                setTextPos("KiNaでは、フォルダと単語帳が作れるよ").start()
                goNextOnClickAnyWhere()
            }
            fun createFlashCard1prt1(){
                setTextPos("ボタンをタッチして、\n単語帳を作ってみよう" ).start()
                goNextOnClickAnyWhere()
            }
            fun createFlashCard1prt2(){
                actions.changeArrowVisibility(true).start()
                setArrow(MyOrientation.TOP,bnvBtnAdd)
                viewUnderHole = bnvBtnAdd
                goNextOnClickTouchArea(bnvBtnAdd)
            }
            fun createFlashCard2(){
                setArrow(MyOrientation.TOP,createMenuImvFlashCard)
                createAnimateHole = false
                viewUnderHole = createMenuImvFlashCard
                createFileViewModel.setBottomMenuVisible(true)
                goNextOnClickTouchArea(createMenuImvFlashCard)
            }
            fun createFlashCard3(){
                AnimatorSet().apply {
                    playTogether(
                        actions.appearAlphaAnimation(actions.character, false),
                        actions.appearAlphaAnimation(actions.conLaySpeakBubble, false),
                    )
                    doOnEnd {
                        goNextOnClickTouchArea(btnFinish)
                        createFileViewModel.onClickCreateFile(FileStatus.FLASHCARD_COVER)
                        createHoleShape = HoleShape.RECTANGLE
                        viewUnderHole = frameLayEditFile
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

                }
                actions.removeHole()
                val lastId = libraryRv.size
                var newLastId:Int
                hideKeyBoard(edtCreatingFileTitle,activity)
                onInstallBinding.root.children.iterator().forEach {
                    if(it.tag == 1)it.visibility = View.GONE
                }
                actions.makeTouchAreaGone()
                actions.changeArrowVisibility(false).start()
                createFileViewModel.makeFileInGuide(title)
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
                createAnimateHole = true
                viewUnderHole = libraryRv[0]
                characterOrientation = MyOrientationSetNew(MyVerticalOrientation.BOTTOM,MyHorizontalOrientation.LEFT)
                actions.setCharacterSize(R.dimen.character_size_middle)
                setCharacterPos()

                goNextOnClickAnyWhere()
            }
            fun checkInsideNewFlashCard1(){

                actions.changeCharacterVisibility(true).start()
                textPosData = ViewAndSide(actions.character,MyOrientation.RIGHT)
                spbOrientation = MyOrientationSetNew(MyVerticalOrientation.BOTTOM,MyHorizontalOrientation.MIDDLE)

                setTextPos("おめでとう！単語帳が追加されたよ\n中身を見てみよう！",).start()

                 goNextOnClickTouchArea(libraryRv[0])
            }
            fun checkInsideNewFlashCard2(){
                actions.changeSpeakBubbleVisibility(false).start()
                actions.makeTouchAreaGone()
                actions.removeHole()
                changeViewVisibility(actions.holeView,true)
                libraryViewModel.openNextFile(createFileViewModel.returnLastInsertedFile()!!)
                goNextOnClickAnyWhere()
            }
            fun checkInsideNewFlashCard3(){
                setTextPos("まだカードがないね\n早速作ってみよう",
                ).start()
                goNextOnClickAnyWhere()
            }
            fun makeNewCard1(){
                actions.appearAlphaAnimation(actions.character,false).start()
                actions.changeSpeakBubbleVisibility(false).start()
                viewUnderHole = bnvBtnAdd
                goNextOnClickTouchArea(bnvBtnAdd)
            }
            fun makeNewCard2(){
                createAnimateHole = false
                viewUnderHole = createMenuImvNewCard
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
                characterBorderSet = actions.getSimplePosRelation(edtCardFrontContent,MyOrientation.TOP,false)
                setCharacterPos()
                actions.appearAlphaAnimation(actions.character,true).start()
                textFit = true
                textPosData = ViewAndSide(actions.character,MyOrientation.RIGHT)
                setTextPos("上半分は、カードの表" ).start()
                createAnimateHole = true
                viewUnderHole = edtCardFrontContent
                goNextOnClickAnyWhere()
            }
            fun explainCreateCardFrag2(){
                setTextPos("下半分は、カードの裏になっているよ" ).start()
                viewUnderHole = edtCardBackContent
                goNextOnClickAnyWhere()

            }
            fun explainCreateCardFrag3(){
                characterBorderSet = BorderSet(topSideSet = ViewAndSide(edtCardFrontTitle,MyOrientation.BOTTOM),
                    bottomSideSet = ViewAndSide(edtCardBackTitle,MyOrientation.TOP) )
                characterOrientation = MyOrientationSetNew(MyVerticalOrientation.TOP,MyHorizontalOrientation.LEFT)
                setCharacterPos()
                textFit = false
                viewUnderHole = edtCardFrontTitle
                setTextPos("カードの裏表にタイトルを付けることもできるんだ！").start()
                goNextOnClickAnyWhere()
            }
            fun explainCreateCardFrag4(){
                viewUnderHole = edtCardBackTitle
                characterOrientation = MyOrientationSetNew(MyVerticalOrientation.BOTTOM,MyHorizontalOrientation.LEFT)
                setCharacterPos()
                spbBorderSet = BorderSet(bottomSideSet = ViewAndSide(actions.character,MyOrientation.BOTTOM)
                    , leftSideSet = ViewAndSide(actions.character,MyOrientation.RIGHT))
                spbOrientation = MyOrientationSetNew(MyVerticalOrientation.BOTTOM,MyHorizontalOrientation.MIDDLE)
                setTextPos("好みのようにカスタマイズしてね").start()
                goNextOnClickAnyWhere()
            }
            fun explainCreateCardNavigation1(){
                characterBorderSet = BorderSet(bottomSideSet = ViewAndSide(edtCardBackContent,MyOrientation.TOP))
                characterOrientation = MyOrientationSetNew(MyVerticalOrientation.BOTTOM,MyHorizontalOrientation.MIDDLE)
                setTextPos("カードをめくるには、\n下のナビゲーションボタンを使うよ" ).start()
                viewUnderHole = linLayCreateCardNavigation
                goNextOnClickAnyWhere()
            }
            fun explainCreateCardNavigation2(){
                setTextPos("新しいカードを前に挿入するのはここ").start()
                setArrow(MyOrientation.TOP,createCardInsertNext)
                goNextOnClickAnyWhere()
            }
            fun explainCreateCardNavigation3(){
                setTextPos("後ろに挿入するのはここ！").start()
                setArrow(MyOrientation.TOP,createCardInsertPrevious)
                goNextOnClickAnyWhere()
            }
            fun explainCreateCardNavigation4(){
                setTextPos("矢印ボタンでカードを前後にめくってね！" ).start()
                setArrow(MyOrientation.TOP, createCardNavFlipNext)
                goNextOnClickAnyWhere()
            }
            fun explainCreateCardNavigation5(){
                setArrow(MyOrientation.TOP, createCardNavFlipPrevious)
                goNextOnClickAnyWhere()
            }
            fun goodBye1(){
                actions.removeHole()
                actions.changeArrowVisibility(false).start()
                characterOrientation = MyOrientationSetNew(MyVerticalOrientation.MIDDLE,MyHorizontalOrientation.MIDDLE)
                setCharacterPos()
                setTextPos("これでガイドは終わりだよ").start()
                actions.appearAlphaAnimation(actions.character,true).start()
                goNextOnClickAnyWhere()
            }
            fun goodBye2(){
                setTextPos("KiNaを楽しんで！").start()
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
                3   ->  createFlashCard0()
                4   ->  createFlashCard1prt1()
                5   ->  createFlashCard1prt2()
                6   ->  createFlashCard2()
                7   ->  createFlashCard3()
                8   ->  createFlashCard5()
                9   ->  createFlashCard6()
                10  -> checkInsideNewFlashCard1()
                11  -> checkInsideNewFlashCard2()
                12  -> checkInsideNewFlashCard3()
                13  -> makeNewCard1()
                14  -> makeNewCard2()
                15  -> makeNewCard3()
                16  -> explainCreateCardFrag1()
                17  -> explainCreateCardFrag2()
                18  -> explainCreateCardFrag3()
                19  -> explainCreateCardFrag4()
                20  -> explainCreateCardNavigation1()
                21  -> explainCreateCardNavigation2()
                22  -> explainCreateCardNavigation3()
                23  -> explainCreateCardNavigation4()
                24  -> explainCreateCardNavigation5()
                25  -> goodBye1()
                26  -> goodBye2()
                27  ->end()
                else->  return
            }
        }

        guideInOrder(startOrder)

    }
}
