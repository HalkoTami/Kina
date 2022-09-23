package com.korokoro.kina.ui.listener

import android.view.ViewTreeObserver
import android.widget.TextView

open class textSizeListener(val text:String,val view:TextView):ViewTreeObserver.OnGlobalLayoutListener {

    var up:Boolean = false
    override fun onGlobalLayout() {
        val viewBounds = android.graphics.Rect()
        val textBound = android.graphics.Rect()
        view.rootView.getWindowVisibleDisplayFrame(viewBounds)
        val viewWidth = viewBounds.width()
        val viewHeight = viewBounds.height()
//        this.paint.measureText(text.toString())

        var size = view.textSize
        val start = view.layout.getLineStart(2)

        view.paint.measureText(text.toString())
        val lineBound = android.graphics.Rect()
        view.paint.getTextBounds(text.toString(),view.layout.getLineStart(2), text.length?: 0,textBound)
        view.paint.getTextBounds(text.toString(),0, text.length?: 0,lineBound)
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
        if(textWidth>viewWidth){
            size--
            view.textSize = size
            onGlobalLayout()
        }

    }
    open fun onKeyBoardAppear(){

    }
    open fun onKeyBoardDisappear(){

    }
}