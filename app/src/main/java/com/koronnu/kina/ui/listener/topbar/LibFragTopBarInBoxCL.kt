package com.koronnu.kina.ui.listener.topbar
import android.view.View
import com.koronnu.kina.databinding.LibraryFragTopBarInboxBinding
import com.koronnu.kina.ui.tabLibrary.LibraryBaseViewModel

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