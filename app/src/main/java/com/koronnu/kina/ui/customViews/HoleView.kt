package com.koronnu.kina.ui.customViews

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import com.koronnu.kina.R

class CirclePosition(var x: Float, var y: Float, var r: Float)
class RecPosition(var left: Float, var top: Float, var right: Float,var bottom:Float)
enum class HoleShape{
    CIRCLE,RECTANGLE
}
class HoleView (
    context: Context,
    attrs: AttributeSet): View(context, attrs) {

    private val paint: Paint = Paint()
    private var circleHolePaint: Paint = Paint()
    private var recHolePaint: Paint = Paint()
    private var bitmap: Bitmap? = null
    private var layer: Canvas? = null

    //position of hole
    var circleHolePosition: CirclePosition = CirclePosition(0.0f, 0.0f, 0.0f)
        set(value) {
            field = value
            //redraw
            this.invalidate()
        }
    var recHolePosition: RecPosition = RecPosition(0.0f, 0.0f, 0.0f,0.0f)
        set(value) {
            field = value
            //redraw
            this.invalidate()
        }
    var removeAllHoles:Boolean = false
        set(value){
            field = value
            this.invalidate()
        }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (bitmap == null) { configureBitmap() }
        layer?.drawRect(0.0f, 0.0f, width.toFloat(), height.toFloat(), paint)
        if(removeAllHoles.not()){
            layer?.drawRect(recHolePosition.left,recHolePosition.top,recHolePosition.right,recHolePosition.bottom,recHolePaint)
            //draw hole
            layer?.drawCircle(circleHolePosition.x, circleHolePosition.y, circleHolePosition.r, circleHolePaint)
        }
        else {
            layer?.drawRect(0f,0f,0f,0f,recHolePaint)
            //draw hole
            layer?.drawCircle(0f,0f,0f, circleHolePaint)
        }
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
        this.alpha = 0.7f
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