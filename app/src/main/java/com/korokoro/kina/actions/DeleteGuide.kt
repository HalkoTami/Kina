package com.korokoro.kina.actions

import com.korokoro.kina.ui.viewmodel.DeletePopUpViewModel
import com.korokoro.kina.ui.viewmodel.EditFileViewModel
import com.korokoro.kina.ui.viewmodel.LibraryBaseViewModel
import com.korokoro.kina.ui.viewmodel.MainViewModel

class DeleteGuide(){
    fun deleteGuide(startOrder: Int, mainViewModel: MainViewModel,
                    libraryViewModel: LibraryBaseViewModel, editFileViewModel: EditFileViewModel,
                    deletePopUpViewModel: DeletePopUpViewModel
    ) {
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
//            val frameLayInBox       =activity.findViewById<ConstraintLayout>(R.id.frameLay_inBox)
//            val txvInBoxCardAmount       =activity.findViewById<TextView>(R.id.txv_inBox_card_amount)
//            val imvMultiModeMenu           =activity.findViewById<ImageView>(R.id.imv_change_menu_visibility)
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
