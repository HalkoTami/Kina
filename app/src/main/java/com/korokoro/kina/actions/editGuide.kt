package com.korokoro.kina.actions

import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import com.korokoro.kina.R
import com.korokoro.kina.activity.MainActivity
import com.korokoro.kina.customClasses.enumClasses.MyHorizontalOrientation
import com.korokoro.kina.customClasses.enumClasses.MyOrientation
import com.korokoro.kina.customClasses.enumClasses.MyVerticalOrientation
import com.korokoro.kina.customClasses.normalClasses.BorderSet
import com.korokoro.kina.customClasses.normalClasses.MyOrientationSet
import com.korokoro.kina.customClasses.normalClasses.ViewAndPositionData
import com.korokoro.kina.customClasses.normalClasses.ViewAndSide
import com.korokoro.kina.db.dataclass.File
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
                    spbBorderSet = getSimplePosRelation(character,MyOrientation.RIGHT,true)
                    spbOrientation = MyOrientationSet(MyVerticalOrientation.MIDDLE,MyHorizontalOrientation.MIDDLE)
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
            animateSpbPos("編集ボタンを表示するには、アイテムを横にスライドするよ").start()
            setArrowDirection(MyOrientation.LEFT)
            setPositionByMargin(ViewAndPositionData(arrow,getSimplePosRelation(recycler[0],MyOrientation.MIDDLE,true),
                MyOrientationSet(MyVerticalOrientation.MIDDLE,MyHorizontalOrientation.MIDDLE)))
            changeArrowVisibility(true).start()
            val rvItemTLis = object :LibraryRVItemClickListener(recycler.context,activity.findViewById(R.id.frameLay_test),recycler,activity.libraryViewModel){
                override fun doOnSwipeAppeared() {
                    super.doOnSwipeAppeared()
                    explainBtn3()

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
    }
    private fun explainBtn3(){
        actions.apply {
            makeTouchAreaGone()
            animateSpbPos("編集してみよう").start()
            goNextOnClickAnyWhere{explainBtn4()}
        }
    }
    private fun explainBtn4(){
        val btnEditFile=activity.findViewById<ImageView>(R.id.btn_edit_whole)
        actions.apply {
            viewUnderHole = btnEditFile
            setArrow(MyOrientation.LEFT,btnEditFile)
            goNextOnClickTouchArea(btnEditFile){
                editFile1()}
        }
    }
    private fun editFile1(){
        val frameLayEditFile=activity.findViewById<FrameLayout>(R.id.frameLay_edit_file)
        actions.apply {
            animateAllViewsGone {
                onInstallBinding.root.setOnClickListener(null)
                holeShape = HoleShape.RECTANGLE
                animateHole = false
                viewUnderHole = frameLayEditFile
                activity.createFileViewModel.onClickEditFileInRV(
                    activity.libraryViewModel.returnParentRVItems()[0] as File)
                editFile2(frameLayEditFile)
            }.start()
        }
    }
    private fun editFile2(frameLayEditFile:FrameLayout){
        actions.apply {
            characterBorderSet = BorderSet(bottomSideSet = ViewAndSide(frameLayEditFile,MyOrientation.TOP))
            characterOrientation = MyOrientationSet(MyVerticalOrientation.BOTTOM,MyHorizontalOrientation.LEFT)
            animateCharacterPos {
                spbOrientation = MyOrientationSet(MyVerticalOrientation.MIDDLE,MyHorizontalOrientation.LEFT)
                animateSpbPosDoOnEnd("じゃじゃん！"){
                    goNextOnClickAnyWhere { editFile3() }
                }.start()
            }.start()
        }
    }
    private fun editFile3(){
//        val edtCreatingFileTitle        =activity.findViewById<EditText>(R.id.edt_file_title)
        actions.apply {
//            setArrow(MyOrientation.BOTTOM,edtCreatingFileTitle)
            animateSpbPos("タイトルを変えたり").start()
            goNextOnClickAnyWhere { editFile4prt1() }
        }
    }
    private fun editFile4prt1(){
        actions.apply {
            animateSpbPos("色で分けて整理してみてね").start()
            val imvColPallet                =activity.findViewById<ImageView>(R.id.imv_icon_palet)

            setArrow(MyOrientation.BOTTOM,imvColPallet)
            goNextOnClickAnyWhere{editFile4prt2()}
        }
    }
    private fun editFile4prt2(){
        actions.apply {
            val imvColPalRed                =activity.findViewById<ImageView>(R.id.imv_col_red)
            setArrow(MyOrientation.BOTTOM,imvColPalRed)
            imvColPalRed.performClick()
            goNextOnClickAnyWhere{editFile4prt3()}
        }
    }
    private fun editFile4prt3(){
        actions.apply {
            actions.apply {
                val imvColPalBlue                =activity.findViewById<ImageView>(R.id.imv_col_blue)
                setArrow(MyOrientation.BOTTOM,imvColPalBlue)
                imvColPalBlue.performClick()
                goNextOnClickAnyWhere{editFile4prt4()}
            }
        }
    }
    private fun editFile4prt4(){
        actions.apply {
            actions.apply {
                val imvColPalYellow                =activity.findViewById<ImageView>(R.id.imv_col_yellow)
                setArrow(MyOrientation.BOTTOM,imvColPalYellow)
                imvColPalYellow.performClick()
                goNextOnClickAnyWhere{editFile4prt5()}
            }
        }
    }
    private fun editFile4prt5(){
        actions.apply {
            changeArrowVisibility(false).start()
            val imvColPalRed                =activity.findViewById<ImageView>(R.id.imv_col_red)
            val imvColPalBlue               =activity.findViewById<ImageView>(R.id.imv_col_blue)
            val imvColPaYellow              =activity.findViewById<ImageView>(R.id.imv_col_yellow)
            val imvColPalGray               =activity.findViewById<ImageView>(R.id.imv_col_gray)
            val edtCreatingFileTitle        =activity.findViewById<EditText>(R.id.edt_file_title)
            val btnFinish                   =activity.findViewById<Button>(R.id.btn_finish)
            arrayOf(imvColPaYellow,imvColPalBlue,imvColPalRed,imvColPalGray).iterator().forEach {
                cloneView(it)
            }

            copyViewInConLay(edtCreatingFileTitle) .setOnClickListener{
                edtCreatingFileTitle.requestFocus()
                showKeyBoard(edtCreatingFileTitle,activity)
                makeToast(btnFinish.context,"hello")
            }

            goNextOnClickTouchArea(btnFinish){
                btnFinish.performClick()
            }
        }
    }

    fun editGuide(
    ){
//        mainViewModel.setGuideVisibility(true)
//        fun guideInOrder(order:Int){
//
//            val libraryRv                   =activity.findViewById<RecyclerView>(R.id.vocabCardRV)
//
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
//
//
//
//
//




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
