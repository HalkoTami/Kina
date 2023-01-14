package com.koronnu.kina.ui.view_set_up

import android.animation.Animator
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import com.koronnu.kina.R
import com.koronnu.kina.databinding.ItemColorPalletBinding
import com.koronnu.kina.db.enumclass.ColorStatus



object BindingAdapters{
    @BindingAdapter("convertColPalletDraw")
    @JvmStatic
    fun convertDrawable(view:ImageView, colorStatus: ColorStatus){
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
    fun animateVisibility(view:FrameLayout,getAnimation:(v:View,visible:Boolean)->Animator,visible: Boolean){
        getAnimation(view,visible).start()
    }
    @BindingAdapter("isSelected")
    @JvmStatic
    fun setSelected(view:View,selected: Boolean){
        view.isSelected = selected
    }

}


class ColorPalletViewSetUp(val binding: ItemColorPalletBinding) {


    private val context get() =  binding.root.context
    private val resources get() =  context.resources
    private val unselectedAlpha get() = resources.getDimension(R.dimen.imvColPallet_alpha_unselected)
    private val selectedStrokeWidth get() =  resources.getDimensionPixelSize(R.dimen.drawColPallet_strokeWidth_selected)
    private val strokeColor get() =  ContextCompat.getColor(context,R.color.black)
    private val colBindingTag:ColorStatus? get() = binding.root.tag as ColorStatus?
    private fun getImvByColStatus(colorStatus: ColorStatus):ImageView{
        binding.apply {
            return when(colorStatus){
                ColorStatus.YELLOW  -> imvColYellow
                ColorStatus.RED     -> imvColRed
                ColorStatus.GRAY    -> imvColGray
                ColorStatus.BLUE    -> imvColBlue
            }
        }
    }
    fun getDrawable(colPalletCol:ColorStatus, parent: ColorStatus):GradientDrawable{
        val draw = getDrawableByColStatus(colPalletCol)
        if(parent == colPalletCol) draw.setStroke(selectedStrokeWidth,strokeColor)
        else draw.setStroke(0,strokeColor)
        return draw

    }
    private fun getDrawableByColStatus(colorStatus: ColorStatus):GradientDrawable{
        val colId =  when(colorStatus){
            ColorStatus.YELLOW  -> R.color.yellow
            ColorStatus.RED     -> R.color.red
            ColorStatus.GRAY    -> R.color.gray
            ColorStatus.BLUE    -> R.color.blue
        }
        val draw = AppCompatResources.getDrawable(context,R.drawable.circle) as GradientDrawable
        draw.setColor(ContextCompat.getColor(context,colId))
        return draw
    }
    private fun changeSelectedStatus(selected: Boolean,imageView: ImageView){
        imageView.apply {
            alpha = if(selected) 1f else unselectedAlpha
            val draw = drawable as GradientDrawable
            if(selected)draw.setStroke(selectedStrokeWidth,strokeColor)
            else draw.setStroke(0,strokeColor)
            setImageDrawable(draw)
        }
    }
    fun makeSelected(colorStatus: ColorStatus){
        val tagBefore = colBindingTag
        val imvToSelect = getImvByColStatus(colorStatus)
        changeSelectedStatus(true,imvToSelect)
        binding.root.tag = colorStatus
        val imvToUnselect = getImvByColStatus(tagBefore ?:return )
        changeSelectedStatus(false,imvToUnselect)
    }
}