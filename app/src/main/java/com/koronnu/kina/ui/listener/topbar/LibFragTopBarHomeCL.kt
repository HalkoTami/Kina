package com.koronnu.kina.ui.listener.topbar
import android.view.View
import com.koronnu.kina.actions.makeToast
import com.koronnu.kina.databinding.LibraryFragTopBarHomeBinding
import com.koronnu.kina.ui.viewmodel.LibraryBaseViewModel

class LibFragTopBarHomeCL(private val binding: LibraryFragTopBarHomeBinding,
                          private val libVM: LibraryBaseViewModel,
                          ): View.OnClickListener{
    val home = binding


    override fun onClick(v: View?) {
        binding.apply {
            when(v){
                home.frameLayInBox-> libVM.onClickInBox()
            }
        }
    }

}