package com.korokoro.kina.ui.listener

import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import com.korokoro.kina.databinding.AnkiFlipFragBaseBinding
import com.korokoro.kina.ui.viewmodel.customClasses.AnimationAttributes
import com.korokoro.kina.ui.fragment.anki_frag_con.AnkiBoxContentRVFragmentDirections
import com.korokoro.kina.ui.fragment.base_frag_con.CreateCardFragmentBaseDirections
import com.korokoro.kina.ui.viewmodel.*


class FlipBaseFragCL(val binding:AnkiFlipFragBaseBinding,
                     val flipViewModel:AnkiFlipBaseViewModel,
                     val ankiBaseViewModel:AnkiBaseViewModel,
                     val ankiFlipFrag:FragmentActivity,
                     val flipNavCon:NavController,
                     val settingPopUpViewModel: AnkiSettingPopUpViewModel,
                     val createFileViewModel: EditFileViewModel,
                     val createCardViewModel: CreateCardViewModel,
                     val mainNavController: NavController,
                     val ankiNavCon:NavController): View.OnClickListener {

    override fun onClick(v: View?) {
        binding.apply {
            topBinding.apply {
                when(v){
                    btnFlipItemList -> ankiNavCon.navigate(AnkiBoxContentRVFragmentDirections.toFlipItemRvFrag())
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
                        if(v.isSelected)flipViewModel.setCountDownAnim(AnimationAttributes.Pause) else flipViewModel.setCountDownAnim(
                            AnimationAttributes.Resume)
                    }
                    imvEditCard -> {
                        val libOrder = flipViewModel.returnParentCard()?.libOrder ?:return
                        createCardViewModel.setParentPosition(libOrder)
                        mainNavController.navigate(CreateCardFragmentBaseDirections.openCreateCard())
                    }
                }
            }
        }
    }
}