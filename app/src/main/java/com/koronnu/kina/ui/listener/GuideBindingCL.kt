package com.koronnu.kina.ui.listener

import android.view.View
import com.koronnu.kina.ui.viewmodel.GuideViewModel

class GuideBindingCL(private val guideViewModel: GuideViewModel): View.OnClickListener{
    private val guideBinding = guideViewModel.getGuideBinding
    private val confirmEndBinding = guideBinding.confirmEndGuideBinding
    override fun onClick(v: View?) {
        when(v){
            confirmEndBinding.btnCloseConfirmEnd ->  guideViewModel.onClickBtnCloseConfirmEnd()
            confirmEndBinding.btnCancelEnd       ->  guideViewModel.onClickBtnCancelEnd()
            confirmEndBinding.btnCommitEnd       ->  guideViewModel.onClickBtnCommitEnd()
        }
    }
}