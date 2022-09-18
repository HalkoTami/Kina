package com.example.tangochoupdated.ui.customViews

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import com.example.tangochoupdated.R

class HolePosition(var x: Float, var y: Float, var r: Float)
class HoleView (
    context: Context,
    attrs: AttributeSet): View(context, attrs) {

    private val paint: Paint = Paint()
    private var holePaint: Paint = Paint()
    private var bitmap: Bitmap? = null
    private var layer: Canvas? = null

    //position of hole
    var holePosition: HolePosition = HolePosition(0.0f, 0.0f, 0.0f)
        set(value) {
            field = value
            //redraw
            this.invalidate()
        }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (bitmap == null) { configureBitmap() }

        //draw background
        layer?.drawRect(0.0f, 0.0f, width.toFloat(), height.toFloat(), paint)
        //draw hole
        layer?.drawCircle(holePosition.x, holePosition.y, holePosition.r, holePaint)
        //draw bitmap
        canvas.drawBitmap(bitmap!!, 0.0f, 0.0f, paint);
    }

    private fun configureBitmap() {
        //create bitmap and layer
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        layer = Canvas(bitmap!!)
    }

    init {
        //configure background color
        val backgroundAlpha = 0.8
        paint.color = ColorUtils.setAlphaComponent(ContextCompat.getColor(context, R.color.gray), (255 * backgroundAlpha).toInt() )

        //configure hole color & mode
        holePaint.color = ContextCompat.getColor(context, android.R.color.transparent, )
        holePaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }
}