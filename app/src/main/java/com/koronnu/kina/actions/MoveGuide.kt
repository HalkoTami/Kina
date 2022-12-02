package com.koronnu.kina.actions

import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
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
    private val frameLayMoveToThisItem  get() = libRvFirstItem.findViewById<FrameLayout>(R.id.rv_base_frameLay_left)
    private val frameLayConfirmMove     get() = libraryBaseBinding.frameLayConfirmMove
    private val frameLayBnv             get() = mainActivityBinding.frameBnv
    private val stringFlashCard         get() = actions.activity.getString(R.string.flashcard)
    private val stringFolder            get() = actions.activity.getString(R.string.folder)
    private val stringCard              get() = actions.activity.getString(R.string.card)
    private val linLayMenuMoveItem      get() = libraryBaseBinding.multiSelectMenuBinding.linLayMoveSelectedItems

    private fun makeRvOnlyLongClickActive() =  libraryViewModel.setOnlyLongClickActive(true)
    private fun deActivateRvOnlyLongClickActive () = libraryViewModel.setOnlyLongClickActive(false)
    private fun unEnableImvCloseEditFilePopUp() {btnCloseEditFilePopUp.isEnabled = false}
    private fun EnableImvCloseEditFilePopUp()   {btnCloseEditFilePopUp.isEnabled = true}
    private fun getRvFirstItemTypeAsString():String{
        val rvItem = libraryViewModel.returnParentRVItems()
        val rvFiles = rvItem.filterIsInstance<File>()
        val rvCards = rvItem.filterIsInstance<Card>()
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

    private fun setCharacterPosInCenter  () {
        actions.characterBorderSet = BorderSet()
        actions.characterOrientation = MyOrientationSet()
    }
    private fun setCharacterTopLeftUnderRvFirstItem(){
        actions.characterBorderSet = BorderSet(topSideSet = ViewAndSide(libRvFirstItem,MyOrientation.BOTTOM))
        actions.characterOrientation = MyOrientationSet(MyVerticalOrientation.TOP,MyHorizontalOrientation.LEFT)
    }
    private fun setCharacterBottomLeftAboveBnv(){
        actions.characterBorderSet = BorderSet(bottomSideSet = ViewAndSide(frameLayBnv,MyOrientation.TOP))
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
            getSpbPosAnim(activity.getString(R.string.guide_spb_move_3,getRvFirstItemTypeAsString())).start()
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
            animateCharacterAndSpbPos(R.string.guide_spb_move_7,
                {setCharacterPosInCenter()},
                {setSpbPosAboveCharacter()},
                {onClickGoNext { guide9() }})

        }
    }
    private fun guide9(){
        actions.apply {
            animateSpbNoChange(R.string.guide_spb_move_8)
            {onClickGoNext { guide10() }}

        }
    }
    private fun guide10(){
        actions.apply {
            animateSpbNoChange(R.string.guide_spb_move_9)
            {onClickGoNext{guide11()}}

        }
    }
    private fun guide11(){
        actions.apply {
            animateSpbNoChange(R.string.guide_spb_move_10a)
            {onClickGoNext{guide12()}}

        }
    }
    private fun guide12(){
        actions.apply {
            animateSpbNoChange(R.string.guide_spb_move_10b)
            {}

        }
    }


}
