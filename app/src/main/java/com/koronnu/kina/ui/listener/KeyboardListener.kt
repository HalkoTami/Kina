package com.koronnu.kina.ui.listener

import android.graphics.Rect
import android.view.ViewTreeObserver
import androidx.constraintlayout.widget.ConstraintLayout

open class KeyboardListener(private val rootView:ConstraintLayout):ViewTreeObserver.OnGlobalLayoutListener {

    private var heightBefore:Int = 0
    private var keyboardVisible = false
    set(value) {
        field = value
        if(value) onKeyBoardAppear()
        else onKeyBoardDisappear()
    }
    var onKeyBoardAppear:()-> Unit = {}
    var onKeyBoardDisappear:()->Unit = {}
    override fun onGlobalLayout() {
        val r = Rect()
        rootView.getWindowVisibleDisplayFrame(r)
        val heightDiff: Int = rootView.rootView.height - (r.bottom - r.top)
        if (heightDiff>500&&keyboardVisible.not()) {
            keyboardVisible = true
        }else if(keyboardVisible&&(heightDiff-heightBefore<0)){
            keyboardVisible = false
        }
        heightBefore = heightDiff
    }
}