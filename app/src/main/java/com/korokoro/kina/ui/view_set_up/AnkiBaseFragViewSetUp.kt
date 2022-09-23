package com.korokoro.kina.ui.view_set_up

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.korokoro.kina.R
import com.korokoro.kina.databinding.*
import com.korokoro.kina.ui.viewmodel.customClasses.AnkiFilter
import com.korokoro.kina.ui.listener.popUp.AnkiFragAnkiSettingPopUpCL
import com.korokoro.kina.ui.viewmodel.AnkiBaseViewModel
import com.korokoro.kina.ui.viewmodel.AnkiSettingPopUpViewModel

class AnkiBaseFragViewSetUp() {

    fun ankiBackGroundCoverAddCL(bindingBase: AnkiFragBaseBinding,
                                 baseViewModel:AnkiBaseViewModel){
        bindingBase.viewAnkiFragConCover.setOnClickListener {
            baseViewModel.setSettingVisible(false)
        }
    }

    fun ankiSettingPopUpAddCL(binding:AnkiHomeFragPopupAnkiSettingBaseBinding,
                              ankiSettingVM: AnkiSettingPopUpViewModel,
                              ankiFragBaseViewModel: AnkiBaseViewModel,
                              context: Context,
                              ankiNavCon: NavController){
        binding.apply {
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
                        it.setOnClickListener(AnkiFragAnkiSettingPopUpCL(
                            binding,
                            ankiSettingVM,
                            ankiFragBaseViewModel,
                            context,
                            ankiNavCon))
                }
            }
        }


    }

    fun setUpSettingContent(ankiFilter: AnkiFilter, bindingBase: AnkiFragBaseBinding, context: Context){
        val white = ContextCompat.getColor(context,R.color.white)
        val green = ContextCompat.getColor(context,R.color.most_dark_green)
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