package com.koronnu.kina.actions

import android.animation.AnimatorSet
import android.content.Context
import android.view.View
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.doOnEnd
import androidx.core.view.children
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import com.koronnu.kina.R
import com.koronnu.kina.activity.MainActivity
import com.koronnu.kina.customClasses.enumClasses.HoleShape
import com.koronnu.kina.customClasses.enumClasses.MyHorizontalOrientation
import com.koronnu.kina.customClasses.enumClasses.MyOrientation
import com.koronnu.kina.customClasses.enumClasses.MyVerticalOrientation
import com.koronnu.kina.customClasses.normalClasses.BorderSet
import com.koronnu.kina.customClasses.normalClasses.MyOrientationSet
import com.koronnu.kina.customClasses.normalClasses.ViewAndSide
import com.koronnu.kina.db.enumclass.FileStatus
import com.koronnu.kina.ui.customViews.*


class CreateGuide(val activity:MainActivity,
                  frameLay:FrameLayout, ){
    private val onInstallBinding = activity.callOnInstallBinding
    private val createFileViewModel = activity.createFileViewModel
    private val mainViewModel = activity.mainActivityViewModel
    private val libraryViewModel = activity.libraryViewModel
    private val createCardViewModel = activity.createCardViewModel

    private val actions = InstallGuide(activity,frameLay)

    private fun goNextOnClickTouchArea(view: View, func: () -> Unit) {
        onInstallBinding.root.setOnClickListener(null)
        actions.addViewToConLay(view).setOnClickListener {
            actions.makeTouchAreaGone()
            func()
        }
    }

    private fun greeting1(){
        actions.apply {
            onInstallBinding.root.isClickable = false
            callOnFirst()
            animateSpbPos("やあ、僕はとさかくん").start()
            goNextOnClickAnyWhere { greeting2() }
        }
    }
    private fun greeting2(){
        actions.apply {
            animateSpbPos("これから、KiNaの使い方を説明するね").start()
            goNextOnClickAnyWhere{createFlashCard0()}
        }

    }
    private fun createFlashCard0(){
        actions.apply {
            animateSpbPos("KiNaでは、フォルダと単語帳が作れるよ").start()
            goNextOnClickAnyWhere{createFlashCard1prt1()}
        }
    }
    private fun createFlashCard1prt1(){
        actions.apply {
            animateSpbPos("ボタンをタッチして、\n単語帳を作ってみよう" ).start()
            goNextOnClickAnyWhere{createFlashCard1prt2()}
        }

    }
    private fun createFlashCard1prt2(){
        actions.apply {
            val bnvBtnAdd=activity.findViewById<ImageView>(R.id.bnv_imv_add)
            setArrow(MyOrientation.TOP,bnvBtnAdd)
            actions.viewUnderSpotInGuide = bnvBtnAdd
            goNextOnClickTouchArea(bnvBtnAdd){createFlashCard2()}
        }

    }
    private fun createFlashCard2(){
        val createMenuImvFlashCard      =activity.findViewById<FrameLayout>(R.id.frameLay_new_flashcard)
        actions.apply {
            setArrow(MyOrientation.TOP,createMenuImvFlashCard)
            actions.animateHole = false
            actions.viewUnderSpotInGuide = createMenuImvFlashCard
            activity.createFileViewModel.setBottomMenuVisible(true)
            goNextOnClickTouchArea(createMenuImvFlashCard){createFlashCard3()}
        }

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
                actions.getAppearAlphaAnimation(actions.character, false),
                actions.getSpbVisibilityAnim( false)
            )
            doOnEnd {
                actions.apply {
                    goNextOnClickTouchArea(btnFinish){createFlashCard5(edtCreatingFileTitle)}
                    createFileViewModel.onClickCreateFile(FileStatus.FLASHCARD_COVER)
                    actions.holeShapeInGuide = HoleShape.RECTANGLE
                    actions.viewUnderSpotInGuide = frameLayEditFile
                    addViewToConLay(edtCreatingFileTitle)
                    addViewToConLay(edtCreatingFileTitle).setOnClickListener {
                        showKeyBoard(edtCreatingFileTitle,activity)
                    }
                    edtCreatingFileTitle.requestFocus()
                    showKeyBoard(edtCreatingFileTitle,activity)
                    setArrow(MyOrientation.BOTTOM,btnFinish)
                    arrayOf(imvColPalRed,imvColPalBlue,imvColPalGray,imvColPaYellow).onEach {
                        cloneView(it)
                    } }
                }

            start()
        }

    }

    private fun createFlashCard5(edtCreatingFileTitle:EditText){
        val libraryRv                   =activity.findViewById<RecyclerView>(R.id.vocabCardRV)
        actions.apply {
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
            actions.getArrowVisibilityAnim(false).start()
            createFileViewModel.makeFileInGuide(title)
            goNextOnClickAnyWhere {
                createFlashCard6(libraryRv)
            }
        }



    }
    private fun createFlashCard6(libraryRv:RecyclerView){
        actions.apply {
            actions.animateHole = true
            actions.viewUnderSpotInGuide = libraryRv[0]
//            actions.characterOrientation = MyOrientationSet(MyVerticalOrientation.BOTTOM,
//                MyHorizontalOrientation.LEFT)
//            characterSizeDimenId = R.dimen.character_size_middle
//            setCharacterSize()
//            setCharacterPos()
            goNextOnClickAnyWhere{checkInsideNewFlashCard1(libraryRv)}
        }
    }
    private fun checkInsideNewFlashCard1(libraryRv: RecyclerView){

        actions.apply {
            actions.characterOrientation = MyOrientationSet(MyVerticalOrientation.BOTTOM,
                MyHorizontalOrientation.LEFT)
            characterSizeDimenId = R.dimen.character_size_middle
            doAfterCharacterPosChanged = {
                textFit = true
                actions.spbPosSimple = ViewAndSide(actions.character, MyOrientation.RIGHT)
                animateSpbPos("おめでとう！単語帳が追加されたよ\n中身を見てみよう！").start()
            }
            getCharacterPosChangeAnim().start()


        }


        goNextOnClickTouchArea(libraryRv[0]){checkInsideNewFlashCard2()}
    }
    private fun checkInsideNewFlashCard2(){
        actions.apply {
            actions.getSpbVisibilityAnim(false).start()
            actions.makeTouchAreaGone()
            actions.removeHole()
            changeViewVisibility(actions.holeView,true)
            libraryViewModel.openNextFile(createFileViewModel.returnLastInsertedFile()!!)
            goNextOnClickAnyWhere{checkInsideNewFlashCard3()}
        }

    }
    private fun checkInsideNewFlashCard3(){
        actions.apply {
            animateSpbPos("まだカードがないね\n早速作ってみよう",
            ).start()
            goNextOnClickAnyWhere{makeNewCard1()}
        }

    }
    private fun makeNewCard1(){
        val bnvBtnAdd=activity.findViewById<ImageView>(R.id.bnv_imv_add)
        actions.getAppearAlphaAnimation(actions.character,false).start()
        actions.getSpbVisibilityAnim(false).start()
        actions.viewUnderSpotInGuide = bnvBtnAdd
        goNextOnClickTouchArea(bnvBtnAdd){makeNewCard2()}
    }
    private fun makeNewCard2(){
        val createMenuImvNewCard        =activity.findViewById<FrameLayout>(R.id.frameLay_new_card)
        actions.animateHole = false
        actions.viewUnderSpotInGuide = createMenuImvNewCard
        createFileViewModel.setBottomMenuVisible(true)
        goNextOnClickTouchArea(createMenuImvNewCard){makeNewCard3()}
    }
    private fun makeNewCard3(){
        actions.apply {
            createFileViewModel.setBottomMenuVisible(false)
            actions.makeTouchAreaGone()
            actions.removeHole()
            createCardViewModel.onClickAddNewCardBottomBar()
            goNextOnClickAnyWhere{explainCreateCardFrag1()}
        }

    }
    private fun explainCreateCardFrag1(){
        actions.apply {
            val edtCardFrontContent         =activity.findViewById<EditText>(R.id.edt_front_content)
            actions.characterBorderSet = actions.getSimplePosRelation(edtCardFrontContent,
                MyOrientation.TOP,false)
            setCharacterPos()
            actions.getAppearAlphaAnimation(actions.character,true).start()
            actions.textFit = true
            actions.spbPosSimple = ViewAndSide(actions.character, MyOrientation.RIGHT)
            animateSpbPos("上半分は、カードの表" ).start()
            actions.animateHole = true
            actions.viewUnderSpotInGuide = edtCardFrontContent
            goNextOnClickAnyWhere{explainCreateCardFrag2()}
        }

    }
    private fun explainCreateCardFrag2(){
        val edtCardBackContent          =activity.findViewById<EditText>(R.id.edt_back_content)
        actions.apply {
            animateSpbPos("下半分は、カードの裏になっているよ" ).start()
            actions.viewUnderSpotInGuide = edtCardBackContent
            goNextOnClickAnyWhere{explainCreateCardFrag3()}
        }
    }
    private fun explainCreateCardFrag3(){
        val edtCardFrontTitle           =activity.findViewById<EditText>(R.id.edt_front_title)
        val edtCardBackTitle            =activity.findViewById<EditText>(R.id.edt_back_title)
        actions.apply {
            actions.characterBorderSet = BorderSet(topSideSet = ViewAndSide(edtCardFrontTitle, MyOrientation.BOTTOM),
                bottomSideSet = ViewAndSide(edtCardBackTitle, MyOrientation.TOP) )
            actions.characterOrientation = MyOrientationSet(MyVerticalOrientation.TOP,
                MyHorizontalOrientation.LEFT)
            setCharacterPos()
            actions.textFit = false
            actions.viewUnderSpotInGuide = edtCardFrontTitle
            animateSpbPos("カードの裏表にタイトルを付けることもできるんだ！").start()
            goNextOnClickAnyWhere{explainCreateCardFrag4(edtCardBackTitle)}
        }

    }
    private fun explainCreateCardFrag4(edtCardBackTitle:EditText){
        actions.apply {
            actions.viewUnderSpotInGuide = edtCardBackTitle
            actions.characterOrientation = MyOrientationSet(MyVerticalOrientation.BOTTOM,
                MyHorizontalOrientation.LEFT)
            setCharacterPos()
            actions.spbBorderSet = BorderSet(bottomSideSet = ViewAndSide(actions.character, MyOrientation.BOTTOM)
                , leftSideSet = ViewAndSide(actions.character, MyOrientation.RIGHT))
            actions.spbOrientation = MyOrientationSet(MyVerticalOrientation.BOTTOM, MyHorizontalOrientation.MIDDLE)
            animateSpbPos("好みのようにカスタマイズしてね").start()
            goNextOnClickAnyWhere{explainCreateCardNavigation1()}
        }

    }
    private fun explainCreateCardNavigation1(){
        val edtCardBackContent          =activity.findViewById<EditText>(R.id.edt_back_content)
        val linLayCreateCardNavigation  =activity.findViewById<ConstraintLayout>(R.id.lay_navigate_buttons)
        actions.apply {
            actions.characterBorderSet = BorderSet(bottomSideSet = ViewAndSide(edtCardBackContent,
                MyOrientation.TOP))
            actions.characterOrientation = MyOrientationSet(MyVerticalOrientation.BOTTOM,
                MyHorizontalOrientation.MIDDLE)
            animateSpbPos("カードをめくるには、\n下のナビゲーションボタンを使うよ" ).start()
            actions.viewUnderSpotInGuide = linLayCreateCardNavigation
            goNextOnClickAnyWhere{explainCreateCardNavigation2()}
        }

    }
    private fun explainCreateCardNavigation2(){
        val createCardInsertNext        =activity.findViewById<ImageView>(R.id.btn_insert_next)
        actions.apply {
            animateSpbPos("新しいカードを前に挿入するのはここ").start()
            setArrow(MyOrientation.TOP,createCardInsertNext)
            goNextOnClickAnyWhere{explainCreateCardNavigation3()}
        }

    }
    private fun explainCreateCardNavigation3(){
        val createCardInsertPrevious    =activity.findViewById<ImageView>(R.id.btn_insert_previous)
        actions.apply {
            animateSpbPos("後ろに挿入するのはここ！").start()
            setArrow(MyOrientation.TOP,createCardInsertPrevious)
            goNextOnClickAnyWhere{explainCreateCardNavigation4()}
        }

    }
    private fun explainCreateCardNavigation4(){
        val createCardNavFlipNext       =activity.findViewById<NavigateBtnCreateCard>(R.id.btn_next)
        actions.apply {
            animateSpbPos("矢印ボタンでカードを前後にめくってね！" ).start()
            setArrow(MyOrientation.TOP, createCardNavFlipNext)
            goNextOnClickAnyWhere{explainCreateCardNavigation5()}
        }
    }
    private fun explainCreateCardNavigation5(){
        val createCardNavFlipPrevious   =activity.findViewById<NavigateBtnCreateCard>(R.id.btn_previous)
        actions.apply {
            setArrow(MyOrientation.TOP, createCardNavFlipPrevious)
            goNextOnClickAnyWhere{goodBye1()}
        }

    }
    private fun goodBye1(){
        actions.apply {
            actions.removeHole()
            actions.getArrowVisibilityAnim(false).start()
            actions.characterBorderSet = BorderSet()
            actions.characterOrientation = MyOrientationSet(MyVerticalOrientation.MIDDLE,
                MyHorizontalOrientation.MIDDLE)
            setCharacterPos()
            actions.spbPosSimple = ViewAndSide(actions.character, MyOrientation.TOP)
            animateSpbPos("これでガイドは終わりだよ").start()
            actions.getAppearAlphaAnimation(actions.character,true).start()
            goNextOnClickAnyWhere{goodBye2()}
        }

    }
    private fun goodBye2(){
        actions.apply {
            animateSpbPos("KiNaを楽しんで！").start()
            goNextOnClickAnyWhere{end()}

        }

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
        greeting1()

    }
}
