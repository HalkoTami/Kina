package com.example.tangochoupdated.ui.customViews

import android.R.attr
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.widget.Toast
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Paint


class MyTextView(context: Context,attributeSet: AttributeSet):androidx.appcompat.widget.AppCompatTextView(context,attributeSet){


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

    }
    override fun setText(text: CharSequence?, type: BufferType?) {
        super.setText(text, type)
        val bounds = android.graphics.Rect()
        val textBound = android.graphics.Rect()
        this.rootView.getWindowVisibleDisplayFrame(bounds)
        val viewWidth = bounds.width()
        val viewHeight = bounds.height()
        this.paint.measureText(text.toString())
        var size = this.textSize
        this.paint.getTextBounds(text.toString(), 0, text?.length ?: 0,textBound)
        var textWidth = textBound.width()
        var textHeight = textBound.height()
//            this.paint.measureText(text.toString())
        Toast.makeText(this.context,"view ${bounds.width()} text ${textWidth}",Toast.LENGTH_SHORT).show()
        while(viewWidth<textWidth){
            this.isSingleLine = false
            size--
            this.textSize = size
            this.paint.getTextBounds(text.toString(), 0, text?.length ?: 0,textBound)
            textWidth = textBound.width()

        }

    }
}