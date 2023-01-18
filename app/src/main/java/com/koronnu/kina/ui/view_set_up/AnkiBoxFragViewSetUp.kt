package com.koronnu.kina.ui.view_set_up

import android.animation.ValueAnimator
import android.content.Context
import android.view.View
import android.widget.ImageView
import androidx.core.animation.doOnEnd
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.koronnu.kina.R

import com.koronnu.kina.databinding.*
import com.koronnu.kina.db.dataclass.ActivityData
import com.koronnu.kina.db.dataclass.Card
import com.koronnu.kina.db.dataclass.File
import com.koronnu.kina.db.enumclass.ActivityStatus
import com.koronnu.kina.customClasses.enumClasses.AnkiBoxFragments
import com.koronnu.kina.customClasses.normalClasses.ParentFileAncestors
import com.koronnu.kina.ui.tabAnki.ankiBox.AnkiBoxListAdapter
import com.koronnu.kina.ui.tabAnki.ankiBox.AnkiBoxFileRVCL
import com.koronnu.kina.ui.tabAnki.ankiBox.AnkiBoxRVStringCardCL
import com.koronnu.kina.ui.viewmodel.AnkiBoxViewModel
import kotlin.math.abs


class AnkiBoxFragViewSetUp() {



    fun setUpRVCard(cardBinding: ListItemAnkiBoxRvCardBinding,card: Card,lifecycleOwner: LifecycleOwner,ankiBoxVM: AnkiBoxViewModel){
        val resources = cardBinding.root.context.resources
        LibrarySetUpItems().setUpRVStringCardBinding(cardBinding.bindingListItemCardString,card.stringData)

        fun setOnCL(){
            arrayOf(cardBinding.imvChbIsInAnkiBox,).onEach { it.setOnClickListener(
                AnkiBoxRVStringCardCL(card,cardBinding,ankiBoxVM)) }
        }
        fun getStringByRemembered(remembered:Boolean):String{
            return resources.getString(if(remembered) R.string.remembered else R.string.not_remembered)
        }
        fun getStringByLastLooked(lastLooked:ActivityData?,):String{

            return resources.getString(R.string.lastLookedDate,lastLooked?.dateTime ?:resources.getString(R.string.no_data))
        }
        setOnCL()
        ankiBoxVM.ankiBoxItems.observe(lifecycleOwner){
            cardBinding.imvChbIsInAnkiBox.isSelected = it.contains(card)
        }
        ankiBoxVM.getCardActivityFromDB(card.id).observe(lifecycleOwner){
            val lastLooked =it.findLast { it.activityStatus == ActivityStatus.CARD_OPENED }
            cardBinding.txvAnkiRvLastFlipped.text = getStringByLastLooked(lastLooked)
        }
        cardBinding.apply {
            imvAnkiRvCardRemembered.isSelected = card.remembered
            txvAnkiRvRememberedStatus.text = getStringByRemembered(card.remembered)
        }



    }
fun setUpAnkiBoxRVListAdapter(recyclerView: RecyclerView,
                              context: Context,
                              ankiBoxVM: AnkiBoxViewModel, tab: AnkiBoxFragments?,
                              lifecycleOwner: LifecycleOwner): AnkiBoxListAdapter {
    val adapter = AnkiBoxListAdapter(context,ankiBoxVM,tab,lifecycleOwner)
    recyclerView.adapter = adapter
    recyclerView.layoutManager = LinearLayoutManager(context)
    recyclerView.isNestedScrollingEnabled = false
    return adapter
}




    fun setUpRVFileBinding(binding:ListItemAnkiBoxRvFileBinding,
                           file: File, tab: AnkiBoxFragments,
                           ankiBoxVM: AnkiBoxViewModel,
                           context:Context,
                           lifecycleOwner: LifecycleOwner
){
        fun getAnimation(list:List<Card>):ValueAnimator{
            val remPer = list.filter { it.remembered }.size.toDouble()/list.size
            val rememberedProgressBar = binding.rememberedProgressBarBinding.progressbarRemembered
            val progressBefore =rememberedProgressBar.progress
            val imvEndIcon = binding.rememberedProgressBarBinding.imvRememberedEndIcon
            val a = ValueAnimator.ofFloat(progressBefore.toFloat(),getPercentage(remPer).toFloat())
            a.addUpdateListener {
                val value = it.animatedValue as Float
                rememberedProgressBar.progress = value.toInt()
                val translation = rememberedProgressBar.width*value/100-imvEndIcon.width/2
                imvEndIcon.translationX = if(translation<0) 0f else translation
            }
            a.duration = abs(progressBefore-getPercentage(remPer)).times(30).toLong()
            return a
        }
        val getDraw = GetCustomDrawables(context)
        val descendantsCardsObserver = Observer<List<Card>>{ list->
            binding.apply {
                arrayOf(txvAnkiBoxCardAmount,imvContainingCardsIcon
                ).onEach { it.visibility = if(list.isEmpty()) View.INVISIBLE else View.VISIBLE }
                binding.txvAnkiBoxCardAmount.text = list.size.toString()
            }
            getAnimation(list).start()

        }
        val descendantsFoldersObserver = Observer<List<File>>{ list->
            binding.apply {
                arrayOf(txvAnkiBoxFolderAmount,imvContainingFoldersIcon
                ).onEach { it.visibility = if(list.isEmpty()) View.INVISIBLE else View.VISIBLE }
                binding.txvAnkiBoxFolderAmount.text = list.size.toString()
            }
        }
        val descendantsFlashCardsObserver = Observer<List<File>>{ list->
            binding.apply {
                arrayOf(txvAnkiBoxFlashCardAmount,imvContainingFlashcardsIcon
                ).onEach { it.visibility = if(list.isEmpty()) View.INVISIBLE else View.VISIBLE }
                binding.txvAnkiBoxFlashCardAmount.text = list.size.toString()
            }
        }
        val ancestorsObserver = Observer<List<File>>{ ancestors ->
            val a = ParentFileAncestors(
                gGrandPFile  = if(ancestors.size>=3) ancestors[2] else null,
                gParentFile = if(ancestors.size>=2) ancestors[1] else null,
                ParentFile =  null
            )
            binding.ancestorsBinding.apply {
                CommonViewSetUp().apply {
                    setUpEachAncestor(lineLayGGFile,txvGGrandParentFileTitle,imvGGrandParentFile,a.gGrandPFile)
                    setUpEachAncestor(lineLayGPFile,txvGrandParentFileTitle,imvGrandParentFile,a.gParentFile)
                    setUpEachAncestor(lineLayParentFile,txvParentFileTitle,imvParentFile,a.ParentFile)
                    binding.frameAnkiBoxRvAncestors.visibility =
                        if(ancestors.size == 1) View.GONE
                    else View.VISIBLE
                }

            }
        }

            binding.apply {
                ankiBoxVM.ankiBoxFileIds.observe(lifecycleOwner){
                    imvChbIsInAnkiBox.isSelected = (it.contains(file.fileId))
                }
                imvFileType.setImageDrawable(
                    getDraw.getFileIconByFile(file)
                )
                txvFileTitle.text = file.title

                arrayOf(root,imvChbIsInAnkiBox).onEach { it.setOnClickListener(AnkiBoxFileRVCL(
                    file,
                    ankiBoxVM = ankiBoxVM,
                    binding = binding,
                    tab = tab)) }
                ankiBoxVM.ankiBoxFileAncestorsFromDB(file.fileId)       .observe(lifecycleOwner,ancestorsObserver)
                ankiBoxVM.getAnkiBoxRVCards(file.fileId)                .observe(lifecycleOwner,descendantsCardsObserver)
                ankiBoxVM.getAnkiBoxRVDescendantsFolders(file.fileId)   .observe(lifecycleOwner,descendantsFoldersObserver)
                ankiBoxVM.getAnkiBoxRVDescendantsFlashCards(file.fileId)   .observe(lifecycleOwner,descendantsFlashCardsObserver)

            }
        }



    fun getRememberedPercentage(list: MutableList<Card>):Double{
        return if(list.size!=0) (list.filter { it.remembered == true }.size.toDouble()/list.size)
        else 0.0

    }
    fun getFlippedPercentage(list: MutableList<Card>, timesFlipped:Int):Double{
        return if(list.size!=0) (list.filter { it.timesFlipped >= timesFlipped }.size.toDouble()/list.size)
        else 0.0

    }
    fun moveViewInCircle(percentage:Double,
                         view:ImageView,
                         circleWidth: Int){
        val radious = Math.toRadians(360*percentage-90.0)
        val dx: Float = (circleWidth-view.layoutParams.width)/2 * Math.cos(radious).toFloat()
        val dy: Float = (circleWidth-view.layoutParams.height)/2 * Math.sin(radious).toFloat()
        view.translationX = dx
        view.translationY =dy
    }
    fun setUpAnkiBoxRing(list: MutableList<Card>,binding: PgbAnkiBoxDataRememberedBinding){
        val resources = binding.root.resources
        val rememberedPercentage = getRememberedPercentage(list)
        val rememberedCardsSize = list.filter { it.remembered == true }.size
        binding.apply {
            binding.imvRememberedEndIcon.visibility = View.VISIBLE
            txvInsideRing.text = resources.getString(R.string.remembered_progress,rememberedCardsSize,list.size)

        }
        val progressRingWidth = binding.ringProgressBar.layoutParams.width
        val progressBefore = binding.ringProgressBar.progress
        val beforePercentage = binding.ringProgressBar.progress.toFloat()/100
        val newProgress = rememberedPercentage.times(100).toInt()
        if(progressBefore==newProgress) {
            binding.ringProgressBar.progress = newProgress
            binding.imvRememberedEndIcon.visibility = if(newProgress!=0)  View.VISIBLE else View.INVISIBLE
            moveViewInCircle(rememberedPercentage,binding.imvRememberedEndIcon,progressRingWidth)
            return
        }
        fun getAnimation(percentageBefore:Float,newPercentage:Float):ValueAnimator{
            val percentageAnim = ValueAnimator.ofFloat(percentageBefore,newPercentage)
            percentageAnim.doOnEnd {
                binding.imvRememberedEndIcon.visibility = if(rememberedPercentage!=0.0)  View.VISIBLE else View.INVISIBLE
            }
            percentageAnim.addUpdateListener {
                val value = it.animatedValue as Float
                binding.ringProgressBar.progress = value.times(100).toInt()
                moveViewInCircle(value.toDouble(),binding.imvRememberedEndIcon,progressRingWidth)
            }
            percentageAnim.duration=abs(newProgress-progressBefore).times(30).toLong()
            return percentageAnim
        }
        getAnimation(beforePercentage,rememberedPercentage.toFloat()).start()

    }
    fun getPercentage(per:Double):Int{
        return per.times(100).toInt()
    }

    fun checkCovered(view1:View,view2: View):Boolean{
        val location = IntArray(2)
        view1.getLocationOnScreen(location)
        val view1Right = location[0]
        val view1Left = location[0]+ view1.layoutParams.width
        val location2 = IntArray(2)
        view2.getLocationOnScreen(location2)
        val view2Right = location2[0]
        val view2Left = location2[0]+ view2.layoutParams.width
        return ((view1Right in view2Left..view2Right)
                ||(view1Left in view2Left..view2Right))
                ||(view1Left<=view2Left&& view1Right >= view2Right)
    }
}