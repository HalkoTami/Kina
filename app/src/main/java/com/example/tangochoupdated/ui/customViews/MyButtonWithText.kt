package com.example.tangochoupdated.ui.customViews

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import com.example.tangochoupdated.MyTouchListener
import com.example.tangochoupdated.R
import com.example.tangochoupdated.ui.listener.textSizeListener


class MyButtonWithText(context: Context, attributeSet: AttributeSet):androidx.appcompat.widget.AppCompatTextView(context,attributeSet){

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        this.background = AppCompatResources.getDrawable(this.context, R.drawable.rectangle_button)
        this.setTextColor(ContextCompat.getColor(this.context,R.color.white))
        this.gravity = Gravity.CENTER
        this.elevation= 10f
        this.viewTreeObserver.addOnGlobalLayoutListener {
            resize(this.text)
        }
    }
    override fun setOnClickListener(l: OnClickListener?) {
        super.setOnClickListener(l)
        this.setOnTouchListener(object : MyTouchListener(this.context){
            override fun onDown() {
                super.onDown()
                this@MyButtonWithText.elevation = 0f
            }

            override fun onSingleTap() {
                super.onSingleTap()
                l?.onClick(this@MyButtonWithText)
                this@MyButtonWithText.elevation = 10f
            }
        })
    }





    var resize = true
    fun resize(text:CharSequence){
        resize = false
        val viewBounds = android.graphics.Rect()
        val textBound = android.graphics.Rect()
        this.rootView.getWindowVisibleDisplayFrame(viewBounds)
        val viewWidth = viewBounds.width()

        var size = this.textSize
        this.onPreDraw()
        val start = this.layout.getLineStart(this.layout.lineCount-1)

        this.paint.measureText(text.toString())
        val lineBound = android.graphics.Rect()
        this.paint.getTextBounds(text.toString(),start, text.length?: 0,textBound)
        this.paint.getTextBounds(text.toString(),0, text.length?: 0,lineBound)
        var textWidth = textBound.width()
        while(textWidth>viewWidth){
            size--
            this.textSize = size
            this.onPreDraw()
            this.paint.getTextBounds(text.toString(),this.layout.getLineStart(this.maxLines-1), text.length?: 0,textBound)
            textWidth = textBound.width()
        }

        resize = false
    }

}