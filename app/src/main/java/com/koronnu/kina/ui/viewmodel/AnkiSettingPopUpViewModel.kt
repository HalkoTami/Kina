package com.koronnu.kina.ui.viewmodel

import android.content.SharedPreferences
import android.content.res.Resources
import android.view.View
import androidx.core.content.edit
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.koronnu.kina.R
import com.koronnu.kina.application.RoomApplication
import com.koronnu.kina.customClasses.enumClasses.AnkiFragments
import com.koronnu.kina.customClasses.normalClasses.AnkiFilter
import com.koronnu.kina.customClasses.normalClasses.AutoFlip
import com.koronnu.kina.db.MyRoomRepository

class AnkiSettingPopUpViewModel(val repository: MyRoomRepository,
                                val ankiBaseViewModel: AnkiBaseViewModel,
                                val resources: Resources) : ViewModel() {



    companion object{
        fun  getFactory(ankiBaseViewModel: AnkiBaseViewModel): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]) as RoomApplication
                val repository = application.repository
                val resources = application.resources
                return AnkiSettingPopUpViewModel(repository,ankiBaseViewModel,resources) as T
            }
        }

    }
    private val ankiFlipBaseViewModel get() = ankiBaseViewModel.ankiFlipBaseViewModel
    private var _settingSharedPref : SharedPreferences? = null
    set(value) {
        field = value
        doAfterSettingSharedPrefSet()
    }
    private val settingSharedPref get() = _settingSharedPref!!
    private val ankiBaseBinding get() = ankiBaseViewModel.ankiFragBaseBinding
    private val settingBinding  get() = ankiBaseBinding.bindingSetting
    private val contentBin      get() = settingBinding.bindingSettingContent

    private fun doAfterSettingSharedPrefSet(){

        val reverseSide =  settingSharedPref.getBoolean(
            resources.getString(R.string.sp_anki_setting_reverse_side),false)
        val autoFLip = settingSharedPref.getBoolean(
            resources.getString(R.string.sp_anki_setting_auto_flip_active),false)
        val typeAnswer = settingSharedPref.getBoolean(
            resources.getString(R.string.sp_anki_setting_type_answer),false)
        val filterRemembered =  settingSharedPref.getBoolean(
            resources.getString(R.string.sp_anki_setting_filter_remembered),false)
        val autoFlipSec = settingSharedPref.getInt(
            resources.getString(R.string.sp_anki_setting_auto_flip_seconds),
            AutoFlip().seconds)
        setReverseCardSide(reverseSide)
        setAutoFlip(AutoFlip(autoFLip,autoFlipSec))
        setAnkiFilter(AnkiFilter(rememberedFilterActive = filterRemembered))
        setTypeAnswer(typeAnswer)
    }
    fun setListener(){
        contentBin.edtAutoFlipSeconds.addTextChangedListener { setAutoFlipSeconds() }
        ankiBaseBinding.frameLayAnkiSetting         .setOnClickListener(null)
        contentBin.checkboxFilterCardRememberStatus .setOnClickListener { changeAnkiFilterRememberedActiveStatus() }
        contentBin.checkboxAutoFlip                 .setOnClickListener { reverseAutoFlipActiveStatus() }
        contentBin.checkboxReverseSides             .setOnClickListener { changeReverseCardSideActiveStatus() }
        contentBin.checkboxTypeAnswer               .setOnClickListener { changeTypeAnswerActiveStatus() }
        settingBinding.imvCloseSetting              .setOnClickListener { ankiBaseViewModel.setSettingVisible(false) }
        settingBinding.btnStartAnki                 .setOnClickListener { onClickStartAnki() }
    }
    fun afterBindingSet(lifecycleOwner: LifecycleOwner,getSharedPref:(int:Int)->SharedPreferences){
        observeLiveData(lifecycleOwner)
        _settingSharedPref = getSharedPref(R.string.sp_title_ankiSetting)
    }
    private fun observeLiveData(lifecycleOwner: LifecycleOwner){
        ankiFlipBaseViewModel.ankiFlipItems.observe(lifecycleOwner){
            if(it.isNotEmpty()) settingBinding.btnStartAnki.isEnabled = true
        }
    }


    private fun onClickStartAnki() {
        ankiBaseViewModel.setSettingVisible(false)
        ankiBaseViewModel.navigateInAnkiFragments(AnkiFragments.Flip)
    }

    private fun setSelectedStateAndAlpha(stateView:View,alphaChangeView:View,boolean: Boolean){
        stateView.isSelected = boolean
        alphaChangeView.alpha = if(boolean) 1f else 0.5f
    }

    private fun changeSelectedStateAndAlpha(stateView:View,alphaChangeView:View,selected:Boolean){
        setSelectedStateAndAlpha(stateView,alphaChangeView,selected)
    }

    private fun putBooleanToSP(stringId:Int,boolean: Boolean){
        settingSharedPref.edit {
            putBoolean(resources.getString(stringId),boolean)
            apply()
        }
    }
    private fun putIntToSP(stringId:Int,int: Int){
        settingSharedPref.edit {
            putInt(resources.getString(stringId),int)
            apply()
        }
    }
    private fun getAutoFlipSec():Int{
        val text = contentBin.edtAutoFlipSeconds.text.toString()
        return if(text.isBlank()) AutoFlip().seconds
        else text.toInt()
    }

    private val _ankiFilter = MutableLiveData<AnkiFilter>()
    private fun setAnkiFilter(ankiFilter: AnkiFilter){
        _ankiFilter.value = ankiFilter
        doAfterSetAnkiFilter()
    }
    private fun doAfterSetAnkiFilter(){
        putBooleanToSP(R.string.sp_anki_setting_filter_remembered,getAnkiFilter.rememberedFilterActive)
        changeSelectedStateAndAlpha(
            contentBin.checkboxFilterCardRememberStatus,
            contentBin.linLayAnkiSettingFilterRemembered,
            getAnkiFilter.rememberedFilterActive)
    }
    private fun changeAnkiFilterRememberedActiveStatus(){
        val before = getAnkiFilter
        val after = { before.apply { rememberedFilterActive = !rememberedFilterActive }
                before }
        setAnkiFilter(after())

    }

    val getAnkiFilter get() =  _ankiFilter.value !!

    private val _autoFlip = MutableLiveData<AutoFlip>()
    private fun setAutoFlip(autoFlip: AutoFlip){
        _autoFlip.value = autoFlip
        doAfterSetAutoFlip()
    }

    private fun reverseAutoFlipActiveStatus(){
        val before = getAutoFlip
        val after = { before.apply { active=!active }
                before }
        setAutoFlip(after())
    }
    private fun setAutoFlipSeconds(){
        val before = getAutoFlip
        val after = {before.apply { seconds = getAutoFlipSec() }}
        setAutoFlip(after())
    }
    private fun doAfterSetAutoFlip(){
        putBooleanToSP(R.string.sp_anki_setting_auto_flip_active,getAutoFlip.active)
        putIntToSP(R.string.sp_anki_setting_auto_flip_seconds,getAutoFlip.seconds)
        changeSelectedStateAndAlpha(
            contentBin.checkboxAutoFlip,
            contentBin.linLayAnkiSettingAutoFlip,getAutoFlip.active)
        contentBin.edtAutoFlipSeconds.isEnabled = getAutoFlip.active

    }
    val autoFlip:LiveData<AutoFlip> = _autoFlip
    val getAutoFlip get() =   _autoFlip.value!!

    private val _typeAnswer = MutableLiveData<Boolean>()
    private fun setTypeAnswer(boolean: Boolean){
        _typeAnswer.value = boolean
        doAfterSetTypeAnswer()
    }
    private fun doAfterSetTypeAnswer(){
        putBooleanToSP(R.string.sp_anki_setting_type_answer,getTypeAnswer)
        changeSelectedStateAndAlpha(
            contentBin.checkboxTypeAnswer,
            contentBin.linLayAnkiSettingTypeAnswer,getTypeAnswer)
    }
    private fun changeTypeAnswerActiveStatus(){
        val after = getTypeAnswer.not()
        setTypeAnswer(after)
    }
    val typeAnswer:LiveData<Boolean> = _typeAnswer
    val getTypeAnswer  get() = _typeAnswer.value!!

    private val _reverseCardSide = MutableLiveData<Boolean>()
    private fun setReverseCardSide(boolean: Boolean){
        _reverseCardSide.value = boolean
        doAfterSetReverseCardSide()
    }
    private fun changeReverseCardSideActiveStatus(){
        val after = getReverseCardSideActive.not()
        setReverseCardSide(after)
    }
    private fun doAfterSetReverseCardSide(){
        changeSelectedStateAndAlpha(
            contentBin.checkboxReverseSides,
            contentBin.linLayAnkiSettingReverseSide, getReverseCardSideActive)
        putBooleanToSP(R.string.sp_anki_setting_reverse_side,getReverseCardSideActive)
    }
    val getReverseCardSideActive get() = _reverseCardSide.value!!



}