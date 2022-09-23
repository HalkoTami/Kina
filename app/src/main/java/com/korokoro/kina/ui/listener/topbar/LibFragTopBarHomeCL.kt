package com.korokoro.kina.ui.listener.topbar
import android.view.View
import com.korokoro.kina.databinding.LibraryFragTopBarHomeBinding
import com.korokoro.kina.ui.animation.makeToast
import com.korokoro.kina.ui.viewmodel.LibraryBaseViewModel

class LibFragTopBarHomeCL(private val binding: LibraryFragTopBarHomeBinding,
                          private val libVM: LibraryBaseViewModel,
                          ): View.OnClickListener{
    val home = binding


    override fun onClick(v: View?) {
        binding.apply {
            when(v){
                home.frameLayInBox-> libVM.onClickInBox()
                home.imvBookMark -> makeToast(v.context,"todo")
            }
        }
    }

}