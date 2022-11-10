package com.korokoro.kina.ui.customViews

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.ViewTreeObserver
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import com.korokoro.kina.R
import com.korokoro.kina.actions.ViewChangeActions
import com.korokoro.kina.actions.makeToast
import com.korokoro.kina.customClasses.CirclePosition
import com.korokoro.kina.customClasses.RecPosition

class HoleViewVer2 (
    context: Context,
    attrs: AttributeSet): View(context, attrs) {

    private val paint: Paint = Paint()
    private var holePaint: Paint = Paint()
    private var bitmap: Bitmap? = null
    private var layer: Canvas? = null
    private val globalLayoutSet = mutableMapOf<View, ViewTreeObserver.OnGlobalLayoutListener>()

    //position of hole
    var recRadius:Float = 0f
        set(value){
            field = value
            this.invalidate()
        }
    var holeShape:HoleShape = HoleShape.CIRCLE
        set(value){
            field = value
            this.invalidate()
        }
    var holeMargin:Int = 0
        set(value){
            field = value
            this.invalidate()
        }

    fun removeGlobalLayout(){
        globalLayoutSet.onEach {
            it.key.viewTreeObserver.removeOnGlobalLayoutListener(it.value)
        }
    }


    var viewUnderHole:View? = null
        set(value){
            field = value
            val view = field
            if(view == null){
                this.invalidate()
            } else{
                noHole = false
                removeGlobalLayout()
                view.viewTreeObserver.addOnGlobalLayoutListener (
                    object :ViewTreeObserver.OnGlobalLayoutListener{
                    override fun onGlobalLayout() {
                        globalLayoutSet[view] = this
                        recHolePosition = ViewChangeActions().getRecPos(view)
                        circleHolePosition = ViewChangeActions().getCirclePos(view)
                        noHole = (view.visibility == View.GONE)||(view.visibility == INVISIBLE)
                        this@HoleViewVer2.invalidate()

                    }
                    }
                )


            }

        }
    private var circleHolePosition: CirclePosition = CirclePosition(0.0f, 0.0f, 0.0f)

    private var recHolePosition: RecPosition = RecPosition(0.0f, 0.0f, 0.0f,0.0f)

    var noHole:Boolean = true
        set(value){
            field = value
            this.invalidate()
        }
    private fun getRecPos(view: View):RecPosition{
        return ViewChangeActions().getRecPos(view)
    }
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (bitmap == null) { configureBitmap() }
        layer?.drawRect(0.0f, 0.0f, width.toFloat(), height.toFloat(), paint)
        if(noHole.not()){
            when(holeShape){
                HoleShape.RECTANGLE  ->
                        layer?.drawRoundRect(recHolePosition.left-holeMargin,
                            recHolePosition.top-holeMargin,
                            recHolePosition.right+holeMargin,
                            recHolePosition.bottom+holeMargin,recRadius,recRadius,holePaint)
                HoleShape.CIRCLE     ->
                    layer?.drawCircle(circleHolePosition.x, circleHolePosition.y,
                        circleHolePosition.r+holeMargin, holePaint)

            }
        }
        else {
            layer?.drawRect(0f,0f,0f,0f,holePaint)
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
        holePaint.color = ContextCompat.getColor(context, android.R.color.transparent)
        holePaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }
}