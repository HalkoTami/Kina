package com.koronnu.kina.ui.customViews

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.ViewTreeObserver
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import com.koronnu.kina.R
import com.koronnu.kina.actions.ViewChangeActions
import com.koronnu.kina.customClasses.enumClasses.HoleShape
import com.koronnu.kina.customClasses.normalClasses.CirclePosition
import com.koronnu.kina.customClasses.normalClasses.RecPosition

class HoleViewVer2 (
    context: Context,
    attrs: AttributeSet): View(context, attrs) {

    private val paint: Paint = Paint()
    private var holePaint: Paint = Paint()
    private var bitmap: Bitmap? = null
    private var layer: Canvas? = null
    private lateinit var activity: AppCompatActivity
    private val globalLayoutSet = mutableMapOf<View, ViewTreeObserver.OnGlobalLayoutListener>()

    //position of hole
    private var recRadius:Float = 20f
        set(value){
            field = value
            this.invalidate()
        }
    var holeShape: HoleShape = HoleShape.CIRCLE
        set(value){
            field = value
            this.invalidate()
        }
    private var holeMargin:Int = 20
        set(value){
            field = value
            this.invalidate()
        }

    fun removeGlobalLayout(){
        globalLayoutSet.onEach {
            it.key.viewTreeObserver.removeOnGlobalLayoutListener(it.value)
        }
    }
    fun initActivity(mainActivity: AppCompatActivity){
        activity = mainActivity
    }

    var animate:Boolean = true

    var viewUnderHole:View? = null
        set(value){
            field = value
            val view = field
            if(view == null){
                this.invalidate()
            } else{
                val beforeWasNoHole = noHole
                if(beforeWasNoHole) noHole = false
                removeGlobalLayout()
                if(animate) {
                    view.viewTreeObserver.addOnGlobalLayoutListener(
                        object :ViewTreeObserver.OnGlobalLayoutListener{
                            override fun onGlobalLayout() {
                                val centerPos = ViewChangeActions().getCenterPos(view,activity )
                                val startRecPos = if(beforeWasNoHole) RecPosition(
                                        top = centerPos.y,
                                        bottom = centerPos.y,
                                        left = centerPos.x,
                                        right = centerPos.x
                                    ) else recHolePosition
                                val startCirclePos = if(beforeWasNoHole) CirclePosition(
                                        x = centerPos.x,
                                        y = centerPos.y,
                                        r = 0f
                                    ) else circleHolePosition
                                when(holeShape){
                                    HoleShape.RECTANGLE -> animateRecHole(startRecPos,getRecPos(view))
                                    HoleShape.CIRCLE    -> animateCircleHole(startCirclePos,ViewChangeActions().getCirclePos(view,activity))
                                }
                                this@HoleViewVer2.invalidate()
                                view.viewTreeObserver.removeOnGlobalLayoutListener(this)

                            }
                        }
                    )
                } else view.viewTreeObserver.addOnGlobalLayoutListener (
                        object :ViewTreeObserver.OnGlobalLayoutListener{
                            override fun onGlobalLayout() {
                                globalLayoutSet[view] = this
                                recHolePosition = getRecPos(view)
                                circleHolePosition = ViewChangeActions().getCirclePos(view,activity)
                                noHole = (view.visibility == GONE)||(view.visibility == INVISIBLE)
                                this@HoleViewVer2.invalidate()
                            }
                        }
                )



            }

        }
    private var animRecPos : RecPosition? = null
    set(value) {
        field = value
        if(value!=null)
            recHolePosition = value
        this.invalidate()
    }
    private var animCirclePos : CirclePosition? = null
        set(value) {
            field = value
            if(value!=null)
                circleHolePosition = value
            this.invalidate()
        }
    fun animateRecHole(recPositionStart: RecPosition, recPositionEnd: RecPosition){
        val movingRecPos = RecPosition(
            top = recPositionStart.top,
            left = recPositionStart.left,
            bottom = recPositionStart.bottom,
            right = recPositionStart.right)
        AnimatorSet().apply {
            val topAnimation = ValueAnimator.ofFloat(recPositionStart.top,recPositionEnd.top)
            val leftAnimation = ValueAnimator.ofFloat(recPositionStart.left,recPositionEnd.left)
            val bottomAnimation = ValueAnimator.ofFloat(recPositionStart.bottom,recPositionEnd.bottom)
            val rightAnimation = ValueAnimator.ofFloat(recPositionStart.right,recPositionEnd.right)

            topAnimation.addUpdateListener {
                movingRecPos.top = it.animatedValue as Float
                animRecPos = movingRecPos
            }
            leftAnimation.addUpdateListener {
                movingRecPos.left = it.animatedValue as Float
            }
            rightAnimation.addUpdateListener {
                movingRecPos.right = it.animatedValue as Float
            }
            bottomAnimation.addUpdateListener {
                movingRecPos.bottom = it.animatedValue as Float
            }
            playTogether(topAnimation,leftAnimation,rightAnimation,bottomAnimation)

            duration =300
        }.start()
    }

    fun animateCircleHole(circlePosStart: CirclePosition, circlePosEnd: CirclePosition){
        val movingCirclePos = CirclePosition(
            x = circlePosStart.x,
            y = circlePosStart.y,
            r = circlePosStart.r)
        AnimatorSet().apply {
            val xAnimation = ValueAnimator.ofFloat(circlePosStart.x ,circlePosEnd.x)
            val yAnimation = ValueAnimator.ofFloat(circlePosStart.y,circlePosEnd.y)
            val rAnimation = ValueAnimator.ofFloat(circlePosStart.r,circlePosEnd.r)

            xAnimation.addUpdateListener {
                movingCirclePos.x = it.animatedValue as Float
                animCirclePos = movingCirclePos
            }
            yAnimation.addUpdateListener {
                movingCirclePos.y = it.animatedValue as Float
            }
            rAnimation.addUpdateListener {
                movingCirclePos.r = it.animatedValue as Float
            }
            playTogether(xAnimation,yAnimation,rAnimation)

            duration =300
        }.start()
    }

    private var circleHolePosition: CirclePosition = CirclePosition(0.0f, 0.0f, 0.0f)
    private var recHolePosition: RecPosition = RecPosition(0.0f, 0.0f, 0.0f,0.0f)
    set(value) {
        field = value
        this.invalidate()
    }
    var noHole:Boolean = true
        set(value){
            field = value
            this.invalidate()
        }
    private fun getRecPos(view: View): RecPosition {
        return ViewChangeActions().getRecPos(view,activity)
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
        paint.color = ColorUtils.setAlphaComponent(ContextCompat.getColor(context, R.color.black), (255 * backgroundAlpha) )

        //configure hole color & mode
        holePaint.color = ContextCompat.getColor(context, android.R.color.transparent)
        holePaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)

    }
}