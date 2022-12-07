package com.koronnu.kina.ui.listener.libraryBaseFragment

import android.view.View
import com.koronnu.kina.ui.viewmodel.LibraryBaseViewModel
import com.koronnu.kina.ui.viewmodel.PopUpJumpToGuideViewModel

class PopUpJumpToGuideCL(private val popUpJumpToGuideViewModel: PopUpJumpToGuideViewModel): View.OnClickListener{
        private val binding = popUpJumpToGuideViewModel.popupJumpToGuideBinding
        override fun onClick(v: View?) {
            binding.apply {
                when(v){
                    binding.conLayPopUpJumpToGuideContent->  popUpJumpToGuideViewModel.onClickConLayPopUpJumpToGuideContent()
                    binding.imvPopUpJumpToGuideClose -> popUpJumpToGuideViewModel.onClickImvPopUpJumpToGuideClose()
                }
            }
        }
    }