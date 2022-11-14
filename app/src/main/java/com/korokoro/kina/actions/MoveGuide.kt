package com.korokoro.kina.actions

import androidx.appcompat.app.AppCompatActivity
import com.korokoro.kina.databinding.CallOnInstallBinding
import com.korokoro.kina.ui.viewmodel.*


class MoveGuide(val activity:AppCompatActivity, val onInstallBinding: CallOnInstallBinding){
    fun moveGuide(startOrder: Int,mainViewModel: MainViewModel,
                  libraryViewModel:LibraryBaseViewModel,editFileViewModel: EditFileViewModel,
                  moveToViewModel: ChooseFileMoveToViewModel,
                  createCardViewModel: CreateCardViewModel
    ){
//        mainViewModel.setGuideVisibility(true)
//        var selectedView:View? = null
//        fun guideInOrder(order: Int){
//
//            onInstallBinding.root.children.iterator().forEach { if(it.tag == 1) it.visibility = View.GONE }
//            val libraryRv                   =activity.findViewById<RecyclerView>(R.id.vocabCardRV)
//            val imvMultiModeMenu           =activity.findViewById<ImageView>(R.id.imv_change_menu_visibility)
//            val frameLayMultiModeMenu       =activity.findViewById<FrameLayout>(R.id.frameLay_multi_mode_menu)
//            val lineLayMoveSelectedItem       =activity.findViewById<LinearLayoutCompat>(R.id.linLay_move_selected_items)
//            val bnvBtnAdd                   =activity.findViewById<ImageView>(R.id.bnv_imv_add)
//            val createMenuImvFlashCard      =activity.findViewById<ImageView>(R.id.imvnewTangocho)
//            val createMenuImvFolder      =activity.findViewById<ImageView>(R.id.imvnewfolder)
//            val frameLayBottomMenu      =activity.findViewById<FrameLayout>(R.id.frame_bottomMenu)
//            val btnFinish                   =activity.findViewById<Button>(R.id.btn_finish)
//            val frameLayPopUpConfirmMove       =activity.findViewById<FrameLayout>(R.id.frameLay_confirm_move)
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
//                removeHole()
//                appearAlphaAnimation(holeView,true).start()
//                appearAlphaAnimation(character,true).start()
//                explainTextAnimation("これから、\nアイテムを移動する方法を説明するよ", MyOrientation.TOP,character).start()
//                goNextOnClickAnyWhere()
//            }
//            fun greeting2(){
//                explainTextAnimation("フォルダを整理したり、\nインボックスのカードを移動するよ！", MyOrientation.TOP,character).start()
//                goNextOnClickAnyWhere()
//            }
//            fun guideMultiMode(){
//                setHole(libraryRv,HoleShape.RECTANGLE)
//                explainTextAnimation("長押ししてみよう",MyOrientation.TOP,character).start()
//                addTouchArea(libraryRv).setOnTouchListener(object :MyTouchListener(libraryRv.context){
//                    override fun onLongClick(motionEvent: MotionEvent?) {
//                        super.onLongClick(motionEvent)
//                        selectedView = libraryRv.findChildViewUnder(motionEvent!!.x,motionEvent.y)
//                        if(selectedView!=null){
//                            guideInOrder(order+1)
//                        }
//                    }
//                })
//
//            }
//            fun guideMultiMode2(){
//                libraryViewModel.setMultipleSelectMode(true)
//                setHole(selectedView!!,HoleShape.RECTANGLE)
//                selectedView!!.findViewById<ImageView>(R.id.btn_select).apply {
//                    isSelected = true
//                }
//                val position = libraryRv.getChildAdapterPosition(selectedView!!)
//                libraryViewModel.onClickSelectableItem(libraryViewModel.returnParentRVItems()[position],true)
//                goNextOnClickAnyWhere()
//            }
//            fun guideMultiMode3(){
//                explainTextAnimation("メニューを開くよ",MyOrientation.TOP,character).start()
//                setHole(imvMultiModeMenu,HoleShape.CIRCLE)
//                goNextOnClickTouchArea(imvMultiModeMenu)
//            }
//            fun guideMultiMode4(){
//                makeTxvGone()
//                appearAlphaAnimation(character,false).start()
//                libraryViewModel.setMultiMenuVisibility(true)
//                setHole(frameLayMultiModeMenu,HoleShape.RECTANGLE)
//                setArrow(MyOrientation.LEFT,lineLayMoveSelectedItem)
//                goNextOnClickTouchArea(lineLayMoveSelectedItem)
//            }
//            fun moveToFragment(){
//                appearAlphaAnimation(holeView,false).apply {
//                    doOnEnd {
//                        changeViewVisibility(holeView,false)
//                        libraryViewModel.returnLibraryNavCon()?.navigate(LibraryChooseFileMoveToFragDirections.selectFileMoveTo(null))
//                    }
//                    start()
//                }
//                goNextOnClickAnyWhere()
//            }
//            fun moveToFragment2(){
//                setPositionByXY(bnvBtnAdd,character,MyOrientation.TOP,0,false)
//                appearAlphaAnimation(character,true).start()
//                explainTextAnimation("アイテムの移動先が表示されるよ",MyOrientation.TOP,character).start()
//                onInstallBinding.root.setOnClickListener {
//                    if(libraryRv.size==0) guideInOrder(order+1) else guideInOrder(8)
//                }
//            }
//            fun moveToFragment3(){
//                explainTextAnimation("また移動先がないね",MyOrientation.TOP,character).start()
//                goNextOnClickAnyWhere()
//            }
//            fun createMovableItem(){
//                explainTextAnimation("追加してみよう",MyOrientation.TOP,character).start()
//                goNextOnClickAnyWhere()
//            }
//            fun createMovableItem2(){
//                setHole(bnvBtnAdd,HoleShape.CIRCLE)
//                appearAlphaAnimation(holeView,true).start()
//                goNextOnClickTouchArea(bnvBtnAdd)
//            }
//            fun createMovableItem3(){
//                editFileViewModel.setBottomMenuVisible(true)
//                addTouchArea(createMenuImvFolder).setOnClickListener {
//                    editFileViewModel.onClickCreateFile(FileStatus.FOLDER)
//                    guideInOrder(order+1)
//                }
//                addTouchArea(createMenuImvFlashCard).setOnClickListener {
//                    editFileViewModel.onClickCreateFile(FileStatus.FLASHCARD_COVER)
//                    guideInOrder(order+1)
//                }
//                setHole(frameLayBottomMenu,HoleShape.RECTANGLE)
//            }
//            fun createMovableItem4(){
//                createGuide(5,createCardViewModel,editFileViewModel,libraryViewModel,mainViewModel)
//                goNextOnClickTouchArea(btnFinish)
//            }
//            fun createMovableItem5(){
//                val lastId = libraryRv.size
//                createGuide(6,createCardViewModel,editFileViewModel,libraryViewModel,mainViewModel)
//                libraryRv.itemAnimator = object:DefaultItemAnimator(){
//                    override fun onAnimationFinished(viewHolder: RecyclerView.ViewHolder) {
//                        super.onAnimationFinished(viewHolder)
//                        val rv = activity.findViewById<RecyclerView>(R.id.vocabCardRV)
//                        val newLastId = rv.size
//                        if (lastId + 1 == newLastId) {
//                            guideInOrder(order + 1)
//                        }
//                    }
//                }
//            }
//            fun createMovableItem6(){
//                createGuide(7,createCardViewModel,editFileViewModel,libraryViewModel,mainViewModel)
//                goNextOnClickAnyWhere()
//            }
//            fun selectMovableItem(){
//                setHole(libraryRv,HoleShape.RECTANGLE)
//                explainTextAnimation("移動先を選択できるよ",MyOrientation.TOP,character).start()
//                goNextOnClickAnyWhere()
//            }
//            fun selectMovableItem2(){
//                explainTextAnimation("カードは単語帳、\n単語帳はフォルダへ移動することしかできないよ！",MyOrientation.TOP,character).start()
//                goNextOnClickAnyWhere()
//            }
//            fun selectMovableItem3(){
//                val linLayMove = libraryRv[0].findViewById<LinearLayoutCompat>(R.id.linLay_move_selected_items)
//                setHole(linLayMove,HoleShape.RECTANGLE)
//                setPositionByXY(libraryRv[0],character,MyOrientation.BOTTOM,10,false)
//                explainTextAnimation("ここをタップして移動するよ",MyOrientation.BOTTOM,character).start()
//                setArrow(MyOrientation.BOTTOM,linLayMove)
//                goNextOnClickTouchArea(linLayMove)
//            }
//            fun explainPopUp(){
//                moveToViewModel.setPopUpVisible(true)
//                makeArrowGone()
//                setHole(frameLayPopUpConfirmMove,HoleShape.RECTANGLE)
//                setPositionByXY(frameLayPopUpConfirmMove,character,MyOrientation.BOTTOM,0,false)
//                explainTextAnimation("最後は確認画面が表示されるよ",MyOrientation.TOP,frameLayPopUpConfirmMove).start()
//                goNextOnClickAnyWhere()
//            }
//            fun end(){
//                appearAlphaAnimation(holeView,false).start()
//                appearAlphaAnimation(character,false).doOnEnd {
//                    mainViewModel.setGuideVisibility(false)
//                }
//                makeTxvGone()
//                makeArrowGone()
//                moveToViewModel.setPopUpVisible(false)
//            }
//            when(order){
//                0   -> greeting()
//                1   -> greeting2()
//                2   -> guideMultiMode()
//                3   -> guideMultiMode2()
//                4   -> guideMultiMode3()
//                5   -> guideMultiMode4()
//                6   -> moveToFragment()
//                7   -> moveToFragment2()
//                8   -> moveToFragment3()
//                9   -> createMovableItem()
//                10  -> createMovableItem2()
//                11  -> createMovableItem3()
//                12  -> createMovableItem4()
//                13  -> createMovableItem5()
//                14  -> createMovableItem6()
//                15  -> selectMovableItem()
//                16  -> selectMovableItem2()
//                17  -> selectMovableItem3()
//                18  -> explainPopUp()
//                19  -> end()
//            }
//        }
//        guideInOrder(startOrder)
    }
}
