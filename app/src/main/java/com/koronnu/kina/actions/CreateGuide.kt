package com.koronnu.kina.actions

import android.content.Context
import android.view.View
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.get
import com.koronnu.kina.R
import com.koronnu.kina.customClasses.enumClasses.HoleShape
import com.koronnu.kina.customClasses.enumClasses.MyHorizontalOrientation
import com.koronnu.kina.customClasses.enumClasses.MyOrientation
import com.koronnu.kina.customClasses.enumClasses.MyVerticalOrientation
import com.koronnu.kina.customClasses.normalClasses.BorderSet
import com.koronnu.kina.customClasses.normalClasses.MyOrientationSet
import com.koronnu.kina.customClasses.normalClasses.ViewAndSide
import com.koronnu.kina.ui.customViews.*


class CreateGuide(val actions: GuideActions ){
//    private val onInstallBinding = actions.callOnInstallBinding
//    private val createFileViewModel = actions.activity.createFileViewModel
//    private val mainViewModel = actions.activity.mainActivityViewModel
//    private val libraryViewModel = actions.activity.libraryViewModel
//
//
//
//    private fun goNextOnClickTouchArea(view: View, func: () -> Unit) {
//        onInstallBinding.root.setOnClickListener(null)
//        actions.addViewToConLay(view).setOnClickListener {
//            actions.makeTouchAreaGone()
//            func()
//        }
//    }

//    private fun guide1(){
//        actions.apply {
//            getSpbPosAnim("やあ、僕はとさかくん").start()
//            goNextOnClickAnyWhere { greeting2() }
//        }
//    }
//    private fun greeting2(){
//        actions.apply {
//            getSpbPosAnim("これから、KiNaの使い方を説明するね").start()
//            goNextOnClickAnyWhere{ this@CreateGuide.guide1() }
//        }
//
//    }
    fun guide1(){
        actions.apply {
            animateCharacterAndSpbPos(R.string.guide_spb_create_1,
                {setCharacterPosInCenter()},
                {setSpbPosAboveCharacter()},
                {onClickGoNext { guide2() }})
        }
    }
    private fun guide2(){
        actions.apply {
            animateSpbNoChange(R.string.guide_spb_create_2)
            {onClickGoNext{guide3()}}
        }

    }
    private fun guide3(){
        actions.apply {
            setArrow(MyOrientation.TOP,imvBnvBtnAdd)
            viewUnderSpotInGuide = imvBnvBtnAdd
            goNextOnClickTouchArea(imvBnvBtnAdd){guide4()}
        }

    }
    private fun guide4(){
        actions.apply {
            setArrow(MyOrientation.TOP,frameLayCreateFlashCard)
            animateHole = false
            viewUnderSpotInGuide = frameLayCreateFlashCard
            makeBottomMenuVisible()
            goNextOnClickTouchArea(frameLayCreateFlashCard){guide5()}
        }

    }
    private fun guide5(){
        actions.apply{
            makeEditFileVisible(frameLayCreateFlashCard)
            val editText = activity.findViewById<EditText>(R.id.edt_file_title)
            addViewToConLay(btnCloseEditFilePopUp).setOnClickListener(null)
            goNextOnClickTouchArea(btnCreateFile) {
                goNextAfterNewFileCreated {
                    viewUnderSpotInGuide = null
                    guide6() }
                createFileViewModel.makeFileInGuide(editText.text.toString())
            }
            setArrow(MyOrientation.BOTTOM,btnCreateFile)
            holeShapeInGuide = HoleShape.RECTANGLE
            viewUnderSpotInGuide = frameLayEditFile
            getAllConLayChildrenGoneAnim().start()
            makeHereTouchable(frameLayEditFile)

        }
    }


    private fun guide6(){
        actions.apply {
            animateHole = true
            viewUnderSpotInGuide = libRvFirstItem
            makeHereTouchable(null)
            onClickGoNext{guide7()}
        }
    }
    private fun guide7(){

        actions.apply {
            animateCharacterAndSpbPos(R.string.guide_spb_create_3,
                {setCharacterBottomLeftAboveBnv()},
                {setSpbPosRightNextToCharacter()},
                {setArrow(MyOrientation.BOTTOM,libRvFirstItem)
                    onClickGoNext{guide8()}})
            goNextOnClickTouchArea(libRvFirstItem){guide8()}
        }
    }
    private fun guide8(){
        actions.apply {
            viewUnderSpotInGuide = null
            libraryViewModel.openNextFile(createFileViewModel.returnLastInsertedFile()!!)
            arrowVisibilityAnimDoOnEnd= { onClickGoNext{guide9()}}
            getArrowVisibilityAnim(false).start()
        }

    }
    private fun guide9(){
        actions.apply {
            animateSpbNoChange(R.string.guide_spb_create_4)
            {onClickGoNext{guide10()}}
        }

    }
    private fun guide10(){
        actions.apply{
            allConLayChildrenGoneAnimDoOnEnd = {
                actions.viewUnderSpotInGuide = imvBnvBtnAdd
                goNextOnClickTouchArea(imvBnvBtnAdd) { guide11() }
            }
            getAllConLayChildrenGoneAnim().start()
        }
    }
    private fun guide11(){
        actions.apply {
            actions.animateHole = false
            actions.viewUnderSpotInGuide = frameLayCreateCard
            makeBottomMenuVisible()
            goNextOnClickTouchArea(frameLayCreateCard){guide12()}
        }

    }
    private fun guide12(){
        actions.apply {
            viewUnderSpotInGuide = null
            createCardViewModel.onClickAddNewCardBottomBar()
            onClickGoNext{guide13()}
        }

    }
    private fun guide13(){
        actions.apply {
            val edtCardFrontContent         =activity.findViewById<EditText>(R.id.edt_front_content)
            actions.animateHole = true
            actions.viewUnderSpotInGuide = edtCardFrontContent
            animateCharacterAndSpbPos(R.string.guide_spb_create_5,
                {characterBorderSet = actions.getSimplePosRelation(edtCardFrontContent,
                    MyOrientation.TOP,false)},
                {setSpbPosRightNextToCharacter()},
                {onClickGoNext{guide14()}})
        }

    }
    private fun guide14(){
        actions.apply {
            val edtCardBackContent          =activity.findViewById<EditText>(R.id.edt_back_content)
            animateSpbNoChange(R.string.guide_spb_create_6)
            {onClickGoNext{guide15()}}
            viewUnderSpotInGuide = edtCardBackContent
        }
    }
    private fun guide15(){
        actions.apply {
            val edtCardFrontTitle           =activity.findViewById<EditText>(R.id.edt_front_title)
            val edtCardBackTitle            =activity.findViewById<EditText>(R.id.edt_back_title)
            viewUnderSpotInGuide = edtCardFrontTitle
            animateCharacterAndSpbPos(R.string.guide_spb_create_7,
                {characterBorderSet = BorderSet(topSideSet = ViewAndSide(edtCardFrontTitle, MyOrientation.BOTTOM),
                    bottomSideSet = ViewAndSide(edtCardBackTitle, MyOrientation.TOP) )
                    characterOrientation = MyOrientationSet(MyVerticalOrientation.TOP,
                        MyHorizontalOrientation.LEFT)},
                {},{onClickGoNext{guide17()}})
        }

    }
    private fun guide17(){
        actions.apply {
            val edtCardBackTitle            =activity.findViewById<EditText>(R.id.edt_back_title)
            viewUnderSpotInGuide = edtCardBackTitle
            animateCharacterAndSpbPos(R.string.guide_spb_create_8,
                {characterOrientation = MyOrientationSet(MyVerticalOrientation.BOTTOM,
                    MyHorizontalOrientation.LEFT)},
                {spbBorderSet = BorderSet(bottomSideSet = ViewAndSide(actions.character, MyOrientation.BOTTOM)
                    , leftSideSet = ViewAndSide(actions.character, MyOrientation.RIGHT))
                    spbOrientation = MyOrientationSet(MyVerticalOrientation.BOTTOM, MyHorizontalOrientation.MIDDLE)},
                { onClickGoNext{guide18()}}
            )
        }

    }
    private fun guide18(){
        actions.apply {
            val edtCardBackContent          =activity.findViewById<EditText>(R.id.edt_back_content)
            val linLayCreateCardNavigation  =activity.findViewById<ConstraintLayout>(R.id.lay_navigate_buttons)
            actions.viewUnderSpotInGuide = linLayCreateCardNavigation
            animateCharacterAndSpbPos(R.string.guide_spb_create_9,
                { characterBorderSet = BorderSet(bottomSideSet = ViewAndSide(edtCardBackContent,
                    MyOrientation.TOP))
                    characterOrientation = MyOrientationSet(MyVerticalOrientation.BOTTOM,
                        MyHorizontalOrientation.MIDDLE)},
                {setSpbPosAboveCharacter()},
                {onClickGoNext{guide19()}})

        }

    }
    private fun guide19(){
        actions.apply {
            val createCardInsertNext        =activity.findViewById<ImageView>(R.id.btn_insert_next)
            animateSpbNoChange(R.string.guide_spb_create_10)
            {onClickGoNext {guide20()  }}
            setArrow(MyOrientation.TOP,createCardInsertNext)
        }

    }
    private fun guide20(){
        actions.apply {
            val createCardInsertPrevious    =activity.findViewById<ImageView>(R.id.btn_insert_previous)
            animateSpbNoChange(R.string.guide_spb_create_11)
            {onClickGoNext{guide21()}}
            setArrow(MyOrientation.TOP,createCardInsertPrevious)
        }

    }
    private fun guide21(){
        actions.apply {
            val createCardNavFlipNext       =activity.findViewById<NavigateBtnCreateCard>(R.id.btn_next)
            setArrow(MyOrientation.TOP, createCardNavFlipNext)
            animateSpbNoChange(R.string.guide_spb_create_12 )
            { onClickGoNext { guide22() }}

        }
    }
    private fun guide22(){
        actions.apply {
            val createCardNavFlipPrevious   =activity.findViewById<NavigateBtnCreateCard>(R.id.btn_previous)
            arrowVisibilityAnimDoOnEnd = { onClickGoNext { guide23() } }
            setArrow(MyOrientation.TOP, createCardNavFlipPrevious)

        }

    }
    private fun guide23(){
        actions.apply {
            actions.removeHole()
            actions.getArrowVisibilityAnim(false).start()
            animateCharacterAndSpbPos(R.string.guide_spb_create_13,
                {setCharacterPosInCenter()},
                {setSpbPosAboveCharacter()},
                {onClickGoNext { guideViewModel.endGuide() }})
        }

    }
}
