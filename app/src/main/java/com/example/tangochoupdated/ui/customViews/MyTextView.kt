package com.example.tangochoupdated.ui.customViews

import android.content.Context
import android.util.AttributeSet
import android.widget.Toast
import com.example.tangochoupdated.ui.listener.textSizeListener


class MyTextView(context: Context,attributeSet: AttributeSet):androidx.appcompat.widget.AppCompatTextView(context,attributeSet){

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        this.viewTreeObserver.addOnGlobalLayoutListener {
            resize(this.text)
        }
    }





    var resize = true
    fun resize(text:CharSequence){
        resize = false
        val viewBounds = android.graphics.Rect()
        val textBound = android.graphics.Rect()
        this.rootView.getWindowVisibleDisplayFrame(viewBounds)
        val viewWidth = viewBounds.width()
        val viewHeight = viewBounds.height()
//        this.paint.measureText(text.toString())

        var size = this.textSize
        this.onPreDraw()
        val start = this.layout.getLineStart(this.layout.lineCount)

        this.paint.measureText(text.toString())
        val lineBound = android.graphics.Rect()
        this.paint.getTextBounds(text.toString(),start, text.length?: 0,textBound)
        this.paint.getTextBounds(text.toString(),0, text.length?: 0,lineBound)
        var linewidth = lineBound.width()
        var textWidth = textBound.width()
        var textHeight = textBound.height()
//        while(viewWidth<textWidth){
//
//            size--
//            this.textSize = size
//            this.paint.getTextBounds(text.toString(), 0, text.length ?: 0,textBound)
//            textWidth = textBound.width()
//
//        }
        while(textWidth>viewWidth){
            size--
            this.textSize = size
            this.onPreDraw()
            this.paint.getTextBounds(text.toString(),this.layout.getLineStart(4), text.length?: 0,textBound)
            textWidth = textBound.width()
        }

        resize = false
    }



    override fun setText(text: CharSequence?, type: BufferType?) {
        super.setText(text, type)

    }
}