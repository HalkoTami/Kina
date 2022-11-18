package com.koronnu.kina.actions

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.view.View
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.view.children
import com.koronnu.kina.R
import com.koronnu.kina.activity.MainActivity
import com.koronnu.kina.customClasses.enumClasses.*
import com.koronnu.kina.customClasses.normalClasses.*
import com.koronnu.kina.databinding.CallOnInstallBinding
import com.koronnu.kina.databinding.TouchAreaBinding
import com.koronnu.kina.ui.animation.Animation
import com.koronnu.kina.ui.customViews.HoleViewVer2


class InstallGuide(val activity:MainActivity,private val frameLay:FrameLayout){
    private var _callOnInstallBinding : CallOnInstallBinding? =null
    private val callOnInstallBinding get() = _callOnInstallBinding!!

    private var _guideParentConLay:ConstraintLayout? = null
    val guideParentConLay get() = _guideParentConLay!!

    private var _arrow :ImageView? = null
    val arrow get() = _arrow!!

    private var _character:ImageView? = null
    val character get() = _character!!

    private var _holeView :HoleViewVer2? = null
    val holeView get() = _holeView!!

    private var _textView:TextView? = null
    val textView get() = _textView!!

    private var _bottom:View? = null
    val bottom get() = _bottom!!

    val globalLayoutSet = mutableMapOf<View, ViewTreeObserver.OnGlobalLayoutListener>()
    private val touchAreaTag = 1

    var holeShapeInGuide: HoleShape = HoleShape.CIRCLE
    var animateHole :Boolean = true
    var viewUnderSpotInGuide:View? = null
        set(value) {
            field = value
            doOnViewUnderHoleSet()
        }

    var characterBorderSet: BorderSet = BorderSet()
    var characterOrientation: MyOrientationSet = MyOrientationSet()
    var spbPosSimple: ViewAndSide? = null
        set(value) {
            field = value
            doOnSpbPosSimpleSet()
        }

    var spbBorderSet: BorderSet = BorderSet()
    var spbOrientation: MyOrientationSet = MyOrientationSet(verticalOrientation =  MyVerticalOrientation.BOTTOM)
    var textFit:Boolean = false

    private val arrowMargin:MyMargin = MyMargin(10,10,10,10)

    private var freshCreated = true

    fun makeHereTouchable(view: View){
        callOnInstallBinding.apply {
            guideParentConLay.setOnClickListener(null)
            root.isClickable = false
            arrayOf(viewUnTouchableLeft,viewUnTouchableRight,viewUnTouchableBottom,viewUnTouchableTop).onEach {
                it.isClickable = true
            }
            val oriSet = MyOrientationSet(borderAttributes = BorderAttributes.FillBorder)
            setPositionByMargin(ViewAndPositionData(viewUnTouchableLeft,
                getSimplePosRelation(view,MyOrientation.LEFT,false), oriSet))
            setPositionByMargin(ViewAndPositionData(viewUnTouchableBottom,
                getSimplePosRelation(view,MyOrientation.BOTTOM,false), oriSet))
            setPositionByMargin(ViewAndPositionData(viewUnTouchableRight,
                getSimplePosRelation(view,MyOrientation.RIGHT,false), oriSet))
            setPositionByMargin(ViewAndPositionData(viewUnTouchableTop,
                getSimplePosRelation(view,MyOrientation.TOP,false),oriSet))
        }
    }

    fun setCharacterPos(){
        setCharacterSize()
        setPositionByMargin(ViewAndPositionData(character,characterBorderSet,characterOrientation))
    }
    var allViewsGoneAnimDoOnEnd:()-> Unit = {}

    fun animateAllViewsGone():AnimatorSet{
        return AnimatorSet().apply {
            playTogether(changeCharacterVisibility(false),changeArrowVisibility(false),getSpbVisibilityAnim(false))
            doOnEnd{
                allViewsGoneAnimDoOnEnd()
            }
        }
    }
    var characterPosChangeAnimDoOnEnd:()->Unit = {}
    var doAfterCharacterPosChanged:()->Unit = {}
    fun animateCharacterPos():AnimatorSet{
        return AnimatorSet().apply {
            characterVisibilityAnimDoOnEnd = {
                setCharacterSize()
                setCharacterPos()
                doAfterCharacterPosChanged()
                characterVisibilityAnimDoOnEnd ={}
            }
            playSequentially(changeCharacterVisibility(false),changeCharacterVisibility(true))
            doOnEnd{
                characterPosChangeAnimDoOnEnd()
            }
        }

    }
    fun goNextOnClickAnyWhere(func:()->Unit){
        callOnInstallBinding.root.setOnClickListener {
            makeTouchAreaGone()
            func()
        }
    }
    var spbVisibilityAnimDoOnEnd:()-> Unit = {}
    fun getSpbVisibilityAnim(visible: Boolean):AnimatorSet{
        return AnimatorSet().apply {
            playTogether(getAppearAlphaAnimation(textView,visible),
                getAppearAlphaAnimation(bottom,visible))
            doOnEnd{
                spbVisibilityAnimDoOnEnd()
            }
        }
    }
    var characterVisibilityAnimDoOnEnd:()->Unit = {}
    var arrowVisibilityAnimDoOnEnd:()->Unit = {}
    private fun changeCharacterVisibility(visible: Boolean):ValueAnimator{
        appearAlphaAnimDonOnEnd = {
            characterVisibilityAnimDoOnEnd()
            characterVisibilityAnimDoOnEnd = {}
        }
        return getAppearAlphaAnimation(character,visible)
    }
    fun changeArrowVisibility(visible: Boolean):ValueAnimator{
        appearAlphaAnimDonOnEnd = {
            arrowVisibilityAnimDoOnEnd()
            arrowVisibilityAnimDoOnEnd = {}
        }
        return getAppearAlphaAnimation(arrow,visible)
    }
    fun makeTouchAreaGone(){
        guideParentConLay.children.iterator().forEach {
            if(it.tag == touchAreaTag) it.visibility = View.GONE
        }
    }
    var appearAlphaAnimDonOnEnd :()->Unit = {}
    fun getAppearAlphaAnimation(view :View, visible:Boolean): ValueAnimator {
        return Animation().appearAlphaAnimation(view,visible,if(view == holeView)0.7f else 1f){
            appearAlphaAnimDonOnEnd()
            appearAlphaAnimDonOnEnd = {}
        }
    }
    fun getSimplePosRelation(standardView:View, orientation: MyOrientation, fit:Boolean): BorderSet {
        return ViewChangeActions().getSimpleBorderSet(standardView,orientation,fit)
    }
    var characterSizeDimenId:Int = R.dimen.character_size_large
    private fun setCharacterSize(){
        character.apply {
            val sizeLarge = getPixelSize(characterSizeDimenId)
            layoutParams.width = sizeLarge
            layoutParams.height = sizeLarge
            requestLayout()
        }
    }
    private fun setConfirmEndGuideCL(){
        callOnInstallBinding.confirmEndGuideBinding.apply {
            setClickListeners(arrayOf(
                btnCancelEnd,
                btnCloseConfirmEnd,
                btnCommitEnd),GuideEndPopUpCL())
        }
    }
    private fun refreshInstallGuide(){
        activity._callOnInstallBinding = CallOnInstallBinding.inflate(activity.layoutInflater)
        _callOnInstallBinding = activity.callOnInstallBinding
        setLateInitVars()
        setConfirmEndGuideCL()
    }
    fun callOnFirst(){
        frameLay.removeAllViews()
        refreshInstallGuide()
        frameLay.addView(callOnInstallBinding.root)
        activity.mainActivityViewModel.setGuideVisibility(true)
        freshCreated = false
        makeTouchAreaGone()
        changeMulVisibility(arrayOf(character,arrow,textView,bottom),false)
        holeView.initActivity(activity)
        removeHole()
        setCharacterPos()
        setCharacterSize()
        spbPosSimple = ViewAndSide(character,MyOrientation.TOP)

        changeCharacterVisibility(true).start()

    }
    private fun getArrowDirectionFromArrowPos(arrowPosition: MyOrientation):MyOrientation{
        return when(arrowPosition){
            MyOrientation.BOTTOM-> MyOrientation.TOP
            MyOrientation.LEFT -> MyOrientation.RIGHT
            MyOrientation.RIGHT -> MyOrientation.LEFT
            MyOrientation.TOP -> MyOrientation.BOTTOM
            else ->  MyOrientation.TOP
        }
    }

    fun setArrow(arrowPosition: MyOrientation,
                 view: View){
        fun getArrowPositionData():ViewAndPositionData{
            val getBorderSet = getSimplePosRelation(view,arrowPosition, true)
            getBorderSet.margin = arrowMargin
            return ViewAndPositionData(arrow,
                getBorderSet,
                ViewChangeActions().getOriSetByNextToPosition(arrowPosition, BorderAttributes.None))
        }
        setPositionByMargin(getArrowPositionData())
        setArrowDirection(getArrowDirectionFromArrowPos(arrowPosition))
        changeArrowVisibility(true).start()
    }


//    fun setSpbPos(string: String){
//        changeViewVisibility(conLaySpeakBubble,false)
//        textView.text = string
//        val posData = ViewAndPositionData(
//            conLaySpeakBubble,
//            borderSet = spbBorderSet ,
//            orientation= spbOrientation)
//        setPositionByMargin(posData)
//    }

    fun animateSpbPos(string: String):AnimatorSet{
        return AnimatorSet().apply {
            spbVisibilityAnimDoOnEnd = {
                changeMulVisibility(arrayOf(bottom,textView),false)
                textView.text = string
                val posData = ViewAndPositionData(
                    textView,
                    borderSet = spbBorderSet ,
                    orientation= spbOrientation)
                setPositionByMargin(posData) }
            playSequentially(getSpbVisibilityAnim(false),speakBubbleTextAnimation())
        }

    }
    fun removeHole(){
        holeView.removeGlobalLayout()
        holeView.noHole = true
    }
    fun cloneView(view: View) {
        copyViewInConLay(view).setOnClickListener {
            view.callOnClick()
        }
    }
    fun goNextOnClickTouchArea(view: View, func: () -> Unit) {
        guideParentConLay.setOnClickListener(null)
        copyViewInConLay(view).setOnClickListener {
            makeTouchAreaGone()
            func()
        }
    }
    fun copyViewInConLay(view:View):View{
        guideParentConLay.setOnClickListener(null)
        val a = TouchAreaBinding.inflate(activity.layoutInflater)
        a.touchView.tag = 1

        val id = View.generateViewId()
        a.touchView.id = id
        guideParentConLay.addView(a.touchView)
        val con = ConstraintSet()
        con.clone(guideParentConLay)
//
        con.connect(id, ConstraintSet.RIGHT ,ConstraintSet.PARENT_ID,ConstraintSet.RIGHT)
        con.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID,ConstraintSet.TOP)
        con.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID,ConstraintSet.BOTTOM)
        con.connect(id, ConstraintSet.LEFT, ConstraintSet.PARENT_ID,ConstraintSet.LEFT)

        con.applyTo(guideParentConLay)
//        saveBorderDataMap(a.touchView, BorderSet(
//            topSideSet = ViewAndSide(view, MyOrientation.TOP),
//            bottomSideSet = ViewAndSide(view, MyOrientation.BOTTOM),
//            leftSideSet = ViewAndSide(view, MyOrientation.LEFT),
//            rightSideSet = ViewAndSide(view, MyOrientation.RIGHT),
//
//            ),borderDataMap
//        )
        val positionData = ViewAndPositionData(a.touchView
            ,getSimplePosRelation(view, MyOrientation.MIDDLE,true)
            , MyOrientationSet(MyVerticalOrientation.MIDDLE,
                MyHorizontalOrientation.MIDDLE,
                BorderAttributes.FillBorder))
        setPositionByMargin(
            positionData, )


        return a.touchView
    }
    fun setArrowDirection(direction: MyOrientation){
        arrow.rotation =
            when(direction){
                MyOrientation.BOTTOM-> -450f
                MyOrientation.LEFT -> 0f
                MyOrientation.RIGHT -> 900f
                MyOrientation.TOP -> 450f
                else -> return
            }

    }


    private fun speakBubbleTextAnimation(): AnimatorSet {

        val finalDuration:Long = 200

        val txvScaleAnim1 = ValueAnimator.ofFloat(0.7f,1.1f)
        val txvScaleAnim2 = ValueAnimator.ofFloat(1.1f,1f)

        arrayOf(txvScaleAnim1,txvScaleAnim2).onEach { animator ->
            animator.addUpdateListener {
                val progressPer = it.animatedValue as Float
                ViewChangeActions().setScale(textView,progressPer,progressPer)
            }
        }
        val txvScaleAnimSet = AnimatorSet().apply {
            playSequentially( txvScaleAnim1,txvScaleAnim2)
            doOnStart {
                ViewChangeActions().setScale(textView,0.7f,0.7f)
//                ViewChangeActions().setScale(textView,0.7f,0.7f)

            }
            txvScaleAnim1.duration = finalDuration*0.7.toLong()
            txvScaleAnim2.duration = finalDuration*0.3.toLong()
        }
        val bottomAnim1 = ValueAnimator.ofFloat(0f,1f)

        val bottomTransAnim1 = ValueAnimator.ofFloat(1f,0.2f)
        val bottomTransAnim2 = ValueAnimator.ofFloat(0.4f,1.1f)
        val bottomTransAnim3 = ValueAnimator.ofFloat(1.1f,1f)
        arrayOf(bottomTransAnim1,bottomTransAnim2,bottomTransAnim3).onEach { animator ->
            animator.addUpdateListener {
                ViewChangeActions().setScale(bottom,it.animatedValue as Float,1f)
                bottom.pivotX = 0f
            }
        }

        val bottomTransAnim = AnimatorSet().apply {
            playSequentially( bottomTransAnim1,bottomTransAnim2,bottomTransAnim3)
        }
        val finalAnim = AnimatorSet().apply {
            playTogether(txvScaleAnimSet,bottomAnim1,bottomTransAnim,getSpbVisibilityAnim(true))
            txvScaleAnimSet.duration = finalDuration
            bottomTransAnim.duration = finalDuration
        }

        return finalAnim
    }
    private fun removeGlobalListener(globalLayoutSet: MutableMap<View, ViewTreeObserver.OnGlobalLayoutListener>){
        globalLayoutSet.onEach {
            it.key.viewTreeObserver.removeOnGlobalLayoutListener(it.value)
        }
    }
    private fun getPixelSize(dimenId:Int):Int{
        return activity.resources.getDimensionPixelSize(dimenId)
    }
    fun setPositionByMargin(positionData: ViewAndPositionData){
        removeGlobalListener(globalLayoutSet)
        val view = positionData.view
        view.viewTreeObserver.addOnGlobalLayoutListener(object :ViewTreeObserver.OnGlobalLayoutListener{
            override fun onGlobalLayout() {
                ViewChangeActions().setPositionByMargin(positionData,guideParentConLay,activity)
                globalLayoutSet[view] = this
            }
        })
        view.requestLayout()
    }
    private fun makeHole(){
        holeView.apply {
            holeShape = holeShapeInGuide
            animate = animateHole
            viewUnderHole = viewUnderSpotInGuide
        }
    }
    private fun doOnViewUnderHoleSet(){
        if(viewUnderSpotInGuide!=null) makeHole() else removeHole()
    }
    private fun doOnSpbPosSimpleSet(){
        val value = spbPosSimple ?:return
        spbBorderSet = ViewChangeActions().getSimpleBorderSet(value.view,value.side,textFit)
        spbOrientation = ViewChangeActions().getOriSetByNextToPosition(value.side, BorderAttributes.FillIfOutOfBorder)
    }
    private fun setLateInitVars(){
        callOnInstallBinding.apply {
            _arrow = imvFocusArrow
            _character = imvCharacter
            _holeView = viewWithHole
            _textView = txvSpeakBubble
            _bottom = sbBottom
            _guideParentConLay = root
        }
    }
    inner class GuideEndPopUpCL:View.OnClickListener{
        override fun onClick(v: View?) {
            callOnInstallBinding.confirmEndGuideBinding.apply {
                when(v){
                    btnCancelEnd,btnCloseConfirmEnd -> activity.mainActivityViewModel.setConfirmEndGuidePopUpVisible(false)
                    btnCommitEnd                    -> activity.mainActivityViewModel.onClickEndGuide(activity)
                }
            }
        }
    }

}