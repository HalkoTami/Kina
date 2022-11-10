package com.korokoro.kina.actions

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup.LayoutParams
import android.view.ViewTreeObserver
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.view.children
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import com.korokoro.kina.R
import com.korokoro.kina.customClasses.*
import com.korokoro.kina.databinding.CallOnInstallBinding
import com.korokoro.kina.databinding.TouchAreaBinding
import com.korokoro.kina.db.dataclass.File
import com.korokoro.kina.ui.animation.Animation
import com.korokoro.kina.ui.fragment.lib_frag_con.LibraryHomeFragDirections
import com.korokoro.kina.ui.listener.MyTouchListener
import com.korokoro.kina.ui.viewmodel.*
import kotlin.math.abs


class InstallGuide(val activity:AppCompatActivity,val onInstallBinding: CallOnInstallBinding){
    val arrow = onInstallBinding.imvFocusArrow
    val touchArea = onInstallBinding.viewTouchArea
    val character = onInstallBinding.imvCharacter
    val holeView = onInstallBinding.viewWithHole
    val globalLayoutSet = mutableMapOf<View, ViewTreeObserver.OnGlobalLayoutListener>()
    val textView = onInstallBinding.txvExplain
    val heightDiff = getWindowDisplayHeightDiff(activity.resources)




    fun saveBorderDataMap(view: View,set:BorderSet,borderDataMap:MutableMap<View,BorderSet>){
        if(borderDataMap[view]!=null) borderDataMap.remove(view)
        borderDataMap[view] = set
    }






    fun setPositionByMargin(view: View,myOrientationSet:MyOrientationSet,borderDataMap: MutableMap<View, BorderSet>,
                            globalLayoutSet: MutableMap<View, ViewTreeObserver.OnGlobalLayoutListener>,matchSize:Boolean){
        globalLayoutSet.onEach {
            it.key.viewTreeObserver.removeOnGlobalLayoutListener(it.value)
        }
        view.viewTreeObserver.addOnGlobalLayoutListener(object :ViewTreeObserver.OnGlobalLayoutListener{
            override fun onGlobalLayout() {
                ViewChangeActions().setPositionByMargin(view,matchSize,myOrientationSet,borderDataMap,onInstallBinding.root)
                globalLayoutSet[view] = this
            }
        })

    }

    fun removeGlobalListener(){
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
        changeViewVisibility(touchArea,false)
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
    fun saveSimplePosRelation(movingView:View,standardView:View,orientation: MyOrientation,fit:Boolean,borderDataMap: MutableMap<View, BorderSet>){
        ViewChangeActions().saveSimplePosRelation(movingView,standardView,orientation,fit, borderDataMap)
    }
    fun setHoleMargin(int: Int){
        holeView.holeMargin = int
    }
    fun setHoleRecRadius(int: Int){
        holeView.recRadius = int.toFloat()
    }
     fun explainTextAnimation(string: String, orientation: MyOrientation,view: View,borderDataMap: MutableMap<View, BorderSet>,fit: Boolean
                              ,globalLayoutSet: MutableMap<View, ViewTreeObserver.OnGlobalLayoutListener>): AnimatorSet {


        textView.text = string
        textView.layoutParams.width = LayoutParams.WRAP_CONTENT
        textView.requestLayout()
        saveSimplePosRelation(textView,view,orientation,fit,borderDataMap)
        val set = when(orientation){
            MyOrientation.TOP     -> MyOrientationSet(verticalOrientation = MyOrientation.BOTTOM,horizontalOrientation = MyOrientation.MIDDLE)
            MyOrientation.LEFT    -> MyOrientationSet(verticalOrientation = MyOrientation.MIDDLE,horizontalOrientation = MyOrientation.RIGHT)
            MyOrientation.RIGHT   -> MyOrientationSet(verticalOrientation = MyOrientation.MIDDLE,horizontalOrientation = MyOrientation.LEFT)
            MyOrientation.BOTTOM  -> MyOrientationSet(verticalOrientation = MyOrientation.TOP,horizontalOrientation = MyOrientation.MIDDLE)
            MyOrientation.MIDDLE  -> MyOrientationSet(verticalOrientation = MyOrientation.MIDDLE,horizontalOrientation = MyOrientation.MIDDLE)
        }
        setPositionByMargin(textView,set, borderDataMap,globalLayoutSet,false)
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
     fun setArrow(arrowPosition: MyOrientation, view: View,borderDataMap: MutableMap<View, BorderSet>,){
        appearAlphaAnimation(arrow,true).start()
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

        saveSimplePosRelation(arrow,view,arrowPosition,true, borderDataMap)
        setPositionByMargin(arrow, getOriSetByNextToPosition(arrowPosition),borderDataMap,globalLayoutSet,false)
    }

    fun setHole(viewUnderHole:View){
        holeView.viewUnderHole = viewUnderHole
    }

    fun removeHole(){
        holeView.removeGlobalLayout()
        holeView.noHole = true
    }
    fun addTouchArea(view:View,borderDataMap: MutableMap<View, BorderSet>):View{
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
        setPositionByMargin(
            view = a.touchView,
            myOrientationSet = MyOrientationSet(MyOrientation.MIDDLE,MyOrientation.MIDDLE),
            matchSize = true,
            borderDataMap = borderDataMap,
            globalLayoutSet = globalLayoutSet)


        return a.touchView
    }


}
