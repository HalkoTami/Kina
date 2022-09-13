package com.example.tangochoupdated.ui.listener.popUp

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.tangochoupdated.R
import com.example.tangochoupdated.databinding.AnkiHomeFragPopupAnkiSettingBaseBinding
import com.example.tangochoupdated.ui.viewmodel.customClasses.AnkiFilter
import com.example.tangochoupdated.ui.viewmodel.customClasses.AnkiOrder
import com.example.tangochoupdated.ui.viewmodel.customClasses.AutoFlip
import com.example.tangochoupdated.ui.fragment.anki_frag_con.AnkiFragFlipBaseFragmentDirections
import com.example.tangochoupdated.ui.viewmodel.AnkiFragBaseViewModel
import com.example.tangochoupdated.ui.viewmodel.AnkiSettingPopUpViewModel

class AnkiFragAnkiSettingPopUpCL(val binding: AnkiHomeFragPopupAnkiSettingBaseBinding,
                                 val settingVM: AnkiSettingPopUpViewModel,
                                 val baseViewModel:AnkiFragBaseViewModel,
                                 val context: Context ,
                                 val navCon:NavController): View.OnClickListener{

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
//                                when(baseViewModel.returnActiveFragment()){
//                                    AnkiFragments.AnkiBox -> navCon.navigate(AnkiFragFlipBaseFragmentDirections.toFlipFrag())
//                                    AnkiFragments.Flip -> return
//                                }
                                baseViewModel.returnAnkiBoxNavCon()?.navigate(AnkiFragFlipBaseFragmentDirections.toFlipFrag())

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
                                                    checkAbility =checkboxFilterTypedAnswer,
                                                    txv= v,
                                                    opposite =txvFilterTypedAnswerCorrect, ))
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