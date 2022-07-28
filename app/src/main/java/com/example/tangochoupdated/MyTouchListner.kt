package com.example.tangochoupdated

import android.content.Context
import android.view.*
import java.lang.Math.abs

open class MyTouchListener(context: Context) : View.OnTouchListener {

    private val gestureDetector = GestureDetector(context, MyGestureListener())




    private inner class MyGestureListener : GestureDetector.OnGestureListener {
        override fun onShowPress(e: MotionEvent?) {

        }

        override fun onSingleTapUp(e: MotionEvent?): Boolean {
            onSingleTap()
            return false
        }

        override fun onScroll(
            e1: MotionEvent?,
            e2: MotionEvent?,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            if(distanceX<0){
                onScrollRight()
            } else{
                onScrollLeft()
            }
            return false
        }

        override fun onLongPress(e: MotionEvent?) {
            onLongClick()
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

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        v!!.performClick()
        gestureDetector.onTouchEvent(event)
        return true
    }

    open fun onSwipeRight() {}

    open fun onSwipeLeft() {}

    open fun onLongClick(){}

    open fun onScrollRight(){

    }
    open fun onScrollLeft(){

    }
    open fun onSingleTap(){

    }
    open fun onDown(){

    }
}

