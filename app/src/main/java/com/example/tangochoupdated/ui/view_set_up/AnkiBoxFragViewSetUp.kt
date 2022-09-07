package com.example.tangochoupdated.ui.view_set_up

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.compose.ui.geometry.Rect
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.tangochoupdated.databinding.*
import com.example.tangochoupdated.db.dataclass.Card
import com.example.tangochoupdated.db.dataclass.File
import com.example.tangochoupdated.db.enumclass.AnkiBoxFragments
import com.example.tangochoupdated.ui.listadapter.AnkiBoxListAdapter
import com.example.tangochoupdated.ui.listener.AnkiBoxFragBaseCL
import com.example.tangochoupdated.ui.listener.menuBar.AnkiBoxTabChangeCL
import com.example.tangochoupdated.ui.listener.recyclerview.AnkiBoxFileRVCL
import com.example.tangochoupdated.ui.viewmodel.AnkiBoxFragViewModel
import com.example.tangochoupdated.ui.viewmodel.AnkiFragBaseViewModel
import com.example.tangochoupdated.ui.viewmodel.AnkiSettingPopUpViewModel
import kotlin.math.floor


class AnkiBoxFragViewSetUp() {
//    val ankiBoxVM:AnkiBoxFragViewModel,
//    val context: Context,
//    val bindingAnkiBoxFrag:AnkiHomeFragBaseBinding,
//    val ankiBaseViewModel:AnkiFragBaseViewModel
fun setUpAnkiBoxRVListAdapter(recyclerView: RecyclerView,
                              context: Context,
                              ankiBoxVM: AnkiBoxFragViewModel, tab: AnkiBoxFragments,
                              lifecycleOwner: LifecycleOwner): AnkiBoxListAdapter {
    val adapter = AnkiBoxListAdapter(context,ankiBoxVM,tab,lifecycleOwner)
    recyclerView.adapter = adapter
    recyclerView.layoutManager = LinearLayoutManager(context)
    recyclerView.isNestedScrollingEnabled = false
    return adapter
}

    fun ankiBoxFragAddCL(ankiSettingPopUpViewModel: AnkiSettingPopUpViewModel,
                         binding:AnkiHomeFragBaseBinding,
                         ankiBoxVM:AnkiBoxFragViewModel,
                         ankiBaseViewModel:AnkiFragBaseViewModel,
                         ankiBoxNavCon:NavController){
        binding.apply {
            arrayOf(tabFavouritesToAnkiBox,tabLibraryToAnkiBox,tabAllFlashCardCoverToAnkiBox).onEach {
                it.setOnClickListener(AnkiBoxTabChangeCL(binding,ankiBoxVM,))
            }
            btnStartAnki.setOnClickListener(AnkiBoxFragBaseCL(
                ankiSettingPopUpViewModel, binding,ankiBaseViewModel,ankiBoxVM))
        }


    }


    fun setUpRVFileBinding(binding:AnkiHomeFragRvItemFileBinding,
                           file: File, tab: AnkiBoxFragments,
                           ankiBoxVM: AnkiBoxFragViewModel,
                           context:Context,
                           lifecycleOwner: LifecycleOwner
){
        val getDraw = GetCustomDrawables(context)
        binding.apply {
            imvFileType.setImageDrawable(
                getDraw.getFileIconByFile(file)
            )
            txvFileTitle.text = file.title
            txvAnkiBoxCardAmount.text = file.descendantsData.descendantsCardsAmount.toString()
            txvAnkiBoxFlashCardAmount.text = file.descendantsData.descendantsFlashCardsCoversAmount.toString()
            txvAnkiBoxFolderAmount.text = file.descendantsData.descendantsFoldersAmount.toString()
            arrayOf(root,checkboxAnkiRv).onEach { it.setOnClickListener(AnkiBoxFileRVCL(
                file,
                ankiBoxVM = ankiBoxVM,
                binding = binding,
                tab = tab)) }
            ankiBoxVM.getAnkiBoxRVCards(file.fileId).observe(lifecycleOwner){
                val a = mutableListOf<Card>()
                a.addAll(it)
                setUpFlipProgressBar(a,binding.flippedProgressBarBinding)
                rememberedProgressBarBinding.apply {
                    val remPer = it.filter { it.remembered }.size.toDouble()/it.size
                    progressbarRemembered.progress = getPercentage(remPer)
                    val translation = progressbarRemembered.width*remPer.toFloat()-imvRememberedEndIcon.width/2
                    imvRememberedEndIcon.translationX = if(translation<0) 0f else translation
                }
            }
        }


    }

    fun getRememberedPercentage(list: MutableList<Card>):Double{
        return if(list.size!=0) (list.filter { it.remembered == true }.size.toDouble()/list.size)
        else 0.0

    }
    fun getFlippedPercentage(list: MutableList<Card>, timesFlipped:Int):Double{
        return if(list.size!=0) (list.filter { it.timesFlipped == timesFlipped }.size.toDouble()/list.size)
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
    fun setUpAnkiBoxRing(list: MutableList<Card>,binding: ProgressBarRememberedRingBinding){
        val rememberedPercentage = getRememberedPercentage(list)
        val rememberedCardsSize = list.filter { it.remembered == true }.size
        binding.apply {
            ringProgressBar.progress = rememberedPercentage.times(100).toInt()
            moveViewInCircle(rememberedPercentage,imvRememberedEndIcon,ringProgressBar.layoutParams.width)
            txvInsideRing.text = "$rememberedCardsSize/${list.size}"
        }

    }
    fun getPercentage(per:Double):Int{
        return per.times(100).toInt()
    }

    fun setUpPercentageIcons(flippedBinding: ProgressBarFlippedBinding,
                             percentageBinding:PercentageIconsBinding){

        fun getWidth(percentage: Int):Float{
            return (percentageBinding.root.width*percentage/100).toFloat()
        }
        val progress1 = flippedBinding.progressBarFlipped1.progress
        val progress2 = flippedBinding.progressBarFlipped2.progress
        val progress3 = flippedBinding.progressBarFlipped3.progress
        val progress4above = flippedBinding.progressBarFlippedAbove4.progress
        percentageBinding.apply {
            oncePercentage.translationX       =        getWidth(progress1)
            twicePercentage.translationX      =        getWidth(progress2)
            threePercentage.translationX      =        getWidth(progress3)
            fourAbovePercentage.translationX  =        getWidth(progress4above)
            txvOncePercentage.text = progress1.toString()
            txvTwicePercentage.text = progress2.toString()
            txvThreePercentage.text = progress3.toString()
            txvFourAbovePercentage.text = progress4above.toString()
            arrayOf(oncePercentage,twicePercentage,threePercentage,fourAbovePercentage).onEach {
                it.visibility = if(it.translationX == 0f )  View.INVISIBLE else View.VISIBLE
                if(it.translationX + it.width > this.root.width) it.translationX = this.root.width.toFloat()
            }
        }


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
    fun setUpFlipProgressBar(list: MutableList<Card>,binding: ProgressBarFlippedBinding){
        val per1 = getFlippedPercentage(list,1)
        val per2 = getFlippedPercentage(list,2)
        val per3 = getFlippedPercentage(list,3)
        val per4 = getFlippedPercentage(list,4)


        binding. apply {

            progressBarFlipped1.progress =      getPercentage(per1)
            progressBarFlipped2.progress =      getPercentage(per2)
            progressBarFlipped3.progress =      getPercentage(per3)
            progressBarFlippedAbove4.progress = getPercentage(per4)
            arrayOf(txv1,txv2,txv3,txv4Above).onEach {
                it.visibility = View.VISIBLE
            }
            txv1.translationX =  root.width*per1.toFloat()
            txv2.translationX = root.width*per2.toFloat()
            txv3.translationX = root.width*per3.toFloat()
            txv4Above.translationX = root.width*per4.toFloat()
            arrayOf(txv1,txv2,txv3,txv4Above).onEach {
                if(it.translationX == 0f) it.visibility = View.GONE
            }
            arrayOf(txv2,txv3,txv4Above).onEach {
                if(checkCovered(txv1,it)) txv1.visibility = View.GONE
            }
            arrayOf(txv3,txv4Above).onEach {
                if(checkCovered(txv2,it)) txv2.visibility = View.GONE
            }
            if(checkCovered(txv3,txv4Above)) txv3.visibility = View.GONE

        }


    }
}