package com.korokoro.kina.actions

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.view.View
import android.view.ViewGroup.LayoutParams
import android.view.ViewTreeObserver
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.view.children
import com.korokoro.kina.R
import com.korokoro.kina.customClasses.*
import com.korokoro.kina.databinding.CallOnInstallBinding
import com.korokoro.kina.databinding.TouchAreaBinding
import com.korokoro.kina.ui.animation.Animation


class InstallGuide(val activity:AppCompatActivity,val onInstallBinding: CallOnInstallBinding){
    val arrow = onInstallBinding.imvFocusArrow
    val character = onInstallBinding.imvCharacter
    val holeView = onInstallBinding.viewWithHole
    val textView = onInstallBinding.txvExplaino
    val conLaySpeakBubble = onInstallBinding.linLaySpeakBubble
    val touchAreaTag = 1



    fun getPixelSize(dimenId:Int):Int{
        return activity.resources.getDimensionPixelSize(dimenId)
    }

    fun saveBorderDataMap(view: View,set:BorderSet,borderDataMap:MutableMap<View,BorderSet>){
        if(borderDataMap[view]!=null) borderDataMap.remove(view)
        borderDataMap[view] = set
    }

    fun alphaAnimatePos(data: ViewAndPositionData,globalLayoutSet: MutableMap<View, ViewTreeObserver.OnGlobalLayoutListener>):AnimatorSet{
        val view = data.view
        val disappear = appearAlphaAnimation(view,false)
        disappear.doOnEnd {
            changeViewVisibility(view,false)
            setPositionByMargin(data,globalLayoutSet)
        }
        val appear =  appearAlphaAnimation(view,true)
        return AnimatorSet().apply {

            playSequentially(disappear,appear)
            duration= 500
        }


    }



    fun setPositionByMargin(positionData: ViewAndPositionData,
                            globalLayoutSet: MutableMap<View, ViewTreeObserver.OnGlobalLayoutListener>, ){
        removeGlobalListener(globalLayoutSet)
        val view = positionData.view
        view.viewTreeObserver.addOnGlobalLayoutListener(object :ViewTreeObserver.OnGlobalLayoutListener{
            override fun onGlobalLayout() {
                ViewChangeActions().setPositionByMargin(positionData,onInstallBinding.root)
                globalLayoutSet[view] = this
            }
        })
    }

    fun removeGlobalListener(globalLayoutSet: MutableMap<View, ViewTreeObserver.OnGlobalLayoutListener>){
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
    fun getSimplePosRelation(standardView:View, orientation: MyOrientation, fit:Boolean):BorderSet{
        return ViewChangeActions().getSimpleBorderSet(standardView,orientation,fit)
    }

    fun setUpFirstView(globalLayoutSet: MutableMap<View, ViewTreeObserver.OnGlobalLayoutListener>){
        onInstallBinding.root.children.filter { it.id == touchAreaTag }.onEach {
            onInstallBinding.root.removeView(it)
        }
        changeMulVisibility(arrayOf(character,arrow,conLaySpeakBubble),false)
        removeHole()
        val characterPosData = ViewAndPositionData(
            character,
            BorderSet(),
            MyOrientationSetNew(
                MyVerticalOrientation.MIDDLE,
                MyHorizontalOrientation.MIDDLE,
                BorderAttributes.FillIfOutOfBorder)
        )
        setPositionByMargin(characterPosData,globalLayoutSet,)
        character.apply {
            val sizeLarge = getPixelSize(R.dimen.character_size_large)
            layoutParams.width = sizeLarge
            layoutParams.height = sizeLarge
            requestLayout()
        }
        changeCharacterVisibility(true).start()

    }
    fun setHoleMargin(int: Int){
        holeView.holeMargin = int
    }
    fun setHoleRecRadius(int: Int){
        holeView.recRadius = int.toFloat()
    }
     fun explainTextAnimation(string: String, orientation: MyOrientation,view: View,fit: Boolean
                              ,globalLayoutSet: MutableMap<View, ViewTreeObserver.OnGlobalLayoutListener>): AnimatorSet {


        textView.text = string
        textView.layoutParams.width = LayoutParams.WRAP_CONTENT
        textView.requestLayout()
         onInstallBinding.linLaySpeakBubble.requestLayout()
         val posData = ViewAndPositionData(
             conLaySpeakBubble,
             getSimplePosRelation(view,orientation,fit) ,
             getOriSetByNextToPosition(orientation,BorderAttributes.FillIfOutOfBorder))
         setPositionByMargin(posData,globalLayoutSet)
         conLaySpeakBubble.visibility = if(string=="") View.GONE else View.VISIBLE


        val finalDuration:Long = 200

         val anim1 = ValueAnimator.ofFloat(0.7f,1.1f)
         val anim2 = ValueAnimator.ofFloat(1.1f,1f)

         arrayOf(anim1,anim2).onEach {
             it.addUpdateListener {
                 val progressPer = it.animatedValue as Float
                 ViewChangeActions().setScale(conLaySpeakBubble,progressPer,progressPer)
                 ViewChangeActions().setScale(textView,progressPer,progressPer)

             }
         }
        val scaleAnim = AnimatorSet().apply {
            playSequentially( anim1,anim2)
            doOnStart {
                ViewChangeActions().setScale(textView,0.7f,0.7f)
                ViewChangeActions().setScale(conLaySpeakBubble,0.7f,0.7f)
            }
            anim1.duration = finalDuration*0.7.toLong()
            anim2.duration = finalDuration*0.3.toLong()
        }
         val bottomAnim1 = ValueAnimator.ofFloat(0f,1f)

         val bottomTransAnim1 = ValueAnimator.ofFloat(1f,0.2f)
         val bottomTransAnim2 = ValueAnimator.ofFloat(0.4f,1.1f)
         val bottomTransAnim3 = ValueAnimator.ofFloat(1.1f,1f)
         arrayOf(bottomTransAnim1,bottomTransAnim2,bottomTransAnim3).onEach {
             it.addUpdateListener {
                 ViewChangeActions().setScale(onInstallBinding.sbBottom,it.animatedValue as Float,1f)
                 onInstallBinding.sbBottom.pivotX = 0f
             }
         }

         val bottomTransAnim = AnimatorSet().apply {
             playSequentially( bottomTransAnim1,bottomTransAnim2,bottomTransAnim3)
         }
        val finalAnim = AnimatorSet().apply {
            playTogether(scaleAnim,bottomAnim1,bottomTransAnim)
            scaleAnim.duration = finalDuration
            bottomTransAnim.duration = finalDuration
        }

        return finalAnim
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
    fun getOriSetByNextToPosition(movingViewPosition:MyOrientation,attributes: BorderAttributes):MyOrientationSetNew{
        return when(movingViewPosition){
            MyOrientation.BOTTOM-> MyOrientationSetNew(MyVerticalOrientation.TOP , MyHorizontalOrientation.MIDDLE,attributes )
            MyOrientation.LEFT -> MyOrientationSetNew( MyVerticalOrientation.MIDDLE, MyHorizontalOrientation.RIGHT,attributes)
            MyOrientation.RIGHT -> MyOrientationSetNew(MyVerticalOrientation.MIDDLE , MyHorizontalOrientation.LEFT,attributes )
            MyOrientation.TOP -> MyOrientationSetNew(MyVerticalOrientation.BOTTOM, MyHorizontalOrientation.MIDDLE ,attributes)
            else -> MyOrientationSetNew(MyVerticalOrientation.MIDDLE,MyHorizontalOrientation.MIDDLE,attributes)
        }
    }
    fun setMarginByNextToPosition(movingViewPosition:MyOrientation,margin: Int,borderSet: BorderSet):BorderSet{
        when(movingViewPosition){
            MyOrientation.BOTTOM->   borderSet.margin.topMargin = margin
            MyOrientation.LEFT ->    borderSet.margin.rightMargin = margin
            MyOrientation.RIGHT ->   borderSet.margin.leftMargin = margin
            MyOrientation.TOP ->     borderSet.margin.bottomMargin = margin
            MyOrientation.MIDDLE -> {}
        }
        return borderSet
    }
     fun setArrow(arrowPosition: MyOrientation,
                  view: View,
                  globalLayoutSet: MutableMap<View, ViewTreeObserver.OnGlobalLayoutListener>,
                  margin:Int){
        appearAlphaAnimation(arrow,true).start()

         val getBorderSet = getSimplePosRelation(view,arrowPosition, true)
         val borderSetWithMargin = setMarginByNextToPosition(arrowPosition,margin,getBorderSet)
         val positionData = ViewAndPositionData(arrow,
             borderSetWithMargin,
             getOriSetByNextToPosition(arrowPosition,BorderAttributes.FillIfOutOfBorder))

        setPositionByMargin(positionData,globalLayoutSet)
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
    }

    private fun setHole(viewUnderHole:View){
        holeView.viewUnderHole = viewUnderHole
    }

    fun removeHole(){
        holeView.removeGlobalLayout()
        holeView.noHole = true
    }
    fun copyViewInConLay(view:View, borderDataMap: MutableMap<View, BorderSet>, globalLayoutSet: MutableMap<View, ViewTreeObserver.OnGlobalLayoutListener>):View{
        onInstallBinding.root.setOnClickListener(null)
        val a = TouchAreaBinding.inflate(activity.layoutInflater)
        a.touchView.tag = 1

        val id = View.generateViewId()
        a.touchView.id = id
        onInstallBinding.root.addView(a.touchView)
        val con = ConstraintSet()
        con.clone(onInstallBinding.root)
//
        con.connect(id, ConstraintSet.RIGHT ,ConstraintSet.PARENT_ID,ConstraintSet.RIGHT,)
        con.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID,ConstraintSet.TOP,)
        con.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID,ConstraintSet.BOTTOM,)
        con.connect(id, ConstraintSet.LEFT, ConstraintSet.PARENT_ID,ConstraintSet.LEFT,)

        con.applyTo(onInstallBinding.root)
        saveBorderDataMap(a.touchView, BorderSet(
            topSideSet = ViewAndSide(view,MyOrientation.TOP),
            bottomSideSet = ViewAndSide(view,MyOrientation.BOTTOM),
            leftSideSet = ViewAndSide(view,MyOrientation.LEFT),
            rightSideSet = ViewAndSide(view,MyOrientation.RIGHT),

            ),borderDataMap
        )
        val positionData = ViewAndPositionData(a.touchView
            ,getSimplePosRelation(view,MyOrientation.MIDDLE,true)
            ,MyOrientationSetNew(MyVerticalOrientation.MIDDLE,MyHorizontalOrientation.MIDDLE,BorderAttributes.FillBorder))
        setPositionByMargin(
            positionData,
            globalLayoutSet = globalLayoutSet)


        return a.touchView
    }


}
