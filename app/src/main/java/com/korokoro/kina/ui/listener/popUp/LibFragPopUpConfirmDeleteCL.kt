package com.korokoro.kina.ui.listener.popUp

import android.view.View
import com.korokoro.kina.databinding.LibraryFragPopupConfirmDeleteAllChildrenBinding
import com.korokoro.kina.databinding.LibraryFragPopupConfirmDeleteBinding
import com.korokoro.kina.ui.viewmodel.DeletePopUpViewModel
import com.korokoro.kina.ui.viewmodel.LibraryBaseViewModel

class LibFragPopUpConfirmDeleteCL(val onlyP: LibraryFragPopupConfirmDeleteBinding,
                                  val deleteAllC: LibraryFragPopupConfirmDeleteAllChildrenBinding,
                                  val libVM: LibraryBaseViewModel,
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