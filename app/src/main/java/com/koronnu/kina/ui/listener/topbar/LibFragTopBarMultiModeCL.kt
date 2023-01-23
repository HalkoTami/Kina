package com.koronnu.kina.ui.listener.topbar

import android.view.View
import com.koronnu.kina.ui.tabLibrary.LibraryBaseViewModel

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