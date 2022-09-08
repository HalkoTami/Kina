package com.example.tangochoupdated.ui.animation

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Build
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.view.children
import kotlin.math.absoluteValue

class Animation {
    fun animateFrameBottomMenu(frameBottomMenu: FrameLayout, visibility:Int){
        val btmMenuAnimator = AnimatorSet().apply{
            val a = ObjectAnimator.ofFloat(frameBottomMenu, View.TRANSLATION_Y, 300f,0f)
            val b = ObjectAnimator.ofFloat(frameBottomMenu, View.ALPHA,0f,1f)
            playTogether(a,b)
            duration = 200
        }
        when (visibility){
            View.VISIBLE -> {
                btmMenuAnimator.start()
                frameBottomMenu.visibility = View.VISIBLE
            }
            View.GONE ->{
                btmMenuAnimator.doOnEnd {
                    frameBottomMenu.visibility = View.GONE
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    btmMenuAnimator.reverse()
                } else frameBottomMenu.visibility = View.GONE
            }
        }
    }
    fun animatePopUpAddFile(popUpAddFile: FrameLayout, visibility: Int){
        val appearAnimator = AnimatorSet().apply {
            duration= 200
            play(ObjectAnimator.ofFloat(popUpAddFile, View.ALPHA, 0f,1f ))
        }
        when (visibility){
            View.VISIBLE -> {
                appearAnimator.start()
                popUpAddFile.visibility = View.VISIBLE
            }
            View.GONE ->{
                appearAnimator.doOnEnd {
                    popUpAddFile.visibility = View.GONE
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    appearAnimator.reverse()
                } else popUpAddFile.visibility = View.GONE
            }
        }
    }

    fun animateColPallet(colPallet:LinearLayoutCompat,visibility: Int){
        val eachColAnimation = mutableListOf<Animator>()
        var delay:Long = 0
        colPallet.children.iterator().forEach { view->
            val a =  ObjectAnimator.ofFloat(view, View.TRANSLATION_X,-300f,0f)
            val b = ObjectAnimator.ofFloat(view,View.ALPHA,0f,1f)
            a.duration = 200
            b.duration = 200
            when (visibility){
                View.VISIBLE -> a.doOnStart { view.visibility = View.VISIBLE }
                View.GONE ->a.doOnEnd { view.visibility = View.GONE }
            }
            a.startDelay = delay
            b.startDelay = delay
            eachColAnimation.add(a)
            eachColAnimation.add(b)
            delay +=70
        }
        val mainAnimator = AnimatorSet().apply {
            playTogether(eachColAnimation)

        }
        when (visibility){
            View.VISIBLE -> {
                colPallet.visibility = View.VISIBLE
                mainAnimator.start()
            }
            View.GONE ->{
                mainAnimator.doOnEnd { colPallet.visibility = View.GONE }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    mainAnimator.reverse()
                } else colPallet.visibility = View.GONE

            }
        }

    }
    fun animateLibRVLeftSwipeLay(frameLayout: LinearLayoutCompat,visible:Boolean){
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
        }

        val appearAnim = ValueAnimator.ofInt(frameLayout.width,100)
        appearAnim.duration = (100 - frameLayout.width).absoluteValue * 5.toLong()
        appearAnim.addUpdateListener {
            frameLayout.layoutParams.width = it.animatedValue as Int
            frameLayout.requestLayout()
        }
        appearAnim.doOnEnd {
            frameLayout.visibility = View.VISIBLE
        }
        if(!visible) disappearAnim.start()
        else if (visible) appearAnim.start()
    }

}
