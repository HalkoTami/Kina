package com.example.tangochoupdated.ui.listener.popUp

import android.view.View
import com.example.tangochoupdated.databinding.LibraryFragPopupConfirmDeleteAllChildrenBinding
import com.example.tangochoupdated.databinding.LibraryFragPopupConfirmDeleteBinding
import com.example.tangochoupdated.ui.viewmodel.DeletePopUpViewModel
import com.example.tangochoupdated.ui.viewmodel.LibraryViewModel

class LibFragPopUpConfirmDeleteCL(val onlyP: LibraryFragPopupConfirmDeleteBinding,
                                  val deleteAllC: LibraryFragPopupConfirmDeleteAllChildrenBinding,
                                  val libVM: LibraryViewModel,
                                  val deletePopUpViewModel: DeletePopUpViewModel): View.OnClickListener{
    override fun onClick(v: View?) {
        when(v){
            onlyP.btnCloseConfirmDeleteOnlyParentPopup -> deletePopUpViewModel.setConfirmDeleteVisible(false)
            onlyP.btnCommitDeleteOnlyParent -> {
                deletePopUpViewModel.apply {
                    if(checkDeletingItemsHasChildren()) {
                        setConfirmDeleteVisible(false)
                        setConfirmDeleteWithChildrenVisible(true)
                    } else deleteOnlyFile()
                }

            }
            onlyP.btnCancel -> deletePopUpViewModel.setConfirmDeleteVisible(false,)
            deleteAllC.btnCloseConfirmDeleteOnlyParentPopup -> deletePopUpViewModel.setConfirmDeleteWithChildrenVisible(false)
            deleteAllC.btnDeleteAllChildren -> deletePopUpViewModel.deleteFileWithChildren()
            deleteAllC.deleteOnlyFile -> deletePopUpViewModel.deleteOnlyFile()
            deleteAllC.btnCancel -> deletePopUpViewModel.setConfirmDeleteWithChildrenVisible(false)


        }
    }
}