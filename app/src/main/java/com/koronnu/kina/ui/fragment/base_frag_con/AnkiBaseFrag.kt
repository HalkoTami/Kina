package com.koronnu.kina.ui.fragment.base_frag_con

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import com.koronnu.kina.R
import com.koronnu.kina.actions.changeViewVisibility
import com.koronnu.kina.databinding.AnkiFragBaseBinding
import com.koronnu.kina.ui.fragment.anki_frag_con.AnkiFlipBaseFragDirections
import com.koronnu.kina.customClasses.MainFragment
import com.koronnu.kina.ui.viewmodel.*
import com.koronnu.kina.customClasses.AnkiFilter
import com.koronnu.kina.customClasses.AnkiFragments
import com.koronnu.kina.customClasses.AutoFlip


class AnkiBaseFrag  : Fragment(),View.OnClickListener {

    private var _binding: AnkiFragBaseBinding? = null
    private val mainViewModel: MainViewModel by activityViewModels()
    private val ankiSettingPopUpViewModel: AnkiSettingPopUpViewModel by activityViewModels()
    private val ankiBaseViewModel : AnkiBaseViewModel by activityViewModels()
    private lateinit var ankiSettingSharedPref:SharedPreferences

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
                            linLayAnkiSettingAutoFlip,
                            linLayAnkiSettingFilterRemembered,
                            linLayAnkiSettingReverseSide,
                            linLayAnkiSettingTypeAnswer,
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

            binding.bindingSetting.bindingSettingContent.apply {
                val reverseSide =  ankiSettingSharedPref.getBoolean(
                    requireActivity().getString(R.string.s_p_anki_setting_reverse_side),false)
                val autoFLip = ankiSettingSharedPref.getBoolean(
                    requireActivity().getString(R.string.s_p_anki_setting_auto_flip_active),false)
                val typeAnswer = ankiSettingSharedPref.getBoolean(
                    requireActivity().getString(R.string.s_p_anki_setting_type_answer),false)
                val filterRemembered =  ankiSettingSharedPref.getBoolean(
                    requireActivity().getString(R.string.s_p_anki_setting_filter_remembered),false)
                val autoFlipSec = ankiSettingSharedPref.getInt(
                    requireActivity().getString(R.string.s_p_anki_setting_auto_flip_seconds),
                    AutoFlip().seconds)
                setSelectedStateAndAlpha(checkboxReverseSides,linLayAnkiSettingReverseSide,reverseSide)
                setSelectedStateAndAlpha(checkboxAutoFlip,linLayAnkiSettingAutoFlip,autoFLip)
                edtAutoFlipSeconds.isEnabled = autoFLip
                setSelectedStateAndAlpha(checkboxTypeAnswer,linLayAnkiSettingTypeAnswer,typeAnswer)
                setSelectedStateAndAlpha(checkboxFilterCardRememberStatus,linLayAnkiSettingFilterRemembered,filterRemembered)
                edtAutoFlipSeconds.text = SpannableStringBuilder(autoFlipSec.toString())
                ankiSettingPopUpViewModel.apply {
                    setReverseCardSide(reverseSide)
                    setTypeAnswer(typeAnswer)
                    setAnkiFilter(AnkiFilter(rememberedFilterActive = filterRemembered))
                    setAutoFlip(AutoFlip(autoFLip,autoFlipSec))
                }
            }

        }
        _binding =  AnkiFragBaseBinding.inflate(inflater, container, false)
        val a = childFragmentManager.findFragmentById(binding.ankiFragContainerView.id) as NavHostFragment
        val myNavCon = a.navController
        ankiSettingSharedPref = requireActivity().getSharedPreferences("anki_setting",Context.MODE_PRIVATE)
        val settingVisibilityObserver = Observer<Boolean>{ settingVisible ->
            saveAutoFlipSec()
            arrayOf(binding.frameLayAnkiSetting,binding.viewAnkiFragConCover).onEach {
                it.visibility = if(settingVisible) View.VISIBLE else View.GONE
            }
        }
        requireActivity().findViewById<ImageView>(R.id.bnv_imv_add).apply {
            visibility = View.INVISIBLE
            isEnabled = false
        }
        mainViewModel.setChildFragmentStatus(MainFragment.Anki)
        setUpSettingContent()

        ankiSettingPopUpAddCL()
        ankiBackGroundCoverAddCL()
        ankiBaseViewModel. setAnkiBaseNavCon(myNavCon)
        ankiBaseViewModel.settingVisible.observe(viewLifecycleOwner,settingVisibilityObserver)




        val root: View = binding.root




        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireActivity().findViewById<ImageView>(R.id.bnv_imv_add).apply {
            visibility = View.VISIBLE
            isEnabled = true
        }
        _binding = null
    }


    private fun setSelectedStateAndAlpha(stateView:View,alphaChangeView:View,boolean: Boolean){
        stateView.isSelected = boolean
        alphaChangeView.alpha = if(boolean) 1f else 0.5f
    }

    private fun changeSelectedStateAndAlpha(stateView:View,alphaChangeView:View):Boolean{
        setSelectedStateAndAlpha(stateView,alphaChangeView,stateView.isSelected.not())
        return stateView.isSelected
    }
    private fun changeSelectedState(v: View):Boolean{
        v.isSelected = v.isSelected.not()
        return v.isSelected
    }
    private fun putBooleanToSP(stringId:Int,boolean: Boolean){
        ankiSettingSharedPref.edit {
            putBoolean(requireActivity().getString(stringId),boolean)
            apply()
        }
    }
    private fun putIntToSP(stringId:Int,int: Int){
        ankiSettingSharedPref.edit {
            putInt(requireActivity().getString(stringId),int)
            apply()
        }
    }
    private fun getAutoFlipSec():Int{
        val text = binding.bindingSetting.bindingSettingContent.edtAutoFlipSeconds.text.toString()
        return if(text=="") AutoFlip().seconds
        else text.toInt()
    }
    private fun saveAutoFlipSec(){
        binding.bindingSetting.bindingSettingContent.apply {
            val autoSetting = AutoFlip(checkboxAutoFlip.isSelected,getAutoFlipSec())
            ankiSettingPopUpViewModel.setAutoFlip(autoSetting)
            putIntToSP(R.string.s_p_anki_setting_auto_flip_seconds,autoSetting.seconds)

        }
    }

    override fun onClick(p0: View?) {

        val  scrollBinding = binding.bindingSetting.bindingSettingContent

        fun onClickAutoFlipCheckBox(){
            scrollBinding.apply {
                val autoFlip =  changeSelectedStateAndAlpha(checkboxAutoFlip,linLayAnkiSettingAutoFlip)
                edtAutoFlipSeconds.isEnabled = autoFlip
                ankiSettingPopUpViewModel.setAutoFlip(AutoFlip(autoFlip,getAutoFlipSec()))
                putBooleanToSP(R.string.s_p_anki_setting_auto_flip_active,autoFlip)
            }
        }
        fun onClickReverseSides(){
            scrollBinding.apply {
                val reverseSide = changeSelectedStateAndAlpha(checkboxReverseSides,linLayAnkiSettingReverseSide)
                ankiSettingPopUpViewModel.setReverseCardSide(reverseSide)
                putBooleanToSP(R.string.s_p_anki_setting_reverse_side,reverseSide)
            }
        }
        fun onClickTypeAnswer(){
            scrollBinding.apply {
                val typeAnswer = changeSelectedStateAndAlpha(checkboxTypeAnswer,linLayAnkiSettingTypeAnswer)
                ankiSettingPopUpViewModel.setTypeAnswer(typeAnswer)
                putBooleanToSP(R.string.s_p_anki_setting_type_answer,typeAnswer)
            }
        }

        fun onClickStartAnki() {
            saveAutoFlipSec()
            ankiBaseViewModel.setSettingVisible(false)
            if(ankiBaseViewModel.returnActiveFragment()== AnkiFragments.Flip)
                ankiBaseViewModel.returnAnkiBaseNavCon()?.popBackStack()
            ankiBaseViewModel.returnAnkiBaseNavCon()
                ?.navigate(AnkiFlipBaseFragDirections.toFlipFrag())
        }

        fun onClickFilterCheckBoxRemembered(){
            val ankiFilter = ankiSettingPopUpViewModel.returnAnkiFilter()
            scrollBinding.apply {
                val remFilter = changeSelectedStateAndAlpha(checkboxFilterCardRememberStatus,linLayAnkiSettingFilterRemembered)
                ankiFilter.rememberedFilterActive = remFilter
                ankiSettingPopUpViewModel.setAnkiFilter(ankiFilter)
                putBooleanToSP(R.string.s_p_anki_setting_filter_remembered,remFilter)
            }
        }
        fun makeSelected(view: View){
            view.isSelected = true
        }

        binding.apply {
            bindingSetting.apply {
                bindingSettingContent.apply {
                    ankiSettingPopUpViewModel.apply {
                        when(p0){
                            edtAutoFlipSeconds                          -> makeSelected(checkboxAutoFlip)
                            checkboxAutoFlip                            -> onClickAutoFlipCheckBox()
                            checkboxReverseSides                        -> onClickReverseSides()
                            checkboxTypeAnswer                          -> onClickTypeAnswer()
                            imvCloseSetting                             -> ankiBaseViewModel.setSettingVisible(false)
                            btnStartAnki                                -> onClickStartAnki()
                            checkboxFilterCardRememberStatus            -> onClickFilterCheckBoxRemembered()


                        }
                    }
                }


            }

        }
    }
}