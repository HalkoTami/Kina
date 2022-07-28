package com.example.tangochoupdated.ui.mainactivity

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.view.children
import com.example.tangochoupdated.R

class Animation {
    fun animateFrameBottomMenu(frameBottomMenu: FrameLayout, visibility:Int){
        val btmMenuAnimator = AnimatorSet().apply{
            val a = ObjectAnimator.ofFloat(frameBottomMenu, View.TRANSLATION_Y, 300f,0f)
            val b = ObjectAnimator.ofFloat(frameBottomMenu, View.ALPHA,0f,1f)
            playTogether(a,b)
            duration = 500
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
                btmMenuAnimator.reverse()
            }
        }
    }
    fun animatePopUpAddFile(popUpAddFile: FrameLayout, visibility: Int){
        val appearAnimator = AnimatorSet().apply {
            duration= 500
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
                appearAnimator.reverse()
            }
        }
    }
    fun animateColPallet(colPallet:LinearLayoutCompat,visibility: Int){
        val eachColAnimation = mutableListOf<Animator>()
        var delay:Long = 0
        colPallet.children.iterator().forEachRemaining { view->
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
                mainAnimator.reverse()

            }
        }

    }
}
