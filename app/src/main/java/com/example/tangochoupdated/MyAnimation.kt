package com.example.tangochoupdated

import android.animation.Animator
import android.animation.ValueAnimator
import android.view.View
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart



fun appearByWidth(view: View,duration: Long):Animator{
    return animateWidth(view,0,view.width,duration)
}
fun disAppearByWidth(view: View,duration: Long):Animator{
    return  animateWidth(view,view.width,0,duration)
}

fun animateWidth(view:View,from:Int,to:Int,duration:Long):Animator{

    val widthAnim = ValueAnimator.ofInt(if(from==0) 1 else from ,
        if(to==0) 1 else to)
    widthAnim.apply {
        addUpdateListener {
            view.layoutParams.width = it.animatedValue as Int
            view.requestLayout()
        }
        if(view.visibility == View.VISIBLE){
            if(to==0){
                doOnEnd {
                    view.visibility = View.GONE
                    view.layoutParams.width = from
                }
            }

        } else{
            if(from == 0){
                doOnStart {
                    view.layoutParams.width = 1
                    view.requestLayout()
                    view.visibility = View.VISIBLE
                }
            }

        }
        this.duration = duration
    }
    return widthAnim






}

fun animateHeight(view:View,from:Int,to:Int,duration:Long):Animator{
    val heightAnim = ValueAnimator.ofInt(if(from==0) 1 else from ,
        if(to==0) 1 else to)
    heightAnim.apply {
        addUpdateListener {
            view.layoutParams.height = it.animatedValue as Int
            view.requestLayout()
        }
        if(view.visibility == View.VISIBLE){
            if(to==0){
                doOnEnd {
                    view.visibility = View.GONE
                }
            }

        } else{
            if(from == 0){
                doOnStart {
                    view.layoutParams.height = 1
                    view.requestLayout()
                    view.visibility = View.VISIBLE
                }
            }

        }
        this.duration = duration
    }
    return heightAnim






}