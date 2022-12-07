package com.koronnu.kina.actions

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.app.ActionBar.LayoutParams
import android.view.View
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.view.children
import androidx.core.view.get
import com.koronnu.kina.R
import com.koronnu.kina.activity.MainActivity
import com.koronnu.kina.customClasses.enumClasses.*
import com.koronnu.kina.customClasses.normalClasses.*
import com.koronnu.kina.databinding.TouchAreaBinding
import com.koronnu.kina.db.dataclass.Card
import com.koronnu.kina.db.dataclass.File
import com.koronnu.kina.db.enumclass.FileStatus
import com.koronnu.kina.ui.animation.Animation


class GuideActions(val activity:MainActivity){

    private val callOnInstallBinding get() = activity.mainActivityViewModel.guideViewModel.getGuideBinding
    val libraryViewModel = activity.mainActivityViewModel.libraryBaseViewModel
    val mainViewModel = activity.mainActivityViewModel
    val moveToViewModel  = libraryViewModel.chooseFileMoveToViewModel
    val createFileViewModel = activity.mainActivityViewModel.editFileViewModel
    val guideViewModel = activity.mainActivityViewModel.guideViewModel
    val createCardViewModel = activity.createCardViewModel
    val deletePopUpViewModel = activity.deletePopUpViewModel
    val btnDeleteFile                     get() = libRvFirstItem.findViewById<ImageView>(R.id.btn_delete)!!
    val frameLayConfirmDelete             get() =libraryBaseBinding.frameLayConfirmDelete
    val frameLayConfirmDeleteWithChildren get() = libraryBaseBinding.frameLayConfirmDeleteWithChildren
    val btnCommitDeleteWithChildren       get() = libraryBaseBinding.confirmDeleteChildrenPopUpBinding.btnDeleteAllChildren
    val btnDeleteOnlyParent               get() = libraryBaseBinding.confirmDeleteChildrenPopUpBinding.deleteOnlyFile
    val frameLayInBox                     get() = activity.findViewById<ConstraintLayout>(R.id.frameLay_inBox)!!
    val lineLayMenuDelete                 get() = libraryChildBinding.multiSelectMenuBinding.linLayDeleteSelectedItems
    private val createCardStringCardBinding get() = createCardViewModel.createCardFragStringFragBinding
    private val createCardFragMainBinding   get() = createCardViewModel.createCardFragMainBinding
    val edtCardFrontTitle           get() = createCardStringCardBinding.edtFrontTitle
    val edtCardBackTitle            get() = createCardStringCardBinding.edtBackTitle
    val edtCardBackContent          get() = createCardStringCardBinding.edtBackContent
    val edtCardFrontContent         get() = createCardStringCardBinding.edtFrontContent
    val linLayCreateCardNavigation  get() = createCardFragMainBinding.layNavigateButtons
    val createCardInsertNext        get() = createCardFragMainBinding.btnInsertNext
    val createCardInsertPrevious    get() = createCardFragMainBinding.btnInsertPrevious
    val createCardNavFlipNext       get() = createCardFragMainBinding.btnNext
    val createCardNavFlipPrevious   get() = createCardFragMainBinding.btnPrevious
    val editText                    get() = mainActivityBinding.editFileBinding.edtFileTitle
    val guideParentConLay           get() =  callOnInstallBinding.root
    val arrow                       get() = callOnInstallBinding.imvFocusArrow
    val conLayGoNext                get() = callOnInstallBinding.conLayGuideGoNext
    val character                   get() =  callOnInstallBinding.imvCharacter
    private val holeView                    get() = callOnInstallBinding.viewWithHole
    val textView                    get() = callOnInstallBinding.txvSpeakBubble
    val bottom                      get() = callOnInstallBinding.sbBottom
    private val libraryChildBinding         get() = activity.mainActivityViewModel.libraryBaseViewModel.getChildFragBinding
    private val libraryBaseBinding          get() = libraryViewModel.libraryFragBinding
    private val mainActivityBinding         get() = activity.mainActivityViewModel.mainActivityBinding
    val libraryRv                   get() = libraryChildBinding.vocabCardRV
    val libRvFirstItem              get() = libraryRv[0]
    val imvOpenMultiModeMenu        get() = libraryChildBinding.topBarMultiselectBinding.imvChangeMenuVisibility
    val frameLayMultiMenu           get() = libraryChildBinding.frameLayMultiModeMenu
    val imvBnvBtnAdd                get() = mainActivityBinding.bnvBinding.bnvImvAdd
    val frameLayCreateFlashCard     get() = mainActivityBinding.bindingAddMenu.frameLayNewFlashcard
    private val frameLayCreateFolder        get() = mainActivityBinding.bindingAddMenu.frameLayNewFolder
    val frameLayCreateCard          get() = mainActivityBinding.bindingAddMenu.frameLayNewCard
    val frameLayEditFile            get() = mainActivityBinding.frameLayEditFile
    val btnCloseEditFilePopUp       get() = mainActivityBinding.editFileBinding.btnClose
    val btnCreateFile               get() = mainActivityBinding.editFileBinding.btnFinish
    val frameLayMoveToThisItem      get() = libraryRv.findViewById<FrameLayout>(R.id.rv_base_frameLay_left)!!
    val frameLayConfirmMove         get() = libraryChildBinding.frameLayConfirmMove
    private val frameLayBnv                 get() = mainActivityBinding.frameBnv
    private val stringFlashCard             get() = activity.getString(R.string.flashcard)
    private val stringFolder                get() = activity.getString(R.string.folder)
    private val stringCard                  get() = activity.getString(R.string.card)
    val linLayMenuMoveItem          get() = libraryChildBinding.multiSelectMenuBinding.linLayMoveSelectedItems
    val btnCommitMove               get() = libraryChildBinding.confirmMoveToBinding.btnCommitMove
    val selectedItemAsString        get() = getListFirstItemAsString(moveToViewModel.returnMovingItems())
    val movableItemAsString         get() = getMovableItemTypeAsString(selectedItemAsString)
    val notMovableItemAsString      get() = getNotMovableItemTypeAsString(selectedItemAsString)

    fun makeBottomMenuVisible(){
        createFileViewModel.setBottomMenuVisible(true)
        actionsBeforeEndGuideList.add{
            createFileViewModel.setBottomMenuVisible(false)
        }
    }
    fun makeEditFileVisible(onClickFrameLay:FrameLayout) {
        onClickFrameLay.performClick()
        actionsBeforeEndGuideList.add{
            createFileViewModel.setEditFilePopUpVisible(false)
        }
    }
    fun goNextAfterNewFileCreated(next: () -> Unit){
        actionsBeforeEndGuideList.add{createFileViewModel.setDoAfterNewFileCreated{}}
        createFileViewModel.setDoAfterNewFileCreated {
            next()
            createFileViewModel.setDoAfterNewFileCreated {  }
        }
    }

    fun getCreatingMenuItemFrameLay():View{
        return when(moveToViewModel.getMovableFileStatus){
            FileStatus.FLASHCARD_COVER -> frameLayCreateFlashCard
            FileStatus.FOLDER   -> frameLayCreateFolder
            else -> imvBnvBtnAdd
        }
    }
    fun getListFirstItemAsString(list: List<Any>):String{
        val rvFiles = list.filterIsInstance<File>()
        val rvCards = list.filterIsInstance<Card>()
        return if(rvFiles.isNotEmpty())
            when(rvFiles[0].fileStatus){
                FileStatus.FLASHCARD_COVER -> stringFlashCard
                FileStatus.FOLDER           -> stringFlashCard
                else ->""
            }
        else if(rvCards.isEmpty().not()){
            stringCard
        } else ""
    }
    private fun getMovableItemTypeAsString(string: String):String{
        return when (string){
            stringFlashCard,stringFolder -> stringFolder
            stringCard  -> stringFlashCard
            else -> ""
        }
    }
    private fun getNotMovableItemTypeAsString(string: String):String{
        return when (string){
            stringFlashCard,stringFolder -> stringFlashCard
            stringCard  -> stringFolder
            else -> ""
        }
    }


    fun setCharacterPosInCenter  () {
        characterSizeDimenId = R.dimen.character_size_large
        characterBorderSet = BorderSet()
        characterOrientation = MyOrientationSet()
    }
    fun setCharacterTopLeftUnderRvFirstItem(){
        characterSizeDimenId = R.dimen.character_size_middle
        characterBorderSet = BorderSet(topSideSet = ViewAndSide(libRvFirstItem,MyOrientation.BOTTOM))
        characterOrientation = MyOrientationSet(MyVerticalOrientation.TOP,MyHorizontalOrientation.LEFT)
    }
    fun setCharacterBottomLeftAboveBnv(){
        characterSizeDimenId = R.dimen.character_size_middle
        characterBorderSet = BorderSet(bottomSideSet = ViewAndSide(frameLayBnv,MyOrientation.TOP))
        characterOrientation = MyOrientationSet(MyVerticalOrientation.BOTTOM,MyHorizontalOrientation.LEFT)
    }
    fun setCharacterBottomLeftAboveConfirmMovePopUp(){
        characterSizeDimenId = R.dimen.character_size_middle
        characterBorderSet = BorderSet(bottomSideSet = ViewAndSide(frameLayConfirmMove,MyOrientation.TOP))
        characterOrientation = MyOrientationSet(MyVerticalOrientation.BOTTOM,MyHorizontalOrientation.LEFT)
    }
    fun setSpbPosRightNextToCharacter(){
        textFit = true
        spbPosSimple = ViewAndSide(character,MyOrientation.RIGHT)
    }
    fun setSpbPosAboveCharacter(){
        textFit = false
        spbPosSimple = ViewAndSide(character,MyOrientation.TOP)
    }
    fun setSpbPosAboveCharacterUnderMenuFrameLay(){
        textFit = false
        spbBorderSet = BorderSet(bottomSideSet = ViewAndSide(character,MyOrientation.TOP),
            topSideSet = ViewAndSide(frameLayMultiMenu,MyOrientation.BOTTOM))
        spbOrientation = MyOrientationSet(MyVerticalOrientation.BOTTOM,MyHorizontalOrientation.MIDDLE)
    }
    fun goNextWhenLongClicked(next:()->Unit){
        actionsBeforeEndGuideList.add { libraryViewModel.setDoAfterLongClick(false) {} }
        libraryViewModel.setDoAfterLongClick(true){
            actionsBeforeEndGuideList.add { libraryViewModel.setMultipleSelectMode(false) }
            next()
        }
    }
    fun goNextWhenSwiped(next:()->Unit){
        actionsBeforeEndGuideList.add { libraryViewModel.setDoOnSwipeEnd(false) {} }
        libraryViewModel.setDoOnSwipeEnd(true)
        { actionsBeforeEndGuideList.add { libraryViewModel.makeAllUnSwiped() }
            next()}
    }
    fun goNextWhenDeletePopUpVisible(next: () -> Unit){
        actionsBeforeEndGuideList.add{deletePopUpViewModel.setDoOnPopUpVisibilityChanged(false){} }
        deletePopUpViewModel.setDoOnPopUpVisibilityChanged(true)
        {   actionsBeforeEndGuideList.add { deletePopUpViewModel.setConfirmDeleteVisible(false) }
            next()}
    }
    fun goNextWhenDeletePopUpWithChildrenVisible(next: () -> Unit){
        actionsBeforeEndGuideList.add{deletePopUpViewModel.setDoOnPopUpVisibilityChanged(false){} }
        deletePopUpViewModel.setDoOnPopUpVisibilityChanged(true)
        {   actionsBeforeEndGuideList.add { deletePopUpViewModel.setConfirmDeleteWithChildrenVisible(false) }
            next()}
    }
    fun makeOnlySwipeActive(){
        actionsBeforeEndGuideList.add{libraryViewModel.setOnlySwipeActive(false)}
        libraryViewModel.setOnlySwipeActive(true)
    }
    fun makeOnlyLongClickActive(){
        actionsBeforeEndGuideList.add{libraryViewModel.setOnlyLongClickActive(false)}
        libraryViewModel.setOnlyLongClickActive(true)
    }
    fun animateCharacterAndSpbPos(stringId:Int, characterPos:()->Unit,spbPos:()->Unit,doOnEnd:()->Unit){
        characterPos()
        doAfterCharacterPosChanged = {
            spbPos()
            getSpbPosAnim(getString(stringId)).start()
        }
        characterPosChangeAnimDoOnEnd ={doOnEnd()}
        getCharacterPosChangeAnim().start()


    }
    fun animateSpbNoChange(stringId: Int,doOnEnd: () -> Unit){
        spbPosAnimDoOnEnd = {doOnEnd()}
        getSpbPosAnim(getString(stringId)).start()
    }
    fun animateCharacterMiddleSpbTop(stringId: Int,doOnEnd: () -> Unit){
        animateCharacterAndSpbPos(stringId,
            {setCharacterPosInCenter()},
            {setSpbPosAboveCharacter()},
            {doOnEnd()})
    }
    fun animateCharacterRvBottomSpbRight(stringId: Int,doOnEnd: () -> Unit){
        animateCharacterAndSpbPos(stringId,
            {setCharacterTopLeftUnderRvFirstItem()},
            {setSpbPosRightNextToCharacter()},
            {doOnEnd()})
    }

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
        set(value) {
            field = value
            addSpbMargin()
        }
    var spbOrientation: MyOrientationSet = MyOrientationSet(verticalOrientation =  MyVerticalOrientation.BOTTOM)
    private var textFit:Boolean = false

    private val arrowMargin:MyMargin = MyMargin(10,10,10,10)


    fun makeHereTouchable(view: View?){
        callOnInstallBinding.apply {
            guideParentConLay.setOnClickListener(null)
            root.isClickable = false
            arrayOf(viewUnTouchableLeft,viewUnTouchableRight,viewUnTouchableBottom,viewUnTouchableTop).onEach {
                it.isClickable = true
            }
            val oriSet = MyOrientationSet(borderAttributes = BorderAttributes.FillBorder)
            if(view!=null){
                setPositionByMargin(ViewAndPositionData(viewUnTouchableLeft,
                    getSimplePosRelation(view,MyOrientation.LEFT,false), oriSet))
                setPositionByMargin(ViewAndPositionData(viewUnTouchableBottom,
                    getSimplePosRelation(view,MyOrientation.BOTTOM,false), oriSet))
                setPositionByMargin(ViewAndPositionData(viewUnTouchableRight,
                    getSimplePosRelation(view,MyOrientation.RIGHT,false), oriSet))
                setPositionByMargin(ViewAndPositionData(viewUnTouchableTop,
                    getSimplePosRelation(view,MyOrientation.TOP,false),oriSet))
            } else setPositionByMargin(ViewAndPositionData(viewUnTouchableLeft,
                BorderSet(),oriSet))

        }
    }

    private fun setCharacterPos(){
        changeViewVisibility(character,false)
        setCharacterSize()
        setPositionByMargin(ViewAndPositionData(character,characterBorderSet,characterOrientation))
    }
    var allConLayChildrenGoneAnimDoOnEnd:()-> Unit = {}

    fun getAllConLayChildrenGoneAnim():AnimatorSet{
        return AnimatorSet().apply {
            playTogether(getCharacterVisibilityAnim(false),getArrowVisibilityAnim(false),getSpbVisibilityAnim(false))
            doOnEnd{
                allConLayChildrenGoneAnimDoOnEnd()
                allConLayChildrenGoneAnimDoOnEnd = {}
            }
        }
    }
    var characterPosChangeAnimDoOnEnd:()->Unit = {}
    var doAfterCharacterPosChanged:()->Unit = {}
    fun getCharacterPosChangeAnim():AnimatorSet{
        return AnimatorSet().apply {
            characterVisibilityAnimDoOnEnd = {
                setCharacterPos()
                doAfterCharacterPosChanged()
                doAfterCharacterPosChanged = {}
            }
            playSequentially(getCharacterVisibilityAnim(false),getCharacterVisibilityAnim(true))
            doOnEnd{
                characterPosChangeAnimDoOnEnd()
                characterPosChangeAnimDoOnEnd = {}
            }
        }

    }
    fun onClickGoNext(func: () -> Unit){
        conLayGoNext.setOnClickListener{
            makeTouchAreaGone()
            func()
            it.setOnClickListener(null)
        }
    }
    fun goNextOnClickAnyWhere(func:()->Unit){
        callOnInstallBinding.root.setOnClickListener {
            makeTouchAreaGone()
            func()
        }
    }
    var spbVisibilityAnimDoOnEnd:()-> Unit = {}
    private fun getSpbVisibilityAnim(visible: Boolean):AnimatorSet{
        return AnimatorSet().apply {
            playTogether(getAppearAlphaAnimation(textView,visible),
                getAppearAlphaAnimation(bottom,visible))
            doOnEnd{
                spbVisibilityAnimDoOnEnd()
                spbVisibilityAnimDoOnEnd = {}
            }
        }
    }
    var characterVisibilityAnimDoOnEnd:()->Unit = {}
    var arrowVisibilityAnimDoOnEnd:()->Unit = {}
    private fun getCharacterVisibilityAnim(visible: Boolean):ValueAnimator{
        appearAlphaAnimDonOnEnd = {
            characterVisibilityAnimDoOnEnd()
            characterVisibilityAnimDoOnEnd = {}
        }
        return getAppearAlphaAnimation(character,visible)
    }
    fun getArrowVisibilityAnim(visible: Boolean):ValueAnimator{
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
            val size = getPixelSize(characterSizeDimenId)
            layoutParams.width = size
            layoutParams.height = size
        }
    }
    fun callOnFirst(guide: Guides){
        makeHereTouchable(null)
        changeMulVisibility(arrayOf(character,arrow,textView,bottom),false)
        holeView.initActivity(activity)
        viewUnderSpotInGuide = null
        setCharacterPos()
        spbPosSimple = ViewAndSide(character,MyOrientation.TOP)
        libraryViewModel.setMultipleSelectMode(false)
        libraryViewModel.makeAllUnSwiped()

        when(guide){
            Guides.CreateItems  ->CreateGuide(this).guide1()
            Guides.MoveItems    ->MoveGuide(this).guide1()
            Guides.EditItems    ->EditGuide(this).greeting1()
            Guides.DeleteItems  ->DeleteGuide(this).guide1()
        }
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
        getArrowVisibilityAnim(true).start()
    }
    var spbPosAnimDoOnEnd :()->Unit = {}

    fun setSpbPos(){
        ViewChangeActions().setSize(textView,LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT)
        val posData = ViewAndPositionData(
            textView,
            borderSet = spbBorderSet ,
            orientation= spbOrientation)
        setPositionByMargin(posData)
    }
    fun getSpbPosAnim(string: String):AnimatorSet{
        return AnimatorSet().apply {
            spbVisibilityAnimDoOnEnd = {
                changeMulVisibility(arrayOf(bottom,textView),false)
                textView.text = string
                setSpbPos() }
            playSequentially(getSpbVisibilityAnim(false),speakBubbleTextAnimation())
            doOnEnd {
                spbPosAnimDoOnEnd()
                spbPosAnimDoOnEnd = {}
            }
        }

    }
    fun removeHole(){
        holeView.removeGlobalLayout()
        holeView.noHole = true
    }

    fun goNextOnClickTouchArea(view: View, func: () -> Unit) {
//        guideParentConLay.setOnClickListener(null)
        addViewToConLay(view).setOnClickListener {
            makeTouchAreaGone()
            func()
        }
    }
    fun addViewToConLay(view:View):View{
//        guideParentConLay.setOnClickListener(null)
        val a = TouchAreaBinding.inflate(activity.layoutInflater)
        a.touchView.tag = 1

        val id = View.generateViewId()
        a.touchView.id = id
        ViewChangeActions().addViewToConLay(a.touchView,guideParentConLay)
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
    fun getString(id:Int):String{
        return activity.resources.getString(id)
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
    private fun addSpbMargin(){
        val spbMargin = getPixelSize(R.dimen.whole_spb_margin)
        spbBorderSet.margin = MyMargin(
            bottomMargin = getPixelSize(R.dimen.spb_bottom_rec_marginTop)+getPixelSize(R.dimen.spb_bottom_rec_height)+spbMargin,
            topMargin = spbMargin,
            leftMargin = spbMargin,
            rightMargin = spbMargin)
    }


    val actionsBeforeEndGuideList: MutableList<()->Unit> get() = activity.mainActivityViewModel.guideViewModel.actionsBeforeEndGuideList


}
