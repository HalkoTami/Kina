package com.korokoro.kina.ui.listener.popUp

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.korokoro.kina.R
import com.korokoro.kina.databinding.AnkiHomeFragPopupAnkiSettingBaseBinding
import com.korokoro.kina.ui.fragment.anki_frag_con.AnkiFlipBaseFragDirections
import com.korokoro.kina.ui.customClasses.AnkiFilter
import com.korokoro.kina.ui.customClasses.AnkiOrder
import com.korokoro.kina.ui.customClasses.AutoFlip
import com.korokoro.kina.ui.viewmodel.AnkiBaseViewModel
import com.korokoro.kina.ui.viewmodel.AnkiSettingPopUpViewModel

class AnkiFragAnkiSettingPopUpCL(private val binding: AnkiHomeFragPopupAnkiSettingBaseBinding,
                                 private val settingVM: AnkiSettingPopUpViewModel,
                                 private val baseViewModel:AnkiBaseViewModel,
                                 private val context: Context, ): View.OnClickListener{

    override fun onClick(v: View?) {
        fun changeSelectedStateAndVisibility(stateView:View,visibilityChangeView:View){
            stateView.isSelected = !stateView.isSelected
            visibilityChangeView.visibility = if(stateView.isSelected) View.VISIBLE else View.GONE
        }
        fun changeSelectedStateAndAlpha(stateView:View,alphaChangeView:View){
            stateView.isSelected = !stateView.isSelected
            alphaChangeView.alpha = if(stateView.isSelected) 1f else 0.5f
        }
        fun changeSelectedState(v: View){
            v.isSelected = v.isSelected.not() ?:false
        }
        fun changeTxvSelectedState(v:TextView,selected:Boolean){
            v.isSelected = selected
            v.setTextColor(ContextCompat.getColor(context,
            when(selected){
                true -> R.color.white
                false -> R.color.most_dark_green
            }
            ))
        }
        fun onCLickEnumTxv(checkAbility:View,txv:View,opposite:View):Boolean{
            return if(txv.isSelected||checkAbility.isSelected.not()) false else {
                changeTxvSelectedState(txv as TextView, true)
                changeTxvSelectedState(opposite as TextView,false)
                true
            }
        }


        binding.apply {
            bindingSettingContent.apply {
                settingVM.apply {
                        when(v){
                            txvFilterSetting,imvFilterOptionVisibility  -> changeSelectedStateAndVisibility(imvFilterOptionVisibility,conLayFilterSetting)
                            lineLayParentOrder                          -> changeSelectedStateAndVisibility(imvOrderOptionVisibility,lineLayOrderMenu)
                            checkboxAutoFlip                            -> {
                                val change = returnAutoFlip()
                                changeSelectedStateAndAlpha(v,lineLayAutoFlipDuration)
                                edtAutoFlipSeconds.isEnabled = v.isSelected
                                change?.active = v.isSelected
                                setAutoFlip(change?: AutoFlip())
                            }
                            checkboxReverseSides                        ->  {
                                changeSelectedState(v)
                                setReverseCardSide(v.isSelected)
                            }
                            checkboxTypeAnswer                          -> {
                                changeSelectedState(v)
                                setTypeAnswer(v.isSelected)
                            }
                            txvOrderLibrary                             -> setAnkiOrder(AnkiOrder.Library)
                            txvOrderMostMissed                          -> setAnkiOrder(AnkiOrder.MostMissed)
                            txvOrderRandom                              -> setAnkiOrder(AnkiOrder.Random)
                            imvCloseSetting                             ->  baseViewModel.setSettingVisible(false)
                            btnStartAnki                                -> {
                                baseViewModel.setSettingVisible(false)
                                baseViewModel.returnAnkiBaseNavCon()?.navigate(AnkiFlipBaseFragDirections.toFlipFrag())

                            }
                            else -> {
                                val filterChange = returnAnkiFilter() ?: AnkiFilter()
                                filterChange.apply {
                                    when(v){
                                        txvFilterTypedAnswerCorrect                 ->
                                            if(onCLickEnumTxv(
                                                    checkAbility =checkboxFilterTypedAnswer,
                                                    txv= v,
                                                    opposite =txvFilterTypedAnswerMissed, ))
                                                correctAnswerTyped = true
                                        txvFilterTypedAnswerMissed                  ->
                                            if(onCLickEnumTxv(
                                                    checkAbility =checkboxFilterTypedAnswer,
                                                    txv= v,
                                                    opposite =txvFilterTypedAnswerCorrect, ))
                                                correctAnswerTyped = false
                                        txvFilterCardRemembered                     ->
                                            if(onCLickEnumTxv(
                                                    checkAbility =  checkboxFilterCardRememberStatus ,
                                                    txv= v,
                                                    opposite =  txvFilterCardNotRemembered)) remembered = true
                                        txvFilterCardNotRemembered                  ->
                                            if(onCLickEnumTxv(
                                                    checkAbility =checkboxFilterCardRememberStatus,
                                                    txv= v,
                                                    opposite =txvFilterCardRemembered, ))
                                                remembered = false
                                        txvFilterWithFlag                           ->
                                            if(onCLickEnumTxv(checkAbility =checkboxFilterFlag,
                                                    txv= v,
                                                    opposite =txvFilterWithoutFlag )) flag = true
                                        txvFilterWithoutFlag                        ->
                                            if(onCLickEnumTxv(
                                                    checkAbility =checkboxFilterFlag,
                                                    txv= v,
                                                    opposite =txvFilterWithFlag, ))  flag = false
                                        checkboxFilterFlag                          -> {
                                            changeSelectedStateAndAlpha(v,lineLayFilterFlag)
                                            flagFilterActive = v.isSelected
                                        }
                                        checkboxFilterTypedAnswer                   -> {
                                            changeSelectedStateAndAlpha(v,linLayFilterTypedAnswer)
                                            answerTypedFilterActive = v.isSelected
                                        }
                                        checkboxFilterCardRememberStatus            -> {
                                            changeSelectedStateAndAlpha(v,lineLayFilterRememberStatus)
                                            rememberedFilterActive = v.isSelected
                                        }

                                    }
                                    setAnkiFilter(filterChange)
                                }



                            }

                    }
                }

            }

        }
    }
}