package com.example.tangochoupdated.ui.customViews

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import com.example.tangochoupdated.MyTouchListener

class ImvChangeAlphaOnDown(context: Context, attrs: AttributeSet) : androidx.appcompat.widget.AppCompatImageView(context,attrs){

    override fun setOnClickListener(l: OnClickListener?) {
        super.setOnClickListener(l)
        this.setOnTouchListener(object :MyTouchListener(this.context){
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