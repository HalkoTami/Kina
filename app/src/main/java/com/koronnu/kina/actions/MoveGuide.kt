package com.koronnu.kina.actions


import androidx.core.view.size
import com.koronnu.kina.R
import com.koronnu.kina.customClasses.enumClasses.HoleShape
import com.koronnu.kina.customClasses.enumClasses.MyOrientation

class MoveGuide(val actions: GuideActions){

    fun guide1(){
        actions.apply {
            guideViewModel.setPopUpContentEndGuide()
            animateCharacterAndSpbPos(R.string.guide_spb_move_1,
                {setCharacterPosInCenter()},
                {setSpbPosAboveCharacter()},
                {onClickGoNext{guide2()}})
        }
    }
    private fun guide2(){
        actions.apply {
            animateCharacterAndSpbPos(R.string.guide_spb_move_2,
                {setCharacterTopLeftUnderRvFirstItem()},
                {setSpbPosRightNextToCharacter()},
                {onClickGoNext { guide3() }})
            holeShapeInGuide = HoleShape.RECTANGLE
            viewUnderSpotInGuide = libRvFirstItem
        }
    }
    private fun guide3(){
        actions.apply {
            spbPosAnimDoOnEnd = { onClickGoNext { guide4() } }
            getSpbPosAnim(activity.getString(R.string.guide_spb_move_3,
                getListFirstItemAsString(libraryViewModel.returnParentRVItems()))).start()
        }
    }
    private fun guide4(){
        actions.apply {
            makeOnlyLongClickActive()
            animateConLayGoNextVisibility(false)
            makeHereTouchable(libRvFirstItem)
            animateSpbNoChange(R.string.guide_spb_move_4)
            {goNextWhenLongClicked{guide5()}}
        }
    }
    private fun guide5(){
        actions.apply {
            animateConLayGoNextVisibility(true)
            makeHereTouchable(null)
            actionsBeforeEndGuideList.add{libraryViewModel.setMultipleSelectMode(false)}
            libraryViewModel.setOnlyLongClickActive(false)
            animateSpbNoChange(R.string.guide_spb_move_5)
            {onClickGoNext{guide6()}}
        }
    }private fun guide6(){
        actions.apply {
            animateConLayGoNextVisibility(false)
            goNextOnClickTouchArea(imvOpenMultiModeMenu){guide7()}
            setArrow(MyOrientation.BOTTOM,imvOpenMultiModeMenu)
            viewUnderSpotInGuide = imvOpenMultiModeMenu
            animateCharacterAndSpbPos(R.string.guide_spb_move_6,
                {setCharacterBottomLeftAboveBnv()},
                {setSpbPosAboveCharacterUnderMenuFrameLay()},
                {  })
        }
    }
    private fun guide7(){
        actions.apply {
            viewUnderSpotInGuide = frameLayMultiMenu
            libraryViewModel.setMultiMenuVisibility(true)
            actionsBeforeEndGuideList.add{libraryViewModel.setMultiMenuVisibility(false)}
            goNextOnClickTouchArea(linLayMenuMoveItem){guide8()}
            setArrow(MyOrientation.LEFT,linLayMenuMoveItem)
        }
    }
    private fun guide8(){
        actions.apply {
            linLayMenuMoveItem.performClick()
            actionsBeforeEndGuideList.add{libraryViewModel.returnLibraryNavCon()?.popBackStack()}
            viewUnderSpotInGuide= if(libraryRv.size==0) null else libRvFirstItem
            animateCharacterAndSpbPos(R.string.guide_spb_move_7,
                {setCharacterPosInCenter()},
                {setSpbPosAboveCharacter()},
                {onClickGoNext { guide9() }})

        }
    }
    private fun guide9(){
        actions.apply {
            spbPosAnimDoOnEnd = {onClickGoNext{guide10()}}
            getSpbPosAnim(activity.getString(R.string.guide_spb_move_8,
            selectedItemAsString,notMovableItemAsString)).start()
        }
    }
    private fun guide10(){
        actions.apply {
            spbPosAnimDoOnEnd = { if(moveToViewModel.getMovableFiles.isNullOrEmpty())
                onClickGoNext{guide11()}
                else onClickGoNext { guide18() }    }
            getSpbPosAnim(activity.getString(R.string.guide_spb_move_9,movableItemAsString)).start()
        }
    }
    private fun guide11(){
        actions.apply {
            animateCharacterAndSpbPos(R.string.guide_spb_move_10a,
                {setCharacterBottomLeftAboveBnv()},
                {setSpbPosAboveCharacter()},
                {conLayGoNext.setOnClickListener{guide12()}})

        }
    }
    private fun guide12(){
        actions.apply {
            guideViewModel.setPopUpContentCreateMovableFile()
            { guide13() }
            guideViewModel.setPopUpConfirmEndGuideVisibility(true)
        }
    }
    private fun guide13(){
        actions.apply {
            animateSpbNoChange(R.string.guide_spb_move_10c ){ onClickGoNext{guide14()}  }
        }
    }
    private fun guide14(){
        actions.apply {
            viewUnderSpotInGuide = imvBnvBtnAdd
            animateCharacterAndSpbPos(R.string.guide_spb_move_10d,
                {setCharacterPosInCenter()},
                {setSpbPosAboveCharacter()},
                {})
            goNextOnClickTouchArea(imvBnvBtnAdd)
            {guide15()}
        }
    }
    private fun guide15(){
        actions.apply {
            allConLayChildrenGoneAnimDoOnEnd = {
                animateHole = false
                viewUnderSpotInGuide = getCreatingMenuItemFrameLay()
                setArrow(MyOrientation.TOP,getCreatingMenuItemFrameLay())
                goNextOnClickTouchArea(getCreatingMenuItemFrameLay())
                {   getCreatingMenuItemFrameLay().performClick()
                    actionsBeforeEndGuideList.add{createFileViewModel.setEditFilePopUpVisible(false)}
                    guide16()}
                imvBnvBtnAdd.performClick()
                actionsBeforeEndGuideList.add{createFileViewModel.setBottomMenuVisible(false)}
            }
            getAllConLayChildrenGoneAnim().start()

        }
    }
    private fun guide16(){
        actions.apply {
            viewUnderSpotInGuide = frameLayEditFile
            makeHereTouchable(frameLayEditFile)
            setArrow(MyOrientation.BOTTOM,btnCreateFile)
            addViewToConLay(btnCloseEditFilePopUp).setOnClickListener(null)
            actionsBeforeEndGuideList.add{createFileViewModel.setDoAfterNewFileCreated{}}
            createFileViewModel.setDoAfterNewFileCreated {
                guide17()
            createFileViewModel.setDoAfterNewFileCreated {  }}

        }
    }
    private fun guide17(){
        actions.apply {
            makeHereTouchable(null)
            animateHole = true
            viewUnderSpotInGuide = libraryRv
            getArrowVisibilityAnim(false).start()
            animateCharacterAndSpbPos(R.string.guide_spb_move_10e,
                {setCharacterBottomLeftAboveBnv()},
                {setSpbPosRightNextToCharacter()},
                {onClickGoNext{guide18()}})
        }
    }
    private fun guide18(){
        actions.apply {
            viewUnderSpotInGuide = frameLayMoveToThisItem
            animateSpbNoChange(R.string.guide_spb_move_11)
            {onClickGoNext{guide19()}}
        }
    }
    private fun guide19(){
        actions.apply {
            animateSpbNoChange(R.string.guide_spb_move_12)
            {onClickGoNext{guide19prt2()}}
        }
    }
    private fun guide19prt2(){
        actions.apply {
            allConLayChildrenGoneAnimDoOnEnd = {
                moveToViewModel.setPopUpVisible(true)
                actionsBeforeEndGuideList.add{moveToViewModel.setPopUpVisible(false)}
                viewUnderSpotInGuide = frameLayConfirmMove
                onClickGoNext{guide20()}
            }
            getAllConLayChildrenGoneAnim().start()
        }

    }
    private fun guide20(){
        actions.apply {
            setArrow(MyOrientation.BOTTOM,btnCommitMove)
            animateCharacterAndSpbPos(R.string.guide_spb_move_13,
                {setCharacterBottomLeftAboveConfirmMovePopUp()},
                {setSpbPosRightNextToCharacter()},
                {onClickGoNext{guide21()}})
        }
    }
    private fun guide21(){
        actions.apply {
            moveToViewModel.setPopUpVisible(false)
            viewUnderSpotInGuide = null
            getArrowVisibilityAnim(false).start()
            animateCharacterAndSpbPos(R.string.guide_spb_move_14,
                {setCharacterPosInCenter()},
                {setSpbPosAboveCharacter()},
                {onClickGoNext { guide22() }})
        }
    }
    private fun guide22(){
        actions.apply {
            guideViewModel.endGuide()
        }

    }


}
