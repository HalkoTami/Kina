package com.koronnu.kina.util

import android.animation.Animator
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.koronnu.kina.R
import com.koronnu.kina.data.source.local.entity.File
import com.koronnu.kina.data.source.local.entity.enumclass.ColorStatus
import com.koronnu.kina.util.view_set_up.GetCustomDrawables


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


        fun getDraw(selected: Boolean):Drawable{
            val res = if(selected) R.drawable.colpallet_unselected else R.drawable.colpallet_selected
            val unselected = AppCompatResources.getDrawable(view.context,res) as GradientDrawable
            unselected.setColor(ContextCompat.getColor(view.context,colId))
            if(!selected) return unselected
            unselected.setStroke(3,ContextCompat.getColor(view.context,R.color.black))
            return  unselected
        }

        val stateDraw = StateListDrawable()
        stateDraw.addState(intArrayOf(android.R.attr.state_selected),getDraw(true) )
        stateDraw.addState(intArrayOf(-android.R.attr.state_selected),getDraw(false) )



        view.setImageDrawable(stateDraw)
    }
    @BindingAdapter("convertFileStatusDraw")
    @JvmStatic
    fun convertFileStatusDrawable(view: ImageView, file: File?){
        file ?:return
        view.setImageDrawable(
            GetCustomDrawables(view.context).getFileIconByFile(file)
        )
    }

    @BindingAdapter("horizontal_constraint_percentage")
    @JvmStatic
    fun setLayoutConstraintGuideBegin(view: View, progress:Int) {
        val constraintSet = ConstraintSet()
        val parentView = view.parent
        if(parentView !is ConstraintLayout) return
        constraintSet.clone(parentView)
        val biasedValue = progress.toFloat()/100
        constraintSet.setHorizontalBias(view.id,biasedValue)
        constraintSet.applyTo(parentView)
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