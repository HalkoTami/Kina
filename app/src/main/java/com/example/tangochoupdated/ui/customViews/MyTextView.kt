package com.example.tangochoupdated.ui.customViews

import android.content.Context
import android.graphics.Rect
import android.text.TextUtils
import android.util.AttributeSet
import android.view.ViewTreeObserver
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout


class MyTextView(context: Context,attributeSet: AttributeSet):androidx.appcompat.widget.AppCompatTextView(context,attributeSet){



    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        this.ellipsize = null
        val viewBounds = android.graphics.Rect()
        val lineBound = android.graphics.Rect()
        val viewWidth = right-left
        val viewHeight = height

        var size = this.textSize
        this.onPreDraw()
        val start = this.layout.getLineStart(this.layout.lineCount-1)

        this. paint.measureText(text.toString())
        val textBound = android.graphics.Rect()
        this.paint.getTextBounds(text.toString(),start, text.length?: 0,lineBound)
        var lastLineWidth = lineBound.width()
        while(lastLineWidth>viewWidth){
            size--
            this.textSize = size
            this.onPreDraw()
            this.paint.getTextBounds(text.toString(),this.layout.getLineStart(this.layout.lineCount-1), text.length?: 0,lineBound)
            lastLineWidth = lineBound.width()
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        val viewBounds = android.graphics.Rect()
        val lineBound = android.graphics.Rect()
        val viewWidth = right-left
        val viewHeight = height

        var size = this.textSize
        this.onPreDraw()
        val start = this.layout.getLineStart(this.layout.lineCount-1)

        this. paint.measureText(text.toString())
        val textBound = android.graphics.Rect()
        this.paint.getTextBounds(text.toString(),start, text.length?: 0,lineBound)
        var lastLineWidth = lineBound.width()
        while(lastLineWidth>viewWidth){
            size--
            this.textSize = size
            this.onPreDraw()
            this.paint.getTextBounds(text.toString(),this.layout.getLineStart(this.layout.lineCount-1), text.length?: 0,lineBound)
            lastLineWidth = lineBound.width()
        }
    }

    open class MyGlobalListener(private val textView: MyTextView,private val height:Int): ViewTreeObserver.OnGlobalLayoutListener {
        fun resize(text:CharSequence, height:Int){





        }
        override fun onGlobalLayout() {
            resize(textView.text,height)
        }
        open fun onKeyBoardAppear(){

        }
        open fun onKeyBoardDisappear(){

        }
    }





    var resize = true




    override fun setText(text: CharSequence?, type: BufferType?) {
        super.setText(text, type)

    }
}