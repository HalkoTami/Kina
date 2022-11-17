package com.koronnu.kina.actions

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.view.View
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.view.children
import com.koronnu.kina.R
import com.koronnu.kina.activity.MainActivity
import com.koronnu.kina.customClasses.enumClasses.*
import com.koronnu.kina.customClasses.normalClasses.BorderSet
import com.koronnu.kina.customClasses.normalClasses.MyOrientationSet
import com.koronnu.kina.customClasses.normalClasses.ViewAndPositionData
import com.koronnu.kina.customClasses.normalClasses.ViewAndSide
import com.koronnu.kina.databinding.CallOnInstallBinding
import com.koronnu.kina.databinding.TouchAreaBinding
import com.koronnu.kina.ui.animation.Animation


class InstallGuide(val activity:MainActivity,val onInstallBinding: CallOnInstallBinding,val frameLay:FrameLayout){
    val arrow = onInstallBinding.imvFocusArrow
    val character = onInstallBinding.imvCharacter
    val holeView = onInstallBinding.viewWithHole
    val textView = onInstallBinding.txvSpeakBubble
    val conLaySpeakBubble = onInstallBinding.linLaySpeakBubble
    val globalLayoutSet = mutableMapOf<View, ViewTreeObserver.OnGlobalLayoutListener>()
    private val touchAreaTag = 1
    var holeShape: HoleShape = HoleShape.CIRCLE
    var animateHole :Boolean = true
    var viewUnderHole:View? = null
        set(value) {
            field = value
            holeView.apply {
                holeShape = this@InstallGuide.holeShape
                animate = animateHole
                viewUnderHole = value
            }

        }
    var characterBorderSet: BorderSet = BorderSet()
    var characterOrientation: MyOrientationSet =
        MyOrientationSet(MyVerticalOrientation.MIDDLE,
            MyHorizontalOrientation.MIDDLE,
            BorderAttributes.FillIfOutOfBorder)
        set(value) {
            value.borderAttributes = BorderAttributes.FillIfOutOfBorder
            field = value

        }
    var spbPosData: ViewAndSide = ViewAndSide(character, MyOrientation.TOP)
        set(value) {
            field = value
            spbBorderSet = ViewChangeActions().getSimpleBorderSet(value.view,value.side,textFit)
            spbOrientation = ViewChangeActions().getOriSetByNextToPosition(value.side, BorderAttributes.FillIfOutOfBorder)
        }
    var spbBorderSet: BorderSet = BorderSet(bottomSideSet = ViewAndSide(character,
        MyOrientation.TOP))
    var spbOrientation: MyOrientationSet = MyOrientationSet(MyVerticalOrientation.BOTTOM,
        MyHorizontalOrientation.MIDDLE,
        BorderAttributes.FillIfOutOfBorder)
    var textFit:Boolean = false
    var spbAppearOnEnd = true
    private val arrowMargin = 10
    private var freshCreated = true

    fun makeHereTouchable(view: View){
        onInstallBinding.apply {
            onInstallBinding.root.setOnClickListener(null)
            onInstallBinding.root.isClickable = false
            arrayOf(viewUnTouchableLeft,viewUnTouchableRight,viewUnTouchableBottom,viewUnTouchableTop).onEach {
                it.isClickable = true
            }
            val oriSet = MyOrientationSet(borderAttributes = BorderAttributes.FillBorder)
            setPositionByMargin(ViewAndPositionData(onInstallBinding.viewUnTouchableLeft,
                getSimplePosRelation(view,MyOrientation.LEFT,false), oriSet))
            setPositionByMargin(ViewAndPositionData(onInstallBinding.viewUnTouchableBottom,
                getSimplePosRelation(view,MyOrientation.BOTTOM,false), oriSet))
            setPositionByMargin(ViewAndPositionData(onInstallBinding.viewUnTouchableRight,
                getSimplePosRelation(view,MyOrientation.RIGHT,false), oriSet))
            setPositionByMargin(ViewAndPositionData(onInstallBinding.viewUnTouchableTop,
                getSimplePosRelation(view,MyOrientation.TOP,false),oriSet))
        }

    }

    fun setCharacterPos(){
        characterOrientation.borderAttributes = BorderAttributes.FillIfOutOfBorder
        setCharacterSize()
        setPositionByMargin(ViewAndPositionData(character,characterBorderSet,characterOrientation))
    }
    fun animateAllViewsGone(doOnEnd:()-> Unit):AnimatorSet{
        return AnimatorSet().apply {
            playTogether(changeCharacterVisibility(false){},changeArrowVisibility(false){},changeSpeakBubbleVisibility(false){})
            doOnEnd{
                doOnEnd()
            }
        }
    }
    fun animateCharacterPos(doAfterPosChanged: () -> Unit,doOnEnd: () -> Unit):ValueAnimator{
        return changeCharacterVisibility(false){
            setCharacterSize()
            characterOrientation.borderAttributes = BorderAttributes.FillIfOutOfBorder
            setPositionByMargin(ViewAndPositionData(character,characterBorderSet,characterOrientation))
            doAfterPosChanged()
            changeCharacterVisibility(true){doOnEnd()}.start()
        }

    }
    fun goNextOnClickAnyWhere(func:()->Unit){
        onInstallBinding.root.setOnClickListener {
            makeTouchAreaGone()
            func()
        }
    }
    fun changeSpeakBubbleVisibility(visible: Boolean,doOnEnd: () -> Unit):ValueAnimator{
        return appearAlphaAnimation(conLaySpeakBubble,visible){doOnEnd()}
    }
    fun changeCharacterVisibility(visible: Boolean,doOnEnd: () -> Unit):ValueAnimator{
        return appearAlphaAnimation(character,visible){doOnEnd()}
    }
    fun changeCharacterVisibilityOnEnd(visible: Boolean,doOnEnd: () -> Unit):ValueAnimator{
        return appearAlphaAnimation(character,visible){doOnEnd()}
    }
    fun changeArrowVisibility(visible: Boolean,doOnEnd: () -> Unit):ValueAnimator{
        return appearAlphaAnimation(arrow,visible){doOnEnd()}
    }
    fun makeTouchAreaGone(){
        onInstallBinding.root.children.iterator().forEach {
            if(it.tag == touchAreaTag) it.visibility = View.GONE
        }

    }
    fun appearAlphaAnimation(view :View, visible:Boolean,doOnEnd: () -> Unit): ValueAnimator {
        return Animation().appearAlphaAnimation(view,visible,if(view == holeView)0.7f else 1f){
            doOnEnd()
        }
    }
    fun getSimplePosRelation(standardView:View, orientation: MyOrientation, fit:Boolean): BorderSet {
        return ViewChangeActions().getSimpleBorderSet(standardView,orientation,fit)
    }
    var characterSizeDimenId:Int = R.dimen.character_size_large
    fun setCharacterSize(){
        character.apply {
            val sizeLarge = getPixelSize(characterSizeDimenId)
            layoutParams.width = sizeLarge
            layoutParams.height = sizeLarge
            requestLayout()
        }
    }
    fun setUpFirstView(){
        frameLay.removeAllViews()
        frameLay.addView(onInstallBinding.root)
        activity.mainActivityViewModel.setGuideVisibility(true)
        freshCreated = false
        makeTouchAreaGone()
        onInstallBinding.root.children.filter { it.id == touchAreaTag }.onEach {
            onInstallBinding.root.removeView(it)
        }
        changeMulVisibility(arrayOf(character,arrow,conLaySpeakBubble),false)
        holeView.initActivity(activity)
        removeHole()
        val characterPosData = ViewAndPositionData(
            character,
            BorderSet(),
            MyOrientationSet(
                MyVerticalOrientation.MIDDLE,
                MyHorizontalOrientation.MIDDLE,
                BorderAttributes.FillIfOutOfBorder)
        )
        setPositionByMargin(characterPosData)
        setCharacterSize()
        changeCharacterVisibility(true){}.start()

    }
    fun setArrow(arrowPosition: MyOrientation,
                 view: View){
        val getBorderSet = getSimplePosRelation(view,arrowPosition, true)
        val borderSetWithMargin = setMarginByNextToPosition(arrowPosition,arrowMargin,getBorderSet)
        val positionData = ViewAndPositionData(arrow,
            borderSetWithMargin,
            ViewChangeActions().getOriSetByNextToPosition(arrowPosition, BorderAttributes.None))

        setPositionByMargin(positionData)
        when(arrowPosition){
            MyOrientation.BOTTOM-> setArrowDirection(MyOrientation.TOP)
            MyOrientation.LEFT -> setArrowDirection(MyOrientation.RIGHT)
            MyOrientation.RIGHT -> setArrowDirection(MyOrientation.LEFT)
            MyOrientation.TOP -> setArrowDirection(MyOrientation.BOTTOM)
            else ->  {}
        }
        changeArrowVisibility(true){}.start()
    }
    fun animateArrow(arrowPosition: MyOrientation,
                 view: View){
        changeArrowVisibility(false){
            val getBorderSet = getSimplePosRelation(view,arrowPosition, true)
            val borderSetWithMargin = setMarginByNextToPosition(arrowPosition,arrowMargin,getBorderSet)
            val positionData = ViewAndPositionData(arrow,
                borderSetWithMargin,
                ViewChangeActions().getOriSetByNextToPosition(arrowPosition, BorderAttributes.None))

            setPositionByMargin(positionData)
            when(arrowPosition){
                MyOrientation.BOTTOM-> setArrowDirection(MyOrientation.TOP)
                MyOrientation.LEFT -> setArrowDirection(MyOrientation.RIGHT)
                MyOrientation.RIGHT -> setArrowDirection(MyOrientation.LEFT)
                MyOrientation.TOP -> setArrowDirection(MyOrientation.BOTTOM)
                else ->  {}
            }
            changeArrowVisibility(true){}.start()
        }.start()

//         changeViewVisibility(arrow,true)

     }

    fun animateSpbPosDoOnEnd(string: String,doOnEnd: () -> Unit):ValueAnimator{
        return changeSpeakBubbleVisibility(false) {
            changeViewVisibility(conLaySpeakBubble,false)
            textView.text = string
            val posData = ViewAndPositionData(
                conLaySpeakBubble,
                borderSet = spbBorderSet ,
                orientation= spbOrientation)
            setPositionByMargin(posData)
            if(spbAppearOnEnd) speakBubbleTextAnimation(){doOnEnd()}.start()
        }
    }
    fun setSpbPos(string: String){
        changeViewVisibility(conLaySpeakBubble,false)
        textView.text = string
        val posData = ViewAndPositionData(
            conLaySpeakBubble,
            borderSet = spbBorderSet ,
            orientation= spbOrientation)
        setPositionByMargin(posData)
    }

    fun animateSpbPos(string: String):ValueAnimator{

        val anim = changeSpeakBubbleVisibility(false){
            changeViewVisibility(conLaySpeakBubble,false)
            textView.text = string
            val posData = ViewAndPositionData(
                conLaySpeakBubble,
                borderSet = spbBorderSet ,
                orientation= spbOrientation)
            setPositionByMargin(posData)
            speakBubbleTextAnimation(){}.start()
        }

        return anim

    }
    fun removeHole(){
        holeView.removeGlobalLayout()
        holeView.noHole = true
    }
    fun cloneView(view: View) {
        copyViewInConLay(view).setOnClickListener {
            view.callOnClick()
        }
    }
    fun goNextOnClickTouchArea(view: View, func: () -> Unit) {
        onInstallBinding.root.setOnClickListener(null)
        copyViewInConLay(view).setOnClickListener {
            makeTouchAreaGone()
            func()
        }
    }
    fun copyViewInConLay(view:View,):View{
        onInstallBinding.root.setOnClickListener(null)
        val a = TouchAreaBinding.inflate(activity.layoutInflater)
        a.touchView.tag = 1

        val id = View.generateViewId()
        a.touchView.id = id
        onInstallBinding.root.addView(a.touchView)
        val con = ConstraintSet()
        con.clone(onInstallBinding.root)
//
        con.connect(id, ConstraintSet.RIGHT ,ConstraintSet.PARENT_ID,ConstraintSet.RIGHT)
        con.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID,ConstraintSet.TOP)
        con.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID,ConstraintSet.BOTTOM)
        con.connect(id, ConstraintSet.LEFT, ConstraintSet.PARENT_ID,ConstraintSet.LEFT)

        con.applyTo(onInstallBinding.root)
//        saveBorderDataMap(a.touchView, BorderSet(
//            topSideSet = ViewAndSide(view, MyOrientation.TOP),
//            bottomSideSet = ViewAndSide(view, MyOrientation.BOTTOM),
//            leftSideSet = ViewAndSide(view, MyOrientation.LEFT),
//            rightSideSet = ViewAndSide(view, MyOrientation.RIGHT),
//
//            ),borderDataMap
//        )
        val positionData = ViewAndPositionData(a.touchView
            ,getSimplePosRelation(view, MyOrientation.MIDDLE,true)
            , MyOrientationSet(MyVerticalOrientation.MIDDLE,
                MyHorizontalOrientation.MIDDLE,
                BorderAttributes.FillBorder))
        setPositionByMargin(
            positionData, )


        return a.touchView
    }
    fun setArrowDirection(direction: MyOrientation){
        arrow.rotation =
            when(direction){
                MyOrientation.BOTTOM-> -450f
                MyOrientation.LEFT -> 0f
                MyOrientation.RIGHT -> 900f
                MyOrientation.TOP -> 450f
                else -> return
            }

    }
    private fun setMarginByNextToPosition(movingViewPosition: MyOrientation, margin: Int, borderSet: BorderSet): BorderSet {
        when(movingViewPosition){
            MyOrientation.BOTTOM->   borderSet.margin.topMargin = margin
            MyOrientation.LEFT ->    borderSet.margin.rightMargin = margin
            MyOrientation.RIGHT ->   borderSet.margin.leftMargin = margin
            MyOrientation.TOP ->     borderSet.margin.bottomMargin = margin
            MyOrientation.MIDDLE -> {}
        }
        return borderSet
    }
    private fun speakBubbleTextAnimation(doOnEnd: () -> Unit): AnimatorSet {

        val finalDuration:Long = 200

        val txvScaleAnim1 = ValueAnimator.ofFloat(0.7f,1.1f)
        val txvScaleAnim2 = ValueAnimator.ofFloat(1.1f,1f)

        arrayOf(txvScaleAnim1,txvScaleAnim2).onEach { animator ->
            animator.addUpdateListener {
                val progressPer = it.animatedValue as Float
                ViewChangeActions().setScale(conLaySpeakBubble,progressPer,progressPer)
            }
        }
        val txvScaleAnimSet = AnimatorSet().apply {
            playSequentially( txvScaleAnim1,txvScaleAnim2)
            doOnStart {
                ViewChangeActions().setScale(conLaySpeakBubble,0.7f,0.7f)
//                ViewChangeActions().setScale(textView,0.7f,0.7f)

            }
            txvScaleAnim1.duration = finalDuration*0.7.toLong()
            txvScaleAnim2.duration = finalDuration*0.3.toLong()
        }
        val bottomAnim1 = ValueAnimator.ofFloat(0f,1f)

        val bottomTransAnim1 = ValueAnimator.ofFloat(1f,0.2f)
        val bottomTransAnim2 = ValueAnimator.ofFloat(0.4f,1.1f)
        val bottomTransAnim3 = ValueAnimator.ofFloat(1.1f,1f)
        arrayOf(bottomTransAnim1,bottomTransAnim2,bottomTransAnim3).onEach { animator ->
            animator.addUpdateListener {
                ViewChangeActions().setScale(onInstallBinding.sbBottom,it.animatedValue as Float,1f)
                onInstallBinding.sbBottom.pivotX = 0f
            }
        }

        val bottomTransAnim = AnimatorSet().apply {
            playSequentially( bottomTransAnim1,bottomTransAnim2,bottomTransAnim3)
        }
        val finalAnim = AnimatorSet().apply {
            playTogether(txvScaleAnimSet,bottomAnim1,bottomTransAnim,changeSpeakBubbleVisibility(true){})
            txvScaleAnimSet.duration = finalDuration
            bottomTransAnim.duration = finalDuration
            doOnEnd { doOnEnd() }
        }

        return finalAnim
    }
    private fun removeGlobalListener(globalLayoutSet: MutableMap<View, ViewTreeObserver.OnGlobalLayoutListener>){
        globalLayoutSet.onEach {
            it.key.viewTreeObserver.removeOnGlobalLayoutListener(it.value)
        }
    }
    private fun getPixelSize(dimenId:Int):Int{
        return activity.resources.getDimensionPixelSize(dimenId)
    }
    fun setPositionByMargin(positionData: ViewAndPositionData, ){
        removeGlobalListener(globalLayoutSet)
        val view = positionData.view
        view.viewTreeObserver.addOnGlobalLayoutListener(object :ViewTreeObserver.OnGlobalLayoutListener{
            override fun onGlobalLayout() {
                ViewChangeActions().setPositionByMargin(positionData,onInstallBinding.root,activity)
                globalLayoutSet[view] = this
            }
        })
        view.requestLayout()
    }

}
