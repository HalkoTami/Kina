package com.korokoro.kina.ui.listener.topbar
import android.view.View
import com.korokoro.kina.databinding.LibraryFragTopBarInboxBinding
import com.korokoro.kina.ui.viewmodel.LibraryBaseViewModel

class LibFragTopBarInBoxCL(val binding: LibraryFragTopBarInboxBinding, val libVM: LibraryBaseViewModel, ): View.OnClickListener{
    val inBox = binding


    override fun onClick(v: View?) {
        binding.apply {
            when(v){

                inBox.imvMoveToFlashCard -> {
                    libVM.onClickMoveInBoxCardToFlashCard()
                }
                inBox.imvCloseInbox -> {
                    libVM.returnLibraryNavCon()?.popBackStack()
                }




            }
        }
    }

}