package com.koronnu.kina.util.view_set_up

import android.animation.ValueAnimator
import android.content.Context
import android.view.View
import android.widget.ImageView
import androidx.core.animation.doOnEnd
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.koronnu.kina.R
import com.koronnu.kina.data.model.enumClasses.AnkiBoxFragments
import com.koronnu.kina.data.model.normalClasses.ParentFileAncestors
import com.koronnu.kina.data.source.local.entity.Card
import com.koronnu.kina.data.source.local.entity.File
import com.koronnu.kina.databinding.ListItemAnkiBoxRvFileBinding
import com.koronnu.kina.databinding.PgbAnkiBoxDataRememberedBinding
import com.koronnu.kina.ui.tabAnki.ankiBox.AnkiBoxFileRVCL
import com.koronnu.kina.ui.tabAnki.ankiBox.AnkiBoxListAdapter
import com.koronnu.kina.ui.tabAnki.ankiBox.AnkiBoxViewModel
import kotlin.math.abs


class AnkiBoxFragViewSetUp() {









    fun getRememberedPercentage(list: MutableList<Card>):Double{
        return if(list.size!=0) (list.filter { it.remembered == true }.size.toDouble()/list.size)
        else 0.0

    }

    fun moveViewInCircle(percentage:Double,
                         view:ImageView,
                         circleWidth: Int){
        val radious = Math.toRadians(360*percentage-90.0)
        val dx: Float = (circleWidth-view.layoutParams.width)/2 * Math.cos(radious).toFloat()
        val dy: Float = (circleWidth-view.layoutParams.height)/2 * Math.sin(radious).toFloat()
        view.translationX = dx
        view.translationY =dy
    }
    fun setUpAnkiBoxRing(list: MutableList<Card>, binding: PgbAnkiBoxDataRememberedBinding){
        val resources = binding.root.resources
        val rememberedPercentage = getRememberedPercentage(list)
        val rememberedCardsSize = list.filter { it.remembered == true }.size
        binding.apply {
            binding.imvRememberedEndIcon.visibility = View.VISIBLE
            txvInsideRing.text = resources.getString(R.string.remembered_progress,rememberedCardsSize,list.size)

        }
        val progressRingWidth = binding.ringProgressBar.layoutParams.width
        val progressBefore = binding.ringProgressBar.progress
        val beforePercentage = binding.ringProgressBar.progress.toFloat()/100
        val newProgress = rememberedPercentage.times(100).toInt()
        if(progressBefore==newProgress) {
            binding.ringProgressBar.progress = newProgress
            binding.imvRememberedEndIcon.visibility = if(newProgress!=0)  View.VISIBLE else View.INVISIBLE
            moveViewInCircle(rememberedPercentage,binding.imvRememberedEndIcon,progressRingWidth)
            return
        }
        fun getAnimation(percentageBefore:Float,newPercentage:Float):ValueAnimator{
            val percentageAnim = ValueAnimator.ofFloat(percentageBefore,newPercentage)
            percentageAnim.doOnEnd {
                binding.imvRememberedEndIcon.visibility = if(rememberedPercentage!=0.0)  View.VISIBLE else View.INVISIBLE
            }
            percentageAnim.addUpdateListener {
                val value = it.animatedValue as Float
                binding.ringProgressBar.progress = value.times(100).toInt()
                moveViewInCircle(value.toDouble(),binding.imvRememberedEndIcon,progressRingWidth)
            }
            percentageAnim.duration=abs(newProgress-progressBefore).times(30).toLong()
            return percentageAnim
        }
        getAnimation(beforePercentage,rememberedPercentage.toFloat()).start()

    }

}