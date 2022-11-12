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
        v.viewTreeObserver.addOnGlobalLayoutListener(
            object :ViewTreeObserver.OnGlobalLayoutListener{
                override fun onGlobalLayout() {
                    v.scaleX = x
                    v.scaleY = y
                    v.pivotX = v.width.toFloat()/2
                    v.pivotY = v.height.toFloat()/2
                    v.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            }
        )
        v.requestLayout()
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
    fun getSimpleBorderSet(standardView:View, orientation: MyOrientation, fit:Boolean, ):BorderSet{
        val borderSet = when(orientation){
            MyOrientation.TOP -> BorderSet(bottomSideSet = ViewAndSide(standardView,MyOrientation.TOP),
                leftSideSet = if(fit) ViewAndSide(standardView,MyOrientation.LEFT) else null,
                rightSideSet = if(fit) ViewAndSide(standardView,MyOrientation.RIGHT) else null
            )
            MyOrientation.LEFT -> BorderSet(rightSideSet = ViewAndSide(standardView,MyOrientation.LEFT),
                topSideSet = if(fit) ViewAndSide(standardView,MyOrientation.TOP) else null,
                bottomSideSet = if(fit) ViewAndSide(standardView,MyOrientation.BOTTOM) else null
            )
            MyOrientation.RIGHT -> BorderSet(leftSideSet = ViewAndSide(standardView,MyOrientation.RIGHT),
                topSideSet = if(fit) ViewAndSide(standardView,MyOrientation.TOP) else null,
                bottomSideSet = if(fit) ViewAndSide(standardView,MyOrientation.BOTTOM) else null)
            MyOrientation.BOTTOM -> BorderSet(topSideSet = ViewAndSide(standardView,MyOrientation.BOTTOM),
                leftSideSet = if(fit) ViewAndSide(standardView,MyOrientation.LEFT) else null,
                rightSideSet = if(fit) ViewAndSide(standardView,MyOrientation.RIGHT) else null)
            MyOrientation.MIDDLE -> BorderSet(
                bottomSideSet = ViewAndSide(standardView,MyOrientation.BOTTOM),
                leftSideSet = ViewAndSide(standardView,MyOrientation.LEFT),
                rightSideSet = ViewAndSide(standardView,MyOrientation.RIGHT),
                topSideSet = ViewAndSide(standardView,MyOrientation.TOP))
        }
        return borderSet
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
                                          verticalOrientation: MyVerticalOrientation,
                                          horizontalOrientation: MyHorizontalOrientation
    ):RecPosition{
        val subMiddleTop :Float
        val subMiddleBottom :Float
        val verticalCenter = borderPosition.top + (borderPosition.bottom-borderPosition.top)/2
        when(verticalOrientation){
            MyVerticalOrientation.TOP-> {
                subMiddleTop = borderPosition.top
                subMiddleBottom = borderPosition.top + view.height
            }
            MyVerticalOrientation.MIDDLE -> {
                subMiddleTop = verticalCenter-view.height/2
                subMiddleBottom = verticalCenter + view.height/2
            }
            MyVerticalOrientation.BOTTOM ->{
                subMiddleTop = borderPosition.bottom - view.height
                subMiddleBottom = borderPosition.bottom
            }
        }
        val top = subMiddleTop
        val bottom = subMiddleBottom


        val horizontalCenter = borderPosition.left + (borderPosition.right-borderPosition.left)/2
        val subMiddleLeft :Float
        val subMiddleRight :Float
        when(horizontalOrientation){
            MyHorizontalOrientation.LEFT-> {
                subMiddleLeft = borderPosition.left
                subMiddleRight = borderPosition.left + view.width
            }
            MyHorizontalOrientation.MIDDLE -> {
                subMiddleLeft = horizontalCenter-view.width/2
                subMiddleRight = horizontalCenter + view.width/2
            }
            MyHorizontalOrientation.RIGHT ->{
                subMiddleLeft = borderPosition.right - view.width
                subMiddleRight = borderPosition.right
            }
        }
        val left = (if(subMiddleLeft>borderPosition.left) subMiddleLeft else borderPosition.left)
        val right = (if(subMiddleRight<borderPosition.right)subMiddleRight else borderPosition.right)
        return RecPosition(left,top, right, bottom)
    }
    fun setPositionByMargin(
        positionData: ViewAndPositionData,
        constraintLayout: ConstraintLayout){
        val view = positionData.view
        val borderSet = positionData.borderSet
        view.viewTreeObserver.addOnGlobalLayoutListener(
            object :ViewTreeObserver.OnGlobalLayoutListener{
                override fun onGlobalLayout() {

                    val rotationBefore = view.rotation
                    view.rotation = 0f
                    val leftSideSet = positionData.borderSet.leftSideSet
                    val borderLeft = if(leftSideSet!=null) getViewBorderPos(leftSideSet.view,leftSideSet.side)
                    else getViewBorderPos(constraintLayout,MyOrientation.LEFT)
                    val topSideSet = positionData.borderSet.topSideSet
                    val borderTop = if(topSideSet!=null) getViewBorderPos(topSideSet.view,topSideSet.side)
                    else getViewBorderPos(constraintLayout, MyOrientation.TOP)
                    val rightSideSet = positionData.borderSet.rightSideSet
                    val borderRight = if(rightSideSet!=null) getViewBorderPos(rightSideSet.view,rightSideSet.side)
                    else getViewBorderPos(constraintLayout,MyOrientation.RIGHT)
                    val bottomSideSet = positionData.borderSet.bottomSideSet
                    val borderBottom = if(bottomSideSet!=null) getViewBorderPos(bottomSideSet.view,bottomSideSet.side)
                    else getViewBorderPos(constraintLayout,MyOrientation.BOTTOM)



                    val borderRecPos = RecPosition(borderLeft,borderTop,borderRight,borderBottom)
                    when(positionData.orientation.borderAttributes){
                        BorderAttributes.FillBorder -> {
                            view.layoutParams.width = abs(borderLeft-borderRight).toInt()
                            view.layoutParams.height = abs(borderTop-borderBottom).toInt()
                            view.requestLayout()
                        }
                        BorderAttributes.FillIfOutOfBorder->{
                            val borderWidth = abs(borderRecPos.right-borderRecPos.left).toInt()
                            val borderHeight = abs(borderRecPos.top-borderRecPos.bottom).toInt()
                            if(view.width>borderWidth) view.layoutParams.width = borderWidth
                            if(view.height>borderHeight) view.layoutParams.height = borderHeight
                            view.requestLayout()
                        }
                        BorderAttributes.None -> {}
                    }

                    val result =calculatePositionInBorder(
                        view ,
                        borderRecPos,
                        positionData.orientation.verticalOrientation,
                        positionData.orientation.horizontalOrientation
                    )
                    val topDiff = abs(borderTop-getViewBorderPos(constraintLayout, MyOrientation.TOP))
                    val rightDiff = abs(borderRight-getViewBorderPos(constraintLayout, MyOrientation.RIGHT))
                    val leftDiff = abs(borderLeft-getViewBorderPos(constraintLayout, MyOrientation.LEFT))
                    val bottomDiff = abs(borderBottom-getViewBorderPos(constraintLayout, MyOrientation.BOTTOM))
                    fun setPosition(recPosition: RecPosition){
                        val con = ConstraintSet()
                        con.clone(constraintLayout)
                        val marginTop    = (abs(borderTop   -recPosition.top    )-bottomDiff ).toInt() + borderSet.topMargin
                        val marginBottom = (abs(borderBottom-recPosition.bottom )-topDiff    ).toInt() + borderSet.bottomMargin
                        val marginStart  = (abs(borderLeft  -recPosition.left   )-rightDiff  ).toInt() + borderSet.leftMargin
                        val marginEnd    = (abs(borderRight -recPosition.right  )-leftDiff   ).toInt() + borderSet.rightMargin
//
                        con.setMargin(view.id, ConstraintSet.TOP, marginTop)
                        con.setMargin(view.id, ConstraintSet.BOTTOM, marginBottom)
                        con.setMargin(view.id, ConstraintSet.START, marginStart)
                        con.setMargin(view.id, ConstraintSet.END, marginEnd)

                        con.applyTo(constraintLayout)

                    }
                    setPosition(result)
                    view.rotation = rotationBefore
                    view.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }

            }
        )
        view.requestLayout()

    }
}