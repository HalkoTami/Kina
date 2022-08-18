package com.example.tangochoupdated.ui.view_set_up

import android.app.ActionBar
import android.content.Context
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.ScaleDrawable
import android.graphics.drawable.ShapeDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.graphics.drawable.WrappedDrawable
import androidx.core.view.marginEnd
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tangochoupdated.R
import com.example.tangochoupdated.databinding.*
import com.example.tangochoupdated.db.dataclass.Card
import com.example.tangochoupdated.db.dataclass.File
import com.example.tangochoupdated.db.enumclass.AnkiBoxTab
import com.example.tangochoupdated.ui.fragment.anki_frag_con.AnkiFragmentAnkiBox
import com.example.tangochoupdated.ui.listadapter.AnkiBoxListAdapter
import com.example.tangochoupdated.ui.listener.AnkiBoxFragBaseCL
import com.example.tangochoupdated.ui.listener.menuBar.AnkiBoxTabChangeCL
import com.example.tangochoupdated.ui.listener.recyclerview.AnkiBoxFileRVCL
import com.example.tangochoupdated.ui.viewmodel.AnkiBoxFragViewModel
import kotlin.math.floor

class AnkiBoxFragViewSetUp(val ankiBoxVM:AnkiBoxFragViewModel,
                           val context: Context,
                           val bindingAnkiBoxFrag:AnkiHomeFragBaseBinding) {


    fun ankiBoxFragAddCL(){
        bindingAnkiBoxFrag.apply {
            arrayOf(tabFavouritesToAnkiBox,tabLibraryToAnkiBox,tabAllFlashCardCoverToAnkiBox).onEach {
                it.setOnClickListener(AnkiBoxTabChangeCL(bindingAnkiBoxFrag,ankiBoxVM))
            }
            btnStartAnki.setOnClickListener(AnkiBoxFragBaseCL(bindingAnkiBoxFrag,ankiBoxVM))
        }


    }

    val getDraw = GetCustomDrawables(context)

    fun dpToPx(dp: Int): Float {
        val metrics = context.resources.displayMetrics
        return dp * metrics.density
    }


    fun pxToDp(px: Int, context: Context): Float {
        val metrics = context.getResources().getDisplayMetrics()
        return px / metrics.density
    }



    fun setUpRVFileBinding(binding:AnkiHomeFragRvItemFileBinding,
                           file: File, tab: AnkiBoxTab ){
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





        }


    }
    fun setUpGraphBinding(binding: AnkiHomeFragDataGraphSmallBinding,file: File){
        binding.apply {
            val rememberedPercentage =
                if(file.descendantsData.descendantsCardsAmount!=0)
                    floor(file.rememberedCardAmount.toFloat()/file.descendantsData.descendantsCardsAmount.toFloat()*10.0)
                else 0.0
            txvRememberedPercentage.text =
                "${rememberedPercentage}" +"%"
            guideRemembered.setGuidelinePercent(rememberedPercentage.toFloat())
            if(graphRemembered.width<imvRememberedEndIcon.width){
                val a = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT)
                a.marginEnd = -imvRememberedEndIcon.width
                imvRememberedEndIcon.layoutParams = a
            }
        }
    }

    fun getRememberedPercentage(list: MutableList<Card>):Double{
        return if(list.size!=0) (list.filter { it.remembered == true }.size/list.size).toDouble()
        else 0.0

    }
    fun getFlippedPercentage(list: MutableList<Card>, timesFlipped:Int):Double{
        return if(list.size!=0) (list.filter { it.timesFlipped == timesFlipped }.size/list.size).toDouble()
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
    fun setUpAnkiBoxRing(list: MutableList<Card>){
        val rememberedPercentage = 0.50
//            getRememberedPercentage(list)
        val rememberedCardsSize = list.filter { it.remembered == true }.size
        bindingAnkiBoxFrag. ringBinding.apply {
            ringProgressBar.progress = rememberedPercentage.times(100).toInt()
            moveViewInCircle(rememberedPercentage,imvRememberedEndIcon,ringProgressBar.layoutParams.width)
            txvInsideRing.text = "$rememberedCardsSize/${list.size}"
        }



    }
    fun setUpFlipProgressBar(list: MutableList<Card>){
        val per1 = getFlippedPercentage(list,1)
        val per2 = getFlippedPercentage(list,2)
        val per3 = getFlippedPercentage(list,3)
        val per4 = getFlippedPercentage(list,4)
        fun getPercentage(per:Double):Int{
            return per.times(100).toInt()
        }

        bindingAnkiBoxFrag. flipGraphBinding.apply {
            progressBarFlipped1.progress =      getPercentage(per1)
            progressBarFlipped2.progress =      getPercentage(per2)
            progressBarFlipped3.progress =      getPercentage(per3)
            progressBarFlippedAbove4.progress = getPercentage(per4)
            txv1.translationX =  root.width*per1.toFloat()
            txv2.translationX = root.width*per2.toFloat()
            txv3.translationX = root.width*per3.toFloat()
            txv4Above.translationX = root.width*per4.toFloat()

        }

    }
}