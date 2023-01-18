package com.koronnu.kina.binding

import android.animation.Animator
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.koronnu.kina.R
import com.koronnu.kina.data.source.local.entity.enumclass.ColorStatus

object ViewBinding{
    @BindingAdapter("convertColPalletDraw")
    @JvmStatic
    fun convertDrawable(view: ImageView, colorStatus: ColorStatus){
        val colId =  when(colorStatus){
            ColorStatus.YELLOW  -> R.color.yellow
            ColorStatus.RED     -> R.color.red
            ColorStatus.GRAY    -> R.color.gray
            ColorStatus.BLUE    -> R.color.blue
        }
        val draw = AppCompatResources.getDrawable(view.context,R.drawable.circle) as GradientDrawable
        draw.setColor(ContextCompat.getColor(view.context,colId))
        view.setImageDrawable(draw)
    }

    @BindingAdapter("visibilityAnim","animVisibility", requireAll = true)
    @JvmStatic
    fun animateVisibility(view: FrameLayout, getAnimation:(v: View, visible:Boolean)-> Animator, visible: Boolean){
        getAnimation(view,visible).start()
    }
    @BindingAdapter("isSelected")
    @JvmStatic
    fun setSelected(view:View,selected: Boolean){
        view.isSelected = selected
    }

}