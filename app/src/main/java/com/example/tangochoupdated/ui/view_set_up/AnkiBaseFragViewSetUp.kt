package com.example.tangochoupdated.ui.view_set_up

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.tangochoupdated.R
import com.example.tangochoupdated.databinding.*
import com.example.tangochoupdated.db.enumclass.AnkiFilter
import com.example.tangochoupdated.ui.listener.popUp.AnkiFragAnkiSettingPopUpCL
import com.example.tangochoupdated.ui.viewmodel.AnkiFragBaseViewModel
import com.example.tangochoupdated.ui.viewmodel.AnkiSettingPopUpViewModel

class AnkiBaseFragViewSetUp(val baseViewModel: AnkiFragBaseViewModel,
                            val bindingBase:AnkiFragBaseBinding,
                            val context: Context,
                            val ankiSettingVM:AnkiSettingPopUpViewModel,
                            val ankiNavCon:NavController) {
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
                        it.setOnClickListener(AnkiFragAnkiSettingPopUpCL(binding,ankiSettingVM,baseViewModel,context,ankiNavCon))
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