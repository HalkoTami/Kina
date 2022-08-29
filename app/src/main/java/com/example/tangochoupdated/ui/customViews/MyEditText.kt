package com.example.tangochoupdated.ui.customViews

import android.content.Context
import android.text.SpannableStringBuilder
import android.util.AttributeSet
import android.widget.Toast
import androidx.core.widget.addTextChangedListener


class MyEditText(context: Context, attributeSet: AttributeSet):androidx.appcompat.widget.AppCompatEditText(context,attributeSet){




    var newLine = true
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        this.addTextChangedListener {
            if(this.lineCount>2&&newLine){
                newLine = false
                val a = text?.substring(0,this.layout.getLineEnd(this.lineCount-1)) +"\n" + text?.substring(this.layout.getLineStart(this.lineCount),this.text?.length ?:0)
                this.text = SpannableStringBuilder(a)
                newLine = true
            }
        }
    }

    override fun onTextChanged(
        text: CharSequence?,
        start: Int,
        lengthBefore: Int,
        lengthAfter: Int
    ) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)



    }
}