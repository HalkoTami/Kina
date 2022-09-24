package com.korokoro.kina.ui.fragment.base_frag_con

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import com.korokoro.kina.R
import com.korokoro.kina.databinding.AnkiFragBaseBinding
import com.korokoro.kina.ui.fragment.anki_frag_con.AnkiFlipBaseFragDirections
import com.korokoro.kina.ui.customClasses.MainFragment
import com.korokoro.kina.ui.viewmodel.*
import com.korokoro.kina.ui.customClasses.AnkiFilter
import com.korokoro.kina.ui.customClasses.AnkiOrder
import com.korokoro.kina.ui.customClasses.AutoFlip


class AnkiBaseFrag  : Fragment(),View.OnClickListener {

    private var _binding: AnkiFragBaseBinding? = null
    private val mainViewModel: MainViewModel by activityViewModels()
    private val ankiSettingPopUpViewModel: AnkiSettingPopUpViewModel by activityViewModels()
    private val ankiBaseViewModel : AnkiBaseViewModel by activityViewModels()

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        fun ankiBackGroundCoverAddCL(){
            binding.viewAnkiFragConCover.setOnClickListener {
                ankiBaseViewModel.setSettingVisible(false)
            }
        }

        fun ankiSettingPopUpAddCL(){
            binding.apply {
                bindingSetting.apply {
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
                            it.setOnClickListener(
                                this@AnkiBaseFrag
                            )
                        }
                    }

                }
            }
        }

        fun setUpSettingContent(){
            val ankiFilter = ankiSettingPopUpViewModel.returnAnkiFilter()
            val white = ContextCompat.getColor(requireActivity(), R.color.white)
            val green = ContextCompat.getColor(requireActivity(), R.color.most_dark_green)
            binding.bindingSetting.bindingSettingContent.apply {
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
        _binding =  AnkiFragBaseBinding.inflate(inflater, container, false)
        val a = childFragmentManager.findFragmentById(binding.ankiFragContainerView.id) as NavHostFragment
        val myNavCon = a.navController

        val settingVisibilityObserver = Observer<Boolean>{ settingVisible ->
            if(settingVisible){
                setUpSettingContent()
            }
            arrayOf(binding.frameLayAnkiSetting,binding.viewAnkiFragConCover).onEach {
                it.visibility = if(settingVisible) View.VISIBLE else View.GONE
            }
        }
        mainViewModel.setChildFragmentStatus(MainFragment.Anki)


        ankiSettingPopUpAddCL()
        ankiBackGroundCoverAddCL()
        ankiSettingPopUpViewModel.start()
        ankiBaseViewModel. setAnkiBaseNavCon(myNavCon)
        ankiBaseViewModel.settingVisible.observe(viewLifecycleOwner,settingVisibilityObserver)




        val root: View = binding.root




        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(p0: View?) {
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
        fun changeTxvSelectedState(v: TextView, selected:Boolean){
            v.isSelected = selected
            v.setTextColor(ContextCompat.getColor(requireActivity(),
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

        val v = p0

        binding.apply {
            bindingSetting.apply {
                bindingSettingContent.apply {
                    ankiSettingPopUpViewModel.apply {
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
                            imvCloseSetting                             ->  ankiBaseViewModel.setSettingVisible(false)
                            btnStartAnki                                -> {
                                ankiBaseViewModel.setSettingVisible(false)
                                setAutoFlip(AutoFlip(checkboxAutoFlip.isSelected,edtAutoFlipSeconds.text.toString().toInt()))
                                ankiBaseViewModel.returnAnkiBaseNavCon()?.navigate(AnkiFlipBaseFragDirections.toFlipFrag())

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
}