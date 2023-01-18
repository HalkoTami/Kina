package com.koronnu.kina.ui.tabLibrary

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import com.koronnu.kina.R
import com.koronnu.kina.data.model.enumClasses.LibRVState
import com.koronnu.kina.util.Animation
import kotlin.math.abs

open class LibraryRVItemClickListener(val context: Context,
                                 val scrollView: NestedScrollView,
                                 val recyclerView: RecyclerView,
                                 val libraryBaseViewModel: LibraryBaseViewModel
) :RecyclerView.OnItemTouchListener{
    private val gestureScrollDetector = GestureDetector(context, ScrollDetector())
    inner class ScrollDetector : GestureDetector.OnGestureListener {
        override fun onShowPress(e: MotionEvent) {}
        override fun onSingleTapUp(e: MotionEvent): Boolean = false
        override fun onScroll(
            e1: MotionEvent,
            e2: MotionEvent,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            if((e2.x-e1.x) < -50)
                onScrollLeft(abs(e2.x-e1.x),e1)
            return ((e2.x-e1.x) < -50)
        }
        override fun onLongPress(e: MotionEvent) {}
        override fun onDown(e: MotionEvent): Boolean = false
        override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean = false
    }
    private var startPosition : MotionEvent? = null
    private var swipingDistance:Float = 1f
    fun onScrollLeft(distanceX:Float, startEvent:MotionEvent){
        if(libraryBaseViewModel.getOnlyLongClickActive) return
        scrollView.requestDisallowInterceptTouchEvent(true)
        startPosition = startEvent
        swipingDistance = distanceX
        val view = recyclerView.findChildViewUnder(startEvent.x,startEvent.y)
        val lineLay = view?.findViewById<LinearLayoutCompat>(R.id.linLay_swipe_show) ?:return
        val conLay = view.findViewById<ConstraintLayout>(R.id.lib_rv_base_container) ?:return
        conLay.apply {
            if(conLay.tag== LibRVState.Plane){
                lineLay.layoutParams.width = 1
                lineLay.requestLayout()
                lineLay.children.iterator().forEach {
                    it.visibility = View.VISIBLE
                }
                lineLay.visibility = View.VISIBLE
                conLay.tag = LibRVState.LeftSwiping

            }else if(conLay.tag== LibRVState.LeftSwiping) {
                if(conLay.tag!= LibRVState.LeftSwiping){
                    conLay.tag = LibRVState.LeftSwiping
                }

                lineLay.layoutParams.width = swipingDistance.toInt()/5 + 1
                lineLay.requestLayout()

            }

        }
    }
    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        return gestureScrollDetector.onTouchEvent(e)
    }


    open fun doOnSwipeAppeared (){

    }

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
        gestureScrollDetector.onTouchEvent(e)
        if(startPosition==null){
            startPosition = e
        } else {
            if((e.actionMasked== MotionEvent.ACTION_UP)){
                val view = recyclerView.findChildViewUnder(startPosition!!.x,startPosition!!.y)
                val lineLay = view?.findViewById<LinearLayoutCompat>(R.id.linLay_swipe_show) ?:return
                val conLay = view.findViewById<ConstraintLayout>(R.id.lib_rv_base_container) ?:return
                if(conLay.tag== LibRVState.LeftSwiping){
                    if(lineLay.layoutParams.width <25){
                        Animation().animateLibRVLeftSwipeLay(lineLay,false){}
                        conLay.tag = LibRVState.Plane
                    }
                    else if (lineLay.layoutParams.width>=25){
                        Animation().animateLibRVLeftSwipeLay(lineLay ,true){libraryBaseViewModel.doOnSwipeEnd()}
                        conLay.tag = LibRVState.LeftSwiped
                        libraryBaseViewModel.setLeftSwipedItemExists(true)
                    }

                }

            }
        }
    }

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {

    }
}