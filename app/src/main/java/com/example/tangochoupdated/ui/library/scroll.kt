package com.example.tangochoupdated.ui.library

import android.content.Context
import android.util.AttributeSet
import android.widget.ScrollView
import androidx.core.widget.NestedScrollView

class ScrollViewWithListener(
    context: Context, attrs: AttributeSet
) : NestedScrollView(context, attrs) {

    private var mOnScrollChangedListener: OnScrollChangedListener? = null

    interface OnScrollChangedListener {
        fun onScrollChanged(x: Int, y: Int, oldx: Int, oldy: Int)
    }

    fun setOnScrollListener(listenerOn: OnScrollChangedListener) {
        this.mOnScrollChangedListener = listenerOn
    }

    override fun onScrollChanged(x: Int, y: Int, oldx: Int, oldy: Int) {
        super.onScrollChanged(x, y, oldx, oldy)
        mOnScrollChangedListener?.onScrollChanged(x, y, oldx, oldy)
    }
}