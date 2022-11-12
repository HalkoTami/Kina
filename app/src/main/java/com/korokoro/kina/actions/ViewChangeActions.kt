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
    fun getViewBorderPos(viewAndSide: ViewAndSide):Float{
        val view = viewAndSide.view
        return when(viewAndSide.side){
            MyOrientation.RIGHT -> getRecPos(view).right
            MyOrientation.LEFT  -> getRecPos(view).left
            MyOrientation.BOTTOM-> getRecPos(view).bottom
            MyOrientation.TOP   -> getRecPos(view).top
            MyOrientation.MIDDLE-> getCenterPos(view).x
        }
    }
    fun calculatePositionInBorder(view: View,
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
    fun getBorderFromBorderSet(borderSet: BorderSet,borderSide:MyOrientation,parentView:View):Float{
        val margin = borderSet.margin
        return when(borderSide){
            MyOrientation.BOTTOM   -> getViewBorderPos(borderSet.bottomSideSet  ?: ViewAndSide(parentView,MyOrientation.BOTTOM)) - margin.bottomMargin
            MyOrientation.TOP      -> getViewBorderPos(borderSet.topSideSet     ?: ViewAndSide(parentView,MyOrientation.TOP   )) + margin.topMargin
            MyOrientation.LEFT     -> getViewBorderPos(borderSet.leftSideSet    ?: ViewAndSide(parentView,MyOrientation.LEFT  )) + margin.leftMargin
            MyOrientation.RIGHT    -> getViewBorderPos(borderSet.rightSideSet   ?: ViewAndSide(parentView,MyOrientation.RIGHT )) - margin.rightMargin
            MyOrientation.MIDDLE   -> getViewBorderPos(ViewAndSide(parentView,MyOrientation.MIDDLE))
        }


    }
    fun getSizeFromRecPos(recPosition: RecPosition):MySizeParams{
        return MySizeParams(
            abs(recPosition.left-recPosition.right).toInt(),
            abs(recPosition.top - recPosition.bottom).toInt()
        )
    }
    fun applyBorderAttributes(borderAttributes: BorderAttributes,view: View,borderRecPosition: RecPosition){
        val size = getSizeFromRecPos(borderRecPosition)
        val borderWidth = size.width
        val borderHeight = size.height
        when(borderAttributes){
            BorderAttributes.FillBorder -> {
                view.layoutParams.width = borderWidth
                view.layoutParams.height = borderHeight
                view.requestLayout()
            }
            BorderAttributes.FillIfOutOfBorder->{
                if(view.width>borderWidth) view.layoutParams.width = borderWidth
                if(view.height>borderHeight) view.layoutParams.height = borderHeight
                view.requestLayout()
            }
            BorderAttributes.None -> {}
        }
    }
    fun getMarginFromRecPos(borderRecPos: RecPosition,viewRecPos: RecPosition):MyMargin{
        val marginTop    = (abs(borderRecPos.top   -viewRecPos.top   )).toInt()
        val marginBottom = (abs(borderRecPos.bottom-viewRecPos.bottom)).toInt()
        val marginStart  = (abs(borderRecPos.left  -viewRecPos.left  )).toInt()
        val marginEnd    = (abs(borderRecPos.right -viewRecPos.right )).toInt()
        return MyMargin(topMargin = marginTop,
        leftMargin = marginStart,
        rightMargin = marginEnd,
        bottomMargin = marginBottom)
    }
    fun setMarginFromRecPosToConLay(view:View,margin: MyMargin,constraintLayout: ConstraintLayout){
        val con = ConstraintSet()
        con.clone(constraintLayout)
//
        con.setMargin(view.id, ConstraintSet.TOP,    margin.topMargin)
        con.setMargin(view.id, ConstraintSet.BOTTOM, margin.bottomMargin)
        con.setMargin(view.id, ConstraintSet.START,  margin.leftMargin)
        con.setMargin(view.id, ConstraintSet.END,    margin.rightMargin)

        con.applyTo(constraintLayout)
    }
    fun setPositionByMargin(
        positionData: ViewAndPositionData,
        constraintLayout: ConstraintLayout){
        val view = positionData.view
        val borderSet = positionData.borderSet
        view.viewTreeObserver.addOnGlobalLayoutListener(
            object :ViewTreeObserver.OnGlobalLayoutListener{
                override fun onGlobalLayout() {

                    fun getBorder(borderSide: MyOrientation):Float{
                        return getBorderFromBorderSet(borderSet,borderSide,constraintLayout)
                    }
                    fun getConBorder(borderSide: MyOrientation):Float{
                        return getViewBorderPos(ViewAndSide(constraintLayout,borderSide))
                    }
                    val rotationBefore = view.rotation
                    view.rotation = 0f

                    val borderLeft = getBorder(MyOrientation.LEFT)
                    val borderTop = getBorder(MyOrientation.TOP)
                    val borderRight = getBorder(MyOrientation.RIGHT)
                    val borderBottom = getBorder(MyOrientation.BOTTOM)



                    val borderRecPos = RecPosition(borderLeft,borderTop,borderRight,borderBottom)
                    applyBorderAttributes(positionData.orientation.borderAttributes,view,borderRecPos)
                    val result =calculatePositionInBorder(
                        view ,
                        borderRecPos,
                        positionData.orientation.verticalOrientation,
                        positionData.orientation.horizontalOrientation
                    )
                    val conBorder = RecPosition(
                        top = getConBorder(MyOrientation.TOP),
                        bottom = getConBorder(MyOrientation.BOTTOM),
                        left =  getConBorder(MyOrientation.LEFT),
                        right = getConBorder(MyOrientation.RIGHT)
                    )
                    val finalMargin = getMarginFromRecPos(conBorder,result)
                    setMarginFromRecPosToConLay(view,finalMargin,constraintLayout)
                    view.rotation = rotationBefore
                    view.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }

            }
        )
        view.requestLayout()

    }
}