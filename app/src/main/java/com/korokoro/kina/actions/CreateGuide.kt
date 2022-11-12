package com.korokoro.kina.actions

import android.animation.AnimatorSet
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
            characterPositionData.borderSet = value
            setPos(characterPositionData)
        }
    private var characterOrientation:MyOrientationSetNew =
        MyOrientationSetNew(MyVerticalOrientation.MIDDLE,
            MyHorizontalOrientation.MIDDLE,
            BorderAttributes.FillIfOutOfBorder)
        set(value) {
            value.borderAttributes = BorderAttributes.FillIfOutOfBorder
            field = value
            characterPositionData.orientation = value
            setPos(characterPositionData)
        }
    private val characterPositionData :ViewAndPositionData = ViewAndPositionData(actions.character,characterBorderSet,characterOrientation)
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
    private fun setPos(data: ViewAndPositionData){
        actions.setPositionByMargin(data,globalLayoutSet)
    }

    private var textPosData:ViewAndSide = ViewAndSide(actions.character,MyOrientation.TOP)
    private var textFit:Boolean = false

    private fun explainTextAnimation(string: String):AnimatorSet{
        return actions.explainTextAnimation(string= string,textPosData.side,textPosData.view,textFit,globalLayoutSet)
    }
    private val arrowMargin = 20
    private fun setArrow(arrowPosition: MyOrientation,view: View){
        actions.setArrow(arrowPosition,view,globalLayoutSet,arrowMargin)
    }
    private fun addTouchArea(view: View):View{
        return actions.copyViewInConLay(view,borderDataMap,globalLayoutSet)
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

            fun deletePreviousTouchAreas(){
                onInstallBinding.root.children.iterator().forEach {
                    if(it.tag == 1) changeViewVisibility(it,false)
                }
            }


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
                actions.setUpFirstView(globalLayoutSet)
                explainTextAnimation("やあ、僕はとさかくん").start()
                goNextOnClickAnyWhere()
            }
            fun greeting2(){
                explainTextAnimation("これから、KiNaの使い方を説明するね").start()
                goNextOnClickAnyWhere()
            }
            fun createFlashCard0(){
                explainTextAnimation("KiNaでは、フォルダと単語帳が作れるよ").start()
                goNextOnClickAnyWhere()
            }
            fun createFlashCard1prt1(){
                explainTextAnimation("ボタンをタッチして、\n単語帳を作ってみよう" ).start()
                goNextOnClickAnyWhere()
            }
            fun createFlashCard1prt2(){
                viewUnderHole = bnvBtnAdd
                setArrow(MyOrientation.TOP,bnvBtnAdd)
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
                    return
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


                goNextOnClickAnyWhere()
            }
            fun checkInsideNewFlashCard1(){
                characterOrientation = MyOrientationSetNew(MyVerticalOrientation.BOTTOM,MyHorizontalOrientation.LEFT)
                actions.changeCharacterVisibility(true).start()
                textPosData = ViewAndSide(actions.character,MyOrientation.RIGHT)
                textFit = true

                actions.appearAlphaAnimation(actions.conLaySpeakBubble,true).start()
                explainTextAnimation("おめでとう！単語帳が追加されたよ\n中身を見てみよう！",
                ).start()
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
                explainTextAnimation("まだカードがないね\n早速作ってみよう",
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
                actions.appearAlphaAnimation(actions.character,true).start()
                explainTextAnimation("上半分は、カードの表" ).start()
                createAnimateHole = true
                viewUnderHole = edtCardFrontContent
                goNextOnClickAnyWhere()
            }
            fun explainCreateCardFrag2(){
                explainTextAnimation("下半分は、カードの裏になっているよ" )
                viewUnderHole = edtCardFrontContent
                goNextOnClickAnyWhere()

            }
            fun explainCreateCardFrag3(){
                characterBorderSet = BorderSet(bottomSideSet = ViewAndSide(edtCardBackTitle,MyOrientation.TOP))
                viewUnderHole = edtCardFrontTitle
                explainTextAnimation("カードの裏表にタイトルを付けることもできるんだ！").start()
                goNextOnClickAnyWhere()
            }
            fun explainCreateCardFrag4(){
                viewUnderHole = edtCardBackTitle
                textPosData = ViewAndSide(edtCardBackTitle,MyOrientation.TOP)
                textFit = false
                explainTextAnimation("好みのようにカスタマイズしてね").start()
                goNextOnClickAnyWhere()
            }
            fun explainCreateCardNavigation1(){
                characterBorderSet = BorderSet(bottomSideSet = ViewAndSide(edtCardBackContent,MyOrientation.TOP))
                characterOrientation = MyOrientationSetNew(MyVerticalOrientation.BOTTOM,MyHorizontalOrientation.MIDDLE)
                explainTextAnimation("カードをめくるには、\n下のナビゲーションボタンを使うよ" ).start()
                viewUnderHole = linLayCreateCardNavigation
                goNextOnClickAnyWhere()
            }
            fun explainCreateCardNavigation2(){
                explainTextAnimation("新しいカードを前に挿入するのはここ").start()
                setArrow(MyOrientation.TOP,createCardInsertNext)
                goNextOnClickAnyWhere()
            }
            fun explainCreateCardNavigation3(){
                explainTextAnimation("後ろに挿入するのはここ！").start()
                setArrow(MyOrientation.TOP,createCardInsertPrevious)
                goNextOnClickAnyWhere()
            }
            fun explainCreateCardNavigation4(){
                explainTextAnimation("矢印ボタンでカードを前後にめくってね！" ).start()
                setArrow(MyOrientation.TOP, createCardNavFlipNext)
                goNextOnClickAnyWhere()
            }
            fun explainCreateCardNavigation5(){
                actions.appearAlphaAnimation(actions.character,false).start()
                actions.changeArrowVisibility(false).start()
                goNextOnClickAnyWhere()
                setArrow(MyOrientation.TOP, createCardNavFlipPrevious)
            }
            fun goodBye1(){
                characterOrientation = MyOrientationSetNew(MyVerticalOrientation.MIDDLE,MyHorizontalOrientation.MIDDLE)
                explainTextAnimation("これでガイドは終わりだよ").start()
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


            deletePreviousTouchAreas()
            when(order){
                1   ->  greeting1()
                2   ->  greeting2()
                3   ->  createFlashCard1prt1()
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
