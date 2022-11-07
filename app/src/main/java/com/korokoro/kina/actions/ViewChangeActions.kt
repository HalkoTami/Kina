package com.korokoro.kina.actions

import android.content.Context
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.korokoro.kina.customClasses.CirclePosition
import com.korokoro.kina.customClasses.RecPosition
import com.korokoro.kina.customClasses.ViewCenterPosition

class ViewChangeActions {
    fun setScale(v: View, x:Float, y:Float){
        v.scaleX = x
        v.scaleY = y
    }
    fun setAlpha(v: View, alpha:Float){
        v.alpha = alpha
    }
    fun getScreenWidth(context:Context):Int{
        return context.resources.displayMetrics.widthPixels
    }
    fun getScreenHeight(context:Context):Int{
        return context.resources.displayMetrics.heightPixels
    }
    fun getRecPos(view: View): RecPosition {
        val screenWidth = getScreenWidth(view.context)
        val screenHeight = getScreenHeight(view.context)
        val a = IntArray(2)
        view.getLocationInWindow(a)
        val viewX = a[0].toFloat()
        val viewY = a[1].toFloat() - getWindowDisplayHeightDiff(view.context.resources)
        val left = viewX
        val top = viewY
        val right = viewX + view.width
        val bottom = viewY + view.height
        val pos = RecPosition(left = left,top=top,right, bottom)
        return pos
    }
    fun getCenterPos(view: View):ViewCenterPosition{
        val recPos = getRecPos(view)
        val horizontalCenter =  recPos.left + (recPos.right-recPos.left)/2
        val verticalCenter = recPos.top + (recPos.bottom - recPos.top)/2
        return ViewCenterPosition(horizontalCenter,verticalCenter)
    }
    fun getCirclePos(view: View): CirclePosition {
        val centerPos = getCenterPos(view)
        val radius = if(view.width>view.height) view.width/2 else view.height/2
        val pos = CirclePosition(x = centerPos.x, y = centerPos.y,radius.toFloat())
        return pos
    }
}