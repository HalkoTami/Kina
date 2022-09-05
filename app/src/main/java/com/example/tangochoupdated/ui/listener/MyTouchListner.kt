package com.example.tangochoupdated

import android.content.Context
import android.view.*
import java.lang.Math.abs
import kotlin.math.absoluteValue

open class MyTouchListener(context: Context) : View.OnTouchListener {

    private val gestureDetector = GestureDetector(context, MyGestureListener())




    private inner class MyGestureListener : GestureDetector.OnGestureListener {
        override fun onShowPress(e: MotionEvent?) {

        }

        override fun onSingleTapUp(e: MotionEvent?): Boolean {
            onSingleTap(e)
            return false
        }

        override fun onScroll(
            e1: MotionEvent?,
            e2: MotionEvent?,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            if((e2!!.x-e1!!.x) > 0){
                onScrollRight()
            } else{
                onScrollLeft((e2!!.x-e1!!.x).absoluteValue,e1)
            }

            return false
        }

        override fun onLongPress(e: MotionEvent?) {
            onLongClick(e)
        }

        private val swipeThreshold = 100
        private val swipeVelocityThreshold = 100

        override fun onDown(e: MotionEvent): Boolean {
            onDown()
            return true
        }




        override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            val result = false
            try {
                val diffY = e2.y - e1.y
                val diffX = e2.x - e1.x
                if (abs(diffX) > abs(diffY)) {
                    if (abs(diffX) > swipeThreshold && abs(velocityX) > swipeVelocityThreshold) {
                        if (diffX > 0) {
                            onSwipeRight()
                        } else {
                            onSwipeLeft()
                        }
                    }
                } else {
                    // onTouch(e);
                }
            } catch (exception: Exception) {
                exception.printStackTrace()
            }

            return result
        }
    }

    var down:Boolean = false
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        v!!.performClick()
        when(event?.action){
            MotionEvent.ACTION_DOWN -> down = true
            MotionEvent.ACTION_UP -> down = false
        }
        if(down){
            onMove(event)
        }

        gestureDetector.onTouchEvent(event)
        return true
    }
    open fun onScrollLeftActionUp(){

    }
    open fun onMove(event: MotionEvent?){

    }

    open fun onSwipeRight() {}

    open fun onSwipeLeft() {}

    open fun onLongClick(motionEvent: MotionEvent?){}

    open fun onScrollRight(){

    }
    open fun onScrollLeft(distanceX: Float,motionEvent:MotionEvent?){

    }
    open fun onSingleTap(motionEvent: MotionEvent?){

    }
    open fun onDown(){

    }
}

