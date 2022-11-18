package com.koronnu.kina.actions

import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import com.koronnu.kina.R
import com.koronnu.kina.activity.MainActivity
import com.koronnu.kina.customClasses.enumClasses.*
import com.koronnu.kina.customClasses.normalClasses.BorderSet
import com.koronnu.kina.customClasses.normalClasses.MyOrientationSet
import com.koronnu.kina.customClasses.normalClasses.ViewAndPositionData
import com.koronnu.kina.customClasses.normalClasses.ViewAndSide
import com.koronnu.kina.db.dataclass.File
import com.koronnu.kina.ui.listener.MyTouchListener
import com.koronnu.kina.ui.listener.recyclerview.LibraryRVItemClickListener

class EditGuide(val activity:MainActivity,
                frameLay: FrameLayout,){
    val actions = InstallGuide(activity,frameLay)
    fun greeting1(){
        actions.apply {
            callOnFirst()
            activity.libraryViewModel.makeAllUnSwiped()
            animateSpbPos("これから、\n単語帳を編集する方法を説明するよ").start()
            goNextOnClickAnyWhere{explainBtn()}
        }
    }
    private fun explainBtn(){
        val libraryRv                   =activity.findViewById<RecyclerView>(R.id.vocabCardRV)

        actions.apply {
            goNextOnClickAnyWhere {  }
            allConLayChildrenGoneAnimDoOnEnd= {
                animateHole = true
                holeShapeInGuide = HoleShape.RECTANGLE
                viewUnderSpotInGuide = libraryRv[0]

                characterBorderSet = getSimplePosRelation(libraryRv[0],MyOrientation.BOTTOM,false)
                characterOrientation = MyOrientationSet(MyVerticalOrientation.TOP,MyHorizontalOrientation.LEFT)
                characterSizeDimenId = R.dimen.character_size_middle
                doAfterCharacterPosChanged = { spbBorderSet = getSimplePosRelation(character,MyOrientation.RIGHT,true)
                    spbOrientation = MyOrientationSet(MyVerticalOrientation.MIDDLE,MyHorizontalOrientation.MIDDLE)
                    animateSpbPos("このアイテムを見てみよう").start()
                }
                characterPosChangeAnimDoOnEnd = { goNextOnClickAnyWhere { explainBtn2(libraryRv) }}
                getCharacterPosChangeAnim().start()
            }
            getAllConLayChildrenGoneAnim().start()

        }
    }
    private fun explainBtn2(recycler:RecyclerView){
        actions.apply {
            animateSpbPos("編集ボタンを表示するには、\nアイテムを横にスライドするよ").start()
            setArrowDirection(MyOrientation.LEFT)
            setPositionByMargin(ViewAndPositionData(arrow,getSimplePosRelation(recycler[0],MyOrientation.MIDDLE,true),
                MyOrientationSet(MyVerticalOrientation.MIDDLE,MyHorizontalOrientation.MIDDLE)))
            getArrowVisibilityAnim(true).start()
            val rvItemTLis =  object :LibraryRVItemClickListener(recycler.context,activity.findViewById(R.id.frameLay_test),recycler,activity.libraryViewModel){
                override fun doOnSwipeAppeared() {
                    super.doOnSwipeAppeared()
                    explainBtn3()
                }
            }
            addViewToConLay(recycler[0]).setOnTouchListener(
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
        val btnEditFile=activity.findViewById<ImageView>(R.id.btn_edit_whole)
        actions.apply {
            makeTouchAreaGone()
            setArrow(MyOrientation.LEFT,btnEditFile)
            animateSpbPos("編集してみよう").start()
            goNextOnClickAnyWhere{explainBtn4(btnEditFile)}
        }
    }
    private fun explainBtn4(btnEditFile:ImageView){
        actions.apply {
            viewUnderSpotInGuide = btnEditFile
            setArrow(MyOrientation.LEFT,btnEditFile)
            goNextOnClickTouchArea(btnEditFile){
                editFile1()}
        }
    }
    private fun editFile1(){
        val frameLayEditFile=activity.findViewById<FrameLayout>(R.id.frameLay_edit_file)
        actions.apply {
            allConLayChildrenGoneAnimDoOnEnd = {
                guideParentConLay.setOnClickListener(null)
                holeShapeInGuide = HoleShape.RECTANGLE
                animateHole = false
                viewUnderSpotInGuide = frameLayEditFile
                activity.createFileViewModel.onClickEditFileInRV(
                    activity.libraryViewModel.returnParentRVItems()[0] as File)
                editFile2(frameLayEditFile)
            }
            getAllConLayChildrenGoneAnim().start()
        }
    }
    private fun editFile2(frameLayEditFile:FrameLayout){
        actions.apply {
            characterBorderSet = BorderSet(bottomSideSet = ViewAndSide(frameLayEditFile,MyOrientation.TOP))
            characterOrientation = MyOrientationSet(MyVerticalOrientation.BOTTOM,MyHorizontalOrientation.LEFT)
            characterPosChangeAnimDoOnEnd = {}
            doAfterCharacterPosChanged = {
                spbOrientation = MyOrientationSet(MyVerticalOrientation.MIDDLE,MyHorizontalOrientation.MIDDLE)
                animateSpbPos("じゃじゃん！").start()
            }
            getCharacterPosChangeAnim ().start()
            goNextOnClickAnyWhere { editFile3() }
        }

    }
    private fun editFile3(){
        val edtCreatingFileTitle        =activity.findViewById<EditText>(R.id.edt_file_title)
        actions.apply {
            setArrow(MyOrientation.BOTTOM,edtCreatingFileTitle)
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

            getArrowVisibilityAnim(false).start()
            val frameLayEditFile=activity.findViewById<FrameLayout>(R.id.frameLay_edit_file)
            val btnFinish                   =activity.findViewById<Button>(R.id.btn_finish)
            val btnClose                    =activity.findViewById<ImageView>(R.id.btn_close)
            addViewToConLay(btnClose).setOnClickListener{
                btnClose.performClick()
                editFile6()
            }
            addViewToConLay(btnFinish).setOnClickListener {
                btnFinish.performClick()
                editFile6()
            }
            makeHereTouchable(frameLayEditFile)
        }
    }
    private fun editFile6(){
        actions.apply {
            guideParentConLay.isEnabled = false
            allConLayChildrenGoneAnimDoOnEnd = {
                characterBorderSet = BorderSet()
                characterOrientation = MyOrientationSet()
                doAfterCharacterPosChanged = {
                    spbPosSimple = ViewAndSide(character,MyOrientation.TOP)
                    animateSpbPos("ガイドは以上だよ").start()
                }
                getCharacterPosChangeAnim().start()
                guideParentConLay.isEnabled = true
                goNextOnClickAnyWhere {
                    appearAlphaAnimDonOnEnd= {
                        activity.mainActivityViewModel.setGuideVisibility(false)
                    }
                    getAppearAlphaAnimation(guideParentConLay,false).start()
                }
            }
            getAllConLayChildrenGoneAnim().start()

        }
    }
}
