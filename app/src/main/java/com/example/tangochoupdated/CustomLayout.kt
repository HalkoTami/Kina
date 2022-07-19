package com.example.tangochoupdated

import android.content.Context
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout

open  class CustomLayout(context: Context): FrameLayout(context) {


    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        Toast.makeText(context, "onClickDelete", Toast.LENGTH_SHORT).show()
        return super.onInterceptTouchEvent(ev)

    }
}