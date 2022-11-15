package com.korokoro.kina.actions

import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.children
import androidx.core.view.get
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import com.korokoro.kina.R
import com.korokoro.kina.activity.MainActivity
import com.korokoro.kina.customClasses.enumClasses.LibraryFragment
import com.korokoro.kina.customClasses.enumClasses.MyHorizontalOrientation
import com.korokoro.kina.customClasses.enumClasses.MyOrientation
import com.korokoro.kina.customClasses.enumClasses.MyVerticalOrientation
import com.korokoro.kina.customClasses.normalClasses.BorderSet
import com.korokoro.kina.customClasses.normalClasses.MyOrientationSet
import com.korokoro.kina.customClasses.normalClasses.ViewAndSide
import com.korokoro.kina.ui.customViews.HoleShape
import com.korokoro.kina.ui.listener.MyTouchListener
import com.korokoro.kina.ui.listener.recyclerview.LibraryRVItemClickListener

class EditGuide(val activity:MainActivity,
                frameLay: FrameLayout,){
    val actions = InstallGuide(activity,activity.callOnInstallBinding,frameLay)
    fun greeting1(){
        actions.apply {
            setUpFirstView()
            animateSpbPos("これから、\n単語帳を編集する方法を説明するよ").start()
            goNextOnClickAnyWhere{explainBtn()}
        }
    }
    private fun explainBtn(){
        val libraryRv                   =activity.findViewById<RecyclerView>(R.id.vocabCardRV)
        actions.apply {
            animateAllViewsGone {
                animateHole = true
                holeShape = HoleShape.RECTANGLE
                viewUnderHole = libraryRv[0]
                characterBorderSet = getSimplePosRelation(libraryRv[0],MyOrientation.BOTTOM,false)
                characterOrientation = MyOrientationSet(MyVerticalOrientation.TOP,MyHorizontalOrientation.LEFT)
                characterSizeDimenId = R.dimen.character_size_middle
                animateCharacterPos {
                    spbBorderSet = BorderSet(leftSideSet = ViewAndSide(character,MyOrientation.RIGHT), topSideSet = ViewAndSide(libraryRv[0],MyOrientation.BOTTOM))
                    spbOrientation = MyOrientationSet(MyVerticalOrientation.TOP,MyHorizontalOrientation.LEFT)
                    animateSpbPos("このアイテムを見てみよう").start()
                }.start()

            }.start()
            goNextOnClickAnyWhere {
                explainBtn2(libraryRv)
            }
        }
    }
    private fun explainBtn2(recycler:RecyclerView){
        actions.apply {
            animateSpbPos("編集ボタンを表示するには、" +
            "\nアイテムを横にスライドするよ").start()
            val rvItemTLis = object :LibraryRVItemClickListener(recycler.context,activity.findViewById(R.id.frameLay_test),recycler,activity.libraryViewModel){
                override fun doOnSwipeAppeared() {
                    super.doOnSwipeAppeared()

                }
            }
            copyViewInConLay(recycler[0]).setOnTouchListener(
                object :MyTouchListener(recycler.context){
                    override fun onScrollLeft(distanceX: Float, motionEvent: MotionEvent?) {
                        super.onScrollLeft(distanceX, motionEvent)
                            rvItemTLis.onScrollLeft(distanceX, motionEvent!!)
                    }
                    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                        v?.performClick()
                        rvItemTLis.onTouchEvent(recycler,event ?:return false)
                        return super.onTouch(v, event)
                    }
                }
            )

        }
//        explainTextAnimation("編集ボタンを表示するには、" +
//                "\nアイテムを横にスライドするよ", MyOrientation.BOTTOM,
//            character).start()
//        val area = addTouchArea(libraryRv)
//        area.setOnTouchListener(
//            object :MyTouchListener(libraryRv.context){
//                override fun onScrollLeft(distanceX: Float, motionEvent: MotionEvent?) {
//                    super.onScrollLeft(distanceX, motionEvent)
//                    val started = libraryRv.findChildViewUnder(motionEvent!!.x,motionEvent.y)
//                    if(started!=null&&libraryRv.indexOfChild(started)==0){
//                        val lineLaySwipeShow = started.findViewById<LinearLayoutCompat>(R.id.linLay_swipe_show) ?:return
//                        started.apply {
//                            if(started.tag== LibRVState.Plane){
//                                lineLaySwipeShow.layoutParams.width = 1
//                                lineLaySwipeShow.requestLayout()
//                                lineLaySwipeShow.children.iterator().forEach {
//                                    it.visibility = View.VISIBLE
//                                }
//                                lineLaySwipeShow.visibility = View.VISIBLE
//                                started.tag = LibRVState.LeftSwiping
//
//                            }else if(started.tag== LibRVState.LeftSwiping) {
//
//                                lineLaySwipeShow.layoutParams.width = distanceX.toInt()/5 + 1
//                                lineLaySwipeShow.requestLayout()
//
//                            }
//
//                        }
//                    }
//                }
//                override fun onTouch(v: View?, event: MotionEvent?): Boolean {
//                    if((event?.actionMasked== MotionEvent.ACTION_UP||event?.actionMasked == MotionEvent.ACTION_CANCEL)){
//                        val started = libraryRv[0]
//                        val lineLaySwipeShow = started.findViewById<LinearLayoutCompat>(R.id.linLay_swipe_show) ?:return false
//                        if(started.tag== LibRVState.LeftSwiping){
//                            if(lineLaySwipeShow.layoutParams.width <25){
//                                Animation().animateLibRVLeftSwipeLay(lineLaySwipeShow,false)
//                                started.tag = LibRVState.Plane
//                            }
//                            else if (lineLaySwipeShow.layoutParams.width>=25){
//                                Animation().animateLibRVLeftSwipeLay(lineLaySwipeShow ,true)
//                                started.tag = LibRVState.LeftSwiped
//                                libraryViewModel.setLeftSwipedItemExists(true)
//                                area.visibility = View.GONE
//                            }
//
//                        }
//
//                    }
//                    return super.onTouch(v, event)
//                }
//            }
//        )
//        onInstallBinding.root.setOnClickListener{
//            if(libraryViewModel.returnLeftSwipedItemExists())
//                guideInOrder(order+1)
//        }

    }
    fun editGuide(
    ){
//        mainViewModel.setGuideVisibility(true)
//        fun guideInOrder(order:Int){
//
//            val libraryRv                   =activity.findViewById<RecyclerView>(R.id.vocabCardRV)
//            val btnEditFile                   =activity.findViewById<ImageView>(R.id.btn_edit_whole)
//            val frameLayEditFile            =activity.findViewById<FrameLayout>(R.id.frameLay_edit_file)
//            val edtCreatingFileTitle        =activity.findViewById<EditText>(R.id.edt_file_title)
//            val imvColPalRed                =activity.findViewById<ImageView>(R.id.imv_col_red)
//            val imvColPalBlue               =activity.findViewById<ImageView>(R.id.imv_col_blue)
//            val imvColPaYellow              =activity.findViewById<ImageView>(R.id.imv_col_yellow)
//            val imvColPalGray               =activity.findViewById<ImageView>(R.id.imv_col_gray)
//            val btnFinish                   =activity.findViewById<Button>(R.id.btn_finish)
//            onInstallBinding.root.children.iterator().forEach { if(it.tag == 1) it.visibility = View.GONE }
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
//            fun greeting1(){
//                if(mainViewModel.returnFragmentStatus()?.now!= MainFragment.Library){
//                    mainViewModel.changeFragment(MainFragment.Library)
//                }
//                if(libraryViewModel.returnLibraryFragment()!= LibraryFragment.Home){
//                    libraryViewModel.returnLibraryNavCon()?.navigate(LibraryHomeFragDirections.toLibHome())
//                }
//                appearAlphaAnimation(holeView,true).start()
//                removeHole()
//                appearAlphaAnimation(character,true).start()
//                explainTextAnimation("これから、\n単語帳を編集する方法を説明するよ", MyOrientation.TOP,character).start()
//                goNextOnClickAnyWhere()
//            }
//            fun explainBtn(){
//                setHole(libraryRv[0])
//                setPositionByXY(libraryRv[0],character, MyOrientation.BOTTOM,20,false)
//                explainTextAnimation("このアイテムを見てみよう", MyOrientation.BOTTOM,character).start()
//                goNextOnClickAnyWhere()
//
//            }
//            fun explainBtn2(){
//                explainTextAnimation("編集ボタンを表示するには、" +
//                        "\nアイテムを横にスライドするよ", MyOrientation.BOTTOM,
//                    character).start()
//                val area = addTouchArea(libraryRv)
//                area.setOnTouchListener(
//                    object :MyTouchListener(libraryRv.context){
//                        override fun onScrollLeft(distanceX: Float, motionEvent: MotionEvent?) {
//                            super.onScrollLeft(distanceX, motionEvent)
//                            val started = libraryRv.findChildViewUnder(motionEvent!!.x,motionEvent.y)
//                            if(started!=null&&libraryRv.indexOfChild(started)==0){
//                                val lineLaySwipeShow = started.findViewById<LinearLayoutCompat>(R.id.linLay_swipe_show) ?:return
//                                started.apply {
//                                    if(started.tag== LibRVState.Plane){
//                                        lineLaySwipeShow.layoutParams.width = 1
//                                        lineLaySwipeShow.requestLayout()
//                                        lineLaySwipeShow.children.iterator().forEach {
//                                            it.visibility = View.VISIBLE
//                                        }
//                                        lineLaySwipeShow.visibility = View.VISIBLE
//                                        started.tag = LibRVState.LeftSwiping
//
//                                    }else if(started.tag== LibRVState.LeftSwiping) {
//
//                                        lineLaySwipeShow.layoutParams.width = distanceX.toInt()/5 + 1
//                                        lineLaySwipeShow.requestLayout()
//
//                                    }
//
//                                }
//                            }
//                        }
//                        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
//                            if((event?.actionMasked== MotionEvent.ACTION_UP||event?.actionMasked == MotionEvent.ACTION_CANCEL)){
//                                val started = libraryRv[0]
//                                val lineLaySwipeShow = started.findViewById<LinearLayoutCompat>(R.id.linLay_swipe_show) ?:return false
//                                if(started.tag== LibRVState.LeftSwiping){
//                                    if(lineLaySwipeShow.layoutParams.width <25){
//                                        Animation().animateLibRVLeftSwipeLay(lineLaySwipeShow,false)
//                                        started.tag = LibRVState.Plane
//                                    }
//                                    else if (lineLaySwipeShow.layoutParams.width>=25){
//                                        Animation().animateLibRVLeftSwipeLay(lineLaySwipeShow ,true)
//                                        started.tag = LibRVState.LeftSwiped
//                                        libraryViewModel.setLeftSwipedItemExists(true)
//                                        area.visibility = View.GONE
//                                    }
//
//                                }
//
//                            }
//                            return super.onTouch(v, event)
//                        }
//                    }
//                )
//                onInstallBinding.root.setOnClickListener{
//                    if(libraryViewModel.returnLeftSwipedItemExists())
//                        guideInOrder(order+1)
//                }
//
//            }
//            fun explainBtn3(){
//                goNextOnClickAnyWhere()
//                explainTextAnimation("編集してみよう", MyOrientation.BOTTOM,character).start()
//            }
//            fun explainBtn4(){
//                setHole(btnEditFile)
//                goNextOnClickTouchArea(btnEditFile)
//                setArrow(MyOrientation.LEFT,btnEditFile)
//                AnimatorSet().apply {
//                    playTogether(appearAlphaAnimation(character,false),
//                        appearAlphaAnimation(textView,false))
//                    start()
//                }
//            }
//            fun editFile1(){
//                setHole(frameLayEditFile)
//                createFileViewModel.onClickEditFileInRV(
//                    libraryViewModel.returnParentRVItems()[0] as File)
//                goNextOnClickAnyWhere()
//
//            }
//            fun editFile2(){
//                setPositionByXY(frameLayEditFile,character, MyOrientation.BOTTOM,10,false)
//                appearAlphaAnimation(character,true).start()
//                explainTextAnimation("じゃじゃん！", MyOrientation.TOP,frameLayEditFile).start()
//                goNextOnClickAnyWhere()
//            }
//            fun editFile3(){
//                setPositionByXY(frameLayEditFile,character, MyOrientation.BOTTOM,10,false)
//                explainTextAnimation("タイトルを変えたり", MyOrientation.TOP,frameLayEditFile).start()
//                setArrow(MyOrientation.BOTTOM,edtCreatingFileTitle)
//                goNextOnClickAnyWhere()
//            }
//            fun editFile4(){
//                setPositionByXY(frameLayEditFile,character, MyOrientation.BOTTOM,10,false)
//                explainTextAnimation("色で分けて整理してみてね", MyOrientation.TOP,frameLayEditFile).start()
//                arrayOf(imvColPaYellow,imvColPalBlue,imvColPalRed,imvColPalGray).onEach {
//                    cloneView(it)
//                }
//                setArrow(MyOrientation.BOTTOM,imvColPalRed)
//                goNextOnClickAnyWhere()
//            }
//            fun editFile5(){
//                addTouchArea(edtCreatingFileTitle).setOnClickListener {
//                    edtCreatingFileTitle.requestFocus()
//                    showKeyBoard(edtCreatingFileTitle,activity)
//                }
//                AnimatorSet().apply {
//                    playTogether(appearAlphaAnimation(character,false),
//                        appearAlphaAnimation(textView,false))
//                    start()
//                }
//                setArrow(MyOrientation.BOTTOM,btnFinish)
//                goNextOnClickTouchArea(btnFinish)
//            }
//            fun editFile6(){
//                createFileViewModel.onClickFinish(edtCreatingFileTitle.text.toString())
//                appearAlphaAnimation(holeView,false).start()
//                hideKeyBoard(edtCreatingFileTitle,activity)
//                mainViewModel.setGuideVisibility(false)
//            }
//
//            when(order){
//                0   -> greeting1()
//                1   -> explainBtn()
//                2   -> explainBtn2()
//                3   -> explainBtn3()
//                4   -> explainBtn4()
//                5   -> editFile1()
//                6   -> editFile2()
//                7   -> editFile3()
//                8   -> editFile4()
//                9   -> editFile5()
//                10  -> editFile6()
//            }
//        }
//        guideInOrder(startOrder)
    }
}
