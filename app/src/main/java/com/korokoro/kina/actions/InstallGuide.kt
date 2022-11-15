package com.korokoro.kina.actions

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.view.View
import android.view.ViewTreeObserver
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.view.children
import com.korokoro.kina.R
import com.korokoro.kina.activity.MainActivity
import com.korokoro.kina.customClasses.enumClasses.BorderAttributes
import com.korokoro.kina.customClasses.enumClasses.MyHorizontalOrientation
import com.korokoro.kina.customClasses.enumClasses.MyOrientation
import com.korokoro.kina.customClasses.enumClasses.MyVerticalOrientation
import com.korokoro.kina.customClasses.normalClasses.BorderSet
import com.korokoro.kina.customClasses.normalClasses.MyOrientationSet
import com.korokoro.kina.customClasses.normalClasses.ViewAndPositionData
import com.korokoro.kina.customClasses.normalClasses.ViewAndSide
import com.korokoro.kina.databinding.CallOnInstallBinding
import com.korokoro.kina.databinding.TouchAreaBinding
import com.korokoro.kina.ui.animation.Animation
import com.korokoro.kina.ui.customViews.HoleShape


class InstallGuide(val activity:MainActivity,val onInstallBinding: CallOnInstallBinding){
    private val arrow = onInstallBinding.imvFocusArrow
    val character = onInstallBinding.imvCharacter
    val holeView = onInstallBinding.viewWithHole
    val textView = onInstallBinding.txvExplaino
    val conLaySpeakBubble = onInstallBinding.linLaySpeakBubble
    val globalLayoutSet = mutableMapOf<View, ViewTreeObserver.OnGlobalLayoutListener>()
    private val touchAreaTag = 1
    var createHoleShape: HoleShape = HoleShape.CIRCLE
    var createAnimateHole :Boolean = true
    var viewUnderHole:View? = null
        set(value) {
            field = value
            holeView.apply {
                holeShape = createHoleShape
                animate = createAnimateHole
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
    var textPosData: ViewAndSide = ViewAndSide(character, MyOrientation.TOP)
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
    private fun getPixelSize(dimenId:Int):Int{
        return activity.resources.getDimensionPixelSize(dimenId)
    }
    private fun saveBorderDataMap(view: View, set: BorderSet, borderDataMap:MutableMap<View, BorderSet>){
        if(borderDataMap[view]!=null) borderDataMap.remove(view)
        borderDataMap[view] = set
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
    fun setCharacterPos(){
        characterOrientation.borderAttributes = BorderAttributes.FillIfOutOfBorder
        setPositionByMargin(ViewAndPositionData(character,characterBorderSet,characterOrientation))
    }
    fun goNextOnClickAnyWhere(func:()->Unit){
        onInstallBinding.root.setOnClickListener {
            makeTouchAreaGone()
            func()
        }
    }
    private fun removeGlobalListener(globalLayoutSet: MutableMap<View, ViewTreeObserver.OnGlobalLayoutListener>){
        globalLayoutSet.onEach {
            it.key.viewTreeObserver.removeOnGlobalLayoutListener(it.value)
        }
    }
    fun changeSpeakBubbleVisibility(visible: Boolean):ValueAnimator{
        return appearAlphaAnimation(conLaySpeakBubble,visible)
    }
    fun changeCharacterVisibility(visible: Boolean):ValueAnimator{
        return appearAlphaAnimation(character,visible)
    }
    fun changeArrowVisibility(visible: Boolean):ValueAnimator{
        return appearAlphaAnimation(arrow,visible)
    }
    fun makeTouchAreaGone(){
        onInstallBinding.root.children.iterator().forEach {
            if(it.tag == 1) it.visibility = View.GONE
        }
    }
    fun appearAlphaAnimation(view :View, visible:Boolean): ValueAnimator {
        return Animation().appearAlphaAnimation(view,visible,if(view == holeView)0.7f else 1f)
    }
    fun getSimplePosRelation(standardView:View, orientation: MyOrientation, fit:Boolean): BorderSet {
        return ViewChangeActions().getSimpleBorderSet(standardView,orientation,fit)
    }
    fun setCharacterSize(dimenId: Int){
        character.apply {
            val sizeLarge = getPixelSize(dimenId)
            layoutParams.width = sizeLarge
            layoutParams.height = sizeLarge
            requestLayout()
        }
    }
    fun setUpFirstView(globalLayoutSet: MutableMap<View, ViewTreeObserver.OnGlobalLayoutListener>){
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
        setCharacterSize(R.dimen.character_size_large)
        changeCharacterVisibility(true).start()

    }
    fun speakBubbleTextAnimation(): AnimatorSet {

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
            playTogether(txvScaleAnimSet,bottomAnim1,bottomTransAnim,changeSpeakBubbleVisibility(true))
            txvScaleAnimSet.duration = finalDuration
            bottomTransAnim.duration = finalDuration
        }

        return finalAnim
    }
    private fun setArrowDirection(direction: MyOrientation){
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
    private val arrowMargin = 0
    fun setArrow(arrowPosition: MyOrientation,
                 view: View){
        appearAlphaAnimation(arrow,true).start()
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
             else ->  {
                 makeTouchAreaGone()
                 return
             }
         }
//         changeViewVisibility(arrow,true)

     }

    fun setTextPos(string: String):ValueAnimator{

        val anim = changeSpeakBubbleVisibility(false).apply {
            doOnEnd {
                changeViewVisibility(conLaySpeakBubble,false)
                textView.text = string
                val posData = ViewAndPositionData(
                    conLaySpeakBubble,
                    borderSet = spbBorderSet ,
                    orientation= spbOrientation)
                setPositionByMargin(posData)
                if(spbAppearOnEnd) speakBubbleTextAnimation().start()
            }
            duration = 100
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


}
