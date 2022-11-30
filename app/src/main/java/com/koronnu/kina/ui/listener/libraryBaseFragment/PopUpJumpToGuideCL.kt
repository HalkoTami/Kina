package com.koronnu.kina.ui.listener.libraryBaseFragment

import android.view.View
import com.koronnu.kina.ui.viewmodel.LibraryBaseViewModel

class PopUpJumpToGuideCL(libraryBaseVM: LibraryBaseViewModel): View.OnClickListener{
        val binding = libraryBaseVM.getChildFragBinding.bindingPopupJumpToGuide
        private val popUpJumpToGuideViewModel = libraryBaseVM.popUpJumpToGuideViewModel
        override fun onClick(v: View?) {
            binding.apply {
                when(v){
                    binding.conLayPopUpJumpToGuideContent->  popUpJumpToGuideViewModel.onClickConLayPopUpJumpToGuideContent()
                    binding.imvPopUpJumpToGuideClose -> popUpJumpToGuideViewModel.onClickImvPopUpJumpToGuideClose()
                }
            }
        }
    }