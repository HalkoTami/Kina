package com.korokoro.kina.ui.listener.topbar
import android.view.View
import com.korokoro.kina.databinding.LibraryFragTopBarChooseFileMoveToBinding
import com.korokoro.kina.ui.viewmodel.LibraryBaseViewModel

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