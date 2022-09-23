package com.korokoro.kina.ui.listener

import android.graphics.Rect
import android.view.ViewTreeObserver
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