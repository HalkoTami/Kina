package com.koronnu.kina.ui.listener.popUp

import android.view.View
import com.koronnu.kina.databinding.LibraryFragPopupConfirmMoveToFileBinding
import com.koronnu.kina.ui.tabLibrary.chooseFileMoveTo.ChooseFileMoveToViewModel

class LibFragPopUpConfirmMoveToFileCL(private val binding: LibraryFragPopupConfirmMoveToFileBinding,
                                      private val chooseFileMoveToViewModel: ChooseFileMoveToViewModel
): View.OnClickListener{
    override fun onClick(v: View?) {
        binding.apply {
            when(v){
                btnCancelMove -> chooseFileMoveToViewModel.setPopUpVisible(false)
                btnCloseConfirmMove -> chooseFileMoveToViewModel.setPopUpVisible(false)
                btnCommitMove -> {
                    chooseFileMoveToViewModel.moveSelectedItemToFile()
                }


            }
        }

    }
}