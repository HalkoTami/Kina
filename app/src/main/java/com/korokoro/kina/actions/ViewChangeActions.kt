package com.korokoro.kina.actions

import android.content.Context
import android.view.View
import android.view.ViewTreeObserver
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.korokoro.kina.customClasses.*
import kotlin.math.abs

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
    fun getViewBorderPos(view: View, position: MyOrientation):Float{
        return when(position){
            MyOrientation.RIGHT-> getRecPos(view).right
            MyOrientation.LEFT -> getRecPos(view).left
            MyOrientation.BOTTOM -> getRecPos(view).bottom
            MyOrientation.TOP   -> getRecPos(view).top
            else -> 0f
        }
    }
    private fun calculatePositionInBorder(view: View,
                                          borderPosition: RecPosition,
                                          verticalOrientation: MyOrientation,
                                          horizontalOrientation: MyOrientation
    ):RecPosition{
        val subMiddleTop :Float
        val subMiddleBottom :Float
        val verticalCenter = borderPosition.top + (borderPosition.bottom-borderPosition.top)/2
        when(verticalOrientation){
            MyOrientation.TOP-> {
                subMiddleTop = borderPosition.top
                subMiddleBottom = borderPosition.top + view.height

            }
            MyOrientation.MIDDLE -> {
                subMiddleTop = verticalCenter-view.height/2
                subMiddleBottom = verticalCenter + view.height/2
            }
            MyOrientation.BOTTOM ->{
                subMiddleTop = borderPosition.bottom - view.height
                subMiddleBottom = borderPosition.bottom


            }
            else -> return RecPosition(0f,0f,0f,0f)
        }

//        val top = (if(subMiddleTop>borderPosition.top)subMiddleTop else borderPosition.top)
//        val bottom = if(subMiddleBottom<borderPosition.bottom)subMiddleBottom else borderPosition.bottom
        val top = subMiddleTop
        val bottom = subMiddleBottom


        val horizontalCenter = borderPosition.left + (borderPosition.right-borderPosition.left)/2
        val subMiddleLeft :Float
        val subMiddleRight :Float
        when(horizontalOrientation){
            MyOrientation.LEFT-> {
                subMiddleLeft = borderPosition.left
                subMiddleRight = borderPosition.left + view.width
            }
            MyOrientation.MIDDLE -> {
                subMiddleLeft = horizontalCenter-view.width/2
                subMiddleRight = horizontalCenter + view.width/2
            }
            MyOrientation.RIGHT ->{
                subMiddleLeft = borderPosition.right - view.width
                subMiddleRight = borderPosition.right
            }
            else -> return RecPosition(0f,0f,0f,0f)
        }
        val left = (if(subMiddleLeft>borderPosition.left) subMiddleLeft else borderPosition.left)
        val right = (if(subMiddleRight<borderPosition.right)subMiddleRight else borderPosition.right)
        return RecPosition(left,top, right, bottom)
    }
    fun setPositionByMargin(
        view: View,
        matchSize:Boolean,
        myOrientationSet: InstallGuide.MyOrientationSet,
        positionDataMap:Map<View, BorderSet>,
        constraintLayout: ConstraintLayout){

        view.viewTreeObserver.addOnGlobalLayoutListener(
            object :ViewTreeObserver.OnGlobalLayoutListener{
                override fun onGlobalLayout() {
                    val leftSideSet = positionDataMap[view]?.leftSideSet
                    val borderLeft = if(leftSideSet!=null) getViewBorderPos(leftSideSet.view,leftSideSet.side)
                    else getViewBorderPos(constraintLayout,MyOrientation.LEFT)
                    val topSideSet = positionDataMap[view]?.topSideSet
                    val borderTop = if(topSideSet!=null) getViewBorderPos(topSideSet.view,topSideSet.side)
                    else getViewBorderPos(constraintLayout, MyOrientation.TOP)
                    val rightSideSet = positionDataMap[view]?.rightSideSet
                    val borderRight = if(rightSideSet!=null) getViewBorderPos(rightSideSet.view,rightSideSet.side)
                    else getViewBorderPos(constraintLayout,MyOrientation.RIGHT)
                    val bottomSideSet = positionDataMap[view]?.bottomSideSet
                    val borderBottom = if(bottomSideSet!=null) getViewBorderPos(bottomSideSet.view,bottomSideSet.side)
                    else getViewBorderPos(constraintLayout,MyOrientation.BOTTOM)



                    val borderRecPos = RecPosition(borderLeft,borderTop,borderRight,borderBottom)
                    if(matchSize) {
                        view.layoutParams.width = abs(borderLeft-borderRight).toInt()
                        view.layoutParams.height = abs(borderTop-borderBottom).toInt()
                        view.requestLayout()
                    }

                    val result =calculatePositionInBorder(
                        view ,
                        borderRecPos,
                        myOrientationSet.verticalOrientation,
                        myOrientationSet.horizontalOrientation
                    )
                    val topDiff = abs(borderTop-getViewBorderPos(constraintLayout, MyOrientation.TOP))
                    val rightDiff = abs(borderRight-getViewBorderPos(constraintLayout, MyOrientation.RIGHT))
                    val leftDiff = abs(borderLeft-getViewBorderPos(constraintLayout, MyOrientation.LEFT))
                    val bottomDiff = abs(borderBottom-getViewBorderPos(constraintLayout, MyOrientation.BOTTOM))
                    fun setPosition(recPosition: RecPosition){
                        val con = ConstraintSet()
                        con.clone(constraintLayout)
                        val marginTop = (abs(borderTop!!-recPosition.top)  -bottomDiff ).toInt()
                        val marginBottom = (abs(borderBottom!!-recPosition.bottom)  - topDiff  ).toInt()
                        val marginStart = (abs(borderLeft!!-recPosition.left) -rightDiff).toInt()
                        val marginEnd = (abs(borderRight!!-recPosition.right) -leftDiff).toInt()
//
                        con.setMargin(view.id, ConstraintSet.TOP, marginTop)
                        con.setMargin(view.id, ConstraintSet.BOTTOM, marginBottom)
                        con.setMargin(view.id, ConstraintSet.START, marginStart)
                        con.setMargin(view.id, ConstraintSet.END, marginEnd)

                        con.applyTo(constraintLayout)

                    }
                    setPosition(result)
                    view.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }

            }
        )
        view.requestLayout()

    }
}