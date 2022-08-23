package com.example.tangochoupdated.ui.view_set_up
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import com.example.tangochoupdated.databinding.*
import com.example.tangochoupdated.ui.listener.FlipBaseFragCL
import com.example.tangochoupdated.ui.viewmodel.AnkiFlipFragViewModel
import com.example.tangochoupdated.ui.viewmodel.AnkiFragBaseViewModel
import com.example.tangochoupdated.ui.viewmodel.AnkiSettingPopUpViewModel

class AnkiFlipFragViewSetUp(private val bindingBase:AnkiFlipFragBaseBinding,
                            private val viewModel:AnkiFlipFragViewModel,
                            private val fragmentActivity: FragmentActivity,
                            private val ankiBaseViewModel:AnkiFragBaseViewModel,
                            private val settingPopUpViewModel: AnkiSettingPopUpViewModel,
                            private val flipNavCon:NavController) {
    fun setUpViewStart(){
        bindingBase.progressBarBinding.frameLayProgressbarRemembered.removeView(bindingBase.progressBarBinding.imvRememberedEndIcon)
        setUpCL()
    }
    fun applyProgress(parentPosition:Int,flipItems:Int,frontSide:Boolean,reverseSides:Boolean){

        val postion:Int =  if((frontSide&&reverseSides.not())||(frontSide.not()&&reverseSides))(parentPosition + 1)*2-1
        else (parentPosition+1)*2
        val all = flipItems*2
        bindingBase.progressBarBinding.progressbarRemembered.progress =
            (postion/all.toDouble()*100 ).toInt()


    }
    fun setUpCL(){
        bindingBase.apply {
            topBinding.apply {
                arrayOf(
                    imvBack,
                    imvAnkiSetting,
                    imvAnkiSetting,
                    btnSetFlag,
                    btnRemembered,
                    btnFlipNext,
                    btnFlipPrevious,
                    btnAddCard).onEach {
                        it.setOnClickListener(FlipBaseFragCL(bindingBase,viewModel,ankiBaseViewModel, fragmentActivity,flipNavCon, settingPopUpViewModel))
                }
            }

        }
    }


}