package com.koronnu.kina.ui.customViews

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.LayerDrawable
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.core.content.ContextCompat
import com.koronnu.kina.R
import com.koronnu.kina.ui.listener.MyTouchListener

class ImvChangeAlphaOnDown(context: Context, attrs: AttributeSet) : androidx.appcompat.widget.AppCompatImageView(context,attrs){

    @SuppressLint("ClickableViewAccessibility")
    override fun setOnClickListener(l: OnClickListener?) {
        super.setOnClickListener(l)
        this.setOnTouchListener(object : MyTouchListener(this.context){
            override fun onDown() {
                super.onDown()
                this@ImvChangeAlphaOnDown.alpha = 1f
            }
            override fun onSingleTap(motionEvent: MotionEvent?) {
                super.onSingleTap(motionEvent)
                l?.onClick(this@ImvChangeAlphaOnDown)
                this@ImvChangeAlphaOnDown.alpha = 0.3f
            }
        })
    }


    var before:Boolean = false


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        this.alpha= 0.3f
    }

}
class NavigateBtnCreateCard(context: Context, attrs: AttributeSet) : androidx.appcompat.widget.AppCompatImageView(context,attrs){

    @SuppressLint("ClickableViewAccessibility")
    override fun setOnClickListener(l: OnClickListener?) {
        super.setOnClickListener(l)
        this.setOnTouchListener(object : MyTouchListener(this.context){
            val draw = this@NavigateBtnCreateCard.drawable as LayerDrawable
            val background =  draw.findDrawableByLayerId(R.id.layer_nav_btn_background)
            override fun onDown() {
                super.onDown()
                background.setTint(ContextCompat.getColor(context,R.color.dark_green))

            }

            override fun onSingleTap(motionEvent: MotionEvent?) {
                super.onSingleTap(motionEvent)
                l?.onClick(this@NavigateBtnCreateCard)
                background.setTint(ContextCompat.getColor(context,R.color.light_green))
            }
        })
    }



}
class ImvChangeAlphaOnSelect(context: Context, attrs: AttributeSet) : androidx.appcompat.widget.AppCompatImageView(context,attrs){

    override fun setSelected(selected: Boolean) {
        super.setSelected(selected)
        if(selected)this.alpha = 1f else this.alpha = 0.3f
    }

    var before:Boolean = false


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        this.alpha= 0.3f
    }

}