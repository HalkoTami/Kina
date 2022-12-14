package com.koronnu.kina.ui.listener.topbar
import android.view.View
import com.koronnu.kina.databinding.LibraryFragTopBarChooseFileMoveToBinding
import com.koronnu.kina.tabLibrary.LibraryBaseViewModel

class LibFragTopBarChooseFileMoveToCL(val binding: LibraryFragTopBarChooseFileMoveToBinding, val libVM: LibraryBaseViewModel,): View.OnClickListener{

    val moveTo = binding

    override fun onClick(v: View?) {
        binding.apply {
            when(v){

                moveTo.imvCloseChooseFileMoveTo -> libVM.returnLibraryNavCon()?.popBackStack()


            }
        }
    }

}