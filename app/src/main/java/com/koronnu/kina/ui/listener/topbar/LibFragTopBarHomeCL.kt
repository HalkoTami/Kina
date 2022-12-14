package com.koronnu.kina.ui.listener.topbar
import android.view.View
import com.koronnu.kina.databinding.LibraryFragTopBarHomeBinding
import com.koronnu.kina.tabLibrary.LibraryBaseViewModel

class LibFragTopBarHomeCL(private val binding: LibraryFragTopBarHomeBinding,
                          private val libVM: LibraryBaseViewModel,
                          ): View.OnClickListener{
    val home = binding
    val guideOptionMenuViewModel = libVM.guideOptionMenuViewModel


    override fun onClick(v: View?) {
        binding.apply {
            when(v){
                home.frameLayInBox-> libVM.onClickInBox()
                btnGuide -> guideOptionMenuViewModel.onclickBtnGuide()
            }
        }
    }

}