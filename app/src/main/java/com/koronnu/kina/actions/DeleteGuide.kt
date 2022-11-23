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
    private val libraryRV get() = actions.activity.findViewById<RecyclerView>(R.id.vocabCardRV)
    private val btnDeleteFile  get()=actions.activity.findViewById<ImageView>(R.id.btn_delete)
    private val frameLayConfirmDelete   get()=actions.activity.findViewById<FrameLayout>(R.id.frameLay_confirm_delete)
    private val frameLayConfirmDeleteWithChildren  get() = actions.activity.findViewById<FrameLayout>(R.id.frameLay_confirm_delete_with_children)
    private val btnCommitDeleteWithChildren get() = actions.activity.findViewById<Button>(R.id.btn_delete_all_children)
    private val btnDeleteOnlyParent get() = actions.activity.findViewById<Button>(R.id.delete_only_file)
    private val imvMultiModeMenu   get() =actions.activity.findViewById<ImageView>(R.id.imv_change_menu_visibility)
    private val frameLayInBox    get()   =actions.activity.findViewById<ConstraintLayout>(R.id.frameLay_inBox)
    private val frameLayMultiModeMenu     get()  =actions.activity.findViewById<FrameLayout>(R.id.frameLay_multi_mode_menu)
    private val lineLayMenuDelete get() = actions.activity.findViewById<LinearLayoutCompat>(R.id.linLay_delete_selected_items)
    fun guide1(){
        actions.apply {
            callOnFirst()
            makeHereTouchable(null)
            getSpbPosAnim(getString(R.string.guide_spb_delete_1)).start()
            onClickGoNext{guide2()}
        }
    }
    private fun guide2(){
        actions.apply {

            characterSizeDimenId = R.dimen.character_size_middle
            characterBorderSet = BorderSet(topSideSet = ViewAndSide(libraryRV[0],MyOrientation.BOTTOM))
            characterOrientation = MyOrientationSet(MyVerticalOrientation.TOP,MyHorizontalOrientation.LEFT)
            textFit = true
            characterPosChangeAnimDoOnEnd = {onClickGoNext{guide3()}}
            doAfterCharacterPosChanged = {
                spbPosSimple = ViewAndSide(character,MyOrientation.RIGHT)
                getSpbPosAnim(getString(R.string.guide_spb_delete_2)).start()}

            allConLayChildrenGoneAnimDoOnEnd = {
                getCharacterPosChangeAnim().start()
            }
            getAllConLayChildrenGoneAnim().start()
            holeShapeInGuide = HoleShape.RECTANGLE
            viewUnderSpotInGuide = libraryRV[0]
        }
    }
    private fun guide3(){
        actions.apply {
            getSpbPosAnim(getString(R.string.guide_spb_delete_3)).start()
            activity.libraryViewModel.apply {
                setOnlySwipeActive(true)
                actionsBeforeEndGuideList.add{activity.libraryViewModel.setOnlySwipeActive(false)}
            }
            makeHereTouchable(libraryRV[0])
            activity.libraryViewModel.setDoOnSwipeEnd(true) {
                activity.libraryViewModel.setOnlySwipeActive(false)
                guide4() }
            actionsBeforeEndGuideList.add { activity.libraryViewModel.setDoOnSwipeEnd(false){} }
            changeViewVisibility(conLayGoNext,false)
        }
    }
    private fun guide4(){
        actions.apply {
            changeViewVisibility(conLayGoNext,true)
            actionsBeforeEndGuideList.add{activity.libraryViewModel.makeAllUnSwiped()}
            getSpbPosAnim(getString(R.string.guide_spb_delete_4)).start()
            makeHereTouchable(null)
            onClickGoNext { guide5() }
        }
    }
    private fun guide5(){
        actions.apply {
            changeViewVisibility(conLayGoNext,false)
            setArrow(MyOrientation.LEFT,btnDeleteFile)
            activity.deletePopUpViewModel.setDoOnPopUpVisibilityChanged(true){
                activity.libraryViewModel.makeAllUnSwiped()
                guide6prt1()}
            getSpbPosAnim(getString(R.string.guide_spb_delete_5)).start()
            viewUnderSpotInGuide = btnDeleteFile
            makeHereTouchable(btnDeleteFile)

        }
    }
    private fun  guide6prt1(){
        actions.apply {
            actionsBeforeEndGuideList.add{activity.deletePopUpViewModel.setConfirmDeleteVisible(false)}
            changeViewVisibility(conLayGoNext,true)
            allConLayChildrenGoneAnimDoOnEnd = {
                makeHereTouchable(null)
                getArrowVisibilityAnim(false).start()
                viewUnderSpotInGuide = frameLayConfirmDelete
                characterBorderSet = BorderSet(bottomSideSet = ViewAndSide(frameLayConfirmDelete,MyOrientation.TOP))
                characterOrientation = MyOrientationSet(MyVerticalOrientation.BOTTOM,MyHorizontalOrientation.LEFT)
                doAfterCharacterPosChanged={getSpbPosAnim(getString(R.string.guide_spb_delete_6prt1)).start()}
                getCharacterPosChangeAnim().start()
                onClickGoNext{guide6prt2()}
            }
            getAllConLayChildrenGoneAnim().start()
        }
    }
    private fun guide6prt2(){
        actions.apply {
            spbPosAnimDoOnEnd = {onClickGoNext{guide7()}}
            getSpbPosAnim(getString(R.string.guide_spb_delete_6prt2)).start()

        }
    }
    private fun guide7(){
        actions.apply {
            spbPosAnimDoOnEnd = { onClickGoNext{guide8()}}
            getSpbPosAnim(getString(R.string.guide_spb_delete_7)).start()

        }
    }
    private fun guide8(){
        actions.apply {

            activity.deletePopUpViewModel.setDoOnPopUpVisibilityChanged(true){
                viewUnderSpotInGuide = frameLayConfirmDeleteWithChildren
                characterBorderSet = BorderSet(bottomSideSet = ViewAndSide(frameLayConfirmDeleteWithChildren,MyOrientation.TOP))
                doAfterCharacterPosChanged = {setSpbPos()}
                characterPosChangeAnimDoOnEnd = {
                    onClickGoNext { guide9() }
                }
                getCharacterPosChangeAnim().start()
            }
            activity.deletePopUpViewModel.setConfirmDeleteWithChildrenVisible(true)
            actionsBeforeEndGuideList.add { activity.deletePopUpViewModel.setConfirmDeleteVisible(false) }
        }
    }
    private fun guide9(){
        actions.apply {
            spbPosAnimDoOnEnd ={onClickGoNext { guide10() }}
            getSpbPosAnim(getString(R.string.guide_spb_delete_8)).start()
            setArrow(MyOrientation.BOTTOM,btnCommitDeleteWithChildren)
        }
    }
    private fun guide10(){
        actions.apply {
            spbPosAnimDoOnEnd = {onClickGoNext { guide11() }}
            getSpbPosAnim(getString(R.string.guide_spb_delete_9)).start()
            setArrow (MyOrientation.BOTTOM, btnDeleteOnlyParent)
        }
    }
    private fun guide11(){
        actions.apply {
            activity.deletePopUpViewModel.setConfirmDeleteWithChildrenVisible(false)
            activity.deletePopUpViewModel.setConfirmDeleteVisible(false)
            getArrowVisibilityAnim(false).start()
            removeHole()
            characterBorderSet = BorderSet()
            characterOrientation = MyOrientationSet()
            doAfterCharacterPosChanged = {
                textFit = false
                spbPosSimple = ViewAndSide(character,MyOrientation.TOP)
                spbPosAnimDoOnEnd = {onClickGoNext { guide12() }}
                getSpbPosAnim(getString(R.string.guide_spb_delete_10)).start()
            }
            getCharacterPosChangeAnim().start()
        }
    }
    private fun guide12(){
        actions.apply {
            spbPosAnimDoOnEnd = {onClickGoNext { guide13() }}
            getSpbPosAnim(getString(R.string.guide_spb_delete_11)).start()
            viewUnderSpotInGuide = frameLayInBox
            setArrow(MyOrientation.BOTTOM,frameLayInBox)
        }
    }
    private fun guide13(){
        actions.apply {
            spbPosAnimDoOnEnd ={onClickGoNext { guide14() } }
            getSpbPosAnim(getString(R.string.guide_spb_delete_12)).start()
        }
    }
    private fun guide14(){
        actions.apply {
            spbPosAnimDoOnEnd = {onClickGoNext { guide15() }}
            arrowVisibilityAnimDoOnEnd = {
                getSpbPosAnim(getString(R.string.guide_spb_delete_13)).start()
            }
            getArrowVisibilityAnim(false).start()
        }
    }
    private fun guide15(){
        actions.apply {
            viewUnderSpotInGuide = libraryRV[0]
            setArrow(MyOrientation.BOTTOM,libraryRV[0])
            characterBorderSet = BorderSet(topSideSet = ViewAndSide(libraryRV[0],MyOrientation.BOTTOM))
            characterOrientation = MyOrientationSet(MyVerticalOrientation.TOP,MyHorizontalOrientation.LEFT)
            doAfterCharacterPosChanged = {
                textFit = true
                spbPosSimple = ViewAndSide(character,MyOrientation.RIGHT)
                getSpbPosAnim(getString(R.string.guide_spb_delete_14)).start()
            }
            spbPosAnimDoOnEnd = {makeHereTouchable(libraryRV[0])
                activity.libraryViewModel.setDoAfterLongClick(true){guide16()}
                actionsBeforeEndGuideList.add { activity.libraryViewModel.setDoAfterLongClick(false){} }
            }
            activity.libraryViewModel.setOnlyLongClickActive(true)
            actionsBeforeEndGuideList.add { activity.libraryViewModel.setOnlyLongClickActive(false) }

            getCharacterPosChangeAnim().start()

        }
    }
    private fun guide16(){
        actions.apply {
            characterOrientation = MyOrientationSet()
            characterPosChangeAnimDoOnEnd = {
                textFit = false
                spbPosSimple = ViewAndSide(character,MyOrientation.TOP)
                getSpbPosAnim(getString(R.string.guide_spb_delete_15)).start()
            }
            spbPosAnimDoOnEnd = {onClickGoNext { guide17() }}
            getCharacterPosChangeAnim().start()
            makeHereTouchable(libraryRV[0])

        }
    }
    private fun guide17(){
        actions.apply {
            spbPosAnimDoOnEnd = {onClickGoNext { guide18() }}
            getSpbPosAnim(getString(R.string.guide_spb_delete_16prt1)).start()
        }
    }
    private fun guide18(){
        actions.apply {
            viewUnderSpotInGuide = imvMultiModeMenu
            setArrow(MyOrientation.BOTTOM,imvMultiModeMenu)
            getSpbPosAnim(getString(R.string.guide_spb_delete_16prt2)).start()
            spbPosAnimDoOnEnd = {onClickGoNext { guide19() }}
            makeHereTouchable(imvMultiModeMenu)
        }
    }
    private fun guide19(){
        actions.apply {
            activity.libraryViewModel.setMultiMenuVisibility(true)
            actionsBeforeEndGuideList.add{activity.libraryViewModel.setMultiMenuVisibility(false)}
            viewUnderSpotInGuide = frameLayMultiModeMenu
            getSpbPosAnim(getString(R.string.guide_spb_delete_17)).start()
            spbPosAnimDoOnEnd = {onClickGoNext { guide20() }}
            setArrow(MyOrientation.LEFT,lineLayMenuDelete)
        }
    }
    private fun guide20(){
        actions.apply {
            activity.libraryViewModel.setMultiMenuVisibility(false)
            removeHole()
            allConLayChildrenGoneAnimDoOnEnd = {
                characterBorderSet = BorderSet()
                characterOrientation = MyOrientationSet()
                doAfterCharacterPosChanged = {
                    spbPosSimple = ViewAndSide(character,MyOrientation.TOP)
                    getSpbPosAnim(getString(R.string.guide_spb_delete_18)).start()
                }
                spbPosAnimDoOnEnd = { onClickGoNext{guide21()}  }
                getCharacterPosChangeAnim().start()
            }
            getAllConLayChildrenGoneAnim().start()

        }
    }
    private fun guide21(){
        actions.apply {
            getAppearAlphaAnimation(guideParentConLay,false).start()
            appearAlphaAnimDonOnEnd = {
                activity.libraryViewModel.setMultipleSelectMode(false)
                activity.mainActivityViewModel.setGuideVisibility(false)
            }
        }
    }
    fun deleteGuide() {
//        mainViewModel.setGuideVisibility(true)
//        fun guideInOrder(order: Int) {
//            onInstallBinding.root.children.iterator().forEach { if(it.tag == 1) it.visibility = View.GONE }
//            val libraryRv                   =activity.findViewById<RecyclerView>(R.id.vocabCardRV)
//            val btnDeleteFile           =activity.findViewById<ImageView>(R.id.btn_delete)
//            val btnConfirmDelete           =activity.findViewById<Button>(R.id.btn_commit_delete_only_parent)
//            val btnConfirmDeleteOnlyParent           =activity.findViewById<Button>(R.id.delete_only_file)
//            val frameLayConfirmDelete       =activity.findViewById<FrameLayout>(R.id.frameLay_confirm_delete)
//            val frameLayConfirmDeleteWithChildren       =activity.findViewById<FrameLayout>(R.id.frameLay_confirm_delete_with_children)
//            val txvContainingCardAmount       =activity.findViewById<TextView>(R.id.txv_containing_card)
//            val txvInBoxCardAmount       =activity.findViewById<TextView>(R.id.txv_inBox_card_amount)
//            val frameLayMultiModeMenu       =activity.findViewById<FrameLayout>(R.id.frameLay_multi_mode_menu)
//
//            fun goNextOnClickAnyWhere(){
//                onInstallBinding.root.setOnClickListener {
//                    guideInOrder(order+1)
//                }
//            }
//            fun goNextOnClickTouchArea(touchArea: View) {
//                onInstallBinding.root.setOnClickListener(null)
//                addTouchArea(touchArea).setOnClickListener {
//                    guideInOrder(order + 1)
//                }
//            }
//            fun greeting(){
//                if(mainViewModel.returnFragmentStatus()?.now!=MainFragment.Library){
//                    mainViewModel.changeFragment(MainFragment.Library)
//                }
//                if(libraryViewModel.returnLibraryFragment()!=LibraryFragment.Home){
//                    libraryViewModel.returnLibraryNavCon()?.navigate(LibraryHomeFragDirections.toLibHome())
//                }
//                removeHole()
//                appearAlphaAnimation(character,true).start()
//                explainTextAnimation("これから、\nアイテムを削除する方法を説明するよ", MyOrientation.TOP,character).start()
//                goNextOnClickAnyWhere()
//            }
//            fun explainBtn(){
//                editGuide(1,mainViewModel,libraryViewModel,editFileViewModel)
//                goNextOnClickAnyWhere()
//            }
//            fun explainBtn2(){
//                editGuide(2,mainViewModel,libraryViewModel,editFileViewModel)
//                onInstallBinding.root.setOnClickListener{
//                    if(libraryViewModel.returnLeftSwipedItemExists())
//                        guideInOrder(order+1)
//                }
//            }
//            fun explainBtn3(){
//                setHole(btnDeleteFile,HoleShape.CIRCLE)
//                setPositionByXY(libraryRv[0],character,MyOrientation.BOTTOM,10,false)
//                explainTextAnimation("このボタンで、アイテムを削除できるよ",MyOrientation.BOTTOM,character).start()
//                goNextOnClickAnyWhere()
//            }
//            fun explainBtn4(){
//                goNextOnClickTouchArea(btnDeleteFile)
//                explainTextAnimation("実際には削除しないので\n" +
//                        "一度押してみよう",MyOrientation.BOTTOM,character).start()
//                setArrow(MyOrientation.LEFT,btnDeleteFile)
//
//            }
//            fun explainDeleteSystem(){
//                val exampleFile = File(
//                    fileId = 0,
//                    title = "サンプル単語帳",
//                    fileStatus = FileStatus.FLASHCARD_COVER)
//                deletePopUpViewModel.setDeletingItem(mutableListOf(exampleFile))
//                deletePopUpViewModel.setConfirmDeleteVisible(true)
//                setHole(frameLayConfirmDelete,HoleShape.RECTANGLE)
//                setPositionByXY(frameLayConfirmDelete,character,MyOrientation.BOTTOM,50,false)
//                setArrow(MyOrientation.BOTTOM,btnConfirmDelete)
//                explainTextAnimation("消されないので、試しに削除を押してね！",MyOrientation.TOP,frameLayConfirmDelete).start()
//                goNextOnClickTouchArea(btnConfirmDelete)
//            }
//            fun explainDeleteSystem2(){
//                deletePopUpViewModel.apply {
//                    setConfirmDeleteVisible(false)
//                    setConfirmDeleteWithChildrenVisible(true)
//                }
//                txvContainingCardAmount.text = "15"
//                setHole(frameLayConfirmDeleteWithChildren,HoleShape.RECTANGLE)
//                setArrow(MyOrientation.BOTTOM,txvContainingCardAmount)
//                explainTextAnimation("単語帳やフォルダに中身が入っていると\n確認画面に映るよ！",MyOrientation.TOP,frameLayConfirmDeleteWithChildren).start()
//                goNextOnClickAnyWhere()
//            }
//            fun explainDeleteSystem3(){
//                explainTextAnimation("中身もすべて消すか、\n残すか選べるよ",MyOrientation.TOP,frameLayConfirmDelete).start()
//                makeArrowGone()
//                goNextOnClickAnyWhere()
//            }
//            fun explainDeleteSystem4(){
//                setHole(btnConfirmDeleteOnlyParent,HoleShape.CIRCLE)
//                explainTextAnimation("中身を残す場合、\n中のカードはどの単語帳にも\n入っていないことになるんだ",MyOrientation.TOP,frameLayConfirmDelete).start()
//                goNextOnClickAnyWhere()
//            }
//            fun explainDeleteSystem5(){
//                explainTextAnimation("そのカードはどこに行くかというと...",
//                    MyOrientation.TOP,frameLayConfirmDelete).start()
//                removeHole()
//                deletePopUpViewModel.setConfirmDeleteWithChildrenVisible(false)
//                goNextOnClickAnyWhere()
//            }
//            fun explainDeleteSystem6(){
//                explainTextAnimation("InBoxの中に移るよ！",
//                    MyOrientation.TOP,character).start()
//                setHole(frameLayInBox,HoleShape.CIRCLE)
//                txvInBoxCardAmount.text = "15"
//                changeViewVisibility(txvInBoxCardAmount,true)
//                goNextOnClickAnyWhere()
//            }
//            fun explainMultiSelectMode(){
//                removeHole()
//                txvInBoxCardAmount.text = ""
//                changeViewVisibility(txvInBoxCardAmount,false)
//                setPositionByXY(frameLayCallOnInstall,character,MyOrientation.MIDDLE,0,false)
//                explainTextAnimation("アイテムをまとめて削除したいときは",
//                    MyOrientation.TOP,character).start()
//                goNextOnClickAnyWhere()
//            }
//            fun explainMultiSelectMode2(){
//                setHole(libraryRv[0],HoleShape.RECTANGLE)
//                addTouchArea(libraryRv[0]).setOnTouchListener(object :MyTouchListener(libraryRv.context){
//                    override fun onLongClick(motionEvent: MotionEvent?) {
//                        super.onLongClick(motionEvent)
//                        guideInOrder(order+1)
//                    }
//                })
//                setPositionByXY(libraryRv[0],character,MyOrientation.BOTTOM,50,false)
//                explainTextAnimation("アイテムを長押ししてみて！",
//                    MyOrientation.BOTTOM,character).start()
//            }
//            fun explainMultiSelectMode3(){
//                libraryRv[0].findViewById<ImageView>(R.id.btn_select).isSelected = true
//                libraryViewModel.setMultipleSelectMode(true)
//                goNextOnClickAnyWhere()
//            }
//            fun explainMultiSelectMode4(){
//                explainTextAnimation("こんなふうにまとめて選択できるよ",
//                    MyOrientation.BOTTOM,character).start()
//                goNextOnClickAnyWhere()
//            }
//            fun explainMultiSelectMode5(){
//                setHole(imvMultiModeMenu,HoleShape.CIRCLE)
//                addTouchArea(imvMultiModeMenu).setOnClickListener {
//                    libraryViewModel.setMultiMenuVisibility(true)
//                    guideInOrder(order+1)
//                }
//                explainTextAnimation("上のメニューで、削除を選んでね",
//                    MyOrientation.BOTTOM,character).start()
//            }
//            fun explainMultiSelectMode6(){
//                setHole(frameLayMultiModeMenu,HoleShape.RECTANGLE)
//                setPositionByXY(frameLayMultiModeMenu,character,MyOrientation.BOTTOM,50,false)
//                makeTxvGone()
//                goNextOnClickAnyWhere()
//            }
//            fun end(){
//                mainViewModel.setGuideVisibility(false)
//                libraryViewModel.setMultipleSelectMode(false)
//                libraryViewModel.setLeftSwipedItemExists(false)
//            }
//            when(order){
//                0   -> greeting()
//                1   -> explainBtn()
//                2   -> explainBtn2()
//                3   -> explainBtn3()
//                4   -> explainBtn4()
//                5   -> explainDeleteSystem()
//                6   -> explainDeleteSystem2()
//                7   -> explainDeleteSystem3()
//                8   -> explainDeleteSystem4()
//                9   -> explainDeleteSystem5()
//                10  -> explainDeleteSystem6()
//                11  -> explainMultiSelectMode()
//                12  -> explainMultiSelectMode2()
//                13  -> explainMultiSelectMode3()
//                14  -> explainMultiSelectMode4()
//                15  -> explainMultiSelectMode5()
//                16  -> explainMultiSelectMode6()
//                17  -> end()
//            }
//        }
//        guideInOrder(startOrder)

    }
}
