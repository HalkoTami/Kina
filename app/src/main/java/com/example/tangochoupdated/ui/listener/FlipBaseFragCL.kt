package com.example.tangochoupdated.ui.listener

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.example.tangochoupdated.R
import com.example.tangochoupdated.databinding.AnkiFlipFragBaseBinding
import com.example.tangochoupdated.db.enumclass.AnimationAttributes
import com.example.tangochoupdated.ui.fragment.base_frag_con.CreateCardFragmentBaseDirections
import com.example.tangochoupdated.ui.viewmodel.*


class FlipBaseFragCL(val binding:AnkiFlipFragBaseBinding,
                     val flipViewModel:AnkiFlipFragViewModel,
                     val ankiBaseViewModel:AnkiFragBaseViewModel,
                     val ankiFlipFrag:FragmentActivity,
                     val flipNavCon:NavController,
                     val settingPopUpViewModel: AnkiSettingPopUpViewModel,
                     val createFileViewModel: CreateFileViewModel,
                     val createCardViewModel: CreateCardViewModel,
                     val mainNavController: NavController): View.OnClickListener {

    override fun onClick(v: View?) {
        binding.apply {
            topBinding.apply {
                when(v){
                    imvBack -> ankiFlipFrag.onBackPressed()
                    imvAnkiSetting -> ankiBaseViewModel.setSettingVisible(true)
                    imvAnkiSetting -> TODO()
                    btnSetFlag -> {
                        v.isSelected = !v.isSelected
                        flipViewModel.changeFlagStatus()
                    }
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
                    btnAddCard -> {
                        createFileViewModel.setBottomMenuVisible(true)
                    }
                    btnStopCount -> {
                        v.isSelected = !v.isSelected
                        if(v.isSelected)flipViewModel.setCountDownAnim(AnimationAttributes.Pause) else flipViewModel.setCountDownAnim(AnimationAttributes.Resume)
                    }
                    imvEditCard -> {
                        val libOrder = flipViewModel.returnParentCard()?.libOrder ?:return
                        createCardViewModel.setStartingPosition(libOrder)
                        mainNavController.navigate(CreateCardFragmentBaseDirections.openCreateCard())
                    }
                }
            }
        }
    }
}