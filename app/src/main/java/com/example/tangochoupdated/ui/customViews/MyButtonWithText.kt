package com.example.tangochoupdated.ui.customViews

import android.content.Context
import android.util.AttributeSet
import android.view.ViewTreeObserver
import android.widget.Toast


class MyButtonWithText(context: Context, attributeSet: AttributeSet):androidx.appcompat.widget.AppCompatButton(context,attributeSet){

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
//        this.background = AppCompatResources.getDrawable(this.context, R.drawable.rectangle_button)
//        this.setTextColor(ContextCompat.getColor(this.context,R.color.white))
//        this.gravity = Gravity.CENTER
//        this.elevation= 10f
        this.viewTreeObserver.addOnGlobalLayoutListener (
            object : ViewTreeObserver.OnGlobalLayoutListener{
                override fun onGlobalLayout() {
                    val myButtonWithText = this@MyButtonWithText
                    myButtonWithText.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    val viewBounds = android.graphics.Rect()
                    val lineBound = android.graphics.Rect()
                    myButtonWithText. getWindowVisibleDisplayFrame(viewBounds)
                    val viewWidth = viewBounds.width()-myButtonWithText.paddingEnd-myButtonWithText.paddingStart
                    val viewHeight = myButtonWithText.height-myButtonWithText.paddingTop

                    var size = myButtonWithText.textSize
                    myButtonWithText.onPreDraw()
                    val start = myButtonWithText.layout.getLineStart(0)

                    myButtonWithText. paint.measureText(text.toString())
                    val textBound = android.graphics.Rect()
                    myButtonWithText.paint.getTextBounds(text.toString(),start, text.length?: 0,lineBound)
                    var lastLineWidth = lineBound.width()
                    while(lastLineWidth>viewWidth){
                        size--
                        myButtonWithText.textSize = size
                        myButtonWithText.onPreDraw()
                        myButtonWithText.paint.getTextBounds(text.toString(),myButtonWithText.layout.getLineStart(start), text.length?: 0,lineBound)
                        lastLineWidth = lineBound.width()
                    }
                    var textHeight =  (lineBound.height()*2)*myButtonWithText.lineCount
                    var outOfBound:Int= (textHeight-viewHeight)
                    while(outOfBound>0){
                        size--
                        myButtonWithText.textSize = size
                        myButtonWithText.onPreDraw()
                        myButtonWithText.paint.getTextBounds(text.toString(),0, 1,lineBound)
                        outOfBound=  ((lineBound.height())*2)*myButtonWithText.lineCount-viewHeight
                    }
                    Toast.makeText(myButtonWithText.context,"$text ${outOfBound},${lineBound.height()}",Toast.LENGTH_SHORT).show()

                }
            }
        )
    }

    open class MyButtonTextListener(private val myButtonWithText: MyButtonWithText, private val height:Int): ViewTreeObserver.OnGlobalLayoutListener {
        fun resize(text:CharSequence, height:Int){


        }
        override fun onGlobalLayout() {
            resize(myButtonWithText.text,height)
        }
        open fun onKeyBoardAppear(){

        }
        open fun onKeyBoardDisappear(){

        }
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