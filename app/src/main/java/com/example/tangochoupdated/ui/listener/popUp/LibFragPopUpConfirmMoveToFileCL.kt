package com.example.tangochoupdated.ui.listener.popUp

import android.view.View
import androidx.navigation.NavController
import com.example.tangochoupdated.databinding.LibraryFragPopupConfirmDeleteAllChildrenBinding
import com.example.tangochoupdated.databinding.LibraryFragPopupConfirmDeleteBinding
import com.example.tangochoupdated.databinding.LibraryFragPopupConfirmMoveToFileBinding
import com.example.tangochoupdated.ui.viewmodel.ChooseFileMoveToViewModel
import com.example.tangochoupdated.ui.viewmodel.DeletePopUpViewModel
import com.example.tangochoupdated.ui.viewmodel.LibraryViewModel

class LibFragPopUpConfirmMoveToFileCL(val binding: LibraryFragPopupConfirmMoveToFileBinding,
                                      val chooseFileMoveToViewModel: ChooseFileMoveToViewModel,
                                      val libNavCon:NavController): View.OnClickListener{
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