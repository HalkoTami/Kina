package com.koronnu.kina.ui.tabLibrary.chooseFileMoveTo

import android.view.View
import com.koronnu.kina.databinding.LibraryFragPopupConfirmMoveToFileBinding

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