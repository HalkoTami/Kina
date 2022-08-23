package com.example.tangochoupdated.ui.listener

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.example.tangochoupdated.R
import com.example.tangochoupdated.databinding.AnkiFlipFragBaseBinding
import com.example.tangochoupdated.ui.viewmodel.AnkiFlipFragViewModel
import com.example.tangochoupdated.ui.viewmodel.AnkiFragBaseViewModel
import com.example.tangochoupdated.ui.viewmodel.AnkiSettingPopUpViewModel


class FlipBaseFragCL(val binding:AnkiFlipFragBaseBinding,
                     val flipViewModel:AnkiFlipFragViewModel,
                     val ankiBaseViewModel:AnkiFragBaseViewModel,
                     val ankiFlipFrag:FragmentActivity,
                     val flipNavCon:NavController,
                     val settingPopUpViewModel: AnkiSettingPopUpViewModel): View.OnClickListener {

    override fun onClick(v: View?) {
        binding.apply {
            topBinding.apply {
                when(v){
                    imvBack -> ankiFlipFrag.onBackPressed()
                    imvAnkiSetting -> ankiBaseViewModel.setSettingVisible(true)
                    imvAnkiSetting -> TODO()
                    btnSetFlag -> TODO()
                    btnRemembered -> {
                        v.isSelected = !v.isSelected
                        flipViewModel.changeRememberStatus()
                    }
                    btnFlipNext -> {
                        settingPopUpViewModel.apply {
                            flipNavCon.navigate(flipViewModel.flipNext(returnReverseCardSide(),returnTypeAnswer()) ?:return )
                        }

                    }
                    btnFlipPrevious -> {
                        settingPopUpViewModel.apply {
                            flipNavCon.navigate(flipViewModel.flipPrevious(returnReverseCardSide(),returnTypeAnswer()) ?:return )
                        }
                    }
                    btnAddCard -> TODO()
                }
            }
        }
    }
}