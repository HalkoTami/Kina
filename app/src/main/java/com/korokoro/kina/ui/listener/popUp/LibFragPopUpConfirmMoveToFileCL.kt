package com.korokoro.kina.ui.listener.popUp

import android.view.View
import androidx.navigation.NavController
import com.korokoro.kina.databinding.LibraryFragPopupConfirmMoveToFileBinding
import com.korokoro.kina.ui.viewmodel.ChooseFileMoveToViewModel

class LibFragPopUpConfirmMoveToFileCL(private val binding: LibraryFragPopupConfirmMoveToFileBinding,
                                      private val chooseFileMoveToViewModel: ChooseFileMoveToViewModel,
                                      private val libNavCon:NavController): View.OnClickListener{
    override fun onClick(v: View?) {
        binding.apply {
            when(v){
                btnCancelMove -> chooseFileMoveToViewModel.setPopUpVisible(false)
                btnCloseConfirmMove -> chooseFileMoveToViewModel.setPopUpVisible(false)
                btnCommitMove -> {
                    chooseFileMoveToViewModel.moveSelectedItemToFile(libNavCon)
                }


            }
        }

    }
}