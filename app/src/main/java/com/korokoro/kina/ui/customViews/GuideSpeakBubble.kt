package com.korokoro.kina.ui.customViews

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import com.korokoro.kina.R
import com.korokoro.kina.customClasses.CirclePosition
import com.korokoro.kina.customClasses.RecPosition


class GuideSpeakBubble (
    context: Context,
    attrs: AttributeSet): androidx.appcompat.widget.AppCompatTextView(context, attrs) {

    private val paint: Paint = Paint()
    private var circleHolePaint: Paint = Paint()
    private var recHolePaint: Paint = Paint()
    private var bitmap: Bitmap? = null
    private var layer: Canvas? = null

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (bitmap == null) { configureBitmap() }
        layer?.drawRoundRect(0.0f, 0.0f, width.toFloat(), height.toFloat(),20f,20f,paint)

        layer?.drawRect(10f,20f,0f,50f,recHolePaint)
        //draw hole
//        layer?.drawCircle(circleHolePosition.x, circleHolePosition.y, circleHolePosition.r, circleHolePaint)
        //draw background

        //draw bitmap
        canvas.drawBitmap(bitmap!!, 0.0f, 0.0f, paint)
    }

    private fun configureBitmap() {
        //create bitmap and layer
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        layer  = Canvas(bitmap!!)
    }

    init {
        //configure background color
        val backgroundAlpha = 1
        paint.color = ColorUtils.setAlphaComponent(ContextCompat.getColor(context, R.color.black), (255 * backgroundAlpha).toInt() )

        //configure hole color & mode
        circleHolePaint.color = ContextCompat.getColor(context, android.R.color.transparent)
        circleHolePaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        recHolePaint.color = ContextCompat.getColor(context, android.R.color.transparent)
        recHolePaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }
}