package com.example.tangochoupdated.ui.listener

import android.content.Context
import android.graphics.Rect
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout

open class KeyboardListener(private val rootView:ConstraintLayout):ViewTreeObserver.OnGlobalLayoutListener {

    var up:Boolean = false
    override fun onGlobalLayout() {
        val r = Rect()
        rootView.getWindowVisibleDisplayFrame(r)
        val heightDiff: Int = rootView.rootView.height - (r.bottom - r.top)
        if(up){
            onKeyBoardDisappear()

            up = false
        }
        if (heightDiff>500) {
            onKeyBoardAppear()

            up = true
            return
        }
    }
    open fun onKeyBoardAppear(){

    }
    open fun onKeyBoardDisappear(){

    }
}