package com.koronnu.kina.actions

import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import com.koronnu.kina.R
import com.koronnu.kina.customClasses.enumClasses.MyHorizontalOrientation
import com.koronnu.kina.customClasses.enumClasses.MyOrientation
import com.koronnu.kina.customClasses.enumClasses.MyVerticalOrientation
import com.koronnu.kina.customClasses.normalClasses.BorderSet
import com.koronnu.kina.customClasses.normalClasses.MyOrientationSet
import com.koronnu.kina.customClasses.normalClasses.ViewAndSide
import com.koronnu.kina.databinding.CallOnInstallBinding
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

    private fun makeRvOnlyLongClickActive() =  libraryViewModel.setOnlyLongClickActive(true)
    private fun deActivateRvOnlyLongClickActive () = libraryViewModel.setOnlyLongClickActive(false)
    private fun unEnableImvCloseEditFilePopUp() {btnCloseEditFilePopUp.isEnabled = false}
    private fun EnableImvCloseEditFilePopUp()   {btnCloseEditFilePopUp.isEnabled = true}

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
    private fun animateCharacterAndSpbPos(stringId:Int, characterPos:()->Unit,spbPos:()->Unit){
        actions.apply {
            actions.conLayGoNext.isEnabled = false
            characterPos()
            doAfterCharacterPosChanged = {
                spbPos()
                getSpbPosAnim(actions.getString(stringId)).start()
            }
            characterPosChangeAnimDoOnEnd ={actions.conLayGoNext.isEnabled = true}
            getCharacterPosChangeAnim().start()
        }
    }
    private fun guide1(){
        actions.apply {
            onClickGoNext{guide2()}
            animateCharacterAndSpbPos(R.string.guide_spb_move_1,{setCharacterPosInCenter()},{setSpbPosAboveCharacter()})
        }
    }
    private fun guide2(){
        actions.apply {
            onClickGoNext{guide3()}
        }
    }
    private fun guide3(){
        actions.apply {
            onClickGoNext{guide4()}

        }
    }
    private fun guide4(){
        actions.apply {
            onClickGoNext{guide5()}

        }
    }
    private fun guide5(){
        actions.apply {
            onClickGoNext{guide6()}

        }
    }private fun guide6(){
        actions.apply {
            onClickGoNext{guide7()}

        }
    }
    private fun guide7(){
        actions.apply {
            onClickGoNext{guide8()}

        }
    }
    private fun guide8(){
        actions.apply {
            onClickGoNext{guide9()}

        }
    }
    private fun guide9(){
        actions.apply {
            onClickGoNext{guide3()}

        }
    }
    private fun guide10(){
        actions.apply {
            onClickGoNext{guide3()}

        }
    }
    private fun guide11(){
        actions.apply {
            onClickGoNext{guide3()}

        }
    }
    private fun guide12(){
        actions.apply {
            onClickGoNext{guide3()}

        }
    }


}
