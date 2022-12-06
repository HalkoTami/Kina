package com.koronnu.kina.actions

import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.core.view.size
import com.koronnu.kina.R
import com.koronnu.kina.customClasses.enumClasses.HoleShape
import com.koronnu.kina.customClasses.enumClasses.MyHorizontalOrientation
import com.koronnu.kina.customClasses.enumClasses.MyOrientation
import com.koronnu.kina.customClasses.enumClasses.MyVerticalOrientation
import com.koronnu.kina.customClasses.normalClasses.BorderSet
import com.koronnu.kina.customClasses.normalClasses.MyOrientationSet
import com.koronnu.kina.customClasses.normalClasses.ViewAndSide
import com.koronnu.kina.databinding.CallOnInstallBinding
import com.koronnu.kina.db.dataclass.Card
import com.koronnu.kina.db.dataclass.File
import com.koronnu.kina.db.enumclass.FileStatus
import com.koronnu.kina.ui.viewmodel.*


class MoveGuide(val actions: GuideActions){
    private val libraryViewModel = actions.activity.mainActivityViewModel.libraryBaseViewModel
    private val moveToViewModel  = libraryViewModel.chooseFileMoveToViewModel
    private val createFileViewModel = actions.activity.mainActivityViewModel.editFileViewModel
    private val guideViewModel = actions.activity.mainActivityViewModel.guideViewModel
    private val libraryBaseBinding      get() = actions.activity.mainActivityViewModel.libraryBaseViewModel.getChildFragBinding
    private val mainActivityBinding     get() = actions.activity.mainActivityViewModel.mainActivityBinding
    private val libraryRv               get() = libraryBaseBinding.vocabCardRV
    private val libRvFirstItem          get() = libraryRv[0]
    private val imvOpenMultiModeMenu    get() = libraryBaseBinding.topBarMultiselectBinding.imvChangeMenuVisibility
    private val frameLayMultiMenu       get() = libraryBaseBinding.frameLayMultiModeMenu
    private val imvBnvBtnAdd            get() = mainActivityBinding.bnvBinding.bnvImvAdd
    private val frameLayCreateFlashCard get() = mainActivityBinding.bindingAddMenu.frameLayNewFlashcard
    private val frameLayCreateFolder    get() = mainActivityBinding.bindingAddMenu.frameLayNewFolder
    private val frameLayEditFile        get() = mainActivityBinding.frameLayEditFile
    private val btnCloseEditFilePopUp   get() = mainActivityBinding.editFileBinding.btnClose
    private val btnCreateFile           get() = mainActivityBinding.editFileBinding.btnFinish
    private val frameLayMoveToThisItem  get() = libraryRv.findViewById<FrameLayout>(R.id.rv_base_frameLay_left)
    private val frameLayConfirmMove     get() = libraryBaseBinding.frameLayConfirmMove
    private val frameLayBnv             get() = mainActivityBinding.frameBnv
    private val stringFlashCard         get() = actions.activity.getString(R.string.flashcard)
    private val stringFolder            get() = actions.activity.getString(R.string.folder)
    private val stringCard              get() = actions.activity.getString(R.string.card)
    private val linLayMenuMoveItem      get() = libraryBaseBinding.multiSelectMenuBinding.linLayMoveSelectedItems
    private val selectedItemAsString    get() = getListFirstItemAsString(moveToViewModel.returnMovingItems())
    private val movableItemAsString     get() = getMovableItemTypeAsString(selectedItemAsString)
    private val notMovableItemAsString  get() = getNotMovableItemTypeAsString(selectedItemAsString)
    private val btnCommitMove           get() = libraryBaseBinding.confirmMoveToBinding.btnCommitMove


    private fun makeRvOnlyLongClickActive() =  libraryViewModel.setOnlyLongClickActive(true)
    private fun deActivateRvOnlyLongClickActive () = libraryViewModel.setOnlyLongClickActive(false)
    private fun unEnableImvCloseEditFilePopUp() {btnCloseEditFilePopUp.isEnabled = false}
    private fun EnableImvCloseEditFilePopUp()   {btnCloseEditFilePopUp.isEnabled = true}

    private fun getCreatingMenuItemFrameLay():View{
        return when(moveToViewModel.getMovableFileStatus){
            FileStatus.FLASHCARD_COVER -> frameLayCreateFlashCard
            FileStatus.FOLDER   -> frameLayCreateFolder
            else -> imvBnvBtnAdd
        }
    }
    private fun getListFirstItemAsString(list: List<Any>):String{
        val rvFiles = list.filterIsInstance<File>()
        val rvCards = list.filterIsInstance<Card>()
        return if(rvFiles.isNotEmpty())
            when(rvFiles[0].fileStatus){
                FileStatus.FLASHCARD_COVER -> stringFlashCard
                FileStatus.FOLDER           -> stringFlashCard
                else ->""
            }
        else if(rvCards.isEmpty().not()){
            stringCard
        } else ""
    }
    private fun getMovableItemTypeAsString(string: String):String{
        return when (string){
            stringFlashCard,stringFolder -> stringFolder
            stringCard  -> stringFlashCard
            else -> ""
        }
    }
    private fun getNotMovableItemTypeAsString(string: String):String{
        return when (string){
            stringFlashCard,stringFolder -> stringFlashCard
            stringCard  -> stringFolder
            else -> ""
        }
    }


    private fun setCharacterPosInCenter  () {
        actions.characterSizeDimenId = R.dimen.character_size_large
        actions.characterBorderSet = BorderSet()
        actions.characterOrientation = MyOrientationSet()
    }
    private fun setCharacterTopLeftUnderRvFirstItem(){
        actions.characterSizeDimenId = R.dimen.character_size_middle
        actions.characterBorderSet = BorderSet(topSideSet = ViewAndSide(libRvFirstItem,MyOrientation.BOTTOM))
        actions.characterOrientation = MyOrientationSet(MyVerticalOrientation.TOP,MyHorizontalOrientation.LEFT)
    }
    private fun setCharacterBottomLeftAboveBnv(){
        actions.characterSizeDimenId = R.dimen.character_size_middle
        actions.characterBorderSet = BorderSet(bottomSideSet = ViewAndSide(frameLayBnv,MyOrientation.TOP))
        actions.characterOrientation = MyOrientationSet(MyVerticalOrientation.BOTTOM,MyHorizontalOrientation.LEFT)
    }
    private fun setCharacterBottomLeftAboveConfirmMovePopUp(){
        actions.characterSizeDimenId = R.dimen.character_size_middle
        actions.characterBorderSet = BorderSet(bottomSideSet = ViewAndSide(frameLayConfirmMove,MyOrientation.TOP))
        actions.characterOrientation = MyOrientationSet(MyVerticalOrientation.BOTTOM,MyHorizontalOrientation.LEFT)
    }
    private fun setSpbPosRightNextToCharacter(){
        actions.textFit = true
        actions.spbPosSimple = ViewAndSide(actions.character,MyOrientation.RIGHT)
    }
    private fun setSpbPosAboveCharacter(){
        actions.textFit = false
        actions.spbPosSimple = ViewAndSide(actions.character,MyOrientation.TOP)
    }
    private fun setSpbPosAboveCharacterUnderMenuFrameLay(){
        actions.textFit = false
        actions.spbBorderSet = BorderSet(bottomSideSet = ViewAndSide(actions.character,MyOrientation.TOP),
        topSideSet = ViewAndSide(frameLayMultiMenu,MyOrientation.BOTTOM))
        actions.spbOrientation = MyOrientationSet(MyVerticalOrientation.BOTTOM,MyHorizontalOrientation.MIDDLE)
    }
    private fun goNextWhenLongClicked(next:()->Unit){
        actions.actionsBeforeEndGuideList.add { libraryViewModel.setDoAfterLongClick(false) {} }
        libraryViewModel.setDoAfterLongClick(true,next)
    }
    private fun animateCharacterAndSpbPos(stringId:Int, characterPos:()->Unit,spbPos:()->Unit,doOnEnd:()->Unit){
        actions.apply {
//            actions.conLayGoNext.isEnabled = false
            characterPos()
            doAfterCharacterPosChanged = {
                spbPos()
                getSpbPosAnim(actions.getString(stringId)).start()
            }
            characterPosChangeAnimDoOnEnd ={doOnEnd()}
            getCharacterPosChangeAnim().start()
        }
    }
    private fun animateSpbNoChange(stringId: Int,doOnEnd: () -> Unit){
        actions.spbPosAnimDoOnEnd = {doOnEnd()}
        actions.getSpbPosAnim(actions.getString(stringId)).start()
    }
    fun guide1(){
        actions.apply {
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
            actionsBeforeEndGuideList.add { libraryViewModel.setOnlySwipeActive(false) }
            libraryViewModel.setOnlyLongClickActive(true)
            makeHereTouchable(libRvFirstItem)
            animateSpbNoChange(R.string.guide_spb_move_4)
            {goNextWhenLongClicked{guide5()}}
        }
    }
    private fun guide5(){
        actions.apply {
            makeHereTouchable(null)
            libraryViewModel.setOnlyLongClickActive(false)
            animateSpbNoChange(R.string.guide_spb_move_5)
            {onClickGoNext{guide6()}}
        }
    }private fun guide6(){
        actions.apply {
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
            goNextOnClickTouchArea(linLayMenuMoveItem){guide8()}
            setArrow(MyOrientation.LEFT,linLayMenuMoveItem)
        }
    }
    private fun guide8(){
        actions.apply {
            linLayMenuMoveItem.performClick()
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
                {onClickGoNext { guide12() }})

        }
    }
    private fun guide12(){
        actions.apply {
            guideViewModel.setPopUpContentCreateMovableFile{
                guide13()
            }
            guideViewModel.setPopUpConfirmEndGuideVisibility(true)
        }
    }
    private fun guide13(){
        actions.apply {
            animateSpbNoChange(R.string.guide_spb_move_10c,
                {guide14()})
        }
    }
    private fun guide14(){
        actions.apply {
            animateHole = false
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
                viewUnderSpotInGuide = getCreatingMenuItemFrameLay()
                setArrow(MyOrientation.TOP,getCreatingMenuItemFrameLay())
                goNextOnClickTouchArea(getCreatingMenuItemFrameLay())
                {   getCreatingMenuItemFrameLay().performClick()
                    guide16()}
                imvBnvBtnAdd.performClick()
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
