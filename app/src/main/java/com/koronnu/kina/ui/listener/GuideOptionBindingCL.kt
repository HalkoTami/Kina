package com.koronnu.kina.ui.listener

import android.view.View
import com.koronnu.kina.ui.viewmodel.GuideOptionMenuViewModel

class GuideOptionBindingCL(private val guideOptionMenuViewModel: GuideOptionMenuViewModel): View.OnClickListener{
    val binding = guideOptionMenuViewModel.getGuideOptionMenuBinding
    override fun onClick(v: View?) {
        binding.apply {
            when(v){
                menuHowToDeleteItems -> guideOptionMenuViewModel.onClickMenuHowToDeleteItems()
                menuHowToCreateItems -> guideOptionMenuViewModel.onClickMenuHowToCreateItems()
                menuHowToEditItems   -> guideOptionMenuViewModel.onClickMenuHowToEditItems()
                menuHowToMoveItems   -> guideOptionMenuViewModel.onClickMenuHowToMoveItems()
            }
        }
    }
}