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
import com.koronnu.kina.util.makeToast
import kotlin.math.abs

open class LibraryRVItemClickListener(val recyclerView: RecyclerView) :RecyclerView.OnItemTouchListener{

    private val gestureScrollDetector = GestureDetector(recyclerView.context, ScrollDetector())
    inner class ScrollDetector : GestureDetector.OnGestureListener {
        override fun onShowPress(e: MotionEvent) {}
        override fun onSingleTapUp(e: MotionEvent): Boolean = false
        override fun onScroll(
            e1: MotionEvent,
            e2: MotionEvent,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            if((e2.x-e1.x) < -50) onScrollLeft(abs(e2.x-e1.x),e1)
            return ((e2.x-e1.x) < -50)
        }
        override fun onLongPress(e: MotionEvent) {}
        override fun onDown(e: MotionEvent): Boolean = false
        override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean = false
    }

    private var scrollingView :View? = null
    private var viewHolder :LibFragPlaneRVListAdapter.LibFragFileViewHolder? = null
    fun onScrollLeft(distanceX:Float, startEvent:MotionEvent){
        recyclerView.parent.requestDisallowInterceptTouchEvent(true)
//        scrollView.requestDisallowInterceptTouchEvent(true)
        val scrolledView = recyclerView.findChildViewUnder(startEvent.x,startEvent.y) ?:return
        if(scrollingView != scrolledView) {
            scrollingView = scrolledView
            viewHolder = recyclerView.getChildViewHolder(scrolledView) as LibFragPlaneRVListAdapter.LibFragFileViewHolder
        }
        viewHolder?.onScrollLeft(distanceX)

    }
    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        return gestureScrollDetector.onTouchEvent(e)
    }


    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
        gestureScrollDetector.onTouchEvent(e)
        if(e.actionMasked == MotionEvent.ACTION_UP) {
            viewHolder?.onMotionEventUp()
            scrollingView = null
        }
    }

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {

    }
}