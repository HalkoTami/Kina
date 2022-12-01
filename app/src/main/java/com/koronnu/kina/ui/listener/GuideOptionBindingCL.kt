package com.koronnu.kina.ui.listener

import android.view.View
import com.koronnu.kina.customClasses.enumClasses.Guides
import com.koronnu.kina.ui.viewmodel.GuideOptionMenuViewModel

class GuideOptionBindingCL(private val guideOptionMenuViewModel: GuideOptionMenuViewModel): View.OnClickListener{
    val binding = guideOptionMenuViewModel.getGuideOptionMenuBinding
    override fun onClick(v: View?) {
        binding.apply {
            guideOptionMenuViewModel.apply {
                when(v){
                    menuHowToDeleteItems -> onClickGuideMenu(Guides.DeleteItems)
                    menuHowToCreateItems -> onClickGuideMenu(Guides.CreateItems)
                    menuHowToEditItems   -> onClickGuideMenu(Guides.EditItems)
                    menuHowToMoveItems   -> onClickGuideMenu(Guides.MoveItems)
                }
            }

        }
    }
}