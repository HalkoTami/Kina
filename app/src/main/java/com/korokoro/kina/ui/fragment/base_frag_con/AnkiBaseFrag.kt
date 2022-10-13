package com.korokoro.kina.ui.fragment.base_frag_con

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import com.korokoro.kina.R
import com.korokoro.kina.actions.changeViewVisibility
import com.korokoro.kina.databinding.AnkiFragBaseBinding
import com.korokoro.kina.ui.fragment.anki_frag_con.AnkiFlipBaseFragDirections
import com.korokoro.kina.ui.customClasses.MainFragment
import com.korokoro.kina.ui.viewmodel.*
import com.korokoro.kina.ui.customClasses.AnkiFilter
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
                            frameLayAnkiSetting,
                            txvFilterSetting,imvFilterOptionVisibility,
                            txvFilterTypedAnswerCorrect,
                            txvFilterTypedAnswerMissed,
                            txvFilterCardRemembered,
                            txvFilterCardNotRemembered,
                            checkboxFilterTypedAnswer,
                            checkboxFilterCardRememberStatus,
                            checkboxAutoFlip,
                            edtAutoFlipSeconds,
                            checkboxReverseSides,
                            checkboxTypeAnswer,
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
                checkboxFilterTypedAnswer.isSelected = ankiFilter.answerTypedFilterActive
                checkboxFilterCardRememberStatus.isSelected = ankiFilter.rememberedFilterActive
                txvFilterCardRemembered.isSelected = ankiFilter.remembered
                txvFilterCardNotRemembered .isSelected = !(ankiFilter.remembered)
                txvFilterTypedAnswerCorrect.isSelected = ankiFilter.correctAnswerTyped
                txvFilterTypedAnswerMissed.isSelected = !ankiFilter.correctAnswerTyped
                arrayOf(txvFilterCardRemembered,
                    txvFilterCardNotRemembered,
                    txvFilterTypedAnswerMissed,
                    txvFilterTypedAnswerCorrect,
                ).onEach { it.setTextColor(if(it.isSelected) white else green) }
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
        val  scrollBinding = binding.bindingSetting.bindingSettingContent
        fun changeFilterOptionVisibility(){
            scrollBinding.apply {
                changeSelectedState(imvFilterOptionVisibility)
                conLayFilterSetting.children.iterator().forEach {
                    if(it!=lineLayTitle) changeViewVisibility(it,imvFilterOptionVisibility.isSelected)
                }
            }
        }
        fun onClickAutoFlipCheckBox(){
            scrollBinding.apply {
                changeSelectedStateAndAlpha(checkboxAutoFlip,lineLayAutoFlipDuration)
                edtAutoFlipSeconds.isEnabled = checkboxAutoFlip.isSelected
            }
        }
        fun onClickReverseSides(){
            scrollBinding.apply {
                changeSelectedState(checkboxReverseSides)
                ankiSettingPopUpViewModel.setReverseCardSide(checkboxReverseSides.isSelected)
            }
        }
        fun onClickTypeAnswer(){
            scrollBinding.apply {
                changeSelectedState(checkboxTypeAnswer)
                ankiSettingPopUpViewModel.setTypeAnswer(checkboxTypeAnswer.isSelected)
            }
        }
        fun saveAutoFlipSec(){
            scrollBinding.apply {
                val secondsText = edtAutoFlipSeconds.text.toString()
                val autoSetting = if(secondsText=="") AutoFlip()
                else AutoFlip(checkboxAutoFlip.isSelected,edtAutoFlipSeconds.text.toString().toInt())
                ankiSettingPopUpViewModel.setAutoFlip(autoSetting)
            }
        }
        fun onClickStartAnki(){
            saveAutoFlipSec()
            ankiBaseViewModel.setSettingVisible(false)
            ankiBaseViewModel.returnAnkiBaseNavCon()?.navigate(AnkiFlipBaseFragDirections.toFlipFrag())
        }
        fun onClickTxvFilterTypedAnswerCorrect(){
            val ankiFilter = ankiSettingPopUpViewModel.returnAnkiFilter()
            scrollBinding.apply {
                val changed =onCLickEnumTxv(
                    checkAbility =checkboxFilterTypedAnswer,
                    txv= txvFilterTypedAnswerCorrect,
                    opposite =txvFilterTypedAnswerMissed, )
                if(changed){
                    ankiFilter.correctAnswerTyped = true
                    ankiSettingPopUpViewModel.setAnkiFilter(ankiFilter)
                }
            }
        }
        fun onClickTxvFilterTypedAnswerMissed(){
            val ankiFilter = ankiSettingPopUpViewModel.returnAnkiFilter()
            scrollBinding.apply {
                val changed = onCLickEnumTxv(
                    checkAbility =checkboxFilterTypedAnswer,
                    txv= txvFilterTypedAnswerMissed,
                    opposite =txvFilterTypedAnswerCorrect, )
                if(changed){
                    ankiFilter.correctAnswerTyped = false
                    ankiSettingPopUpViewModel.setAnkiFilter(ankiFilter)
                }
            }
        }
        fun onClickTxvFilterCardRemembered(){
            val ankiFilter = ankiSettingPopUpViewModel.returnAnkiFilter()
            scrollBinding.apply {
                val changed = onCLickEnumTxv(
                    checkAbility =  checkboxFilterCardRememberStatus ,
                    txv= txvFilterCardRemembered,
                    opposite =  txvFilterCardNotRemembered)
                if(changed){
                    ankiFilter.remembered = true
                    ankiSettingPopUpViewModel.setAnkiFilter(ankiFilter)
                }
            }
        }
        fun onClickTxvFilterCardNotRemembered(){
            val ankiFilter = ankiSettingPopUpViewModel.returnAnkiFilter()
            scrollBinding.apply {
                val changed = onCLickEnumTxv(
                    checkAbility =checkboxFilterCardRememberStatus,
                    txv= txvFilterCardNotRemembered,
                    opposite =txvFilterCardRemembered, )
                if(changed){
                    ankiFilter.remembered = false
                    ankiSettingPopUpViewModel.setAnkiFilter(ankiFilter)
                }
            }
        }
        fun onClickFilterCheckBoxRemembered(){
            val ankiFilter = ankiSettingPopUpViewModel.returnAnkiFilter()
            scrollBinding.apply {
                changeSelectedStateAndAlpha(checkboxFilterCardRememberStatus,lineLayFilterRememberStatus)
                ankiFilter.rememberedFilterActive = checkboxFilterCardRememberStatus.isSelected
            }
            ankiSettingPopUpViewModel.setAnkiFilter(ankiFilter)
        }
        fun onClickCheckBoxTypedAnswer(){
            val ankiFilter = ankiSettingPopUpViewModel.returnAnkiFilter()
            scrollBinding.apply {
                changeSelectedStateAndAlpha(checkboxTypeAnswer,linLayFilterTypedAnswer)
                ankiFilter.rememberedFilterActive = checkboxTypeAnswer.isSelected
            }
            ankiSettingPopUpViewModel.setAnkiFilter(ankiFilter)
        }

        binding.apply {
            bindingSetting.apply {
                bindingSettingContent.apply {
                    ankiSettingPopUpViewModel.apply {
                        when(p0){
                            txvFilterSetting,imvFilterOptionVisibility  -> changeFilterOptionVisibility()
                            checkboxAutoFlip                            -> onClickAutoFlipCheckBox()
                            checkboxReverseSides                        ->  onClickReverseSides()
                            checkboxTypeAnswer                          -> onClickTypeAnswer()
                            imvCloseSetting                             ->  ankiBaseViewModel.setSettingVisible(false)
                            btnStartAnki                                -> onClickStartAnki()
                            txvFilterTypedAnswerCorrect                 -> onClickTxvFilterTypedAnswerCorrect()
                            txvFilterTypedAnswerMissed                  -> onClickTxvFilterTypedAnswerMissed()
                            txvFilterCardRemembered                     -> onClickTxvFilterCardRemembered()
                            txvFilterCardNotRemembered                  -> onClickTxvFilterCardNotRemembered()
                            checkboxFilterTypedAnswer                   -> onClickCheckBoxTypedAnswer()
                            checkboxFilterCardRememberStatus            -> onClickFilterCheckBoxRemembered()


                        }
                    }
                }


            }

        }
    }
}