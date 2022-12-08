package com.koronnu.kina.actions


import com.koronnu.kina.R
import com.koronnu.kina.customClasses.enumClasses.*
import com.koronnu.kina.db.dataclass.File

class EditGuide(val actions: GuideActions){
    fun guide1(){
        actions.apply {
            animateCharacterMiddleSpbTop(R.string.guide_spb_edit_1)
            {onClickGoNext{guide2()}}
        }
    }
    private fun guide2(){
        actions.apply {
            animateCharacterRvBottomSpbRight(R.string.guide_spb_edit_2)
            {onClickGoNext{guide3()}}
            holeShapeInGuide = HoleShape.RECTANGLE
            viewUnderSpotInGuide = libRvFirstItem
        }
    }
    private fun guide3(){
        actions.apply {
            animateSpbNoChange(R.string.guide_spb_edit_3){}
            makeOnlySwipeActive()
            goNextWhenSwiped { guide4() }
            makeHereTouchable(libRvFirstItem)
        }
    }
    private fun guide4(){
        actions.apply {
            makeHereTouchable(null)
            animateSpbNoChange(R.string.guide_spb_edit_4)
            { setArrow(MyOrientation.LEFT,btnEditFile)
            onClickGoNext { guide5() }}
        }
    }
    private fun guide5(){
        actions.apply {
            animateConLayGoNextVisibility(false)
            viewUnderSpotInGuide = btnEditFile
            arrowVisibilityAnimDoOnEnd =
                { goNextOnClickTouchArea(btnEditFile) {guide6()} }
            setArrow(MyOrientation.LEFT,btnEditFile)

        }
    }
    private fun guide6(){
        actions.apply {
            createFileViewModel.onClickEditFileInRV(libraryViewModel.returnParentRVItems()[0] as File)
            allConLayChildrenGoneAnimDoOnEnd = {

                actionsBeforeEndGuideList.add { createFileViewModel.setEditFilePopUpVisible(false) }
                holeShapeInGuide = HoleShape.RECTANGLE
                animateHole = false
                viewUnderSpotInGuide = frameLayEditFile
                animateCharacterBottomLeftAboveViewSpbRightNext(
                    R.string.guide_spb_edit_5, frameLayEditFile, )
                {onClickGoNext{guide7()}}
            }
            getAllConLayChildrenGoneAnim().start()
        }
    }

    private fun guide7(){
        actions.apply {
            setArrow(MyOrientation.BOTTOM,edtEditFileTitle)
            animateSpbNoChange(R.string.guide_spb_edit_6)
            {onClickGoNext{guide8()}}
        }
    }
    private fun guide8(){
        actions.apply {
            animateSpbNoChange(R.string.guide_spb_edit_7)
            {onClickGoNext { guide9()  }}
            setArrow(MyOrientation.BOTTOM,imvColPallet)
        }
    }
    private fun guide9(){
        actions.apply {
            arrowVisibilityAnimDoOnEnd = {onClickGoNext{guide10()}}
            setArrow(MyOrientation.BOTTOM,imvColPalledRed)
            imvColPalledRed.performClick()
        }
    }
    private fun guide10(){
        actions.apply {
            actions.apply {
                arrowVisibilityAnimDoOnEnd = {onClickGoNext{guide11()}}
                setArrow(MyOrientation.BOTTOM,imvColPalledBlue)
                imvColPalledBlue.performClick()
            }
        }
    }
    private fun guide11(){
        actions.apply {
            actions.apply {
                arrowVisibilityAnimDoOnEnd = {onClickGoNext { guide12() }}
                setArrow(MyOrientation.BOTTOM,imvColPalledYellow)
                imvColPalledYellow.performClick()
            }
        }
    }
    private fun guide12(){
        actions.apply {

            animateConLayGoNextVisibility(false)
            getArrowVisibilityAnim(false).start()
            addViewToConLay(btnCloseEditFilePopUp).setOnClickListener(null)
            goNextOnClickTouchArea(btnCreateFile)
            {   guide13() }
            makeHereTouchable(frameLayEditFile)
        }
    }
    private fun guide13(){
        actions.apply {
            btnCreateFile.performClick()
            makeHereTouchable(null)
            animateCharacterMiddleSpbTop(R.string.guide_spb_end)
            {onClickGoNext { guideViewModel.endGuide() }}

        }
    }
}
