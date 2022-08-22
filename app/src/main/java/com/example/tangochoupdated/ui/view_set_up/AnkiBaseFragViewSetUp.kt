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
import androidx.core.content.ContextCompat
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
import com.example.tangochoupdated.db.enumclass.AnkiFilter
import com.example.tangochoupdated.ui.fragment.anki_frag_con.AnkiFragmentAnkiBox
import com.example.tangochoupdated.ui.listadapter.AnkiBoxListAdapter
import com.example.tangochoupdated.ui.listener.AnkiBoxFragBaseCL
import com.example.tangochoupdated.ui.listener.menuBar.AnkiBoxTabChangeCL
import com.example.tangochoupdated.ui.listener.popUp.AnkiFragAnkiSettingPopUpCL
import com.example.tangochoupdated.ui.listener.recyclerview.AnkiBoxFileRVCL
import com.example.tangochoupdated.ui.viewmodel.AnkiBoxFragViewModel
import com.example.tangochoupdated.ui.viewmodel.AnkiFragBaseViewModel
import com.example.tangochoupdated.ui.viewmodel.AnkiSettingPopUpViewModel
import kotlin.math.floor

class AnkiBaseFragViewSetUp(val baseViewModel: AnkiFragBaseViewModel,
                            val bindingBase:AnkiFragBaseBinding,
                            val context: Context,
                            val ankiSettingVM:AnkiSettingPopUpViewModel) {
    val white = ContextCompat.getColor(context,R.color.white)
    val green = ContextCompat.getColor(context,R.color.most_dark_green)

    fun addCL(){
        ankiSettingPopUpAddCL()
        bindingBase.viewAnkiSettingBG.setOnClickListener {
            baseViewModel.setSettingVisible(false)
        }
    }

    fun ankiSettingPopUpAddCL(){
        bindingBase.bindingSetting.apply {
            val binding = bindingBase.bindingSetting
            bindingSettingContent.apply {
                arrayOf(
                    lineLayParentOrder,
                    txvFilterSetting,imvFilterOptionVisibility,
                    txvFilterTypedAnswerCorrect,
                    txvFilterTypedAnswerMissed,
                    txvFilterCardRemembered,
                    txvFilterCardNotRemembered,
                    txvFilterWithFlag,
                    txvFilterWithoutFlag,
                    checkboxFilterFlag,
                    checkboxFilterTypedAnswer,
                    checkboxFilterCardRememberStatus,
                    checkboxAutoFlip,
                    edtAutoFlipSeconds,
                    checkboxReverseSides,
                    checkboxTypeAnswer,
                    txvOrderLibrary,
                    txvOrderMostMissed,
                    txvOrderRandom,
                    imvCloseSetting,
                    btnStartAnki,
                    ).onEach {
                        it.setOnClickListener(AnkiFragAnkiSettingPopUpCL(binding,ankiSettingVM,baseViewModel,context))
                }
            }
        }


    }
    fun setUpSettingContent(ankiFilter:AnkiFilter){
        bindingBase.bindingSetting.bindingSettingContent.apply {
            linLayFilterTypedAnswer.apply {
                alpha = if(!ankiFilter.answerTypedFilterActive) 0.5f else 1f
                isClickable = ankiFilter.answerTypedFilterActive
            }
            lineLayFilterRememberStatus.apply {
                alpha = if(!ankiFilter.rememberedFilterActive) 0.5f else 1f
                isClickable = ankiFilter.rememberedFilterActive
            }
            lineLayFilterFlag.apply {
                alpha = if(!ankiFilter.flagFilterActive) 0.5f else 1f
                isClickable = ankiFilter.flagFilterActive
            }
            checkboxFilterTypedAnswer.isSelected = ankiFilter.answerTypedFilterActive
            checkboxFilterFlag.isSelected = ankiFilter.flagFilterActive
            checkboxFilterCardRememberStatus.isSelected = ankiFilter.rememberedFilterActive
            txvFilterCardRemembered.isSelected = ankiFilter.remembered
            txvFilterCardNotRemembered .isSelected = !(ankiFilter.remembered)
            txvFilterTypedAnswerCorrect.isSelected = ankiFilter.correctAnswerTyped
            txvFilterTypedAnswerMissed.isSelected = !ankiFilter.correctAnswerTyped
            txvFilterWithFlag.isSelected = ankiFilter.flag
            txvFilterWithoutFlag.isSelected = !ankiFilter.flag
            arrayOf(txvFilterCardRemembered,
                txvFilterCardNotRemembered,
                txvFilterTypedAnswerMissed,
                txvFilterTypedAnswerCorrect,
                txvFilterWithoutFlag,
                txvFilterWithFlag,).onEach { it.setTextColor(if(it.isSelected) white else green) }
        }

    }


}