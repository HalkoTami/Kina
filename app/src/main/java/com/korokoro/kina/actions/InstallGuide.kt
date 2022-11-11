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
import com.korokoro.kina.customClasses.*
import com.korokoro.kina.databinding.CallOnInstallBinding
import com.korokoro.kina.databinding.TouchAreaBinding


class InstallGuide(val activity:AppCompatActivity,val onInstallBinding: CallOnInstallBinding){
    val arrow = onInstallBinding.imvFocusArrow
    val character = onInstallBinding.imvCharacter
    val holeView = onInstallBinding.viewWithHole
    val textView = onInstallBinding.txvExplain




    fun saveBorderDataMap(view: View,set:BorderSet,borderDataMap:MutableMap<View,BorderSet>){
        if(borderDataMap[view]!=null) borderDataMap.remove(view)
        borderDataMap[view] = set
    }

    fun alphaAnimatePos(data: ViewAndPositionData,globalLayoutSet: MutableMap<View, ViewTreeObserver.OnGlobalLayoutListener>):AnimatorSet{
        val view = data.view
        val disappear = ValueAnimator.ofFloat(1f,0f)
        disappear.addUpdateListener {
            view.alpha = it.animatedValue as Float
        }
        disappear.doOnEnd {
            changeViewVisibility(view,false)
            setPositionByMargin(data,globalLayoutSet,false,true)
        }
        val appear = ValueAnimator.ofFloat(0f,1f)
        appear.doOnEnd {
            changeViewVisibility(view,true)
        }
        appear.addUpdateListener {
            view.alpha = it.animatedValue as Float
        }
        return AnimatorSet().apply {

            playSequentially(disappear,appear)
            duration= 500
        }


    }



    fun setPositionByMargin(positionData: ViewAndPositionData,
                            globalLayoutSet: MutableMap<View, ViewTreeObserver.OnGlobalLayoutListener>,
                            fillBorder:Boolean,
                            fillIfOutOfBorder:Boolean){
        removeGlobalListener(globalLayoutSet)
        val view = positionData.view
        view.viewTreeObserver.addOnGlobalLayoutListener(object :ViewTreeObserver.OnGlobalLayoutListener{
            override fun onGlobalLayout() {
                ViewChangeActions().setPositionByMargin(fillBorder,fillIfOutOfBorder,positionData,onInstallBinding.root)
                globalLayoutSet[view] = this
            }
        })
    }

    fun removeGlobalListener(globalLayoutSet: MutableMap<View, ViewTreeObserver.OnGlobalLayoutListener>){
        globalLayoutSet.onEach {
            it.key.viewTreeObserver.removeOnGlobalLayoutListener(it.value)
        }
    }
    fun makeTxvGone(){
        appearAlphaAnimation(textView,false).start()
    }
    fun makeArrowGone(){
        changeViewVisibility(arrow,false)
    }
    fun makeTouchAreaGone(){
        onInstallBinding.root.children.iterator().forEach {
            if(it.tag == 1) it.visibility = View.GONE
        }
    }
    fun appearAlphaAnimation(view :View, visible:Boolean): ValueAnimator {

        val appear =if(view == holeView)  ValueAnimator.ofFloat(0f,0.7f) else ValueAnimator.ofFloat(0f,1f)
        val disappear = ValueAnimator.ofFloat(1f,0f)
        arrayOf(appear,disappear).onEach { eachAnimator->
            eachAnimator.addUpdateListener { thisAnimator ->
                 ViewChangeActions().setAlpha(view, thisAnimator.animatedValue as Float)
            }
            eachAnimator.duration = 300
        }
        appear.doOnStart {
            view.alpha = 0f
            changeViewVisibility(view,true)  }
        disappear.doOnEnd {  changeViewVisibility(view,false)  }
        return if(visible) appear else disappear


    }
    fun getSimplePosRelation(standardView:View, orientation: MyOrientation, fit:Boolean):BorderSet{
        return ViewChangeActions().getSimpleBorderSet(standardView,orientation,fit)
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
         val posData = ViewAndPositionData(textView,getSimplePosRelation(view,orientation,fit) ,getOriSetByNextToPosition(orientation))
        setPositionByMargin(posData,globalLayoutSet,false,true)
        textView.visibility = if(string=="") View.GONE else View.VISIBLE

        val finalDuration:Long = 100
        val anim2 = ValueAnimator.ofFloat(1.1f,1f)
        anim2.addUpdateListener {
            val progressPer = it.animatedValue as Float
            ViewChangeActions().setScale(textView,progressPer,progressPer)
        }
        val anim = ValueAnimator.ofFloat(0.7f,1.1f)
        anim.addUpdateListener {
            val progressPer = it.animatedValue as Float
            ViewChangeActions().setScale(textView,progressPer,progressPer)
        }
        val animAlpha = ValueAnimator.ofFloat(0f,1f)
        anim.addUpdateListener {
            ViewChangeActions().setAlpha(textView,it.animatedValue as Float)
        }
        val scaleAnim = AnimatorSet().apply {
            playSequentially(anim,anim2)
            anim2.duration = finalDuration*0.3.toLong()
            anim.duration = finalDuration*0.7.toLong()
        }
        val finalAnim = AnimatorSet().apply {
            playTogether(animAlpha,scaleAnim)
            scaleAnim.duration = finalDuration
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
    fun getOriSetByNextToPosition(movingViewPosition:MyOrientation):MyOrientationSet{
        return when(movingViewPosition){
            MyOrientation.BOTTOM-> MyOrientationSet(verticalOrientation = MyOrientation.TOP , horizontalOrientation = MyOrientation.MIDDLE )
            MyOrientation.LEFT -> MyOrientationSet(verticalOrientation = MyOrientation.MIDDLE, horizontalOrientation = MyOrientation.RIGHT)
            MyOrientation.RIGHT -> MyOrientationSet(verticalOrientation = MyOrientation.MIDDLE , horizontalOrientation = MyOrientation.LEFT )
            MyOrientation.TOP -> MyOrientationSet(verticalOrientation = MyOrientation.BOTTOM, horizontalOrientation = MyOrientation.MIDDLE )
            else -> MyOrientationSet(MyOrientation.MIDDLE,MyOrientation.MIDDLE)
        }
    }
     fun setArrow(arrowPosition: MyOrientation, view: View,globalLayoutSet: MutableMap<View, ViewTreeObserver.OnGlobalLayoutListener>){
        appearAlphaAnimation(arrow,true).start()

         val positionData = ViewAndPositionData(arrow,getSimplePosRelation(view,arrowPosition,true),getOriSetByNextToPosition(arrowPosition))
        setPositionByMargin(positionData,globalLayoutSet,false,false)
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
            ,MyOrientationSet(MyOrientation.MIDDLE,MyOrientation.MIDDLE))
        setPositionByMargin(
            positionData,
            fillBorder = true,
            globalLayoutSet = globalLayoutSet,
            fillIfOutOfBorder = true)


        return a.touchView
    }


}
