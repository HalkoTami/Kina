package com.korokoro.kina.actions

import android.view.View

class ViewChangeActions {
    fun setScale(v: View, x:Float, y:Float){
        v.scaleX = x
        v.scaleY = y
    }
    fun setAlpha(v: View, alpha:Float){
        v.alpha = alpha
    }
}