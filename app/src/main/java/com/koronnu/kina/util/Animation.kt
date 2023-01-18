package com.koronnu.kina.util

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.view.View
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.view.children
import com.koronnu.kina.actions.changeViewVisibility
import kotlin.math.absoluteValue

class Animation {

    fun slideInBottomAnim(view: View,visible: Boolean):ValueAnimator{
        val start = if (visible) 300f else 0f
        val end = if(visible) 0f else 300f
        return ValueAnimator.ofFloat(start,end).apply {
            addUpdateListener {
                view.translationY = it.animatedValue as Float
            }
        }
    }

    fun appearAlphaAnimation(view :View, visible:Boolean,defaultAlpha:Float,doOnEnd: () -> Unit): ValueAnimator {

        val appear =ValueAnimator.ofFloat(0f,defaultAlpha)
        val disappear = ValueAnimator.ofFloat(defaultAlpha,0f)
        arrayOf(appear,disappear).onEach { eachAnimator->
            eachAnimator.addUpdateListener { thisAnimator ->
                view.alpha = thisAnimator.animatedValue as Float
            }
            eachAnimator.duration = 200
        }
        appear.doOnStart {
            view.alpha = 0f
            changeViewVisibility(view,true)  }
        appear.doOnEnd {
            doOnEnd()
        }
        disappear.doOnEnd {  changeViewVisibility(view,false)
        doOnEnd()
        }
        return if(visible) appear else disappear
    }

    fun animateFrameBottomMenu(frameBottomMenu: View, visible:Boolean):AnimatorSet{
        return AnimatorSet().apply {
            duration= 200
            val slideAnim = slideInBottomAnim(frameBottomMenu,visible)
            val appearAlphaAnim = appearAlphaAnimation(frameBottomMenu,visible,1f){}
            playTogether(slideAnim,appearAlphaAnim)
        }
    }

    fun animateLibRVLeftSwipeLay(frameLayout: LinearLayoutCompat,visible:Boolean,doOnEnd: ()-> Unit){
        val disappearAnim = ValueAnimator.ofInt(frameLayout.width,1)
        disappearAnim.duration = frameLayout.width* 5.toLong()
        disappearAnim.addUpdateListener {
            frameLayout.layoutParams.width = it.animatedValue as Int
            frameLayout.requestLayout()
        }
        disappearAnim.doOnEnd {
            frameLayout.children.iterator().forEach {
                it.visibility = View.GONE
            }
            frameLayout.visibility = View.GONE
            doOnEnd()
        }

        val appearAnim = ValueAnimator.ofInt(frameLayout.width,100)
        appearAnim.duration = (100 - frameLayout.width).absoluteValue * 5.toLong()
        appearAnim.addUpdateListener {
            frameLayout.layoutParams.width = it.animatedValue as Int
            frameLayout.requestLayout()
        }
        appearAnim.doOnEnd {
            frameLayout.visibility = View.VISIBLE
            doOnEnd()
        }
        if(!visible) disappearAnim.start()
        else appearAnim.start()
    }


}
