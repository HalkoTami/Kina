package com.koronnu.kina.ui.listener.topbar

import android.view.View
import android.widget.FrameLayout
import com.koronnu.kina.actions.makeToast
import com.koronnu.kina.databinding.LibItemTopBarMenuBinding
import com.koronnu.kina.databinding.LibraryFragTopBarMultiselectModeBinding
import com.koronnu.kina.db.enumclass.FileStatus
import com.koronnu.kina.ui.viewmodel.DeletePopUpViewModel
import com.koronnu.kina.ui.viewmodel.LibraryBaseViewModel

class LibFragTopBarMultiModeCL(
    val binding: LibraryFragTopBarMultiselectModeBinding,
    val menuBinding: LibItemTopBarMenuBinding,
    val menuFrame:FrameLayout,
    val libVM: LibraryBaseViewModel,
    val deletePopUpViewModel:DeletePopUpViewModel
): View.OnClickListener{
    val multi = binding

    override fun onClick(v: View?) {

        val notSelected = libVM.returnSelectedItems().isEmpty()
        binding.apply {
            when(v){

                multi.imvCloseMultiMode -> libVM.setMultipleSelectMode(false)
                multi.imvSelectAll -> {
                    libVM.changeAllRVSelectedStatus(v.isSelected.not())
                    v.isSelected = v.isSelected.not()
                }
                multi.imvChangeMenuVisibility -> libVM.setMultiMenuVisibility(libVM.returnMultiMenuVisibility().not())

                menuBinding.linLayMoveSelectedItems -> libVM.onClickMultiMenuMoveSelectedItemToFile()
                menuBinding.linLayDeleteSelectedItems -> if(notSelected) makeToast(v.context,"todo")
                else {
                    deletePopUpViewModel.setDeletingItem(libVM.returnSelectedItems().toMutableList())
                    deletePopUpViewModel.setConfirmDeleteVisible(true)

                }



            }
        }
    }

}