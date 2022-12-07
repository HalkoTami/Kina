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
    val libVM: LibraryBaseViewModel,
): View.OnClickListener{

    override fun onClick(v: View?) {

        when(v){
            libVM.getMultiModeTopBarBinding.imvCloseMultiMode                 -> libVM.onClickCloseMultiMode()
            libVM.getMultiModeTopBarBinding.imvSelectAll                      -> libVM.onClickMultiMenuMakeAllSelected()
            libVM.getMultiModeTopBarBinding.imvChangeMenuVisibility           -> libVM.onClickImvChangeMultiMenuVisibility()
            libVM.getMultiModeMenuBinding.linLayMoveSelectedItems     -> libVM.onClickMultiMenuMoveSelectedItemToFile()
            libVM.getMultiModeMenuBinding.linLayDeleteSelectedItems   -> libVM.onClickMultiMenuDeleteSelectedItems()
        }
    }

}