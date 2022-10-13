package com.korokoro.kina.ui.listener

import android.graphics.Rect
import android.view.ViewTreeObserver
import androidx.constraintlayout.widget.ConstraintLayout

open class KeyboardListener(private val rootView:ConstraintLayout):ViewTreeObserver.OnGlobalLayoutListener {

    var up:Boolean = false
    var heightBefore:Int = 0
    var keyboardVisible = false
    override fun onGlobalLayout() {
        val r = Rect()
        rootView.getWindowVisibleDisplayFrame(r)
        val heightDiff: Int = rootView.rootView.height - (r.bottom - r.top)
        if (heightDiff>500) {
            onKeyBoardAppear()
            keyboardVisible = true
        }
        if(keyboardVisible&&(heightDiff-heightBefore<0)){
            keyboardVisible = false
            onKeyBoardDisappear()
        }
        heightBefore = heightDiff
    }
    open fun onKeyBoardAppear(){

    }
    open fun onKeyBoardDisappear(){

    }
}