package com.koronnu.kina.util

import android.animation.ValueAnimator
import android.view.View
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.animation.doOnEnd
import androidx.core.view.children
import com.koronnu.kina.ui.tabLibrary.LibraryBaseViewModel
import kotlin.math.absoluteValue

interface LeftScrollReceiver{
    val upDatingView : LinearLayoutCompat
    val libraryBaseViewModel: LibraryBaseViewModel
    fun onScrollLeft(distanceX:Float){
        upDatingView.visibility = View.VISIBLE
        upDatingView.layoutParams.width = (distanceX).toInt()
        upDatingView.requestLayout()
    }
    fun onMotionEventUp(){
        if(upDatingView.layoutParams.width <25){
            animateDisAppear()
        }
        else if (upDatingView.layoutParams.width>=25){
            animateAppear()
            libraryBaseViewModel.setLeftSwipedItemExists(true)
        }
    }
    fun animateAppear() = ValueAnimator.ofInt(upDatingView.width,100).apply {
        duration = (50 - upDatingView.width).absoluteValue * 2.toLong()
        addUpdateListener {
            upDatingView.layoutParams.width = it.animatedValue as Int
            upDatingView.requestLayout()
        }
        doOnEnd {
            upDatingView.visibility = View.VISIBLE
        }
    }.start()
    fun animateDisAppear() = ValueAnimator.ofInt(upDatingView.width,1).apply {
        duration = (50 - upDatingView.width).absoluteValue*2.toLong()
        addUpdateListener {
            upDatingView.layoutParams.width = it.animatedValue as Int
            upDatingView.requestLayout()
        }
        doOnEnd {
            upDatingView.children.iterator().forEach {
                it.visibility = View.GONE
            }
            upDatingView.visibility = View.GONE
        }
    }.start()

}