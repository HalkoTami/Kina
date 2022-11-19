package com.koronnu.kina.ui.listener.topbar
import android.view.View
import com.koronnu.kina.databinding.LibraryFragTopBarAncestorsBinding
import com.koronnu.kina.databinding.LibraryFragTopBarFileBinding
import com.koronnu.kina.ui.viewmodel.LibraryBaseViewModel

class LibFragTopBarFileCL(val binding: LibraryFragTopBarFileBinding,
                          val ancestorsBinding: LibraryFragTopBarAncestorsBinding,
                          val libVM: LibraryBaseViewModel, ): View.OnClickListener{

    val file = binding


    override fun onClick(v: View?) {
        val navCon = libVM.returnLibraryNavCon() ?:return
        binding.apply {
            when(v){

                imvGoBack -> navCon.popBackStack()
                ancestorsBinding.lineLayGGFile -> {
                    navCon.popBackStack()
                    navCon.popBackStack()
                }
                ancestorsBinding.lineLayGPFile ->{
                    navCon.popBackStack()
                }
            }
        }
    }

}