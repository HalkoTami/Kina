package com.koronnu.kina.ui.listener

import android.view.View
import com.koronnu.kina.activity.MainActivity
import com.koronnu.kina.customClasses.enumClasses.Guides
import com.koronnu.kina.ui.viewmodel.GuideOptionMenuViewModel

class GuideOptionBindingCL(val activity: MainActivity): View.OnClickListener{
    private val guideOptionMenuViewModel get() = activity.mainActivityViewModel.guideOptionMenuViewModel
    val binding                 = guideOptionMenuViewModel.getGuideOptionMenuBinding
    override fun onClick(v: View?) {
        binding.apply {
            guideOptionMenuViewModel.apply {
                when(v){
                    menuHowToDeleteItems -> onClickGuideMenu(Guides.DeleteItems,activity)
                    menuHowToCreateItems -> onClickGuideMenu(Guides.CreateItems,activity)
                    menuHowToEditItems   -> onClickGuideMenu(Guides.EditItems,activity)
                    menuHowToMoveItems   -> onClickGuideMenu(Guides.MoveItems,activity)
                }
            }

        }
    }
}