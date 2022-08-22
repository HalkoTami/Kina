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
                            private val settingPopUpViewModel: AnkiSettingPopUpViewModel) {
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
                        it.setOnClickListener(FlipBaseFragCL(bindingBase,viewModel,ankiBaseViewModel, fragmentActivity,settingPopUpViewModel))
                }
            }

        }
    }


}