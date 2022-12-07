package com.koronnu.kina.actions

import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import com.koronnu.kina.R
import com.koronnu.kina.customClasses.enumClasses.HoleShape
import com.koronnu.kina.customClasses.enumClasses.MyHorizontalOrientation
import com.koronnu.kina.customClasses.enumClasses.MyOrientation
import com.koronnu.kina.customClasses.enumClasses.MyVerticalOrientation
import com.koronnu.kina.customClasses.normalClasses.BorderSet
import com.koronnu.kina.customClasses.normalClasses.MyOrientationSet
import com.koronnu.kina.customClasses.normalClasses.ViewAndSide

class DeleteGuide(val actions: GuideActions){
    fun guide1(){
        actions.apply {
            animateCharacterMiddleSpbTop(R.string.guide_spb_delete_1)
            {onClickGoNext{guide2()}}
        }
    }
    private fun guide2(){
        actions.apply {
            allConLayChildrenGoneAnimDoOnEnd = {
                animateCharacterRvBottomSpbRight(R.string.guide_spb_delete_2)
                {onClickGoNext{guide3()}}
            }
            getAllConLayChildrenGoneAnim().start()
            holeShapeInGuide = HoleShape.RECTANGLE
            viewUnderSpotInGuide = libRvFirstItem
        }
    }
    private fun guide3(){
        actions.apply {
            animateSpbNoChange(R.string.guide_spb_delete_3)
            {}
            makeOnlySwipeActive()
            goNextWhenSwiped { guide4() }
            makeHereTouchable(libRvFirstItem)
            changeViewVisibility(conLayGoNext,false)
        }
    }
    private fun guide4(){
        actions.apply {
            libraryViewModel.setOnlySwipeActive(false)
            changeViewVisibility(conLayGoNext,true)
            animateSpbNoChange(R.string.guide_spb_delete_4)
            {onClickGoNext { guide5() } }
            makeHereTouchable(null)
        }
    }
    private fun guide5(){
        actions.apply {
            changeViewVisibility(conLayGoNext,false)
            setArrow(MyOrientation.LEFT,btnDeleteFile)
            goNextWhenDeletePopUpVisible{guide6prt1()}
            animateSpbNoChange(R.string.guide_spb_delete_5){}
            viewUnderSpotInGuide = btnDeleteFile
            makeHereTouchable(btnDeleteFile)

        }
    }
    private fun  guide6prt1(){
        actions.apply {
            libraryViewModel.makeAllUnSwiped()
            getArrowVisibilityAnim(false).start()
            changeViewVisibility(conLayGoNext,true)
            viewUnderSpotInGuide = frameLayConfirmDelete
            animateCharacterAndSpbPos(R.string.guide_spb_delete_6prt1,
                {characterBorderSet = BorderSet(bottomSideSet = ViewAndSide(frameLayConfirmDelete,MyOrientation.TOP))
                    characterOrientation = MyOrientationSet(MyVerticalOrientation.BOTTOM,MyHorizontalOrientation.LEFT) },
                {setSpbPosRightNextToCharacter()},
                {onClickGoNext{guide6prt2()}})
        }
    }
    private fun guide6prt2(){
        actions.apply {
            animateSpbNoChange(R.string.guide_spb_delete_6prt2)
            {onClickGoNext{guide7()}}
        }
    }
    private fun guide7(){
        actions.apply {
            animateSpbNoChange(R.string.guide_spb_delete_7)
            { onClickGoNext{guide8()}}
        }
    }
    private fun guide8(){
        actions.apply {
            goNextWhenDeletePopUpWithChildrenVisible {
                viewUnderSpotInGuide = frameLayConfirmDeleteWithChildren
                characterBorderSet = BorderSet(bottomSideSet = ViewAndSide(frameLayConfirmDeleteWithChildren,MyOrientation.TOP))
                doAfterCharacterPosChanged = {setSpbPos()}
                characterPosChangeAnimDoOnEnd = { onClickGoNext { guide9() } }
                getCharacterPosChangeAnim().start()
            }
            activity.deletePopUpViewModel.setConfirmDeleteWithChildrenVisible(true)
        }
    }
    private fun guide9(){
        actions.apply {
            animateSpbNoChange(R.string.guide_spb_delete_8)
            {onClickGoNext { guide10() }}
            setArrow(MyOrientation.BOTTOM,btnCommitDeleteWithChildren)
        }
    }
    private fun guide10(){
        actions.apply {
            animateSpbNoChange(R.string.guide_spb_delete_9)
            {onClickGoNext { guide11() }}
            setArrow (MyOrientation.BOTTOM, btnDeleteOnlyParent)
        }
    }
    private fun guide11(){
        actions.apply {
            activity.deletePopUpViewModel.setConfirmDeleteWithChildrenVisible(false)
            activity.deletePopUpViewModel.setConfirmDeleteVisible(false)
            getArrowVisibilityAnim(false).start()
            removeHole()
            animateCharacterMiddleSpbTop(R.string.guide_spb_delete_10)
            {onClickGoNext { guide12() }}
        }
    }
    private fun guide12(){
        actions.apply {
            animateSpbNoChange(R.string.guide_spb_delete_11)
            {onClickGoNext { guide13() }}
            viewUnderSpotInGuide = frameLayInBox
            setArrow(MyOrientation.BOTTOM,frameLayInBox)
        }
    }
    private fun guide13(){
        actions.apply {
            animateSpbNoChange(R.string.guide_spb_delete_12)
            {onClickGoNext { guide14() } }
        }
    }
    private fun guide14(){
        actions.apply {
            arrowVisibilityAnimDoOnEnd = {
                animateSpbNoChange(R.string.guide_spb_delete_13)
                {onClickGoNext { guide15() }}
            }
            getArrowVisibilityAnim(false).start()
        }
    }
    private fun guide15(){
        actions.apply {
            viewUnderSpotInGuide = libRvFirstItem
            setArrow(MyOrientation.BOTTOM,libRvFirstItem)
            animateCharacterRvBottomSpbRight(R.string.guide_spb_delete_14)
            {   makeHereTouchable(libRvFirstItem)
                makeOnlyLongClickActive()
                goNextWhenLongClicked { guide16() } }

        }
    }
    private fun guide16(){
        actions.apply {
            animateCharacterAndSpbPos(R.string.guide_spb_delete_15,
                {setCharacterBottomLeftAboveBnv() },
                {setSpbPosAboveCharacterUnderMenuFrameLay()},
                {onClickGoNext { guide17() }})
        }
    }
    private fun guide17(){
        actions.apply {
            animateSpbNoChange(R.string.guide_spb_delete_16prt1)
            {onClickGoNext { guide18() }}
        }
    }
    private fun guide18(){
        actions.apply {
            viewUnderSpotInGuide = imvOpenMultiModeMenu
            setArrow(MyOrientation.BOTTOM,imvOpenMultiModeMenu)
            animateSpbNoChange(R.string.guide_spb_delete_16prt2)
            {onClickGoNext { guide19() }}
            goNextOnClickTouchArea(imvOpenMultiModeMenu){guide19()}
        }
    }
    private fun guide19(){
        actions.apply {
            activity.libraryViewModel.setMultiMenuVisibility(true)
            actionsBeforeEndGuideList.add{activity.libraryViewModel.setMultiMenuVisibility(false)}
            viewUnderSpotInGuide = frameLayMultiMenu
            animateSpbNoChange(R.string.guide_spb_delete_17)
            {onClickGoNext { guide20() }}
            setArrow(MyOrientation.LEFT,lineLayMenuDelete)
        }
    }
    private fun guide20(){
        actions.apply {
            activity.libraryViewModel.setMultiMenuVisibility(false)
            getArrowVisibilityAnim(false).start()
            removeHole()
            animateCharacterMiddleSpbTop(R.string.guide_spb_delete_18)
            { onClickGoNext{guide21()}  }
        }
    }
    private fun guide21(){
        actions.apply {
            getAppearAlphaAnimation(guideParentConLay,false).start()
            appearAlphaAnimDonOnEnd = {
                guideViewModel.endGuide()
            }
        }
    }
}
