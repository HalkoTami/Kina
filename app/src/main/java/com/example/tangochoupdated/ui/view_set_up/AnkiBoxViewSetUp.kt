package com.example.tangochoupdated.ui.view_set_up

import android.app.ActionBar
import android.content.Context
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.ScaleDrawable
import android.graphics.drawable.ShapeDrawable
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.graphics.drawable.WrappedDrawable
import androidx.core.view.marginEnd
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tangochoupdated.R
import com.example.tangochoupdated.databinding.AnkiHomeFragBaseBinding
import com.example.tangochoupdated.databinding.AnkiHomeFragRvItemFileBinding
import com.example.tangochoupdated.db.dataclass.File
import com.example.tangochoupdated.db.enumclass.AnkiBoxTab
import com.example.tangochoupdated.ui.fragment.anki_frag_con.AnkiFragmentAnkiBox
import com.example.tangochoupdated.ui.listadapter.AnkiBoxListAdapter
import com.example.tangochoupdated.ui.listener.recyclerview.AnkiBoxFileRVCL
import com.example.tangochoupdated.ui.viewmodel.AnkiBoxFragViewModel
import kotlin.math.floor

class AnkiBoxViewSetUp(val ankiBoxVM:AnkiBoxFragViewModel,
                       val context: Context,
                       val tab:AnkiBoxTab) {


    val getDraw = GetCustomDrawables(context)
    fun setUpAnkiBoxRVListAdapter(recyclerView: RecyclerView,): AnkiBoxListAdapter {
        val adapter = AnkiBoxListAdapter(this,context)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.isNestedScrollingEnabled = false
        return adapter
    }

    fun pxToDp(px: Int, context: Context): Float {
        val metrics = context.getResources().getDisplayMetrics()
        return px / metrics.density
    }
    fun setUpRVFileBinding(binding:AnkiHomeFragRvItemFileBinding,
                           file: File){
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

            val rememberedPercentage =
                if(file.descendantsData.descendantsCardsAmount!=0)
                    floor(file.rememberedCardAmount.toFloat()/file.descendantsData.descendantsCardsAmount.toFloat()*10.0)
            else 0.0
            ankiBoxGraphBinding.apply {
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


    }
}